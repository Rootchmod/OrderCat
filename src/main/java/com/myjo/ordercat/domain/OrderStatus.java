package com.myjo.ordercat.domain;

/**
 * Created by lee5hx on 17/4/26.
 */
public enum OrderStatus {
    FEEDBACK_SUCCESS("50");
    private String val;
    OrderStatus(String val){
        this.val = val;
    }

    public String getVal() {
        return val;
    }

    public void setVal(String val) {
        this.val = val;
    }
}
