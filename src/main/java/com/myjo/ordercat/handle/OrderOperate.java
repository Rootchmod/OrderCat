package com.myjo.ordercat.handle;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.myjo.ordercat.config.OrderCatConfig;
import com.myjo.ordercat.domain.*;
import com.myjo.ordercat.exception.OCException;
import com.myjo.ordercat.http.TaoBaoHttp;
import com.myjo.ordercat.http.TianmaSportHttp;
import com.myjo.ordercat.spm.ordercat.ordercat.oc_tm_order_records.OcTmOrderRecords;
import com.myjo.ordercat.spm.ordercat.ordercat.oc_tm_order_records.OcTmOrderRecordsImpl;
import com.myjo.ordercat.spm.ordercat.ordercat.oc_tm_order_records.OcTmOrderRecordsManager;
import com.myjo.ordercat.utils.*;
import com.taobao.api.domain.Order;
import com.taobao.api.domain.Trade;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import scala.Int;

import javax.script.ScriptEngine;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by lee5hx on 17/6/13.
 */
public class OrderOperate {

    private static final Logger Logger = LogManager.getLogger(OrderOperate.class);

    private TianmaSportHttp tianmaSportHttp;

    private TaoBaoHttp taoBaoHttp;

    private OcTmOrderRecordsManager ocTmOrderRecordsManager;

    private ScriptEngine scriptEngine;

    public OrderOperate(TianmaSportHttp tianmaSportHttp, TaoBaoHttp taoBaoHttp, ScriptEngine scriptEngine) {
        this.tianmaSportHttp = tianmaSportHttp;
        this.taoBaoHttp = taoBaoHttp;
        this.scriptEngine = scriptEngine;
    }


