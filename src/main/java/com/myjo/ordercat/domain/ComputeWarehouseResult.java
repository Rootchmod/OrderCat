package com.myjo.ordercat.domain;

import java.math.BigDecimal;

/**
 * Created by lee5hx on 2017/7/7.
 */
public class ComputeWarehouseResult {


    private String warehouseId;
    private String warehouseName;
    private String pickRate;
    private BigDecimal proxyPrice;
    private String whUpdateTime;
    private String inventoryCount;

    public String getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(String warehouseId) {
        this.warehouseId = warehouseId;
    }

    public String getInventoryCount() {
        return inventoryCount;
    }

    public void setInventoryCount(String inventoryCount) {
        this.inventoryCount = inventoryCount;
    }

    public String getWarehouseName() {
        return warehouseName;
    }

    public void setWarehouseName(String warehouseName) {
        this.warehouseName = warehouseName;
    }

    public String getPickRate() {
        return pickRate;
    }

    public void setPickRate(String pickRate) {
        this.pickRate = pickRate;
    }

    public BigDecimal getProxyPrice() {
        return proxyPrice;
    }

    public void setProxyPrice(BigDecimal proxyPrice) {
        this.proxyPrice = proxyPrice;
    }

    public String getWhUpdateTime() {
        return whUpdateTime;
    }

    public void setWhUpdateTime(String whUpdateTime) {
        this.whUpdateTime = whUpdateTime;
    }


    //private String
}
