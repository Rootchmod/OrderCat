package com.myjo.ordercat.domain;

/**
 * Created by lee5hx on 2017/7/7.
 */
public class NotOrderWareHousePolicy {

    private String warehouseId ;//= "",reason ="此仓库需要劲浪下单"
    private String reason;

    public String getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(String warehouseId) {
        this.warehouseId = warehouseId;
    }



    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }


    //private String
}