    private Map<String, String> tmsportOrderAndPay(
            long tid,
            Trade trade,
            Map<String, Object> anrtMap,
            String wareHouseId,
            String payPwd1,
            OcTmOrderRecords ocTmOrderRecords
    ) throws Exception {
        Map<String, String> requestMap = new HashMap<>();


        //判断子单
        if (trade.getOrders().size() > 1) {
            throw new OCException(String.format("淘宝订单[%d],子单数量大于1,不能进行下单.", tid));
        }
        Order order = trade.getOrders().get(0);

        String outerSkuid = order.getOuterSkuId();
        String articleno = OcStringUtils.getGoodsNoByOuterId(outerSkuid);
        String size = OcStringUtils.getGoodsNoBySize(outerSkuid);

        Logger.info(String.format("淘宝订单[%d],outerSkuid[%s],articleno[%s],size[%s]",
                tid,
                outerSkuid,
                articleno,
                size
        ));

        requestMap.put("recv_name", trade.getReceiverName());
        requestMap.put("recv_mobile", trade.getReceiverMobile());
        requestMap.put("recv_tel", trade.getReceiverPhone());
        requestMap.put("zipcode", trade.getReceiverZip());
        requestMap.put("recv_address", trade.getReceiverAddress());
        requestMap.put("remark", "OC自动下单");
        requestMap.put("province", trade.getReceiverState());
        requestMap.put("city", trade.getReceiverCity());
        requestMap.put("area", trade.getReceiverDistrict());
        requestMap.put("outer_tid", String.valueOf(tid));
        List<TmArea> list = tianmaSportHttp.getArea("0");
        String province_id = getPidInAreas(list, trade.getReceiverState());
        list = tianmaSportHttp.getArea(province_id);
        String city_id = getPidInAreas(list, trade.getReceiverCity());
        list = tianmaSportHttp.getArea(city_id);
        String area_id = getPidInAreas(list, trade.getReceiverDistrict());
        requestMap.put("province_id", province_id);
        requestMap.put("city_id", city_id);
        requestMap.put("area_id", area_id);


        // Map<String, Object> anrtMap = tianmaSportHttp.getSearchByArticleno(articleno);
        Map<String, TmSizeInfo> tmSizeInfoMap = (Map<String, TmSizeInfo>) anrtMap.get("sizeInfo");

        List<InventoryInfo> whlist = (List<InventoryInfo>) anrtMap.get("whlist");

        Optional<InventoryInfo> optionalInventoryInfo = whlist.parallelStream()
                .filter(o -> o.getWareHouseID().toString().equals(wareHouseId))
                .findFirst();

        if (!optionalInventoryInfo.isPresent()) {
            throw new OCException(String.format("淘宝订单[%d]-仓库ID[%s],没有在快速下单中找到相关信息.", tid, wareHouseId));
        }

        //获得产品ID
        String productID = (String) anrtMap.get("productID");

        //商品重量
        String weight = (String) anrtMap.get("weight");

        //获得仓库信息
        InventoryInfo ii = optionalInventoryInfo.get();

        TmSizeInfo TmSizeInfo;
        //获取TM-skuid
        TmSizeInfo = tmSizeInfoMap.get(size);

        if (TmSizeInfo == null) {
            throw new OCException(String.format("淘宝订单[%d]的尺码[%s],在天马没有找对应信息.", tid, size));
        }

        //选择仓库对应快递公司
        //下单的时候首先记录下默认快递。
        //然后判断是否支持顺丰快递。如果支持，进行下一步判断。如果不支持，则直接选择默认快递。
        //支持顺丰快递则判断顺丰运费是否 ＜25元，如果＜25元则选择顺丰快递。
        //如果≥25元则还是选择默认快递。@lee5hx
        List<TmPostage> postages = tianmaSportHttp.getPostage(ii.getWarehouseName(), trade.getReceiverState(), weight);
        Optional<TmPostage> optionalTmPostage = postages.parallelStream()
                .filter(o -> o.getExpressName().indexOf("到付") < 0)
                .filter(o -> o.getExpressName().indexOf("顺丰") > -1
                        && o.getKdCost().compareTo(new BigDecimal(OrderCatConfig.getOrderOperateSfPriceGate())) < 0)
                .findFirst();

        String express; //普通快递(10.0)
        if (optionalTmPostage.isPresent()) {//顺丰小于25
            express = optionalTmPostage.get().getExpressName();
        } else {
            express = tianmaSportHttp.getdefaultPostage(ii.getWarehouseName(), trade.getReceiverState(), weight);
        }
        if (express == null) {
            throw new OCException(String.format("淘宝订单[%d]的,没有选择出快递公司,在天马中没有选择出快递公司.", tid));
        }

        ocTmOrderRecords.setFreightPriceStr(express);

        StringBuilder jsonStr = new StringBuilder();

        jsonStr.append("[");
        jsonStr.append("{");
        jsonStr.append("\"wareHouseName\": \"" + ii.getWarehouseName() + "\",");
        jsonStr.append("\"size\": \"" + TmSizeInfo.getSize1() + "\",");
        jsonStr.append("\"articleno\": \"" + articleno + "\",");
        jsonStr.append("\"express\": \"" + express + "\",");
        jsonStr.append("\"orderCount\": \"1\",");
        jsonStr.append("\"productID\": \"" + productID + "\",");
        jsonStr.append("\"ercipei\": \"1\",");
        jsonStr.append("\"size1\": \"" + TmSizeInfo.getSize2() + "\",");
        jsonStr.append("\"hezi\": \"1\",");
        jsonStr.append("\"wareHouseID\":" + wareHouseId + ",");
        jsonStr.append("\"sku_id\":" + TmSizeInfo.getTmSukId() + ",");
        jsonStr.append("\"articleno_old\": \"" + ii.getArticlenoOld() + "\"");
        jsonStr.append("}");
        jsonStr.append("]");
        requestMap.put("jsonStr", jsonStr.toString());
        //下单
        String rt = tianmaSportHttp.orderBooking(requestMap);
        Logger.info(String.format("orderBooking下单-rt[%s}", rt));

        PageResult<TianmaOrder> prTmOrders = tianmaSportHttp.tradeOrderDataList(null, null, null, String.valueOf(tid), null, 1, 10);

        if (prTmOrders.getTotal() > 1 || prTmOrders.getRows().size() > 1) {
            throw new OCException(String.format("淘宝订单[%d],在天马中的订单大于1.", tid));
        }
        //支付的ID
        String payTid = prTmOrders.getRows().get(0).getTid();
        //天马的订单ID
        String orderId = prTmOrders.getRows().get(0).getOrderId();

        ocTmOrderRecords.setTmOrderId(orderId);

        tianmaSportHttp.updataBalance(payTid, payPwd1);

        return requestMap;
    }


