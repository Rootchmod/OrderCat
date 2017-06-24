package com.myjo.ordercat.domain;

/**
 * Created by lee5hx on 17/6/16.
 */
public enum TmOrderRecordStatus {
    FAILURE("FAILURE"),//失败
    SUCCESS("SUCCESS");//成功
    private String v;

    TmOrderRecordStatus(String v) {
        this.v = v;
    }

    public String getValue() {
        return v;
    }
}
