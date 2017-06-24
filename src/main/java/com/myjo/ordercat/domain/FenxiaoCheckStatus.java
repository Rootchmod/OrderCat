package com.myjo.ordercat.domain;

public enum FenxiaoCheckStatus {
    NOT_FENXIAO_REFUND("NOT_FENXIAO_REFUND"),
    STATUS_ERROR_FENXIAO_REFUND("STATUS_ERROR_FENXIAO_REFUND"),
    NOT_FENXIAO("NOT_FENXIAO"),
    SUCCESS_REFUND("SUCCESS_REFUND");


    private String v;

    FenxiaoCheckStatus(String v){
        this.v = v;
    }

    public String getValue() {
        return v;
    }

}
