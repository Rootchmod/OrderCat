package com.myjo.ordercat.handle;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.myjo.ordercat.config.OrderCatConfig;
import com.myjo.ordercat.domain.*;
import com.myjo.ordercat.domain.constant.TianmaOrderStatus;
import com.myjo.ordercat.domain.constant.TmOrderRecordStatus;
import com.myjo.ordercat.domain.constant.TmOrderRecordType;
import com.myjo.ordercat.exception.OCException;
import com.myjo.ordercat.http.TaoBaoHttp;
import com.myjo.ordercat.http.TianmaSportHttp;
import com.myjo.ordercat.redis.SetnxLock;
import com.myjo.ordercat.spm.ordercat.ordercat.oc_params.OcParams;
import com.myjo.ordercat.spm.ordercat.ordercat.oc_params.OcParamsManager;
import com.myjo.ordercat.spm.ordercat.ordercat.oc_tm_order_records.OcTmOrderRecords;
import com.myjo.ordercat.spm.ordercat.ordercat.oc_tm_order_records.OcTmOrderRecordsImpl;
import com.myjo.ordercat.spm.ordercat.ordercat.oc_tm_order_records.OcTmOrderRecordsManager;
import com.myjo.ordercat.spm.ordercat.ordercat.oc_tm_repair_order_records.OcTmRepairOrderRecords;
import com.myjo.ordercat.spm.ordercat.ordercat.oc_tm_repair_order_records.OcTmRepairOrderRecordsImpl;
import com.myjo.ordercat.spm.ordercat.ordercat.oc_tm_repair_order_records.OcTmRepairOrderRecordsManager;
import com.myjo.ordercat.utils.*;
import com.taobao.api.domain.Order;
import com.taobao.api.domain.Trade;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.script.ScriptEngine;
import java.io.PrintWriter;
import java.io.StringWriter;
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

    private OcTmRepairOrderRecordsManager ocTmRepairOrderRecordsManager;

    private OcParamsManager ocParamsManager;

    private ScriptEngine scriptEngine;

    public OrderOperate(TianmaSportHttp tianmaSportHttp, TaoBaoHttp taoBaoHttp, ScriptEngine scriptEngine) {
        this.tianmaSportHttp = tianmaSportHttp;
        this.taoBaoHttp = taoBaoHttp;
        this.scriptEngine = scriptEngine;
    }

    private static boolean filterPickRate(com.alibaba.fastjson.JSONObject jsonObject, PickRateDelCondition pickRateDelCondition, String tmSkuId) {
        boolean rt = true;
        BigDecimal pickRate = OcLcUtils.getPickRate(jsonObject.getString("pickRate"));
        BigDecimal llPickRate = new BigDecimal(pickRateDelCondition.getLlPickRate());
        BigDecimal ulPickRate = new BigDecimal(pickRateDelCondition.getUlPickRate());
        Integer quarter = jsonObject.getInteger(tmSkuId);
        if (pickRate.compareTo(llPickRate) >= 0 && pickRate.compareTo(ulPickRate) <= 0) {
            if (quarter.intValue() > pickRateDelCondition.getRepertory()) {//库存大于指定库存数在保留
                rt = true;
            } else {
                rt = false;
            }
        }
        return rt;
    }

    private String conversionAreaName(String name){
        String name2 = name;
        String key = String.format("order.operate.conversion.name.%s",name);
        Optional<OcParams> opt = ocParamsManager.stream()
                .filter(OcParams.PKEY.equal(key))
                .findFirst();
        if(opt.isPresent()){
            name2 = opt.get().getPvalue();
        }
        return name2;
    }

    private Map<String, String> tmsportOrderAndPay(
            long tid,
            Trade trade,
            Map<String, Object> anrtMap,
            String wareHouseId,
            String payPwd1,
            String remark
            //OcTmOrderRecords ocTmOrderRecords
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
        requestMap.put("remark", remark);
        requestMap.put("province", trade.getReceiverState());
        requestMap.put("city", trade.getReceiverCity());
        requestMap.put("area", trade.getReceiverDistrict());
        requestMap.put("outer_tid", String.valueOf(tid));
        List<TmArea> list = tianmaSportHttp.getArea("0");
        String province_id = getPidInAreas(list, conversionAreaName(trade.getReceiverState()));
        if(province_id == null){
            throw new OCException(String.format("[%s],在天马中没有找到匹配的-省份.请人工处理!",trade.getReceiverState()));
        }

        list = tianmaSportHttp.getArea(province_id);
        String city_id = getPidInAreas(list, conversionAreaName(trade.getReceiverCity()));
        if(city_id == null){
            throw new OCException(String.format("[%s],在天马中没有找到匹配的-市.请人工处理!",trade.getReceiverCity()));
        }

        list = tianmaSportHttp.getArea(city_id);
        String area_id = "0";
        if (list.size() > 0) {
            area_id = getPidInAreas(list, conversionAreaName(trade.getReceiverDistrict()));
        }

        if(area_id == null){
            throw new OCException(String.format("[%s],在天马中没有找到匹配的-区县.请人工处理!",trade.getReceiverDistrict()));
        }


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

        //ocTmOrderRecords.setFreightPriceStr(express);
        requestMap.put("FreightPriceStr",express);

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
        //查询待付款的天马订单
        PageResult<TianmaOrder> prTmOrders = tianmaSportHttp.tradeOrderDataList(null, null, TianmaOrderStatus.WAITING_PAYMENT, String.valueOf(tid), null, 1, 10,null);

        if (prTmOrders.getTotal() > 1 || prTmOrders.getRows().size() > 1) {
            throw new OCException(String.format("淘宝订单[%d],在天马中的订单大于1.", tid));
        }
        //支付的ID
        String payTid = prTmOrders.getRows().get(0).getTid();
        //天马的订单ID
        String orderId = prTmOrders.getRows().get(0).getOrderId();

       //ocTmOrderRecords.setTmOrderId(orderId);
        requestMap.put("TmOrderId",orderId);

        tianmaSportHttp.updataBalance(payTid, payPwd1);

        return requestMap;
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



    public String getPidInAreas(List<TmArea> list, String name) {
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

    public Optional<ComputeWarehouseResult> computeWarehouseId(
            List<com.alibaba.fastjson.JSONObject> jsonObjectList,
            String tmSkuId,
            BigDecimal breakEvenPrice,
            LocalDateTime nowDate,
            String cycleWhCompPolicy
    ) {
        ComputeWarehouseResult rt = null;

//        jsonObjectList = jsonObjectList
//                .parallelStream()
//                .filter(jsonObject -> breakEvenPrice.compareTo(jsonObject.getBigDecimal("proxyPrice")) >= 0).collect(Collectors.toList());
//        Logger.info(String.format("过滤掉大于保本价的仓库信息.size[%d].", jsonObjectList.size()));


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


        //屏蔽指定仓库
        List<ShieldWhPolicy> shieldWareHousePolicylist = OrderCatConfig.getShieldWareHousePolicy();
        for (ShieldWhPolicy shieldWhPolicy : shieldWareHousePolicylist) {
            Logger.info(String.format("正在查看是否包含屏蔽的仓库-[%s-%s]", shieldWhPolicy.getWarehouseId(), shieldWhPolicy.getWarehouseName()));
            jsonObjectList = jsonObjectList.parallelStream()
                    .filter(jsonObject -> !jsonObject.getString("wareHouseID").equals(shieldWhPolicy.getWarehouseId()))
                    .collect(Collectors.toList());
        }
        Logger.info(String.format("屏蔽指定仓库.size[%d].", jsonObjectList.size()));


        //删除配货率低于及基础线的直接删除掉  默认50
        BigDecimal opPrtdl = new BigDecimal(OrderCatConfig.getOpPickRateLessThanDelLimit());
        jsonObjectList = jsonObjectList.parallelStream().
                filter(jsonObject -> OcLcUtils.getPickRate(jsonObject.getString("pickRate")).compareTo(opPrtdl) == 1)
                .collect(Collectors.toList());


        Logger.info(String.format("配货率低于[%d]百分比,进行删除.size:%d", OrderCatConfig.getOpPickRateLessThanDelLimit(), jsonObjectList.size()));


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


        //Integer.valueOf(jsonObject.getString("wareHouseID")
        List<PickWhcountCalculatePolicy> list = OrderCatConfig.getPickWhcountCalculatePolicy();
        for (PickWhcountCalculatePolicy p : list) {
            jsonObjectList = jsonObjectList.parallelStream()
                    .filter(jsonObject -> {
                        //String dd1 = StringUtils.substringBeforeLast(jsonObject.getString("pickRate"), "%");
                        String x = OcLcUtils.getPickRate(jsonObject.getString("pickRate")).toPlainString();
                        String y = jsonObject.getInteger(tmSkuId).toString();
                        String z = jsonObject.getString("wareHouseID");
                        return !OcBigDecimalUtils.pickWhcountCalculatePolicyJudge(scriptEngine, x, y,z, p.getEquation());
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



    public Optional<ComputeWarehouseResult> computeRoWarehouseId(
            RepairOrderRecord repairOrderRecord,
            BigDecimal breakEvenPrice
    ){


        List<com.alibaba.fastjson.JSONObject> jsonObjectList = repairOrderRecord.getRoWhSnapshotData();
        String tmSkuId = repairOrderRecord.getRoTmSkuId();
        LocalDateTime nowDate = repairOrderRecord.getNowDateTime();



        ComputeWarehouseResult rt = null;
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



        //屏蔽指定仓库
        List<ShieldWhPolicy> shieldWareHousePolicylist = OrderCatConfig.getRoShieldWareHousePolicy();
        for (ShieldWhPolicy shieldWhPolicy : shieldWareHousePolicylist) {
            Logger.info(String.format("正在查看是否包含屏蔽的仓库-[%s-%s]", shieldWhPolicy.getWarehouseId(), shieldWhPolicy.getWarehouseName()));
            jsonObjectList = jsonObjectList.parallelStream()
                    .filter(jsonObject -> !jsonObject.getString("wareHouseID").equals(shieldWhPolicy.getWarehouseId()))
                    .collect(Collectors.toList());
        }
        Logger.info(String.format("屏蔽指定仓库.size[%d].", jsonObjectList.size()));


        //删除配货率低于及基础线的直接删除掉  默认50
        BigDecimal opPrtdl = new BigDecimal(OrderCatConfig.getRoPickRateLessThanDelLimit());
        jsonObjectList = jsonObjectList.parallelStream().
                filter(jsonObject -> OcLcUtils.getPickRate(jsonObject.getString("pickRate")).compareTo(opPrtdl) == 1)
                .collect(Collectors.toList());


        Logger.info(String.format("配货率低于[%d]百分比,进行删除.size:%d", OrderCatConfig.getOpPickRateLessThanDelLimit(), jsonObjectList.size()));


        for (PickRateDelCondition pickRateDelCondition : OrderCatConfig.getRoPickRateDelConditions()) {
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

        //如果大于1 过滤 上次下单仓库
        if (jsonObjectList.size() > 1) {
            jsonObjectList = jsonObjectList.parallelStream()
                    .filter(jsonObject -> !jsonObject.getString("wareHouseID").equals(repairOrderRecord.getPtcMWarehouseId().toString()))
                    .collect(Collectors.toList());
        }


        if(jsonObjectList.size() == 0){
            rt = null;
        }else {
            Optional<JSONObject> maxPickRate = jsonObjectList
                    .parallelStream()
                    .filter(t -> t != null)
                    .max(
                            Comparator.comparing(p -> OcLcUtils.getPickRate(p.getString("pickRate")))
                    );
            rt = giveWarehouseResult(maxPickRate.get(), tmSkuId);
        }
        return Optional.ofNullable(rt);

    }


    /**
     * 自动下单
     */
    public OcTmOrderRecords autoOrder(long tid, String machineCid) {


        long lock = SetnxLock.opLock(tid, 0);

        if (lock < 0) {
            Logger.error(String.format("该订单[%d]已经被锁定,不能重复下单.",tid));
            return null;
        }

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



            if (trade.getIsDaixiao()) {
                Logger.error(String.format("淘宝订单[%d].是代销订单,OC不能进行下单.", tid));
                return ocTmOrderRecords;
            }

            if (trade.getNum() > 1) {
                throw new OCException(String.format("淘宝订单[%d].数量大于[1]不能进行下单.", tid));
            }


            Long refundId =  trade.getOrders().get(0).getRefundId();
            if(refundId!=null){
                throw new OCException(String.format("淘宝订单[%d].已经存在退款单[%d]不能进行下单.", tid,refundId));
            }


            Order order = trade.getOrders().get(0);
            String outerSkuid = order.getOuterSkuId();


            Logger.info("outerSkuid:" + outerSkuid);

            if(OcStringUtils.judgeFilterOuterId(scriptEngine,outerSkuid)){
                throw new OCException(String.format("淘宝订单[%d],外部供应商编码中包含非下单字符[%s].",tid,outerSkuid));
            }




            Logger.info(String.format("autoOrder-tborder-outerSkuid=[%s]", outerSkuid));
            String articleno = OcStringUtils.getGoodsNoByOuterId(outerSkuid);
            ocTmOrderRecords.setGoodsNo(articleno);
            Logger.info(String.format("autoOrder-tborder-articleno=[%s]", articleno));
            String size = OcStringUtils.getGoodsNoBySize(outerSkuid);
            ocTmOrderRecords.setSize(size);
            Logger.info(String.format("autoOrder-tborder-size=[%s]", size));




            if (OrderCatConfig.isBuyerMessageCheck() && trade.getBuyerMessage() != null) {
                throw new OCException(String.format("淘宝订单[%d],存在买家留言[%s],不能自动下单.", tid, trade.getBuyerMessage()));
            }


            Map<String, Object> anrtMap = tianmaSportHttp.getSearchByArticleno(articleno);
            Map<String, TmSizeInfo> tmSizeInfoMap = (Map<String, TmSizeInfo>) anrtMap.get("sizeInfo");

            List<com.alibaba.fastjson.JSONObject> jsonObjectList = (List<com.alibaba.fastjson.JSONObject>) anrtMap.get("jsonObjectList");


            //赋值尺码于SKU对应关系
            List<TmSizeInfo> tmSizeInfoList = new ArrayList();
            for (Map.Entry<String, TmSizeInfo> entry : tmSizeInfoMap.entrySet()) {
                tmSizeInfoList.add(entry.getValue());
            }
            ocTmOrderRecords.setTmSizeInfoStr(JSON.toJSONString(tmSizeInfoList));

            TmSizeInfo TmSizeInfo;
            //获取TM-skuid
            TmSizeInfo = tmSizeInfoMap.get(size);
            if (TmSizeInfo == null) {
                throw new OCException(String.format("淘宝订单[%d]的尺码[%s],在天马没有找对应信息.", tid, size));
            }
            String tmSkuId = TmSizeInfo.getTmSukId();
            ocTmOrderRecords.setTmSkuId(tmSkuId);
            Logger.info(String.format("autoOrder-tmSkuId=[%s]", tmSkuId));
            BigDecimal payAmount = new BigDecimal(trade.getPayment());
            Logger.info(String.format("autoOrder-payAmount=[%s]", payAmount.toPlainString()));
            ocTmOrderRecords.setTbPayAmount(payAmount);
            BigDecimal breakEvenPrice = OcBigDecimalUtils.toBreakEvenPrice(scriptEngine, payAmount,OrderCatConfig.getBreakEvenPricePolicyEquation());
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


            ocTmOrderRecords.setWhId(Integer.valueOf(warehouseResult.getWarehouseId()));
            ocTmOrderRecords.setWhName(warehouseResult.getWarehouseName());
            ocTmOrderRecords.setWhPickRate(Integer.valueOf(warehouseResult.getPickRate()));
            ocTmOrderRecords.setWhInventoryCount(Integer.valueOf(warehouseResult.getInventoryCount()));
            ocTmOrderRecords.setWhProxyPrice(warehouseResult.getProxyPrice());
            ocTmOrderRecords.setWhUpdateTime(OcDateTimeUtils.string2LocalDateTime(warehouseResult.getWhUpdateTime()));

            //支付密码
            String payPwd1 = OcEncryptionUtils.base64Decoder(OrderCatConfig.getOrderOperateTmPayPwd(), 5);
            //天马下单+支付
            Map<String, String> requestMap = tmsportOrderAndPay(
                    tid,
                    trade,
                    anrtMap,
                    warehouseResult.getWarehouseId(),
                    payPwd1,
                    "OC自动下单"
            );
            ocTmOrderRecords.setTmOrderId(requestMap.get("TmOrderId"));
            ocTmOrderRecords.setFreightPriceStr(requestMap.get("FreightPriceStr"));
            ocTmOrderRecords.setStatus(TmOrderRecordStatus.SUCCESS.getValue());
            ocTmOrderRecords.setOrderInfo(JSON.toJSONString(requestMap));

            taoBaoHttp.addTradeMemo(tid, String.format("OC下单成功,天马订单ID:[%s]", ocTmOrderRecords.getTmOrderId().get()), 3l);
            long end = System.currentTimeMillis();
            elapsed = end - begin;
            Logger.info(String.format("执行耗时(毫秒):%d", elapsed));
            ocTmOrderRecords.setElapsed(elapsed);
        } catch (Exception e) {
            StringWriter errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));
            ocTmOrderRecords.setStatus(TmOrderRecordStatus.FAILURE.getValue());
            ocTmOrderRecords.setFailCause(errors.toString());
            taoBaoHttp.addTradeMemo(tid, String.format("OC下单失败,失败原因:%s", e.getMessage()), 5l);
            long end = System.currentTimeMillis();
            elapsed = end - begin;
            ocTmOrderRecords.setElapsed(elapsed);
            Logger.info(String.format("执行耗时(毫秒):%d", elapsed));
            Logger.error(e);
        } finally {
           //SetnxLock.unOpLock(tid, lock);
        }
        ocTmOrderRecords.setAddTime(LocalDateTime.now());
        ocTmOrderRecordsManager.persist(ocTmOrderRecords);
        return ocTmOrderRecords;
    }

    /**
     * 批量补单
     * @param execJobId
     * @throws Exception
     */
    public void batchRepairOrder(Long execJobId) throws Exception{

        //1.获取天马最近2天内已退款订单
        int dayCount = OrderCatConfig.getOrderRefundedDateIntervalDay();
        Logger.info(String.format("天马已退款订单查询周期(天):%d", dayCount));
        LocalDateTime lend = LocalDateTime.now();
        LocalDateTime lbegin = lend.minusDays(dayCount);
        String startTime = OcDateTimeUtils.localDateTime2String(lbegin, OcDateTimeUtils.OC_DATE);
        String endTime = OcDateTimeUtils.localDateTime2String(lend, OcDateTimeUtils.OC_DATE);
        Logger.info(String.format("已退款订单查询起始日期:[%s],截止日期:%s", startTime,endTime));
        List<TianmaOrder> orderList = tianmaSportHttp.tradeOrderDataList(startTime, endTime, TianmaOrderStatus.REFUNDED, null);


        LocalDateTime now = LocalDateTime.now();
        // 赋值
        List<RepairOrderRecord> repairOrderRecords = orderList.parallelStream().map(tianmaOrder -> {
            RepairOrderRecord repairOrderRecord = new RepairOrderRecord();
            repairOrderRecord.setCustomerName(tianmaOrder.getName());
            repairOrderRecord.setGoodsNo(tianmaOrder.getGoodsNo());
            repairOrderRecord.setOuterOrderId(tianmaOrder.getOuterOrderId());
            //repairOrderRecord.setPayPrice(tianmaOrder.getPayPrice());


            repairOrderRecord.setPctTmOrderid(tianmaOrder.getOrderId());
            repairOrderRecord.setPtcMWarehouseId(tianmaOrder.getWarehouseId());
            repairOrderRecord.setPtcMWarehouseName(tianmaOrder.getWarehouseName());
            repairOrderRecord.setPtcOrderStatus(tianmaOrder.getStatus());
            repairOrderRecord.setPtcTmTradeRemark(tianmaOrder.getTradeRemark());
            repairOrderRecord.setSize1(tianmaOrder.getSize1());
            repairOrderRecord.setSize2(tianmaOrder.getSize2());
            repairOrderRecord.setNowDateTime(now);
            List<TmOrderDetail> tempList = tianmaSportHttp.getOrderDetailsById(repairOrderRecord.getPctTmOrderid());
            if(tempList!=null&&tempList.size()>0){
                TmOrderDetail detail = tempList.get(tempList.size() - 1);
                repairOrderRecord.setPtcODealDate(OcDateTimeUtils.string2LocalDateTime(detail.getDealdate()));
                repairOrderRecord.setPtcODealDescr(detail.getDealdescr());
            }
            return repairOrderRecord;
        }).collect(Collectors.toList());
        Logger.info(String.format("[%d]天内已退款的订单.size:%d", dayCount,repairOrderRecords.size()));


        //2.获取退款订单详细信息
        //  付款时间距离当前时间24小时以内（获取订单详情），订单备注为OC自动下单
        //  1)订单备注为OC自动下单
        repairOrderRecords = repairOrderRecords.parallelStream()
                .filter(repairOrderRecord -> "OC自动下单".equals(repairOrderRecord.getPtcTmTradeRemark()))
                .collect(Collectors.toList());
        Logger.info(String.format("订单备注为OC自动下单.size:%d", repairOrderRecords.size()));

        //  2)付款时间距离当前时间24小时以内（获取订单详情）
        repairOrderRecords = repairOrderRecords.parallelStream()
                .filter(repairOrderRecord -> repairOrderRecord.getPtcODealDate()!=null)
                .filter(repairOrderRecord -> repairOrderRecord.getPtcODealDate().compareTo(repairOrderRecord.getNowDateTime().minusHours(48))==1)
                .collect(Collectors.toList());

        Logger.info(String.format("付款时间距离当前时间24小时以内.size:%d", repairOrderRecords.size()));

        repairOrderRecords.parallelStream().forEach(repairOrderRecord -> {


            Optional<OcTmRepairOrderRecords> optOrm = ocTmRepairOrderRecordsManager.stream()
                    .filter(OcTmRepairOrderRecordsImpl.OUTER_ORDER_ID.equal(repairOrderRecord.getOuterOrderId()))
                    .findFirst();

            if(!optOrm.isPresent()){
                try {
                    repairOrder(repairOrderRecord);
                    repairOrderRecord.setRoStatus(TmOrderRecordStatus.SUCCESS.getValue());
                    taoBaoHttp.updateTradeMemo(
                            Long.valueOf(repairOrderRecord.getOuterOrderId()),
                            String.format("OC补单成功,天马订单ID:[%s]", repairOrderRecord.getRoTmOrderId()), 4l);
                }catch (Exception e){
                    StringWriter errors = new StringWriter();
                    e.printStackTrace(new PrintWriter(errors));
                    repairOrderRecord.setRoStatus(TmOrderRecordStatus.FAILURE.getValue());
                    repairOrderRecord.setRoFailCause(e.getMessage());
                    taoBaoHttp.updateTradeMemo(Long.valueOf(repairOrderRecord.getOuterOrderId()), String.format("OC补单失败,失败原因:%s", e.getMessage()), 5l);
                }

                //持久化数据
                OcTmRepairOrderRecordsImpl ocTmRepairOrderRecord = new OcTmRepairOrderRecordsImpl();
                ocTmRepairOrderRecord.setAddTime(LocalDateTime.now());
                ocTmRepairOrderRecord.setCustomerName(repairOrderRecord.getCustomerName());
                ocTmRepairOrderRecord.setGoodsNo(repairOrderRecord.getGoodsNo());
                ocTmRepairOrderRecord.setOuterOrderId(repairOrderRecord.getOuterOrderId());
                ocTmRepairOrderRecord.setPayPrice(repairOrderRecord.getPayPrice());
                ocTmRepairOrderRecord.setSize1(repairOrderRecord.getSize1());
                ocTmRepairOrderRecord.setSize2(repairOrderRecord.getSize2());

                //ptc
                ocTmRepairOrderRecord.setPctTmOrderId(repairOrderRecord.getPctTmOrderid());
                ocTmRepairOrderRecord.setPtcMwhId(repairOrderRecord.getPtcMWarehouseId());
                ocTmRepairOrderRecord.setPtcMwhName(repairOrderRecord.getPtcMWarehouseName());
                ocTmRepairOrderRecord.setPtcOdealDate(repairOrderRecord.getPtcODealDate());
                ocTmRepairOrderRecord.setPtcOdealDescr(repairOrderRecord.getPtcODealDescr());
                ocTmRepairOrderRecord.setPtcOrderStatus(repairOrderRecord.getPtcOrderStatus().getVal());
                ocTmRepairOrderRecord.setPtcTmTradeRemark(repairOrderRecord.getPtcTmTradeRemark());
                //ro
                ocTmRepairOrderRecord.setRoBreakEvenPrice(repairOrderRecord.getBreakEvenPrice());
                ocTmRepairOrderRecord.setRoFailCause(repairOrderRecord.getRoFailCause());
                ocTmRepairOrderRecord.setRoFreightPriceStr(repairOrderRecord.getRoFreightPriceStr());
                ocTmRepairOrderRecord.setRoOrderInfo(repairOrderRecord.getRoOrderInfo());
                ocTmRepairOrderRecord.setRoStatus(repairOrderRecord.getRoStatus());
                ocTmRepairOrderRecord.setRoTmOrderId(repairOrderRecord.getRoTmOrderId());
                ocTmRepairOrderRecord.setRoTmSizeInfoStr(JSON.toJSONString(repairOrderRecord.getRoTmSizeInfos()));
                ocTmRepairOrderRecord.setRoWhSnapshotData(JSON.toJSONString(repairOrderRecord.getRoWhSnapshotData()));
                ocTmRepairOrderRecord.setRoWhId(repairOrderRecord.getRoWhId());
                ocTmRepairOrderRecord.setRoWhInventoryCount(repairOrderRecord.getRoWhInventoryCount());
                ocTmRepairOrderRecord.setRoWhPickRate(repairOrderRecord.getRoWhPickRate());
                ocTmRepairOrderRecord.setRoWhProxyPrice(repairOrderRecord.getRoWhProxyPrice());
                ocTmRepairOrderRecord.setRoWhName(repairOrderRecord.getRoWhName());
                ocTmRepairOrderRecord.setRoWhUpdateTime(repairOrderRecord.getRoWhUpdateTime());
                ocTmRepairOrderRecord.setRoTmSkuId(repairOrderRecord.getRoTmSkuId());

                ocTmRepairOrderRecordsManager.persist(ocTmRepairOrderRecord);
            }


        });
       // System.out.println(repairOrderRecords);

    }


    private void repairOrder(RepairOrderRecord repairOrderRecord) throws Exception{
        long tid = Long.valueOf(repairOrderRecord.getOuterOrderId());
        Logger.info(String.format("开始补单-淘宝订单[%d]",
                tid
        ));

        Optional<Trade> optionalTrade = taoBaoHttp.getTaobaoTradeFullInfo(tid);
        if (!optionalTrade.isPresent()) {
            throw new OCException(String.format("淘宝订单[%d],没有找到对应的订单信息.", tid));
        }
        Trade trade = optionalTrade.get();
        if (trade.getOrders().size() > 1) {
            throw new OCException(String.format("淘宝订单[%d],子单数量大于1,不能进行下单.", tid));
        }

        Long refundId =  trade.getOrders().get(0).getRefundId();
        if(refundId!=null){
            throw new OCException(String.format("淘宝订单[%d].已经存在退款单[%d]不能进行下单.", tid,refundId));
        }

        // 3.获取根据获取到的订单编号进行天猫查询
        // 如果天猫订单为买家已付款，订单处为绿旗，备注OC下单成功
        // 则进行下一步，否则过滤。
        long sellerFlag = 3l; //绿旗
        String sellerMemo = String.format("OC下单成功,天马订单ID:[%s]",repairOrderRecord.getPctTmOrderid());
        String status = "WAIT_SELLER_SEND_GOODS"; //(等待卖家发货,即:买家已付款)
        if(!(status.equals(trade.getStatus()) && sellerFlag == trade.getSellerFlag() && sellerMemo.equals(trade.getSellerMemo()))){
            throw new OCException(String.format("淘宝订单信息不匹配!订单状态:[%s],旗帜:[%s],备注[%s].",
                    trade.getStatus(),
                    trade.getSellerFlag(),
                    trade.getSellerMemo()
            ));
        }
        // 4.获取根据获取到的订单编号进行天马再次查询
        // (防止有人工补单的特殊情况)
        // 全部订单——输入订单编号——点击查询
        // 如果只有1笔订单则进行下一步，如果有2笔及2笔以上订单则过滤。
        PageResult<TianmaOrder> prTmOrders = tianmaSportHttp.tradeOrderDataList(
                null,
                null,
                null,
                String.valueOf(tid),
                null,
                1,
                10,
                null);

        if (prTmOrders.getTotal() > 1 || prTmOrders.getRows().size() > 1) {
            throw new OCException(String.format("淘宝订单[%d],在天马中的订单大于1.(可能人工已经补单)", tid));
        }
        // 5.根据天猫订单再次下单
        // 计算补单保本价
        BigDecimal payAmount = new BigDecimal(trade.getPayment());
        repairOrderRecord.setPayPrice(payAmount);

        Logger.info(String.format("repairOrder-payAmount=[%s]", payAmount.toPlainString()));
        String ps = OrderCatConfig.getORBreakEvenPricePolicyEquation();
        Logger.info(String.format("repairOrder-getBreakEvenPricePolicyEquation=[%s]", ps));
        BigDecimal breakEvenPrice = OcBigDecimalUtils.toBreakEvenPrice(scriptEngine, payAmount,ps);
        Logger.info(String.format("repairOrder-breakEvenPrice(补单保本价)=[%s]", breakEvenPrice.toPlainString()));
        repairOrderRecord.setBreakEvenPrice(breakEvenPrice);

        //计算补单仓库
        Map<String, Object> anrtMap = tianmaSportHttp.getSearchByArticleno(repairOrderRecord.getGoodsNo());
        Map<String, TmSizeInfo> tmSizeInfoMap = (Map<String, TmSizeInfo>) anrtMap.get("sizeInfo");
        List<com.alibaba.fastjson.JSONObject> jsonObjectList = (List<com.alibaba.fastjson.JSONObject>) anrtMap.get("jsonObjectList");

        String size = repairOrderRecord.getSize1();
        TmSizeInfo tmSizeInfo = tmSizeInfoMap.get(size);
        if (tmSizeInfo == null) {
            throw new OCException(String.format("淘宝订单[%d]的尺码[%s],在天马没有找对应信息.", tid, size));
        }
        List<TmSizeInfo> tmSizeInfoList = new ArrayList();
        for (Map.Entry<String, TmSizeInfo> entry : tmSizeInfoMap.entrySet()) {
            tmSizeInfoList.add(entry.getValue());
        }


        repairOrderRecord.setRoTmSizeInfos(tmSizeInfoList);
        repairOrderRecord.setRoWhSnapshotData(jsonObjectList);
        String tmSkuId = tmSizeInfo.getTmSukId();
        repairOrderRecord.setRoTmSkuId(tmSkuId);

        Optional<ComputeWarehouseResult> optWareHouse = computeRoWarehouseId(
                repairOrderRecord,breakEvenPrice
        );

        if (!optWareHouse.isPresent()) {
            throw new OCException(String.format("淘宝订单[%d],没有计算出仓库信息.请人工处理.", tid, size));
        }

        ComputeWarehouseResult warehouseResult = optWareHouse.get();


        repairOrderRecord.setRoWhId(Integer.valueOf(warehouseResult.getWarehouseId()));
        repairOrderRecord.setRoWhName(warehouseResult.getWarehouseName());
        repairOrderRecord.setRoWhPickRate(Integer.valueOf(warehouseResult.getPickRate()));
        repairOrderRecord.setRoWhInventoryCount(Integer.valueOf(warehouseResult.getInventoryCount()));
        repairOrderRecord.setRoWhProxyPrice(warehouseResult.getProxyPrice());
        repairOrderRecord.setRoWhUpdateTime(OcDateTimeUtils.string2LocalDateTime(warehouseResult.getWhUpdateTime()));

        //支付密码
        String payPwd1 = OcEncryptionUtils.base64Decoder(OrderCatConfig.getOrderOperateTmPayPwd(), 5);
        //天马下单+支付
        Map<String, String> requestMap = tmsportOrderAndPay(
                tid,
                trade,
                anrtMap,
                warehouseResult.getWarehouseId(),
                payPwd1,
                "OC自动补单"
        );
        repairOrderRecord.setRoTmOrderId(requestMap.get("TmOrderId"));
        repairOrderRecord.setRoFreightPriceStr(requestMap.get("FreightPriceStr"));
        repairOrderRecord.setRoOrderInfo(JSON.toJSONString(requestMap));

    }





    public void setOcTmOrderRecordsManager(OcTmOrderRecordsManager ocTmOrderRecordsManager) {
        this.ocTmOrderRecordsManager = ocTmOrderRecordsManager;
    }

    public void setOcParamsManager(OcParamsManager ocParamsManager) {
        this.ocParamsManager = ocParamsManager;
    }


    public void setOcTmRepairOrderRecordsManager(OcTmRepairOrderRecordsManager ocTmRepairOrderRecordsManager) {
        this.ocTmRepairOrderRecordsManager = ocTmRepairOrderRecordsManager;
    }
}
