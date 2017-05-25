package com.myjo.ordercat.handle;

import com.myjo.ordercat.config.OrderCatConfig;
import com.myjo.ordercat.domain.FenxiaoCheckStatus;
import com.myjo.ordercat.http.TaoBaoHttp;
import com.myjo.ordercat.http.TianmaSportHttp;
import com.myjo.ordercat.spm.ordercat.ordercat.oc_fenxiao_check_result.OcFenxiaoCheckResult;
import com.myjo.ordercat.spm.ordercat.ordercat.oc_fenxiao_check_result.OcFenxiaoCheckResultImpl;
import com.myjo.ordercat.spm.ordercat.ordercat.oc_fenxiao_check_result.OcFenxiaoCheckResultManager;
import com.myjo.ordercat.spm.ordercat.ordercat.oc_sync_inventory_item_info.OcSyncInventoryItemInfo;
import com.myjo.ordercat.spm.ordercat.ordercat.oc_sync_inventory_item_info.OcSyncInventoryItemInfoManager;
import com.myjo.ordercat.utils.OcCsvUtils;
import com.myjo.ordercat.utils.OcDateTimeUtils;
import com.taobao.api.domain.PurchaseOrder;
import com.taobao.api.domain.Refund;
import com.taobao.api.domain.RefundDetail;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * 对账相关
 * Created by lee5hx on 17/5/8.
 */
public class AccountCheck {

    private static final Logger Logger = LogManager.getLogger(AccountCheck.class);

    private TianmaSportHttp tianmaSportHttp;

    private TaoBaoHttp taoBaoHttp;

    private OcSyncInventoryItemInfoManager ocSyncInventoryItemInfoManager;

    private OcFenxiaoCheckResultManager ocFenxiaoCheckResultManager;


    public AccountCheck(TianmaSportHttp tianmaSportHttp, TaoBaoHttp taoBaoHttp) {
        this.tianmaSportHttp = tianmaSportHttp;
        this.taoBaoHttp = taoBaoHttp;
    }

    private static <T> Predicate<T> distinctByMap(Function<? super T, String> keyExtractor, Map<?, ?> map, boolean rt) {
        //return t -> map.putIfAbsent(keyExtractor.apply(t), null) == null;
        return t -> map.containsKey(keyExtractor.apply(t)) == rt;
    }

