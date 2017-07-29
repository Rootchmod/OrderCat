package com.myjo.ordercat.domain;

public enum AsRefundCheckStatus {
    DZ_FAILURE("DZ_FAILURE"),//对账失败
    DZ_SUCCESS("DZ_SUCCESS");//对账成功
    private String v;

    AsRefundCheckStatus(String v) {
        this.v = v;
    }

    public String getValue() {
        return v;
    }

}