    private void tianmaOrder(long tid, Map<String, Object> anrtMap, String wareHouseId, String payPwd1, OcTmOrderRecords ocTmOrderRecords) throws Exception {

        Optional<OcTmOrderRecords> obj = ocTmOrderRecordsManager.stream()
                .filter(OcTmOrderRecords.TID.equal(String.valueOf(tid)).and(OcTmOrderRecords.STATUS.equal(TmOrderRecordStatus.SUCCESS.getValue())))
                .findAny();
        if (obj.isPresent()) {
            throw new OCException(String.format("淘宝订单[%d],已经下过订单.禁止重复下单", tid));
        }

        Optional<Trade> optionalTrade = taoBaoHttp.getTaobaoTradeFullInfo(tid);
        if (!optionalTrade.isPresent()) {
            throw new OCException(String.format("淘宝订单[%d],没有找到对应的订单信息.", tid));
        }
        Trade trade = optionalTrade.get();
        if (trade.getOrders().size() > 1) {
            throw new OCException(String.format("淘宝订单[%d],子单数量大于1,不能进行下单.", tid));
        }
        //天马下单+付款
//        Map<String, String> requestMap = tmsportOrderAndPay(tid, trade, anrtMap, wareHouseId, payPwd1);
//        ocTmOrderRecords.setOrderInfo(JSON.toJSONString(requestMap));
    }


    private Trade getTaoBaoTrade(long tid) throws Exception {

        Optional<OcTmOrderRecords> obj = ocTmOrderRecordsManager.stream()
                .filter(OcTmOrderRecords.TID.equal(String.valueOf(tid)).and(OcTmOrderRecords.STATUS.equal(TmOrderRecordStatus.SUCCESS.getValue())))
                .findAny();
        if (obj.isPresent()) {
            throw new OCException(String.format("淘宝订单[%d],已经下过订单.禁止重复下单", tid));
        }

        Optional<Trade> optionalTrade = taoBaoHttp.getTaobaoTradeFullInfo(tid);
        if (!optionalTrade.isPresent()) {
            throw new OCException(String.format("淘宝订单[%d],没有找到对应的订单信息.", tid));
        }
        Trade trade = optionalTrade.get();
        if (trade.getOrders().size() > 1) {
            throw new OCException(String.format("淘宝订单[%d],子单数量大于1,不能进行下单.", tid));
        }

        return trade;
    }


    /**
     * 手工下单
     *
     * @param tid
     */
    public OcTmOrderRecords manualOrder(
            long tid,
            String wareHouseId,
            String payPwd1) throws Exception {
        Logger.info(String.format("开始执行手工下单-淘宝订单[%d],仓库ID[%s]",
                tid,
                wareHouseId
        ));

        OcTmOrderRecords ocTmOrderRecords = new OcTmOrderRecordsImpl();
        ocTmOrderRecords.setTid(String.valueOf(tid));
        ocTmOrderRecords.setType(TmOrderRecordType.MANUAL.getValue());

        try {
            Trade trade = getTaoBaoTrade(tid);
            Order order = trade.getOrders().get(0);
            String outerSkuid = order.getOuterSkuId();
            String articleno = OcStringUtils.getGoodsNoByOuterId(outerSkuid);
            Map<String, Object> anrtMap = tianmaSportHttp.getSearchByArticleno(articleno);
            Map<String, String> requestMap = tmsportOrderAndPay(tid, trade, anrtMap, wareHouseId, payPwd1, ocTmOrderRecords);
            ocTmOrderRecords.setStatus(TmOrderRecordStatus.SUCCESS.getValue());
            ocTmOrderRecords.setOrderInfo(JSON.toJSONString(requestMap));

        } catch (Exception e) {
            ocTmOrderRecords.setStatus(TmOrderRecordStatus.FAILURE.getValue());
            ocTmOrderRecords.setFailCause(e.getMessage());
            Logger.error(e);
        }

        ocTmOrderRecords.setAddTime(LocalDateTime.now());
        ocTmOrderRecordsManager.persist(ocTmOrderRecords);
        return ocTmOrderRecords;
    }

