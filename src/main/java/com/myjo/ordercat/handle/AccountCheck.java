package com.myjo.ordercat.handle;

import com.myjo.ordercat.config.OrderCatConfig;
import com.myjo.ordercat.domain.FenxiaoCheckStatus;
import com.myjo.ordercat.domain.TianmaCheckStatus;
import com.myjo.ordercat.domain.TianmaOrder;
import com.myjo.ordercat.domain.TianmaOrderStatus;
import com.myjo.ordercat.http.TaoBaoHttp;
import com.myjo.ordercat.http.TianmaSportHttp;
import com.myjo.ordercat.spm.ordercat.ordercat.oc_fenxiao_check_result.OcFenxiaoCheckResult;
import com.myjo.ordercat.spm.ordercat.ordercat.oc_fenxiao_check_result.OcFenxiaoCheckResultImpl;
import com.myjo.ordercat.spm.ordercat.ordercat.oc_fenxiao_check_result.OcFenxiaoCheckResultManager;
import com.myjo.ordercat.spm.ordercat.ordercat.oc_sync_inventory_item_info.OcSyncInventoryItemInfo;
import com.myjo.ordercat.spm.ordercat.ordercat.oc_sync_inventory_item_info.OcSyncInventoryItemInfoManager;
import com.myjo.ordercat.spm.ordercat.ordercat.oc_tianma_check_result.OcTianmaCheckResult;
import com.myjo.ordercat.spm.ordercat.ordercat.oc_tianma_check_result.OcTianmaCheckResultImpl;
import com.myjo.ordercat.spm.ordercat.ordercat.oc_tianma_check_result.OcTianmaCheckResultManager;
import com.myjo.ordercat.utils.OcCsvUtils;
import com.myjo.ordercat.utils.OcDateTimeUtils;
import com.myjo.ordercat.utils.OcStringUtils;
import com.taobao.api.domain.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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

    private OcTianmaCheckResultManager ocTianmaCheckResultManager;


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
        LocalDateTime lbegin = lend.minusDays(OrderCatConfig.getFenxiaoOrderDateIntervalDay());
        Date begin = OcDateTimeUtils.localDateTime2Date(lbegin);
        Date end = OcDateTimeUtils.localDateTime2Date(lend);

        //查询退款成功的订单
        List<Refund> refundlist = taoBaoHttp.getReceiveRefunds(begin, end);
        Logger.info(String.format("查询淘宝退款成功的订单 [%s]-[%s] --- size:[%d]",
                OcDateTimeUtils.localDateTime2String(lbegin),
                OcDateTimeUtils.localDateTime2String(lend),
                refundlist.size()));

        //清空
        //ocFenxiaoCheckResultManager.stream().forEach(ocFenxiaoCheckResultManager.remover());

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
        //RefundDetail refundDetail;
        for (Refund r : refundlist) {
            ofcr = fenxiaoMap.get(r.getRefundId());
            if (ofcr == null) { //如果该退款是新的
                ofcr = new OcFenxiaoCheckResultImpl();
                ofcr.setTid(r.getTid());
                ofcr.setNumIid(r.getNumIid());
                ofcr.setRefundId(r.getRefundId());
                ofcr.setTitle(r.getTitle());
                ofcr.setOrderStatus(r.getOrderStatus());
                purchaseOrderlist = taoBaoHttp.getFenxiaoOrdersByTcOrderId(r.getTid());
                if (purchaseOrderlist != null && purchaseOrderlist.size() > 0) {
                    ofcr.setFenxiaoId(purchaseOrderlist.get(0).getFenxiaoId());
                    fxCheckAssignment(ofcr);
                } else {
                    ofcr.setStatus(FenxiaoCheckStatus.NOT_FENXIAO.getValue());
                }
                ofcr.setAddTime(LocalDateTime.now());
                ocFenxiaoCheckResultManager.persist(ofcr);//插入
            } else { //该退款曾经对过账对过账
                //对账状态异常或者，没有分销退款需要再次对账
                if (FenxiaoCheckStatus.STATUS_ERROR_FENXIAO_REFUND.getValue().equals(ofcr.getStatus().get()) ||
                        FenxiaoCheckStatus.NOT_FENXIAO_REFUND.getValue().equals(ofcr.getStatus().get())) {
                    fxCheckAssignment(ofcr);
                    ofcr.setAddTime(LocalDateTime.now());
                    ocFenxiaoCheckResultManager.update(ofcr);//更新
                }
            }
        }

        List<OcFenxiaoCheckResult> olist = ocFenxiaoCheckResultManager.stream().collect(Collectors.toList());

        //输出结果CSV
        OcCsvUtils.writeWithCsvOcFenxiaoCheckResultWriter(olist, execJobId);

        Logger.info(String.format("分销对账-运行结束"));
    }


    public void tianmaCheck(Long execJobId) throws Exception {
        Logger.info(String.format("开始进行天马对账,execJobId:[%d]", execJobId.longValue()));
        LocalDateTime lend = LocalDateTime.now();
        LocalDateTime lbegin = lend.minusDays(OrderCatConfig.getTianmaOrderDateIntervalDay());

        String startTime = OcDateTimeUtils.localDateTime2String(lbegin, OcDateTimeUtils.OC_DATE);
        String endTime = OcDateTimeUtils.localDateTime2String(lend, OcDateTimeUtils.OC_DATE);
        //查询所有订单
        List<TianmaOrder> tianmaOrders = tianmaSportHttp.tradeOrderDataList(startTime, null, null, "created");
        Logger.info(String.format("查询[%s]-[%s]区间,的订单.size:[%d]",
                startTime,
                endTime,
                tianmaOrders.size()
        ));

        //ConcurrentHashMap map = new ConcurrentHashMap();


        tianmaOrders.parallelStream()
                //.filter(tianmaOrder -> tianmaOrder.getOuterOrderId().indexOf("麦巨") == -1)
                //.filter(tianmaOrder -> tianmaOrder.getOuterOrderId().equals("22327221767177426"))
                .forEach(tianmaOrder -> {
                    OcTianmaCheckResult ocTianmaCheckResult = new OcTianmaCheckResultImpl();
                    ocTianmaCheckResult.setTmBuyerName(tianmaOrder.getName());
                    ocTianmaCheckResult.setTmDeliveryName(tianmaOrder.getDeliveryName());
                    ocTianmaCheckResult.setTmDeliveryNo(tianmaOrder.getDeliveryNo());
                    ocTianmaCheckResult.setTmGoodsNo(tianmaOrder.getGoodsNo());
                    ocTianmaCheckResult.setTmNoshipmentRemark(tianmaOrder.getNoShipmentRemark());
                    ocTianmaCheckResult.setTmOrderId(Long.valueOf(tianmaOrder.getOrderId()));
                    ocTianmaCheckResult.setTmOrderStatus(tianmaOrder.getStatus());
                    ocTianmaCheckResult.setTmOuterOrderId(tianmaOrder.getOuterOrderId());
                    ocTianmaCheckResult.setTmPayPrice(tianmaOrder.getPayPrice());
                    ocTianmaCheckResult.setTmPostFee(tianmaOrder.getPostFee());
                    ocTianmaCheckResult.setTmWarehouseId(tianmaOrder.getWarehouseId());
                    ocTianmaCheckResult.setTmWarehouseName(tianmaOrder.getWarehouseName());
                    ocTianmaCheckResult.setSize1(tianmaOrder.getSize1());
                    ocTianmaCheckResult.setSize2(tianmaOrder.getSize2());

                    //外部订单编码必须为数字
                    if (OcStringUtils.isNumeric(tianmaOrder.getOuterOrderId())) {

                        if(!TianmaOrderStatus.REFUNDED.getVal().equals(tianmaOrder.getStatus())){
                            //查询淘宝订单
                            Optional<Trade> trade = taoBaoHttp.getTaobaoTrade(Long.valueOf(tianmaOrder.getOuterOrderId()));
                            if (trade.isPresent()) {
                                List<Order> orders = trade.get().getOrders();
                                if (orders != null && orders.size() > 0) {
                                    //orders.get(0).get
                                    Map<String, Order> orderMap = orders.parallelStream()
                                            .collect(Collectors.toMap(o ->
                                                    o.getOuterSkuId(), Function.identity()));
                                    Order order = orderMap.get(String.format("%s-%s", tianmaOrder.getGoodsNo(), tianmaOrder.getSize1()));
                                    if (order != null) {
                                        ocTianmaCheckResult.setTbNumIid(order.getNumIid());
                                        ocTianmaCheckResult.setTbOrderId(order.getOid());
                                        ocTianmaCheckResult.setTbOrderStatus(order.getStatus());
                                        ocTianmaCheckResult.setTbNum(order.getNum());
                                        ocTianmaCheckResult.setTbPayment(new BigDecimal(order.getPayment()));
                                        ocTianmaCheckResult.setTbRefundId(order.getRefundId());
                                        ocTianmaCheckResult.setTbRefundStatus(order.getRefundStatus());
                                        ocTianmaCheckResult.setTbTitle(order.getTitle());


                                        //如果天猫是已退款，但是天马不是退款
                                        if("SUCCESS".equals(order.getRefundStatus()) && !TianmaOrderStatus.REFUNDED.getVal().equals(tianmaOrder.getStatus())){
                                            String dzDetailsMessage = String.format("TB订单已经退款RefundStatus[%s]，但是天马订单不是退款状态[%s].",
                                                    "SUCCESS",
                                                    tianmaOrder.getStatus());
                                            ocTianmaCheckResult.setDzDetailsMessage(dzDetailsMessage);
                                            ocTianmaCheckResult.setDzStatus(TianmaCheckStatus.REFUND_DOES_NOT_MATCH.getValue());
                                        }

                                    } else {//没有找到淘宝对应订单
                                        StringBuilder sb = new StringBuilder();
                                        for (Order order1 : orders) {
                                            sb.append(order1.getOuterSkuId());
                                            sb.append(",");
                                        }
                                        String sbtb = sb.toString();
                                        if (sbtb.indexOf("麦巨") > -1) {
                                            String dzDetailsMessage = String.format("TB有订单信息-但是TB尺码为[%s],是麦巨囤货.",
                                                    sbtb);
                                            ocTianmaCheckResult.setDzDetailsMessage(dzDetailsMessage);
                                            ocTianmaCheckResult.setDzStatus(TianmaCheckStatus.OUTER_SKU_MAIJU_ERROR.getValue());

                                        } else {
                                            String dzDetailsMessage = String.format("TB有订单信息-但是SKU匹配失败,天马SKU为:[%s],但是TB的SKU为[%s]",
                                                    tianmaOrder.getGoodsNo() + "-" + tianmaOrder.getSize1(),
                                                    sbtb);
                                            ocTianmaCheckResult.setDzDetailsMessage(dzDetailsMessage);
                                            ocTianmaCheckResult.setDzStatus(TianmaCheckStatus.OUTER_SKU_MATCHING_ERROR.getValue());
                                        }
                                    }
                                }
                            } else {
                                ocTianmaCheckResult.setDzDetailsMessage("TM有订单,但是没有找到淘宝订单.");
                                ocTianmaCheckResult.setDzStatus(TianmaCheckStatus.NOT_FOUND_TAOBAO_ORDER.getValue());
                            }
                        }else {
                            String dzDetailsMessage = String.format("天马订单状态为:[%s]已退款。",
                                    tianmaOrder.getStatus());
                            ocTianmaCheckResult.setDzDetailsMessage(dzDetailsMessage);
                            ocTianmaCheckResult.setDzStatus(TianmaCheckStatus.OTHER_ERROR.getValue());
                        }


                    }else {
                        String dzDetailsMessage = String.format("外部供应商编码[%s]不是数字。",
                                tianmaOrder.getOuterOrderId());
                        ocTianmaCheckResult.setDzDetailsMessage(dzDetailsMessage);
                        ocTianmaCheckResult.setDzStatus(TianmaCheckStatus.OTHER_ERROR.getValue());
                    }

                    ocTianmaCheckResult.setAddTime(LocalDateTime.now());
                    ocTianmaCheckResultManager.persist(ocTianmaCheckResult);
                });

        Logger.info(String.format("天马对账-运行结束"));
    }


    private void fxCheckAssignment(OcFenxiaoCheckResult ofcr) throws Exception {
        RefundDetail refundDetail = taoBaoHttp.getFenxiaoRefundBySubOrderId(ofcr.getFenxiaoId().getAsLong());
        if (refundDetail != null) {
            ofcr.setDistributorNick(refundDetail.getDistributorNick());
            ofcr.setFenxiaoPaySupFee(new BigDecimal(refundDetail.getPaySupFee()));
            ofcr.setFenxiaoRefundDesc(refundDetail.getRefundDesc());
            ofcr.setFenxiaoRefundReason(refundDetail.getRefundReason());
            ofcr.setFenxiaoRefundStatus(refundDetail.getRefundStatus().toString());
            ofcr.setFenxiaoRefundFee(new BigDecimal(refundDetail.getRefundFee()));
            ofcr.setSupplierNick(refundDetail.getSupplierNick());

            if (refundDetail.getRefundStatus() == 5l) {
                ofcr.setStatus(FenxiaoCheckStatus.SUCCESS_REFUND.getValue());
            } else {
                ofcr.setStatus(FenxiaoCheckStatus.STATUS_ERROR_FENXIAO_REFUND.getValue());
            }
        } else {
            ofcr.setStatus(FenxiaoCheckStatus.NOT_FENXIAO_REFUND.getValue());
            if (ofcr.getOrderStatus().equals("TRADE_CLOSED")) {//没有退款信息，但是交易关闭。就是退款成功
                ofcr.setStatus(FenxiaoCheckStatus.SUCCESS_REFUND.getValue());
            }
        }
    }


    public void setOcSyncInventoryItemInfoManager(OcSyncInventoryItemInfoManager ocSyncInventoryItemInfoManager) {
        this.ocSyncInventoryItemInfoManager = ocSyncInventoryItemInfoManager;
    }

    public void setOcFenxiaoCheckResultManager(OcFenxiaoCheckResultManager ocFenxiaoCheckResultManager) {
        this.ocFenxiaoCheckResultManager = ocFenxiaoCheckResultManager;
    }

    public void setOcTianmaCheckResultManager(OcTianmaCheckResultManager ocTianmaCheckResultManager) {
        this.ocTianmaCheckResultManager = ocTianmaCheckResultManager;
    }
}
