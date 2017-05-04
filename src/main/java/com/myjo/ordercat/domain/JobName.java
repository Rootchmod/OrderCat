package com.myjo.ordercat.domain;

/**
 *
 */
public enum JobName {
    SYNC_WAREHOUSE_JOB("SyncWarehouseJob"),
    SYNC_TAOBAO_INVENTORY_JOB("SyncTaoBaoInventoryJob"),//FAILURE
    SYNC_SALES_INFO_JOB("SyncSalesInfoJob");//RUNNING

    private String v;

    JobName(String v){
        this.v = v;
    }

    public String getValue() {
        return v;
    }

}
