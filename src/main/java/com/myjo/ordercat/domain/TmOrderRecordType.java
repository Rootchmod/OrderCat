package com.myjo.ordercat.domain;

/**
 * Created by lee5hx on 17/6/16.
 */
public enum TmOrderRecordType {
    MACHINE("MACHINE"),//机器
    MANUAL("MANUAL");//人工
    private String v;

    TmOrderRecordType(String v) {
        this.v = v;
    }

    public String getValue() {
        return v;
    }
}
