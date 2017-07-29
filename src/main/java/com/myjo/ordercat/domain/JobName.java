package com.myjo.ordercat.domain;

public enum JobName {
    SYNC_WAREHOUSE_JOB("SyncWarehouseJob"),
    SYNC_TAOBAO_INVENTORY_JOB("SyncTaoBaoInventoryJob"),
    SYNC_SALES_INFO_JOB("SyncSalesInfoJob"),
    SYNC_LC_JOB("SyncLcJob"),
    AUTO_SEND_GOODS_JOB("AutoSendGoodsJob"),
    //FENXIAO_ACCOUNT_CHECK_JOB("FenxiaoAccountCheckJob"),
    AS_REFUND_ACCOUNT_CHECK_JOB("AsRefundAccountCheckJob"),
    TIANMA_ACCOUNT_CHECK_JOB("TianmaAccountCheckJob");

    private String v;

    JobName(String v){
        this.v = v;
    }

    public String getValue() {
        return v;
    }

}