    private String getPidInAreas(List<TmArea> list, String name) {
        String pid = null;
        for (TmArea t : list) {
            if (t.getName().equals(name)) {
                pid = String.valueOf(t.getId());
                break;
            }
        }
        return pid;
    }


    private ComputeWarehouseResult giveWarehouseResult(JSONObject jsonObject, String tmSkuId) {
        ComputeWarehouseResult rt = new ComputeWarehouseResult();
        rt.setWarehouseId(jsonObject.getString("wareHouseID"));
        rt.setWarehouseName(jsonObject.getString("wareHouseName"));
        rt.setWhUpdateTime(jsonObject.getString("updateTime"));
        rt.setPickRate(OcLcUtils.getPickRate(jsonObject.getString("pickRate")).toPlainString());
        rt.setProxyPrice(jsonObject.getBigDecimal("proxyPrice"));
        rt.setInventoryCount(jsonObject.getString(tmSkuId));
        return rt;
    }


    private static boolean filterPickRate(com.alibaba.fastjson.JSONObject jsonObject, PickRateDelCondition pickRateDelCondition,String tmSkuId) {
        boolean rt = true;
        BigDecimal pickRate = OcLcUtils.getPickRate(jsonObject.getString("pickRate"));
        BigDecimal llPickRate = new BigDecimal(pickRateDelCondition.getLlPickRate());
        BigDecimal ulPickRate = new BigDecimal(pickRateDelCondition.getUlPickRate());
        Integer quarter = jsonObject.getInteger(tmSkuId);
        if (pickRate.compareTo(llPickRate)>=0 && pickRate.compareTo(ulPickRate)<=0) {
            if (quarter.intValue() > pickRateDelCondition.getRepertory()) {//库存大于指定库存数在保留
                rt = true;
            } else {
                rt = false;
            }
        }
        return rt;
    }


