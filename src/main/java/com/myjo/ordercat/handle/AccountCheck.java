package com.myjo.ordercat.handle;

import com.myjo.ordercat.config.OrderCatConfig;
import com.myjo.ordercat.domain.*;
import com.myjo.ordercat.http.TaoBaoHttp;
import com.myjo.ordercat.http.TianmaSportHttp;
import com.myjo.ordercat.spm.ordercat.ordercat.oc_fenxiao_check_result.OcFenxiaoCheckResult;
import com.myjo.ordercat.spm.ordercat.ordercat.oc_fenxiao_check_result.OcFenxiaoCheckResultImpl;
import com.myjo.ordercat.spm.ordercat.ordercat.oc_fenxiao_check_result.OcFenxiaoCheckResultManager;
import com.myjo.ordercat.spm.ordercat.ordercat.oc_sync_inventory_item_info.OcSyncInventoryItemInfo;
import com.myjo.ordercat.spm.ordercat.ordercat.oc_sync_inventory_item_info.OcSyncInventoryItemInfoManager;
import com.myjo.ordercat.spm.ordercat.ordercat.oc_tmsport_check_result.OcTmsportCheckResult;
import com.myjo.ordercat.spm.ordercat.ordercat.oc_tmsport_check_result.OcTmsportCheckResultImpl;
import com.myjo.ordercat.spm.ordercat.ordercat.oc_tmsport_check_result.OcTmsportCheckResultManager;
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

    private OcTmsportCheckResultManager ocTmsportCheckResultManager;


    public AccountCheck(TianmaSportHttp tianmaSportHttp, TaoBaoHttp taoBaoHttp) {
        this.tianmaSportHttp = tianmaSportHttp;
        this.taoBaoHttp = taoBaoHttp;
    }

