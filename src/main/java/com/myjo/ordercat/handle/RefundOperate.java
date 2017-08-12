package com.myjo.ordercat.handle;

import com.myjo.ordercat.config.OrderCatConfig;
import com.myjo.ordercat.domain.RefundOperateRecord;
import com.myjo.ordercat.domain.TianmaOrder;
import com.myjo.ordercat.domain.TianmaOrderStatus;
import com.myjo.ordercat.http.TaoBaoHttp;
import com.myjo.ordercat.http.TianmaSportHttp;
import com.myjo.ordercat.spm.ordercat.ordercat.oc_refund_operate_record.OcRefundOperateRecordImpl;
import com.myjo.ordercat.spm.ordercat.ordercat.oc_refund_operate_record.OcRefundOperateRecordManager;
import com.myjo.ordercat.utils.OcDateTimeUtils;
import com.taobao.api.domain.PurchaseOrder;
import com.taobao.api.domain.Refund;
import com.taobao.api.domain.SubPurchaseOrder;
import com.taobao.api.domain.Trade;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.script.ScriptEngine;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by lee5hx on 2017/8/3.
 */
public class RefundOperate {

    private static final Logger Logger = LogManager.getLogger(RefundOperate.class);

    private TianmaSportHttp tianmaSportHttp;

    private TaoBaoHttp taoBaoHttp;

    private ScriptEngine scriptEngine;


    private OcRefundOperateRecordManager ocRefundOperateRecordManager;


    public RefundOperate(TianmaSportHttp tianmaSportHttp, TaoBaoHttp taoBaoHttp, ScriptEngine scriptEngine) {
        this.tianmaSportHttp = tianmaSportHttp;
        this.taoBaoHttp = taoBaoHttp;
        this.scriptEngine = scriptEngine;
    }