    public Optional<ComputeWarehouseResult> computeWarehouseId(
            List<com.alibaba.fastjson.JSONObject> jsonObjectList,
            String tmSkuId,
            BigDecimal breakEvenPrice,
            LocalDateTime nowDate,
            String cycleWhCompPolicy
    ) {
        ComputeWarehouseResult rt = null;

        jsonObjectList = jsonObjectList
                .parallelStream()
                .filter(jsonObject -> breakEvenPrice.compareTo(jsonObject.getBigDecimal("proxyPrice")) >= 0).collect(Collectors.toList());
        Logger.info(String.format("过滤掉大于保本价的仓库信息.size[%d].", jsonObjectList.size()));


        //根据付款金额计算保本价格
        Logger.info(String.format("保本价格[%s].", breakEvenPrice.toPlainString()));
        //过滤掉大于保本价的仓库信息
        jsonObjectList = jsonObjectList
                .parallelStream()
                .filter(jsonObject -> breakEvenPrice.compareTo(jsonObject.getBigDecimal("proxyPrice")) >= 0).collect(Collectors.toList());
        Logger.info(String.format("过滤掉大于保本价的仓库信息.size[%d].", jsonObjectList.size()));
        //过滤掉不存在改尺码的仓库信息
        jsonObjectList = jsonObjectList
                .parallelStream()
                .filter(jsonObject -> jsonObject.get(tmSkuId) != null).collect(Collectors.toList());
        Logger.info(String.format("过滤掉不存在的尺码的仓库信息.size[%d].", jsonObjectList.size()));


        //删除配货率低于及基础线的直接删除掉  默认50
        BigDecimal opPrtdl = new BigDecimal(OrderCatConfig.getOpPickRateLessThanDelLimit());
        jsonObjectList = jsonObjectList.parallelStream().
                filter(jsonObject -> OcLcUtils.getPickRate(jsonObject.getString("pickRate")).compareTo(opPrtdl)==1)
                .collect(Collectors.toList());


        Logger.info(String.format("配货率低于[%d]百分比,进行删除.size:%d", OrderCatConfig.getOpPickRateLessThanDelLimit(),jsonObjectList.size()));


        for (PickRateDelCondition pickRateDelCondition : OrderCatConfig.getOpPickRateDelConditions()) {
            Logger.info(String.format("配货率在[%d]-[%d]百分比,并且库存小于等于[%d]进行删除.",
                    pickRateDelCondition.getLlPickRate(),
                    pickRateDelCondition.getUlPickRate(),
                    pickRateDelCondition.getRepertory()));

            jsonObjectList = jsonObjectList.parallelStream().
                    filter(jsonObject -> filterPickRate(jsonObject, pickRateDelCondition, tmSkuId))
                    .collect(Collectors.toList());

            Logger.info(String.format("配货率在[%d]-[%d]百分比,并且库存小于等于[%d],进行删除后的记录数为:[%d]",
                    pickRateDelCondition.getLlPickRate(),
                    pickRateDelCondition.getUlPickRate(),
                    pickRateDelCondition.getRepertory(),
                    jsonObjectList.size()));
        }



        Logger.info(String.format("过滤不下单仓库--"));
        List<NotOrderWareHousePolicy> list1 = OrderCatConfig.getNotOrderWareHousePolicy();
        List<JSONObject> jsonObjectList1;
        for (NotOrderWareHousePolicy notOrderWareHousePolicy : list1) {
            Logger.info(String.format("正在过滤-%s-%s", notOrderWareHousePolicy.getWarehouseId(), notOrderWareHousePolicy.getReason()));
            jsonObjectList1 = jsonObjectList.parallelStream()
                    .filter(jsonObject -> notOrderWareHousePolicy.getWarehouseId().equals(jsonObject.getString("wareHouseID")))
                    .collect(Collectors.toList());
            if (jsonObjectList1 != null && jsonObjectList1.size() > 0) {
                throw new OCException(notOrderWareHousePolicy.getReason());
            }
        }

        List<PriorityOrderWhPolicy> list2 = OrderCatConfig.getPriorityOrderWhPolicy();
        for (PriorityOrderWhPolicy priorityOrderWhPolicy : list2) {
            Logger.info(String.format("正在查看仓库中是否包含-[%s-%s]", priorityOrderWhPolicy.getWarehouseId(), priorityOrderWhPolicy.getWarehouseName()));

            jsonObjectList1 = jsonObjectList.parallelStream()
                    .filter(jsonObject -> jsonObject.getString("wareHouseID").equals(priorityOrderWhPolicy.getWarehouseId()))
                    .collect(Collectors.toList());

            if (jsonObjectList1 != null && jsonObjectList1.size() > 0) {
                rt = giveWarehouseResult(jsonObjectList1.get(0), tmSkuId);
                return Optional.ofNullable(rt);
            }
        }

        List<PickWhcountCalculatePolicy> list = OrderCatConfig.getPickWhcountCalculatePolicy();
        for (PickWhcountCalculatePolicy p : list) {
            jsonObjectList = jsonObjectList.parallelStream()
                    .filter(jsonObject -> {
                        //String dd1 = StringUtils.substringBeforeLast(jsonObject.getString("pickRate"), "%");
                        String x = OcLcUtils.getPickRate(jsonObject.getString("pickRate")).toPlainString();
                        String y = jsonObject.getInteger(tmSkuId).toString();
                        return !OcBigDecimalUtils.pickWhcountCalculatePolicyJudge(scriptEngine, x, y, p.getEquation());
                    }).collect(Collectors.toList());
            Logger.info(String.format("----Equation:[%s]-size[%d].", p.getEquation(), jsonObjectList.size()));
        }

        //过滤周六周日不配货仓库
        //LocalDateTime nowDate = LocalDateTime.now();
        List<com.alibaba.fastjson.JSONObject> weekJsonObjectList = null;

        //如果下单时间为周五4:00-周六4:00，则过滤周六周日不发货仓库
        if (nowDate.getDayOfWeek() == DayOfWeek.FRIDAY && nowDate.getHour() >= 4) {
            weekJsonObjectList = jsonObjectList.parallelStream()
                    .filter(jsonObject -> {
                        int pickDate = Integer.valueOf(jsonObject.getString("pick_date"));
                        return pickDate != 0;
                    }).collect(Collectors.toList());
            Logger.info(String.format("-[%s]-weekJsonObjectList-size[%d].", "FRIDAY", weekJsonObjectList.size()));
        }
        if (nowDate.getDayOfWeek() == DayOfWeek.SATURDAY && nowDate.getHour() < 4) {
            weekJsonObjectList = jsonObjectList.parallelStream()
                    .filter(jsonObject -> {
                        int pickDate = Integer.valueOf(jsonObject.getString("pick_date"));
                        return pickDate != 0;
                    }).collect(Collectors.toList());
            Logger.info(String.format("-[%s]-weekJsonObjectList-size[%d].", "SATURDAY", weekJsonObjectList.size()));
        }

        //如果下单时间为周六4:00-周日4:00，则过滤周日不发货仓库
        if (nowDate.getDayOfWeek() == DayOfWeek.SATURDAY && nowDate.getHour() >= 4) {
            weekJsonObjectList = jsonObjectList.parallelStream()
                    .filter(jsonObject -> {
                        int pickDate = Integer.valueOf(jsonObject.getString("pick_date"));
                        return pickDate == 2;
                    }).collect(Collectors.toList());
            Logger.info(String.format("-[%s]-weekJsonObjectList-size[%d].", "SATURDAY", weekJsonObjectList.size()));
        }
        if (nowDate.getDayOfWeek() == DayOfWeek.SUNDAY && nowDate.getHour() < 4) {
            weekJsonObjectList = jsonObjectList.parallelStream()
                    .filter(jsonObject -> {
                        int pickDate = Integer.valueOf(jsonObject.getString("pick_date"));
                        return pickDate == 2;
                    }).collect(Collectors.toList());
            Logger.info(String.format("-[%s]-weekJsonObjectList-size[%d].", "SATURDAY", weekJsonObjectList.size()));
        }

        if (weekJsonObjectList != null) {
            jsonObjectList = weekJsonObjectList;
        }

        //以符合条件的价格最低的仓库为基准仓库，进行对比


        List<com.alibaba.fastjson.JSONObject> cycleWhCompJsonObjectList = new ArrayList<>();

        if (jsonObjectList.size() > 1) {
            Optional<JSONObject> optionalBaselineJsonObject = jsonObjectList
                    .parallelStream()
                    .filter(t -> t != null)
                    .min(
                            Comparator.comparing(p -> p.getBigDecimal("proxyPrice"))
                    );
            JSONObject baselineJsonObject;

            if (optionalBaselineJsonObject.isPresent()) {
                baselineJsonObject = optionalBaselineJsonObject.get();
                String whid = baselineJsonObject.getString("wareHouseID");
                BigDecimal pickRate = OcLcUtils.getPickRate(baselineJsonObject.getString("pickRate"));
                String icount = baselineJsonObject.getString(tmSkuId);
                BigDecimal proxyPrice = baselineJsonObject.getBigDecimal("proxyPrice");
                Logger.info(String.format("baselineJsonObject -- whid=[%s],proxyPrice=[%s],pickRate=[%s] .", whid, proxyPrice, pickRate));

                //先过滤掉基准
                jsonObjectList = jsonObjectList.parallelStream()
                        .filter(jsonObject -> !whid.equals(jsonObject.getString("wareHouseID")))
                        .collect(Collectors.toList());

                Logger.info(String.format("先过滤掉基准.size=[%d] .", jsonObjectList.size()));

                for (JSONObject jsonObject : jsonObjectList) {

                    BigDecimal at = OcBigDecimalUtils.divide(jsonObject.getBigDecimal("proxyPrice").subtract(proxyPrice), proxyPrice);
                    BigDecimal ap = OcLcUtils.getPickRate(jsonObject.getString("pickRate")).subtract(pickRate);
                    for (CycleWhComp cwc : OrderCatConfig.getCycleWhComp()) {
                        Logger.info(String.format(" ---- %s - %s .", cwc.getPickRateBi(), cwc.getProxyPriceBi()));
                        if (at.compareTo(new BigDecimal(cwc.getProxyPriceBi())) <= 0 && ap.compareTo(new BigDecimal(cwc.getPickRateBi())) >= 0) {
                            cycleWhCompJsonObjectList.add(jsonObject);
                            break;
                        }
                    }
                }
                //如果根据基准筛选的结果为0
                if (cycleWhCompJsonObjectList.size() == 0) {
                    rt = giveWarehouseResult(baselineJsonObject, tmSkuId);
                    return Optional.ofNullable(rt);
                }
                //如果根据基准筛选的结果为1
                if (cycleWhCompJsonObjectList.size() == 1) {
                    rt = giveWarehouseResult(cycleWhCompJsonObjectList.get(0), tmSkuId);
                    return Optional.ofNullable(rt);
                }
                //如果根据基准筛选的结果大于1
                if (cycleWhCompJsonObjectList.size() > 1) {

                    if (cycleWhCompPolicy.equals("A")) {
                        Optional<JSONObject> objectOptionalA = cycleWhCompJsonObjectList
                                .parallelStream()
                                .filter(t -> t != null)
                                .min(
                                        Comparator.comparing(p -> p.getBigDecimal("proxyPrice"))
                                );

                        rt = giveWarehouseResult(objectOptionalA.get(), tmSkuId);

                    } else {
                        Optional<JSONObject> objectOptionalB = cycleWhCompJsonObjectList
                                .parallelStream()
                                .filter(t -> t != null)
                                .max(
                                        Comparator.comparing(p -> OcLcUtils.getPickRate(p.getString("pickRate")))
                                );

                        rt = giveWarehouseResult(objectOptionalB.get(), tmSkuId);
                    }
                    return Optional.ofNullable(rt);
                }
            }
        }


        if (jsonObjectList.size() > 1) {
            throw new OCException("计算过后的仓库数量大于1,程序无法处理!,请人工介入!");
        }
        if (jsonObjectList.size() == 0) {
            rt = null;
        }
        if (jsonObjectList.size() == 1) {
            rt = giveWarehouseResult(jsonObjectList.get(0), tmSkuId);
        }
        return Optional.ofNullable(rt);
    }