    public void fenxiaoCheck(Long execJobId) throws Exception {
        Logger.info(String.format("开始进行分销对账,execJobId:[%d]", execJobId.longValue()));
        LocalDateTime lend = LocalDateTime.now();
        LocalDateTime lbegin = lend.minusDays(10);
        Date begin = OcDateTimeUtils.localDateTime2Date(lbegin);
        Date end = OcDateTimeUtils.localDateTime2Date(lend);

        //查询退款成功的订单
        List<Refund> refundlist = taoBaoHttp.getReceiveRefunds(begin, end);
        Logger.info(String.format("查询淘宝退款成功的订单 [%s]-[%s] --- size:[%d]",
                OcDateTimeUtils.localDateTime2String(lbegin),
                OcDateTimeUtils.localDateTime2String(lend),
                refundlist.size()));

        //清空
        ocFenxiaoCheckResultManager.stream().forEach(ocFenxiaoCheckResultManager.remover());

        //查询已经同步库存过的宝贝(为了过滤出分销退款订单)
        Map<String, OcSyncInventoryItemInfo> osiiMap = ocSyncInventoryItemInfoManager
                .stream()
                .collect(Collectors.toMap(o -> o.getNumIid().get(), Function.identity()));
        Logger.info(String.format("查询已经同步库存过的宝贝-size:[%d]", osiiMap.size()));


        refundlist = refundlist.parallelStream()
                //.filter(inventoryInfo -> inventoryInfo.getSize1().indexOf("Y")<0)
                .filter(distinctByMap(o -> String.valueOf(o.getNumIid().longValue()), osiiMap, false))
                .collect(Collectors.toList());
        Logger.info(String.format("根据已经同步库存后的宝贝ID,过滤出分销退款列表-size:[%d]", refundlist.size()));


        //过滤不进行对账的宝贝ID
        List<String> noCheckList = OrderCatConfig.getFeixiaoNoCheckNumIidList();
        noCheckList.parallelStream().forEach(s -> {
            Logger.info(String.format("不进行检查的宝贝ID:[%s]", s));
        });
        Map<String, String> noCheckMap = noCheckList.parallelStream().collect(Collectors.toMap(o -> o, Function.identity()));
        refundlist = refundlist
                .stream()
                .filter(distinctByMap(o -> String.valueOf(o.getNumIid().longValue()), noCheckMap, false))
                .collect(Collectors.toList());

        Logger.info(String.format("过滤不进行检查宝贝ID后-size:[%d]", refundlist.size()));


        Map<Long, OcFenxiaoCheckResult> fenxiaoMap = ocFenxiaoCheckResultManager
                .stream()
                .collect(Collectors.toMap(o -> o.getRefundId().getAsLong(), Function.identity()));
        //查找分销ID并赋值

        OcFenxiaoCheckResult ofcr;
        List<PurchaseOrder> purchaseOrderlist;
        RefundDetail refundDetail;
        for (Refund r : refundlist) {
            ofcr = fenxiaoMap.get(r.getRefundId());
            if (ofcr == null) {
                ofcr = new OcFenxiaoCheckResultImpl();
                ofcr.setTid(r.getTid());
                ofcr.setNumIid(r.getNumIid());
                ofcr.setRefundId(r.getRefundId());
                ofcr.setTitle(r.getTitle());
                ofcr.setOrderStatus(r.getOrderStatus());
                purchaseOrderlist = taoBaoHttp.getFenxiaoOrdersByTcOrderId(r.getTid());

                if (purchaseOrderlist != null && purchaseOrderlist.size() > 0) {
                    ofcr.setFenxiaoId(purchaseOrderlist.get(0).getFenxiaoId());
                    refundDetail = taoBaoHttp.getFenxiaoRefundBySubOrderId(ofcr.getFenxiaoId().getAsLong());
                    if (refundDetail != null) {
                        ofcr.setDistributorNick(refundDetail.getDistributorNick());
                        ofcr.setFenxiaoPaySupFee(new BigDecimal(refundDetail.getPaySupFee()));
                        ofcr.setFenxiaoRefundDesc(refundDetail.getRefundDesc());
                        ofcr.setFenxiaoRefundReason(refundDetail.getRefundReason());
                        ofcr.setFenxiaoRefundStatus(refundDetail.getRefundStatus().toString());
                        ofcr.setFenxiaoRefundFee(new BigDecimal(refundDetail.getRefundFee()));
                        ofcr.setSupplierNick(refundDetail.getSupplierNick());

                        if(refundDetail.getRefundStatus() == 5l){
                            ofcr.setStatus(FenxiaoCheckStatus.SUCCESS_REFUND.getValue());
                        }else {
                            ofcr.setStatus(FenxiaoCheckStatus.STATUS_ERROR_FENXIAO_REFUND.getValue());
                        }
                    } else {
                        ofcr.setStatus(FenxiaoCheckStatus.NOT_FENXIAO_REFUND.getValue());
                        if(ofcr.getOrderStatus().equals("TRADE_CLOSED")){//没有退款信息，但是交易关闭。就是退款成功
                            ofcr.setStatus(FenxiaoCheckStatus.SUCCESS_REFUND.getValue());
                        }
                    }
                } else {
                    ofcr.setStatus(FenxiaoCheckStatus.NOT_FENXIAO.getValue());
                }
                ofcr.setAddTime(LocalDateTime.now());
                ocFenxiaoCheckResultManager.persist(ofcr);
            }
        }


        List<OcFenxiaoCheckResult> olist = ocFenxiaoCheckResultManager.stream().collect(Collectors.toList());


        OcCsvUtils.writeWithCsvOcFenxiaoCheckResultWriter(olist,execJobId);
//        refundlist.stream().forEach(o -> {
//            System.out.println(o.getTid() + "," + o.getRefundId() + "," + o.getNumIid() + "," + o.getTitle() + "," + o.getOuterId());
//        });

        Logger.info(String.format("分销对账-运行结束"));
    }

    public void setOcSyncInventoryItemInfoManager(OcSyncInventoryItemInfoManager ocSyncInventoryItemInfoManager) {
        this.ocSyncInventoryItemInfoManager = ocSyncInventoryItemInfoManager;
    }

    public void setOcFenxiaoCheckResultManager(OcFenxiaoCheckResultManager ocFenxiaoCheckResultManager) {
        this.ocFenxiaoCheckResultManager = ocFenxiaoCheckResultManager;
    }
}