//    private static <T> Predicate<T> distinctByMap(Function<? super T, String> keyExtractor, Map<?, ?> map, boolean rt) {
//        //return t -> map.putIfAbsent(keyExtractor.apply(t), null) == null;
//        return t -> map.containsKey(keyExtractor.apply(t)) == rt;
//    }

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
                .filter(InventoryDataOperate.distinctByMap(o -> String.valueOf(o.getNumIid().longValue()), osiiMap, false))
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
                .filter(InventoryDataOperate.distinctByMap(o -> String.valueOf(o.getNumIid().longValue()), noCheckMap, false))
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


    private boolean reserveCheckTmOrderStatus(TianmaOrderStatus status) {
        boolean rt = false;
        for (TianmaOrderStatus s : TianmaOrderStatus.CHECK_TM_ORDER_STATUS) {
            if (s == status) {
                rt = true;
                break;
            }
        }
        return rt;
    }

    private long getTaoBaoSubOrderNum(List<Order> orders) {
        long sum = 0;
        for (Order o : orders) {
            sum = sum + o.getNum();
        }
        return sum;
    }

    /**
     * 一、天马数据
     * <p>
     * 1、下载一个时间周期内所有订单
     * <p>
     * 2、剔除待付款 待退款 已退款 已退货 反馈失败，只保留 已付款 配货中 反馈成功这三种订单状态。
     * <p>
     * 3、对剩下数据，判断订单编号是否为唯一，是唯一备注为1，非1则备注正确数量。这样我们就有了订单的两种状态，1和非1
     * <p>
     * 4、状态1的就很简单了，直接去天猫看昨天说的时间、货号和订单状态，只要是交易成功 卖家已发货 买家已付款 退款中。这都算对，而如果是待付款，已退款（含售后）交易关闭 ，则报错，而如果没订单肯定更是报错了。
     * <p>
     * 5、再来说非1状态，比如是数量5，则去天猫对 这个订单号下有几笔订单，找到后 删除这几笔订单中的已退款 待付款 交易关闭 已退款（含售后） 剩下订单数量是否为5 是5则是正常 非5则报错 其中对数量，一个子单数量1为1，一个子单数量为4，计算4 累计算5就行。
     * <p>
     * 多笔订单对货号那就是搜索匹配 能否搜索到
     *
     * @param execJobId
     * @throws Exception
     */
    public void tianmaCheck(Long execJobId) throws Exception {
        Logger.info(String.format("开始进行天马对账,execJobId:[%d]", execJobId.longValue()));

        //1、下载一个时间周期内所有订单
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
        //2、剔除待付款 待退款 已退款 已退货 反馈失败，只保留 已付款 配货中 反馈成功这三种订单状态。
        tianmaOrders = tianmaOrders.parallelStream()
                .filter(tianmaOrder -> reserveCheckTmOrderStatus(tianmaOrder.getStatus()))
                .collect(Collectors.toList());
        Logger.info(String.format("天马订单保留[已付款 配货中 反馈成功]这三种订单状态的订单.size:[%d]",
                tianmaOrders.size()
        ));
        //3、对剩下数据，判断订单编号是否为唯一，是唯一备注为1，非1则备注正确数量。这样我们就有了订单的两种状态，1和非1
        //3-1 按照外部供应商订单编码(即:淘宝订单主单号)
        Map<String, List<TianmaOrder>> tianmaOrdersMap =
                tianmaOrders.parallelStream()
                        .collect(Collectors.groupingBy(tianmaOrder -> tianmaOrder.getOuterOrderId()));
        Logger.info(String.format("按照OuterOrderId(淘宝订单号)分组后.size[%d]",
                tianmaOrdersMap.size()
        ));

        //赋值TM订单
        List<TianmaCheckResult> tianmaCheckResultList = tianmaOrdersMap.entrySet().parallelStream()
                .map(o -> {
                    TianmaCheckResult tianmaCheckResult = new TianmaCheckResult();
                    tianmaCheckResult.setTmOuterOrderId(o.getKey());
                    tianmaCheckResult.setTmOrders(o.getValue());
                    tianmaCheckResult.setTmNum(Long.valueOf(o.getValue().size()));
                    tianmaCheckResult.setTmOrderNum(Long.valueOf(o.getValue().size()));
                    return tianmaCheckResult;
                })
                .collect(Collectors.toList());


        Logger.info(String.format("赋值TM订单-tianmaCheckResultList.size[%d]",
                tianmaCheckResultList.size()
        ));


        //赋值TB订单
        tianmaCheckResultList.parallelStream().forEach(tianmaCheckResult -> {
            String outerOrderId = tianmaCheckResult.getTmOuterOrderId();
            if (OcStringUtils.isNumeric(outerOrderId)) { //外部订单编码(淘宝ID)必须为数字
                Optional<Trade> trade = taoBaoHttp.getTaobaoTrade(Long.valueOf(outerOrderId));
                if (trade.isPresent()) {
                    tianmaCheckResult.setTrade(trade.get());
                    tianmaCheckResult.setTbOrders(trade.get().getOrders());
                    tianmaCheckResult.setTbNum(getTaoBaoSubOrderNum(trade.get().getOrders()));
                    tianmaCheckResult.setTbOrderNum(Long.valueOf(trade.get().getOrders().size()));
                    tianmaCheckResult.setTbCreated(OcDateTimeUtils.date2LocalTime(trade.get().getCreated()));
                    tianmaCheckResult.setTbPayTime(OcDateTimeUtils.date2LocalTime(trade.get().getPayTime()));
                    tianmaCheckResult.setTbPayment(new BigDecimal(trade.get().getPayment() == null ? "0" : trade.get().getPayment()));
                    tianmaCheckResult.setTbDiscountFee(new BigDecimal(trade.get().getDiscountFee() == null ? "0" : trade.get().getDiscountFee()));
                    tianmaCheckResult.setTbPrice(new BigDecimal(trade.get().getPrice() == null ? "0" : trade.get().getPrice()));
                    tianmaCheckResult.setTbTotalFee(new BigDecimal(trade.get().getTotalFee() == null ? "0" : trade.get().getTotalFee()));
                } else {
                    String dzDetailsMessage = String.format("外部订单编码[%s],在淘宝没有找到对应的订单信息.",
                            outerOrderId);
                    tianmaCheckResult.setDzDetailsMessage(dzDetailsMessage);
                    tianmaCheckResult.setDzStatus(TianmaCheckStatus.NOT_FOUND_TAOBAO_ORDER);
                }

            } else {
                String dzDetailsMessage = String.format("外部订单编码[%s]不是数字.",
                        outerOrderId);
                tianmaCheckResult.setDzDetailsMessage(dzDetailsMessage);
                tianmaCheckResult.setDzStatus(TianmaCheckStatus.ILLEGAL_OUTER_ORDER_ID);
            }
        });

        Logger.info(String.format("赋值TB订单-tianmaCheckResultList.size[%d]", tianmaCheckResultList.size()));

        //4、状态1的就很简单了，直接去天猫看昨天说的时间、货号和订单状态，
        // 只要是交易成功 卖家已发货 买家已付款 退款中。这都算对，而如果是待付款，已退款（含售后）交易关闭 ，则报错，而如果没订单肯定更是报错了。

        tianmaCheckResultList.parallelStream()
                .filter(tianmaCheckResult -> tianmaCheckResult.getDzStatus() == null)//没有对账错误
                .filter(tianmaCheckResult -> tianmaCheckResult.getTmOrders().size() == 1) //天马订单数量为1
                .forEach(tianmaCheckResult -> {
                    List<TianmaOrder> tmOrders = tianmaCheckResult.getTmOrders();
                    List<Order> tbOrders = tianmaCheckResult.getTbOrders();
                    //校验订单笔数
                    if (tmOrders.size() != tbOrders.size()) {
                        String dzDetailsMessage = String.format("订单数量不一致,TB订单数量为:[%d],TM订单数量为:[%d]",
                                tbOrders.size(),
                                tmOrders.size()
                        );
                        tianmaCheckResult.setDzDetailsMessage(dzDetailsMessage);
                        tianmaCheckResult.setDzStatus(TianmaCheckStatus.ORDER_QUANTITY_IS_NOT_CONSISTENT);
                    }
                    TianmaOrder tmOrder = tmOrders.get(0);
                    Order tbOrder = tbOrders.get(0);


                    //校验时间
                    if (tianmaCheckResult.getDzStatus() == null) {
                        LocalDateTime payTime = OcDateTimeUtils.date2LocalTime(tianmaCheckResult.getTrade().getPayTime());
                        LocalDateTime created = OcDateTimeUtils.string2LocalDateTime(tmOrder.getCreated());
                        if (payTime.plusDays(OrderCatConfig.getTianmaPaytimeDifferDay()).compareTo(created) == -1) {

                            String dzDetailsMessage = String.format("订单时间不匹配,TB订单与TM订单相差[%d]天,TB订单时间为:[%s],TM订订单时间为:[%s]",
                                    OrderCatConfig.getTianmaPaytimeDifferDay(),
                                    OcDateTimeUtils.localDateTime2String(payTime),
                                    OcDateTimeUtils.localDateTime2String(created)
                            );
                            tianmaCheckResult.setDzDetailsMessage(dzDetailsMessage);
                            tianmaCheckResult.setDzStatus(TianmaCheckStatus.ORDER_TIME_DOES_NOT_MATCH);
                        }

                    }

                    //校验货号
                    if (tianmaCheckResult.getDzStatus() == null) {
                        if (tbOrder.getOuterSkuId().indexOf(tmOrder.getGoodsNo()) == -1) {
                            String dzDetailsMessage = String.format("货号不一致,TB货号为:[%s],TM货号为:[%s]",
                                    tbOrder.getOuterSkuId(),
                                    tmOrder.getGoodsNo()
                            );
                            tianmaCheckResult.setDzDetailsMessage(dzDetailsMessage);
                            tianmaCheckResult.setDzStatus(TianmaCheckStatus.ARTICLE_NUMBER_IS_NOT_CONSISTENT);
                        }
                    }

                    //检验订单状态
                    //TRADE_NO_CREATE_PAY(没有创建支付宝交易)
                    //WAIT_BUYER_PAY(等待买家付款)
                    //WAIT_SELLER_SEND_GOODS(等待卖家发货,即:买家已付款)
                    //WAIT_BUYER_CONFIRM_GOODS(等待买家确认收货,即:卖家已发货)
                    //TRADE_BUYER_SIGNED(买家已签收,货到付款专用)
                    //TRADE_FINISHED(交易成功)
                    //TRADE_CLOSED(付款以后用户退款成功，交易自动关闭)
                    //TRADE_CLOSED_BY_TAOBAO(付款以前，卖家或买家主动关闭交易)
                    //PAY_PENDING(国际信用卡支付付款确认中)
                    if (tianmaCheckResult.getDzStatus() == null) {
                        //(待付款，已退款（含售后）交易关闭 ，则报错)
                        if ("WAIT_BUYER_PAY".equals(tbOrder.getStatus()) ||
                                "TRADE_CLOSED".equals(tbOrder.getStatus()) ||
                                "TRADE_CLOSED_BY_TAOBAO".equals(tbOrder.getStatus())
                                ) {
                            String dzDetailsMessage = String.format("订单状态不匹配,TB订单状态为:[%s],TM订单状态为:[%s],货号为:[%s]",
                                    tbOrder.getStatus(),
                                    tmOrder.getStatus().getVal(),
                                    tbOrder.getOuterSkuId()
                            );
                            tianmaCheckResult.setDzDetailsMessage(dzDetailsMessage);
                            tianmaCheckResult.setDzStatus(TianmaCheckStatus.ORDER_STATUS_DOES_NOT_MATCH);
                        }
                    }

                    if (tianmaCheckResult.getDzStatus() == null) {

                        if (tianmaCheckResult.getTbNum() != tianmaCheckResult.getTmNum()) {
                            String dzDetailsMessage = String.format("订单中商品数量不一致,TB商品数量为:[%d],TM商品数量为:[%d]",
                                    tianmaCheckResult.getTbNum(),
                                    tianmaCheckResult.getTmNum()
                            );
                            tianmaCheckResult.setDzDetailsMessage(dzDetailsMessage);
                            tianmaCheckResult.setDzStatus(TianmaCheckStatus.NUM_QUANTITY_IS_NOT_CONSISTENT);
                        }
                    }

                });

        //5、再来说非1状态，比如是数量5，则去天猫对 这个订单号下有几笔订单，
        // 找到后 删除这几笔订单中的已退款 待付款 交易关闭 已退款（含售后） 剩下订单数量是否为5 是5则是正常
        // 非5则报错 其中对数量，一个子单数量1为1，一个子单数量为4，计算4 累计算5就行。

        tianmaCheckResultList.parallelStream()
                .filter(tianmaCheckResult -> tianmaCheckResult.getDzStatus() == null)//没有对账错误
                .filter(tianmaCheckResult -> tianmaCheckResult.getTmOrders().size() > 1) //天马订单数量大于1
                .forEach(tianmaCheckResult -> {
                    //List<TianmaOrder> tmOrders = tianmaCheckResult.getTmOrders();
                    List<Order> tbOrders = tianmaCheckResult.getTbOrders();
                    //删除这几笔订单中的已退款 待付款 交易关闭 已退款（含售后）
                    long tbNum = 0;
                    for (Order o : tbOrders) {
                        if (!"TRADE_CLOSED".equals(o.getStatus())
                                && !"WAIT_BUYER_PAY".equals(o.getStatus())
                                && !"TRADE_CLOSED_BY_TAOBAO".equals(o.getStatus())
                                && !"TRADE_NO_CREATE_PAY".equals(o.getStatus())
                                ) {
                            tbNum = tbNum + 1;

                        }
                    }

                    if (tianmaCheckResult.getTmNum() != tbNum) {
                        String dzDetailsMessage = String.format("订单中商品数量不一致,TB商品数量为:[%d],TM商品数量为:[%d]",
                                tbNum,
                                tianmaCheckResult.getTmNum()
                        );
                        tianmaCheckResult.setDzDetailsMessage(dzDetailsMessage);
                        tianmaCheckResult.setDzStatus(TianmaCheckStatus.NUM_QUANTITY_IS_NOT_CONSISTENT);
                    }
                });


        //6、没有对账报错，就是对账成功!
        tianmaCheckResultList.parallelStream()
                .filter(tianmaCheckResult -> tianmaCheckResult.getDzStatus() == null)//没有对账错误
                .forEach(tianmaCheckResult -> {
                    tianmaCheckResult.setDzStatus(TianmaCheckStatus.DZ_SUCCESS);
                    tianmaCheckResult.setDzDetailsMessage("对账成功.");
                });


        //持久化数据库

        Logger.info(String.format("正在持久化数据库"));


        //已经对过账的所有记录
        Map<String, OcTmsportCheckResult> yetTmsportCheckResultMap = ocTmsportCheckResultManager
                .stream()
                .collect(Collectors.toConcurrentMap(o -> o.getTmOuterOrderId().get(), Function.identity()));

        Logger.info(String.format("已经对过账的所有记录.size:[%d]", yetTmsportCheckResultMap.size()));


        tianmaCheckResultList.parallelStream()
                .filter(tianmaCheckResult -> tianmaCheckResult.getDzStatus() != null)
                .forEach(tianmaCheckResult -> {

                    OcTmsportCheckResult ocTmsportCheckResult = yetTmsportCheckResultMap.get(tianmaCheckResult.getTmOuterOrderId());
                    if (ocTmsportCheckResult != null) {
                        ocTmsportCheckResult.setAddTime(LocalDateTime.now());


                        String citiesCommaSeparated = tianmaCheckResult.getTmOrders().stream()
                                .map(o-> o.getOrderId()).collect(Collectors.joining(","));

                        ocTmsportCheckResult.setTmOrderIds(citiesCommaSeparated);
                        ocTmsportCheckResult.setTbOrderNum(tianmaCheckResult.getTbOrderNum());
                        ocTmsportCheckResult.setTbNum(tianmaCheckResult.getTbNum());
                        ocTmsportCheckResult.setTbTotalFee(tianmaCheckResult.getTbTotalFee());
                        ocTmsportCheckResult.setTbPayment(tianmaCheckResult.getTbPayment());
                        ocTmsportCheckResult.setTbPaytime(tianmaCheckResult.getTbPayTime());
                        ocTmsportCheckResult.setTbCreated(tianmaCheckResult.getTbCreated());
                        ocTmsportCheckResult.setTbPrice(tianmaCheckResult.getTbPrice());
                        ocTmsportCheckResult.setTbDiscountFee(tianmaCheckResult.getTbDiscountFee());
                        ocTmsportCheckResult.setTmOuterOrderId(tianmaCheckResult.getTmOuterOrderId());
                        ocTmsportCheckResult.setTmOrderNum(tianmaCheckResult.getTmOrderNum());
                        ocTmsportCheckResult.setTmNum(tianmaCheckResult.getTmNum());
                        ocTmsportCheckResult.setDzStatus(tianmaCheckResult.getDzStatus().getValue());
                        ocTmsportCheckResult.setDzDetailsMessage(tianmaCheckResult.getDzDetailsMessage());
                        ocTmsportCheckResultManager.update(ocTmsportCheckResult);
                    } else {
                        ocTmsportCheckResult = new OcTmsportCheckResultImpl();
                        String citiesCommaSeparated = tianmaCheckResult.getTmOrders().stream()
                                .map(o-> o.getOrderId()).collect(Collectors.joining(","));

                        ocTmsportCheckResult.setTmOrderIds(citiesCommaSeparated);
                        ocTmsportCheckResult.setAddTime(LocalDateTime.now());
                        ocTmsportCheckResult.setTbOrderNum(tianmaCheckResult.getTbOrderNum());
                        ocTmsportCheckResult.setTbNum(tianmaCheckResult.getTbNum());
                        ocTmsportCheckResult.setTbTotalFee(tianmaCheckResult.getTbTotalFee());
                        ocTmsportCheckResult.setTbPayment(tianmaCheckResult.getTbPayment());
                        ocTmsportCheckResult.setTbPaytime(tianmaCheckResult.getTbPayTime());
                        ocTmsportCheckResult.setTbCreated(tianmaCheckResult.getTbCreated());
                        ocTmsportCheckResult.setTbPrice(tianmaCheckResult.getTbPrice());
                        ocTmsportCheckResult.setTbDiscountFee(tianmaCheckResult.getTbDiscountFee());
                        ocTmsportCheckResult.setTmOuterOrderId(tianmaCheckResult.getTmOuterOrderId());
                        ocTmsportCheckResult.setTmOrderNum(tianmaCheckResult.getTmOrderNum());
                        ocTmsportCheckResult.setTmNum(tianmaCheckResult.getTmNum());
                        ocTmsportCheckResult.setDzStatus(tianmaCheckResult.getDzStatus().getValue());
                        ocTmsportCheckResult.setDzDetailsMessage(tianmaCheckResult.getDzDetailsMessage());
                        ocTmsportCheckResultManager.persist(ocTmsportCheckResult);
                    }
                    //ocTmsportCheckResult.setRemarks(tianmaCheckResult.getRemarks());


                });

        Logger.info(String.format("持久化数据库-完成"));


        Logger.info(String.format("输出天马对账结果CSV."));
        List<OcTmsportCheckResult> outCsvList = ocTmsportCheckResultManager.stream().collect(Collectors.toList());
        OcCsvUtils.writeWithCsvOcTianmaCheckResultWriter(outCsvList, execJobId);
        Logger.info(String.format("输出天马对账结果CSV-完成."));

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

    public void setOcTmsportCheckResultManager(OcTmsportCheckResultManager ocTmsportCheckResultManager) {
        this.ocTmsportCheckResultManager = ocTmsportCheckResultManager;
    }
}