    public void autoRefund(Long execJobId) throws Exception {

        Logger.info(String.format("开始自动退款流程 job-id:[%d]", execJobId));

        //退款列表过滤流程
        //1.调用淘宝接口,获得退款列表
        LocalDateTime lend = LocalDateTime.now();
        LocalDateTime lbegin = lend.minusDays(OrderCatConfig.getRefundDateIntervalDay());
        String startTime = OcDateTimeUtils.localDateTime2String(lbegin, OcDateTimeUtils.OC_DATE);
        String endTime = OcDateTimeUtils.localDateTime2String(lend, OcDateTimeUtils.OC_DATE);
        List<Refund> refundList = taoBaoHttp.getReceiveRefunds(null, OcDateTimeUtils.localDateTime2Date(lbegin), OcDateTimeUtils.localDateTime2Date(lend));
        Logger.info(String.format("查询[%s]-[%s]区间的退款单.size:[%d]",
                startTime,
                endTime,
                refundList.size()
        ));
        //2. 过滤退款状态与原因（保留下面的）
        //status字段
        //  WAIT_SELLER_AGREE(买家已经申请退款，等待卖家同意)
        //  WAIT_BUYER_RETURN_GOODS(卖家已经同意退款，等待买家退货)
        //  WAIT_SELLER_CONFIRM_GOODS(买家已经退货，等待卖家确认收货)
        refundList = refundList.parallelStream()
                .filter(refund -> "WAIT_SELLER_AGREE".equals(refund.getStatus()) || "WAIT_BUYER_RETURN_GOODS".equals(refund.getStatus()) || "WAIT_SELLER_CONFIRM_GOODS".equals(refund.getStatus()))
                .collect(Collectors.toList());
        Logger.info(String.format("退款单据状态过滤后的.size:[%d]",
                refundList.size()
        ));
        //reason字段（参数 后期可配置，方便后期增删）
        //  拍错/多拍
        //  缺货
        //  未按约定时间发货
        //  不喜欢/不想要
        //  物流一直未送到
        //  退运费
        //  7天无理由退换货
        //  买卖双方协商一致退款
        //  我不想要了
        //  试了不合适
        //  卖家发错货
        String[] reasons = {
                "拍错/多拍",
                "缺货",
                "未按约定时间发货",
                "不喜欢/不想要",
                "物流一直未送到",
                "退运费",
                "7天无理由退换货",
                "买卖双方协商一致退款",
                "我不想要了",
                "试了不合适",
                "卖家发错货",
        };
        Map<String, String> reasonsMap = Arrays.asList(reasons).parallelStream().collect(
                Collectors.toMap(o -> o, Function.identity())
        );
        refundList = refundList.parallelStream()
                .filter(refund -> reasonsMap.containsKey(refund.getReason()))
                .collect(Collectors.toList());
        Logger.info(String.format("退款单据原因过滤后的.size:[%d]", refundList.size()));
        //3.调用接口根据订单ID,来区分此订单是否为分销还是购销
        List<RefundOperateRecord> refundOperateRecordList = refundList.stream().map(refund -> {
            RefundOperateRecord ror = new RefundOperateRecord();
            ror.setTid(refund.getTid());
            ror.setRefundId(refund.getRefundId());
            ror.setStatus(refund.getStatus());
            ror.setReason(refund.getReason());
            ror.setSid(refund.getSid());
            ror.setCompanyName(refund.getCompanyName());


            Optional<Trade> optTrade = taoBaoHttp.getTaobaoTrade(ror.getTid());
            if (optTrade.isPresent()) {
                Trade trade = optTrade.get();
                ror.setDaixiao(trade.getIsDaixiao());
            }
            return ror;
        }).collect(Collectors.toList());
        Logger.info(String.format("refundOperateRecordList.size:[%d]", refundOperateRecordList.size()));


        //天马订单处理流程
        refundOperateRecordList.parallelStream()
                .filter(refundOperateRecord -> refundOperateRecord.getDaixiao() != null)
                .filter(refundOperateRecord -> refundOperateRecord.getDaixiao() == false)
                .forEach((RefundOperateRecord refundOperateRecord) -> {
                    Optional<TianmaOrder> optTianmaOrder = tianmaSportHttp.getTianmaOrder(refundOperateRecord.getTid());
                    if (optTianmaOrder.isPresent()) {
                        TianmaOrder tianmaOrder = optTianmaOrder.get();

                        TianmaOrderStatus tianmaOrderStatus = tianmaOrder.getStatus();
                        switch (tianmaOrderStatus) {
                            case PAYMENT_HAS_BEEN://已付款
                                refundOperateRecord.setOperateDetail(String.format("天马订单状态:[%s],天马操作[已付款取消]", TianmaOrderStatus.valueOf(tianmaOrderStatus)));
                                break;
                            case NIL20://已付款
                                refundOperateRecord.setOperateDetail(String.format("天马订单状态:[%s],天马操作[无操作!请检查一下这个订单代表的状态]", TianmaOrderStatus.valueOf(tianmaOrderStatus)));
                                break;
                            case PICKING_30://配货中30
                                refundOperateRecord.setOperateDetail(String.format("天马订单状态:[%s],天马操作[申请修改-取消订单]", TianmaOrderStatus.valueOf(tianmaOrderStatus)));
                                break;
                            case PICKING_40://配货中40
                                refundOperateRecord.setOperateDetail(String.format("天马订单状态:[%s],天马操作[申请修改-取消订单]", TianmaOrderStatus.valueOf(tianmaOrderStatus)));
                                break;
                            case FEEDBACK_SUCCESS://反馈成功
                                refundOperateRecord.setOperateDetail(String.format("天马订单状态:[%s],天马操作[申请售后]", TianmaOrderStatus.valueOf(tianmaOrderStatus)));
                                break;
                            case WAITING_RETURN://待退货
                                refundOperateRecord.setOperateDetail(String.format("天马订单状态:[%s],天马操作[填写买家的sid退回单号]", TianmaOrderStatus.valueOf(tianmaOrderStatus)));
                                break;
                            case FEEDBACK_FAILURE://反馈失败
                                refundOperateRecord.setOperateDetail(String.format("天马订单状态:[%s],天猫操作[同意退款]", TianmaOrderStatus.valueOf(tianmaOrderStatus)));
                                break;
                            case RETURNED://已退货
                                refundOperateRecord.setOperateDetail(String.format("天马订单状态:[%s],天猫操作[同意退款] ", TianmaOrderStatus.valueOf(tianmaOrderStatus)));
                                break;
                            case REFUNDED://已退款
                                refundOperateRecord.setOperateDetail(String.format("天马订单状态:[%s],天猫操作[同意退款]", TianmaOrderStatus.valueOf(tianmaOrderStatus)));
                                break;
                            default:
                                break;
                        }
                    } else {
                        refundOperateRecord.setOperateDetail(String.format("天马中没找到订单信息或者订单数量大于1"));
                    }
                    Logger.info(String.format("退款单号:%d,操作:%s",refundOperateRecord.getRefundId(),refundOperateRecord.getOperateDetail()));
                });


        //代销订单处理流程
        refundOperateRecordList.parallelStream()
                .filter(refundOperateRecord -> refundOperateRecord.getDaixiao() != null)
                .filter(refundOperateRecord -> refundOperateRecord.getDaixiao() == true)
                .forEach((RefundOperateRecord refundOperateRecord) -> {

                    Optional<PurchaseOrder> opt = taoBaoHttp.getPurchaseOrderByTcOrderId(refundOperateRecord.getTid());
                    if(opt.isPresent()){
                        PurchaseOrder purchaseOrder = opt.get();
                        List<SubPurchaseOrder> subPurchaseOrders = purchaseOrder.getSubPurchaseOrders();
                        if(subPurchaseOrders!=null && subPurchaseOrders.size()==1){
                            //SubPurchaseOrder subPurchaseOrder = subPurchaseOrders.get(0);
                            if("WAIT_BUYER_CONFIRM_GOODS".equals(purchaseOrder.getStatus())){
                                refundOperateRecord.setOperateDetail(String.format("采购单子单状态:[%s],供销操作[申请退款]", purchaseOrder.getStatus()));
                            }
                            if("WAIT_BUYER_CONFIRM_GOODS_ACOUNTED".equals(purchaseOrder.getStatus())){
                                refundOperateRecord.setOperateDetail(String.format("采购单子单状态:[%s],供销操作[申请退款]", purchaseOrder.getStatus()));
                            }
                            if("TRADE_FINISHED".equals(purchaseOrder.getStatus())){
                                refundOperateRecord.setOperateDetail(String.format("采购单子单状态:[%s],供销操作[申请退款]", purchaseOrder.getStatus()));
                            }
                            if("TRADE_CLOSED".equals(purchaseOrder.getStatus())){
                                refundOperateRecord.setOperateDetail(String.format("采购单子单状态:[%s],淘宝操作[同意退款]", purchaseOrder.getStatus()));
                            }
                            if(refundOperateRecord.getOperateDetail()==null){
                                refundOperateRecord.setOperateDetail(purchaseOrder.getStatus());
                            }


                        }else {
                            refundOperateRecord.setOperateDetail(String.format("没找到子采购单信息或者子采购单数量大于1"));
                        }
                    }else {
                        refundOperateRecord.setOperateDetail(String.format("没找到采购单信息或者采购单数量大于1"));
                    }
                    Logger.info(String.format("退款单号:%d,操作:%s",refundOperateRecord.getRefundId(),refundOperateRecord.getOperateDetail()));
                });

        //持久化数据
        refundOperateRecordList.parallelStream().forEach(refundOperateRecord -> {
            OcRefundOperateRecordImpl refundOperateRecord1 = new OcRefundOperateRecordImpl();
            refundOperateRecord1.setTid(refundOperateRecord.getTid());
            refundOperateRecord1.setRefundId(refundOperateRecord.getRefundId());
            refundOperateRecord1.setSid(refundOperateRecord.getSid());
            refundOperateRecord1.setStatus(refundOperateRecord.getStatus());
            refundOperateRecord1.setReason(refundOperateRecord.getReason());
            refundOperateRecord1.setOperateDetail(refundOperateRecord.getOperateDetail());
            refundOperateRecord1.setIsDaixiao((short) (refundOperateRecord.getDaixiao().booleanValue()?1:0));
            refundOperateRecord1.setCompanyName(refundOperateRecord.getCompanyName());
            refundOperateRecord1.setAddTime(LocalDateTime.now());
            ocRefundOperateRecordManager.persist(refundOperateRecord1);
        });

        Logger.info(String.format("持久化数据-执行完成.", execJobId));

        Logger.info(String.format("开始自动退款流程 执行完成. job-id:[%d]", execJobId));

    }

    public void setOcRefundOperateRecordManager(OcRefundOperateRecordManager ocRefundOperateRecordManager) {
        this.ocRefundOperateRecordManager = ocRefundOperateRecordManager;
    }
}
