package com.myjo.ordercat.handle;

import com.myjo.ordercat.domain.InventoryInfo;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by lee5hx on 17/4/24.
 */
public class InventoryDataOperate {


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
}
