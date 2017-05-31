package com.myjo.ordercat.domain;

/**
 * Created by lee5hx on 17/5/28.
 */
public class FocusWHInfoReplace {
    private Integer warehouseId;
    private String warehouseName;
    private Integer pickRate;

    public Integer getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(Integer warehouseId) {
        this.warehouseId = warehouseId;
    }

    public String getWarehouseName() {
        return warehouseName;
    }

    public void setWarehouseName(String warehouseName) {
        this.warehouseName = warehouseName;
    }

    public Integer getPickRate() {
        return pickRate;
    }

    public void setPickRate(Integer pickRate) {
        this.pickRate = pickRate;
    }
}
