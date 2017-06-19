package com.myjo.ordercat.handle;

import com.alibaba.fastjson.JSON;
import com.myjo.ordercat.domain.*;
import com.myjo.ordercat.exception.OCException;
import com.myjo.ordercat.http.TaoBaoHttp;
import com.myjo.ordercat.http.TianmaSportHttp;
import com.myjo.ordercat.spm.ordercat.ordercat.oc_tianma_check_result.OcTianmaCheckResultManager;
import com.myjo.ordercat.spm.ordercat.ordercat.oc_tm_order_records.OcTmOrderRecords;
import com.myjo.ordercat.spm.ordercat.ordercat.oc_tm_order_records.OcTmOrderRecordsImpl;
import com.myjo.ordercat.spm.ordercat.ordercat.oc_tm_order_records.OcTmOrderRecordsManager;
import com.myjo.ordercat.utils.OcStringUtils;
import com.taobao.api.domain.Order;
import com.taobao.api.domain.Trade;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Created by lee5hx on 17/6/13.
 */
public class OrderOperate {

    private static final Logger Logger = LogManager.getLogger(OrderOperate.class);

    private TianmaSportHttp tianmaSportHttp;

    private TaoBaoHttp taoBaoHttp;

    private OcTmOrderRecordsManager ocTmOrderRecordsManager;

    public OrderOperate(TianmaSportHttp tianmaSportHttp, TaoBaoHttp taoBaoHttp) {
        this.tianmaSportHttp = tianmaSportHttp;
        this.taoBaoHttp = taoBaoHttp;
    }

    /**
     * 手工下单
     *
     * @param tid
     */
    public void manualOrder(
            long tid,
            String wareHouseId,
            String machineCid,
            String whSnapshotData) {
        Logger.info(String.format("开始执行下单-淘宝订单[%d],仓库ID[%s]",
                tid,
                wareHouseId
        ));

        Optional<OcTmOrderRecords> obj = ocTmOrderRecordsManager.stream()
                .filter(OcTmOrderRecords.TID.equal(String.valueOf(tid)).and(OcTmOrderRecords.STATUS.equal(TmOrderRecordStatus.SUCCESS.getValue())))
                .findAny();
        if(obj.isPresent()){
            throw new OCException(String.format("淘宝订单[%d],已经下过订单.禁止重复下单", tid));
        }


        OcTmOrderRecords ocTmOrderRecords = new OcTmOrderRecordsImpl();
        ocTmOrderRecords.setTid(String.valueOf(tid));

        if(machineCid==null){
            ocTmOrderRecords.setType(TmOrderRecordType.MANUAL.getValue());
        }else {
            ocTmOrderRecords.setType(TmOrderRecordType.MACHINE.getValue());
            ocTmOrderRecords.setMachineCid(machineCid);
            ocTmOrderRecords.setWhSnapshotData(whSnapshotData);
        }
        try {

            Map<String, String> requestMap = new HashMap<>();
            Trade trade;
            //获取淘宝订单
            Optional<Trade> optionalTrade = taoBaoHttp.getTaobaoTradeFullInfo(tid);
            if (optionalTrade.isPresent()) {
                trade = optionalTrade.get();

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
                requestMap.put("remark", "");
                requestMap.put("province", trade.getReceiverState());
                requestMap.put("city", trade.getReceiverCity());
                requestMap.put("area", trade.getReceiverDistrict());
                requestMap.put("outer_tid", "lee5hx-" + String.valueOf(tid));
                List<TmArea> list = tianmaSportHttp.getArea("0");
                String province_id = getPidInAreas(list, trade.getReceiverState());
                list = tianmaSportHttp.getArea(province_id);
                String city_id = getPidInAreas(list, trade.getReceiverCity());
                list = tianmaSportHttp.getArea(city_id);
                String area_id = getPidInAreas(list, trade.getReceiverDistrict());
                requestMap.put("province_id", province_id);
                requestMap.put("city_id", city_id);
                requestMap.put("area_id", area_id);




                Map<String, Object> anrtMap = tianmaSportHttp.getSearchByArticleno(articleno);
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
                //支持顺丰快递则判断顺丰运费是否＜25元，如果＜25元则选择顺丰快递。
                //如果≥25元则还是选择默认快递。@lee5hx
                List<TmPostage> postages = tianmaSportHttp.getPostage(ii.getWarehouseName(), trade.getReceiverState(), weight);
                Optional<TmPostage> optionalTmPostage = postages.parallelStream()
                        .filter(o -> o.getExpressName().indexOf("到付") < 0)
                        .filter(o -> o.getExpressName().indexOf("顺丰") > -1
                                && o.getKdCost().compareTo(new BigDecimal("25")) < 0)
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
                Logger.info(String.format("orderBooking下单-rt[%s}",rt));



                //todo 订单付款 lee5hx

                //tianmaSportHttp.updataBalance();



                ocTmOrderRecords.setStatus(TmOrderRecordStatus.SUCCESS.getValue());
                ocTmOrderRecords.setOrderInfo(JSON.toJSONString(requestMap));

            } else {
                throw new OCException(String.format("淘宝订单[%d],没有找到对应的订单信息.", tid));
            }
        } catch (Exception e) {

            ocTmOrderRecords.setStatus(TmOrderRecordStatus.FAILURE.getValue());
            ocTmOrderRecords.setFailCause(e.getMessage());
            Logger.error(e);
        }

        ocTmOrderRecords.setAddTime(LocalDateTime.now());
        ocTmOrderRecordsManager.persist(ocTmOrderRecords);

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



    /**
     * 自动下单
     */
    public void autoOrder() {

    }

    public void setOcTmOrderRecordsManager(OcTmOrderRecordsManager ocTmOrderRecordsManager) {
        this.ocTmOrderRecordsManager = ocTmOrderRecordsManager;
    }
}
