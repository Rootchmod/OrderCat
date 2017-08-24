package com.myjo.ordercat.handle;

import com.myjo.ordercat.config.OrderCatConfig;
import com.myjo.ordercat.domain.RefundOperateRecord;
import com.myjo.ordercat.domain.ReturnResult;
import com.myjo.ordercat.domain.TianmaOrder;
import com.myjo.ordercat.domain.constant.RefundOperateType;
import com.myjo.ordercat.domain.constant.TianmaOrderStatus;
import com.myjo.ordercat.http.TaoBaoHttp;
import com.myjo.ordercat.http.TianmaSportHttp;
import com.myjo.ordercat.spm.ordercat.ordercat.oc_refund_operate_record.OcRefundOperateRecord;
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
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
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


    private void operate(RefundOperateType refundOperateType, Optional<OcRefundOperateRecord> opt, RefundOperateRecord refundOperateRecord) {

        boolean isOperate = true;
        if (opt.isPresent()) {
            OcRefundOperateRecord ocRefundOperateRecord = opt.get();
            if (refundOperateType == RefundOperateType.valueOfs(ocRefundOperateRecord.getOperateType().orElse(""))) {
                isOperate = false;
            }
            if (isOperate) {
                ocRefundOperateRecordManager.stream()
                        .filter(OcRefundOperateRecord.ID.equal(ocRefundOperateRecord.getId()))
                        .map(OcRefundOperateRecord.IS_LATEST.setTo((short) 0))
                        .forEach(ocRefundOperateRecordManager.updater());
            }
        }
        if (isOperate) {
            switch (refundOperateType) {
                case TM_OP_YFKQX://已付款取消
                    refundOperateRecord.setOperateType(refundOperateType);
                    ReturnResult<String> rt = tianmaSportHttp.orderCancel(refundOperateRecord.getTmOrderId());
                    if (rt.isSuccess()) {
                        refundOperateRecord.setOperateResult(rt.getResult().get());
                    } else {
                        refundOperateRecord.setOperateResult("订单取消失败:" + rt.getErrorCode() + ":" + rt.getErrorMessages());
                    }
                    break;
                case TM_OP_WCZ://天马没有操作
                    refundOperateRecord.setOperateType(refundOperateType);
                    refundOperateRecord.setOperateResult("操作成功!");
                    break;
                case TM_OP_SQXG_QXDD://申请修改-取消订单
                    refundOperateRecord.setOperateType(refundOperateType);
                    ReturnResult<String> rt2 = tianmaSportHttp.appAlterOrder(refundOperateRecord.getTmOrderId());
                    if (rt2.isSuccess()) {
                        refundOperateRecord.setOperateResult(rt2.getResult().get());
                    } else {
                        refundOperateRecord.setOperateResult("申请修改-取消订单失败:" + rt2.getErrorCode() + ":" + rt2.getErrorMessages());
                    }
                    break;
                case TM_OP_SQSH://申请售后
                    refundOperateRecord.setOperateType(refundOperateType);
                    ReturnResult<String> rt3 = tianmaSportHttp.soldProblem(
                            refundOperateRecord.getTmOrderId(),
                            refundOperateRecord.getMarketPrice(),
                            refundOperateRecord.getWarehouseName(),
                            refundOperateRecord.getDiscount(),
                            refundOperateRecord.getDelivery()
                    );
                    if (rt3.isSuccess()) {
                        refundOperateRecord.setOperateResult(rt3.getResult().get());
                    } else {
                        refundOperateRecord.setOperateResult("申请售后失败:" + rt3.getErrorCode() + ":" + rt3.getErrorMessages());
                    }
                    break;
                case TM_OP_HTKDDH://回填快递单号
                    refundOperateRecord.setOperateType(refundOperateType);
                    ReturnResult<String> rt1 = tianmaSportHttp.backExpressNo(
                            refundOperateRecord.getTmOrderId(),
                            refundOperateRecord.getSid(),
                            String.format("OC-%s", refundOperateRecord.getCompanyName())
                    );
                    if (rt1.isSuccess()) {
                        refundOperateRecord.setOperateResult(rt1.getResult().get());
                    } else {
                        refundOperateRecord.setOperateResult("快递单号回填失败:" + rt1.getErrorCode() + ":" + rt1.getErrorMessages());
                    }
                    break;
                case TB_OP_TYTK://同意退款
                    refundOperateRecord.setOperateType(refundOperateType);
                    refundOperateRecord.setOperateResult("暂无操作!");
//                    ReturnResult<RefundMappingResult> rt = taoBaoHttp.agreeTaobaoRpRefunds(
//                            refundOperateRecord.getRefundId(),
//                            refundOperateRecord.getRefundAmount(),
//                            refundOperateRecord.getRefundVersion(),
//                            refundOperateRecord.getRefundPhase()
//                    );
//
//                    if(rt.isSuccess()){
//                        refundOperateRecord.setOperateResult(rt.getResult().get().getMessage());
//                    }else {
//                        refundOperateRecord.setOperateResult(rt.getErrorCode()+":"+rt.getErrorMessages());
//                    }
                    break;
                case GX_OP_SQTK://申请退款
                    refundOperateRecord.setOperateType(refundOperateType);
                    Map<String,Boolean> map1 = new HashMap<>();
                    map1.put("WAIT_BUYER_PAY",false);
                    map1.put("WAIT_BUYER_CONFIRM_GOODS",true);
                    map1.put("TRADE_FOR_PAY",false);
                    map1.put("TRADE_REFUNDING",false);
                    map1.put("TRADE_FINISHED",true);
                    map1.put("TRADE_CLOSED",true);

                    if(!map1.containsKey(refundOperateRecord.getPurchaseOrderStatus())){
                        refundOperateRecord.setOperateResult(String.format("[%s]状态没有找到对应的发货标识",refundOperateRecord.getPurchaseOrderStatus()));
                        break;
                    }

                    long subOrderId = refundOperateRecord.getPurchaseOrderId();
                    boolean isReturnGoods = map1.get(refundOperateRecord.getPurchaseOrderStatus());
                    long returnFee =  Long.valueOf(refundOperateRecord.getPurchaseBuyerPayment().replaceAll("\\.",""));
                    ReturnResult<String> rt5 = taoBaoHttp.createTaobaoFeixiaoRefund(
                            subOrderId,
                            isReturnGoods,
                            returnFee
                    );
                    if(rt5.isSuccess()){
                        refundOperateRecord.setOperateResult(rt5.getResult().get());
                    }else {
                        refundOperateRecord.setOperateResult(rt5.getErrorCode()+":"+rt5.getErrorMessages());
                    }
                    break;
                default:
                    break;
            }
        }
        refundOperateRecord.setPersist(isOperate);
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
        List<String> statusesList = OrderCatConfig.getRefundRetainStatuses();
        Map<String, String> statusesMap = statusesList.parallelStream().collect(
                Collectors.toMap(o -> o, Function.identity())
        );

        refundList = refundList.parallelStream()
                .filter(refund -> statusesMap.containsKey(refund.getStatus()))
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
        List<String> reasonsList = OrderCatConfig.getRefundRetainReasons();
        Map<String, String> reasonsMap = reasonsList.parallelStream().collect(
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
            ror.setRefundVersion(refund.getRefundVersion());
            ror.setRefundPhase(refund.getRefundPhase());
            ror.setRefundAmount(refund.getRefundFee());
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


                    Optional<OcRefundOperateRecord> opt = ocRefundOperateRecordManager.stream()
                            .filter(OcRefundOperateRecord.REFUND_ID.equal(Long.valueOf(refundOperateRecord.getRefundId())))
                            .sorted(OcRefundOperateRecord.ID.comparator().reversed())
                            .findFirst();


                    Optional<TianmaOrder> optTianmaOrder = tianmaSportHttp.getTianmaOrder(refundOperateRecord.getTid());
                    if (optTianmaOrder.isPresent()) {
                        TianmaOrder tianmaOrder = optTianmaOrder.get();
                        refundOperateRecord.setTmOrderId(tianmaOrder.getOrderId());
                        refundOperateRecord.setDiscount(tianmaOrder.getDiscount());
                        refundOperateRecord.setWarehouseName(tianmaOrder.getWarehouseName());
                        refundOperateRecord.setDelivery(tianmaOrder.getDeliveryName());
                        refundOperateRecord.setMarketPrice(tianmaOrder.getMarketPrice());


                        TianmaOrderStatus tianmaOrderStatus = tianmaOrder.getStatus();
                        switch (tianmaOrderStatus) {
                            case PAYMENT_HAS_BEEN://已付款
                                refundOperateRecord.setOperateDetail(String.format("天马订单状态:[%s],天马操作[已付款取消]", TianmaOrderStatus.valueOf(tianmaOrderStatus)));
                                operate(RefundOperateType.TM_OP_YFKQX, opt, refundOperateRecord);
                                break;
                            case NIL20://已付款
                                refundOperateRecord.setOperateDetail(String.format("天马订单状态:[%s],天马操作[无操作!请检查一下这个订单代表的状态]", TianmaOrderStatus.valueOf(tianmaOrderStatus)));
                                operate(RefundOperateType.TM_OP_WCZ, opt, refundOperateRecord);
                                break;
                            case PICKING_30://配货中30
                                refundOperateRecord.setOperateDetail(String.format("天马订单状态:[%s],天马操作[申请修改-取消订单]", TianmaOrderStatus.valueOf(tianmaOrderStatus)));
                                operate(RefundOperateType.TM_OP_SQXG_QXDD, opt, refundOperateRecord);
                                break;
                            case PICKING_40://配货中40
                                refundOperateRecord.setOperateDetail(String.format("天马订单状态:[%s],天马操作[申请修改-取消订单]", TianmaOrderStatus.valueOf(tianmaOrderStatus)));
                                operate(RefundOperateType.TM_OP_SQXG_QXDD, opt, refundOperateRecord);
                                break;
                            case FEEDBACK_SUCCESS://反馈成功
                                refundOperateRecord.setOperateDetail(String.format("天马订单状态:[%s],天马操作[申请售后]", TianmaOrderStatus.valueOf(tianmaOrderStatus)));
                                operate(RefundOperateType.TM_OP_SQSH, opt, refundOperateRecord);
                                break;
                            case WAITING_RETURN://待退货
                                if (refundOperateRecord.getSid() != null) {//快递单号必须不用为空。
                                    refundOperateRecord.setOperateDetail(String.format("天马订单状态:[%s],天马操作[填写买家的sid退回单号]", TianmaOrderStatus.valueOf(tianmaOrderStatus)));
                                    operate(RefundOperateType.TM_OP_HTKDDH, opt, refundOperateRecord);
                                }
                                break;
                            case FEEDBACK_FAILURE://反馈失败
                                refundOperateRecord.setOperateDetail(String.format("天马订单状态:[%s],天猫操作[同意退款]", TianmaOrderStatus.valueOf(tianmaOrderStatus)));
                                operate(RefundOperateType.TB_OP_TYTK, opt, refundOperateRecord);
                                break;
                            case WAITING_REFUNDED://待退款
                                refundOperateRecord.setOperateDetail(String.format("天马订单状态:[%s],天猫操作[同意退款] ", TianmaOrderStatus.valueOf(tianmaOrderStatus)));
                                operate(RefundOperateType.TB_OP_TYTK, opt, refundOperateRecord);
                                break;
                            case REFUNDED://已退款
                                refundOperateRecord.setOperateDetail(String.format("天马订单状态:[%s],天猫操作[同意退款]", TianmaOrderStatus.valueOf(tianmaOrderStatus)));
                                operate(RefundOperateType.TB_OP_TYTK, opt, refundOperateRecord);
                                break;
                            default:
                                break;
                        }
                    } else {
                        refundOperateRecord.setOperateDetail(String.format("天马中没找到订单信息或者订单数量大于1"));
                    }
                    Logger.info(String.format("退款单号:%d,操作:%s", refundOperateRecord.getRefundId(), refundOperateRecord.getOperateDetail()));
                });


        //代销订单处理流程
        refundOperateRecordList.parallelStream()
                .filter(refundOperateRecord -> refundOperateRecord.getDaixiao() != null)
                .filter(refundOperateRecord -> refundOperateRecord.getDaixiao() == true)
                .forEach((RefundOperateRecord refundOperateRecord) -> {


                    Optional<OcRefundOperateRecord> opt1 = ocRefundOperateRecordManager.stream()
                            .filter(OcRefundOperateRecord.REFUND_ID.equal(Long.valueOf(refundOperateRecord.getRefundId())))
                            .sorted(OcRefundOperateRecord.ID.comparator().reversed())
                            .findFirst();

                    Optional<PurchaseOrder> opt = taoBaoHttp.getPurchaseOrderByTcOrderId(refundOperateRecord.getTid());
                    if (opt.isPresent()) {
                        PurchaseOrder purchaseOrder = opt.get();

                        refundOperateRecord.setPurchaseBuyerPayment(purchaseOrder.getBuyerPayment());
                        refundOperateRecord.setPurchaseOrderId(purchaseOrder.getId());
                        refundOperateRecord.setPurchaseOrderStatus(purchaseOrder.getStatus());

                        List<SubPurchaseOrder> subPurchaseOrders = purchaseOrder.getSubPurchaseOrders();
                        if (subPurchaseOrders != null && subPurchaseOrders.size() == 1) {
                            //SubPurchaseOrder subPurchaseOrder = subPurchaseOrders.get(0);
                            if ("WAIT_BUYER_CONFIRM_GOODS".equals(purchaseOrder.getStatus())) {
                                refundOperateRecord.setOperateDetail(String.format("采购单子单状态:[%s],供销操作[申请退款]", purchaseOrder.getStatus()));
                                operate(RefundOperateType.GX_OP_SQTK, opt1, refundOperateRecord);
                            }
                            if ("WAIT_BUYER_CONFIRM_GOODS_ACOUNTED".equals(purchaseOrder.getStatus())) {
                                refundOperateRecord.setOperateDetail(String.format("采购单子单状态:[%s],供销操作[申请退款]", purchaseOrder.getStatus()));
                                operate(RefundOperateType.GX_OP_SQTK, opt1, refundOperateRecord);
                            }
                            if ("TRADE_FINISHED".equals(purchaseOrder.getStatus())) {
                                refundOperateRecord.setOperateDetail(String.format("采购单子单状态:[%s],供销操作[申请退款]", purchaseOrder.getStatus()));
                                operate(RefundOperateType.GX_OP_SQTK, opt1, refundOperateRecord);
                            }
                            if ("TRADE_CLOSED".equals(purchaseOrder.getStatus())) {
                                refundOperateRecord.setOperateDetail(String.format("采购单子单状态:[%s],淘宝操作[同意退款]", purchaseOrder.getStatus()));
                                operate(RefundOperateType.TB_OP_TYTK, opt1, refundOperateRecord);
                            }
                            if (refundOperateRecord.getOperateDetail() == null) {
                                refundOperateRecord.setOperateDetail(purchaseOrder.getStatus());
                            }


                        } else {
                            refundOperateRecord.setOperateDetail(String.format("没找到子采购单信息或者子采购单数量大于1"));
                        }
                    } else {
                        refundOperateRecord.setOperateDetail(String.format("没找到采购单信息或者采购单数量大于1"));
                    }
                    Logger.info(String.format("退款单号:%d,操作:%s", refundOperateRecord.getRefundId(), refundOperateRecord.getOperateDetail()));
                });

        //持久化数据
        refundOperateRecordList.parallelStream()
                .filter(refundOperateRecord -> refundOperateRecord.getPersist() != null)
                .filter(refundOperateRecord -> refundOperateRecord.getPersist().booleanValue() == true)
                .forEach(refundOperateRecord -> {
                    OcRefundOperateRecordImpl refundOperateRecord1 = new OcRefundOperateRecordImpl();
                    refundOperateRecord1.setTid(refundOperateRecord.getTid());
                    refundOperateRecord1.setRefundId(refundOperateRecord.getRefundId());
                    refundOperateRecord1.setSid(refundOperateRecord.getSid());
                    refundOperateRecord1.setStatus(refundOperateRecord.getStatus());
                    refundOperateRecord1.setReason(refundOperateRecord.getReason());
                    refundOperateRecord1.setOperateDetail(refundOperateRecord.getOperateDetail());
                    refundOperateRecord1.setIsDaixiao((short) (refundOperateRecord.getDaixiao().booleanValue() ? 1 : 0));
                    refundOperateRecord1.setCompanyName(refundOperateRecord.getCompanyName());
                    refundOperateRecord1.setOperateType(refundOperateRecord.getOperateType().getValue());
                    refundOperateRecord1.setOperateResult(refundOperateRecord.getOperateResult());
                    refundOperateRecord1.setRefundAmount(refundOperateRecord.getRefundAmount());
                    refundOperateRecord1.setRefundVersion(refundOperateRecord.getRefundVersion());
                    refundOperateRecord1.setRefundPhase(refundOperateRecord.getRefundPhase());
                    refundOperateRecord1.setIsLatest((short) 1);
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
