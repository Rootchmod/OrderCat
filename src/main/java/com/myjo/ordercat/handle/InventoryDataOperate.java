package com.myjo.ordercat.handle;

import com.myjo.ordercat.config.OrderCatConfig;
import com.myjo.ordercat.domain.InventoryInfo;
import com.myjo.ordercat.domain.PickRateDelCondition;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by lee5hx on 17/4/24.
 */
public class InventoryDataOperate {


    private static final Logger Logger = LogManager.getLogger(InventoryDataOperate.class);


    /**
     * 库存信息文件中根据仓库名称进行去重,得出去重过后的仓库列表
     *
     * @param list
     * @return
     */
    public static List<InventoryInfo> distinctWarehouseList(List<InventoryInfo> list) {
        List<InventoryInfo> distinctWarehouseList = list.parallelStream()
                .filter(inventoryInfo -> !inventoryInfo.getDiscount().equals("折扣"))
                //.filter(InventoryInfo.distinctByField(inventoryInfo -> inventoryInfo.getGoodsNo()))
                .filter(InventoryInfo.distinctByField(inventoryInfo -> inventoryInfo.getWarehouseName()))
                .collect(Collectors.toList());
        return distinctWarehouseList;
    }

    private static Integer getWareHouseQuarter(Map<String, Integer> quarterMap, String goodsNo, Integer wareHouseID,String size1) {
        return quarterMap.get(goodsNo + ":" + wareHouseID +":"+size1);
    }

    private static boolean filterPickRate(InventoryInfo inventoryInfo, Map<String, Integer> quarterMap, PickRateDelCondition pickRateDelCondition) {
        boolean rt = true;
        if (inventoryInfo.getPickRate() >= pickRateDelCondition.getLlPickRate() &&
                inventoryInfo.getPickRate() <= pickRateDelCondition.getUlPickRate()) {
            if (getWareHouseQuarter(quarterMap, inventoryInfo.getGoodsNo(), inventoryInfo.getWareHouseID(),inventoryInfo.getSize1())
                    > pickRateDelCondition.getRepertory()) {//库存大于指定库存数在保留
                rt = true;
            } else {
                rt = false;
            }
        }
        return rt;
    }

    public static List<InventoryInfo> filterPickRateList(List<InventoryInfo> intersectionList,Map<String, Integer> quarterMap){

        for (PickRateDelCondition pickRateDelCondition : OrderCatConfig.getPickRateDelConditions()) {
            Logger.info(String.format("配货率在[%d]-[%d]百分比,并且库存小于等于[%d]进行删除.",
                    pickRateDelCondition.getLlPickRate(),
                    pickRateDelCondition.getUlPickRate(),
                    pickRateDelCondition.getRepertory()));

            intersectionList = intersectionList.parallelStream().
                    filter(inventoryInfo -> filterPickRate(inventoryInfo, quarterMap, pickRateDelCondition))
                    .collect(Collectors.toList());

            Logger.info(String.format("配货率在[%d]-[%d]百分比,并且库存小于等于[%d],进行删除后的记录数为:[%d]",
                    pickRateDelCondition.getLlPickRate(),
                    pickRateDelCondition.getUlPickRate(),
                    pickRateDelCondition.getRepertory(),
                    intersectionList.size()));
        }
        return intersectionList;
    }

//    /**
//     * 计算高于平均采购价
//     *
//     * @param inventoryInfo
//     * @param avgPriceMap
//     * @return
//     */
//    private static boolean filterAvgPriceAbove(InventoryInfo inventoryInfo, Map<String, Double> avgPriceMap) {
//        boolean rt;
//        BigDecimal avgPrice = getAvgPrice(avgPriceMap, inventoryInfo.getGoodsNo());
//        avgPrice = avgPrice.add(avgPrice.multiply(BigDecimal.valueOf(OrderCatConfig.getAvgPriceAboveRate() / 100)));
//        if (inventoryInfo.getProxyPrice().compareTo(avgPrice) == 1) {
//            rt = false;
//        } else {
//            rt = true;
//        }
//        return rt;
//    }

    public static BigDecimal getAvgPrice(Map<String, Double> avgPriceMap, String goodsNo) {

        if (avgPriceMap.get(goodsNo) != null) {
            return BigDecimal.valueOf(avgPriceMap.get(goodsNo));
        } else {
            return BigDecimal.ZERO;
        }
    }

    public static List<InventoryInfo> filterAvgPriceList(List<InventoryInfo> intersectionList){
        intersectionList = intersectionList.parallelStream()
                .filter(inventoryInfo -> inventoryInfo.getProxyPrice().compareTo(inventoryInfo.getAvgPrice()) < 1)
                .collect(Collectors.toList());
        return intersectionList;
    }





}