    /**
     * 自动下单
     */
    public OcTmOrderRecords autoOrder(long tid, String machineCid) {

        Logger.info(String.format("开始自动下单-淘宝订单[%d],机器CID[%s]",
                tid,
                machineCid
        ));

        long elapsed = 0l;

        long begin = System.currentTimeMillis();

        OcTmOrderRecords ocTmOrderRecords = new OcTmOrderRecordsImpl();
        ocTmOrderRecords.setTid(String.valueOf(tid));
        ocTmOrderRecords.setType(TmOrderRecordType.MACHINE.getValue());
        ocTmOrderRecords.setMachineCid(machineCid);


        try {
            //获取淘宝订单
            Trade trade = getTaoBaoTrade(tid);


            Logger.info("trade.getBuyerMessage():" + trade.getBuyerMessage());

            if (trade.getBuyerMessage() != null) {
                throw new OCException(String.format("淘宝订单[%d],存在买家留言[%s],不能自动下单.", tid, trade.getBuyerMessage()));
            }

            if (trade.getIsDaixiao()) {
                throw new OCException(String.format("淘宝订单[%d].是代销订单,OC不能进行下单.", tid));
            }

            if (trade.getNum() > 1) {
                throw new OCException(String.format("淘宝订单[%d].数量大于[1]不能进行下单.", tid));
            }


            Order order = trade.getOrders().get(0);
            String outerSkuid = order.getOuterSkuId();
            Logger.info(String.format("autoOrder-tborder-outerSkuid=[%s]", outerSkuid));
            String articleno = OcStringUtils.getGoodsNoByOuterId(outerSkuid);
            Logger.info(String.format("autoOrder-tborder-articleno=[%s]", articleno));
            String size = OcStringUtils.getGoodsNoBySize(outerSkuid);
            Logger.info(String.format("autoOrder-tborder-size=[%s]", size));

            Map<String, Object> anrtMap = tianmaSportHttp.getSearchByArticleno(articleno);
            Map<String, TmSizeInfo> tmSizeInfoMap = (Map<String, TmSizeInfo>) anrtMap.get("sizeInfo");

            List<com.alibaba.fastjson.JSONObject> jsonObjectList = (List<com.alibaba.fastjson.JSONObject>) anrtMap.get("jsonObjectList");

            TmSizeInfo TmSizeInfo;
            //获取TM-skuid
            TmSizeInfo = tmSizeInfoMap.get(size);
            if (TmSizeInfo == null) {
                throw new OCException(String.format("淘宝订单[%d]的尺码[%s],在天马没有找对应信息.", tid, size));
            }
            String tmSkuId = TmSizeInfo.getTmSukId();
            Logger.info(String.format("autoOrder-tmSkuId=[%s]", tmSkuId));

            BigDecimal payAmount = new BigDecimal(trade.getPayment());
            Logger.info(String.format("autoOrder-payAmount=[%s]", payAmount.toPlainString()));
            ocTmOrderRecords.setTbPayAmount(payAmount);
            BigDecimal breakEvenPrice = OcBigDecimalUtils.toBreakEvenPrice(scriptEngine, payAmount);
            Logger.info(String.format("autoOrder-breakEvenPrice=[%s]", breakEvenPrice.toPlainString()));

            ocTmOrderRecords.setBreakEvenPrice(breakEvenPrice);
            ocTmOrderRecords.setWhSnapshotData(JSON.toJSONString(jsonObjectList));

            Optional<ComputeWarehouseResult> optWareHouse = computeWarehouseId(
                    jsonObjectList,
                    tmSkuId,
                    breakEvenPrice,
                    LocalDateTime.now(),
                    OrderCatConfig.getCycleWhCompPolicy()
            );
            
            if (!optWareHouse.isPresent()) {
                throw new OCException(String.format("淘宝订单[%d],没有计算出仓库信息.请人工处理.", tid, size));
            }

            ComputeWarehouseResult warehouseResult = optWareHouse.get();

            ocTmOrderRecords.setSize(size);
            ocTmOrderRecords.setGoodsNo(articleno);
            ocTmOrderRecords.setWhId(Integer.valueOf(warehouseResult.getWarehouseId()));
            ocTmOrderRecords.setWhName(warehouseResult.getWarehouseName());
            ocTmOrderRecords.setWhPickRate(Integer.valueOf(warehouseResult.getPickRate()));
            ocTmOrderRecords.setWhInventoryCount(Integer.valueOf(warehouseResult.getInventoryCount()));
            ocTmOrderRecords.setWhProxyPrice(warehouseResult.getProxyPrice());
            ocTmOrderRecords.setWhUpdateTime(OcDateTimeUtils.string2LocalDateTime(warehouseResult.getWhUpdateTime()));


            //ocTmOrderRecords.setFreightPrice();


            //todo 有一些仓库不下单 lee5hx


            //支付密码
            String payPwd1 = OcEncryptionUtils.base64Decoder(OrderCatConfig.getOrderOperateTmPayPwd(), 5);
            //天马下单+支付
            Map<String, String> requestMap = tmsportOrderAndPay(
                    tid,
                    trade,
                    anrtMap,
                    warehouseResult.getWarehouseId(),
                    payPwd1,
                    ocTmOrderRecords
            );
            ocTmOrderRecords.setStatus(TmOrderRecordStatus.SUCCESS.getValue());
            ocTmOrderRecords.setOrderInfo(JSON.toJSONString(requestMap));

            taoBaoHttp.addTradeMemo(tid, String.format("OC下单成功,天马订单ID:[%s]", ocTmOrderRecords.getTmOrderId().get()), 3l);
            long end = System.currentTimeMillis();
            elapsed = end - begin;
            Logger.info(String.format("执行耗时(毫秒):%d", elapsed));
            ocTmOrderRecords.setElapsed(elapsed);
        } catch (Exception e) {
            ocTmOrderRecords.setStatus(TmOrderRecordStatus.FAILURE.getValue());
            ocTmOrderRecords.setFailCause(e.getMessage());
            taoBaoHttp.addTradeMemo(tid, String.format("OC下单失败,失败原因:%s", e.getMessage()), 5l);
            long end = System.currentTimeMillis();
            elapsed = end - begin;
            ocTmOrderRecords.setElapsed(elapsed);
            Logger.info(String.format("执行耗时(毫秒):%d", elapsed));
            Logger.error(e);
        }
        ocTmOrderRecords.setAddTime(LocalDateTime.now());
        ocTmOrderRecordsManager.persist(ocTmOrderRecords);
        return ocTmOrderRecords;
    }

    public void setOcTmOrderRecordsManager(OcTmOrderRecordsManager ocTmOrderRecordsManager) {
        this.ocTmOrderRecordsManager = ocTmOrderRecordsManager;
    }
}
