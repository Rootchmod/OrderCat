package com.myjo.ordercat.domain;

/**
 * Created by lee5hx on 17/4/26.
 */
public enum TianmaOrderStatus {
    PAYMENT_HAS_BEEN("10"),//已付款
    FEEDBACK_SUCCESS("50"),//反馈成功
    RETURNED("80"),//已退货
    REFUNDED("100");//已退款
    private String val;
    TianmaOrderStatus(String val){
        this.val = val;
    }

    public String getVal() {
        return val;
    }

    public void setVal(String val) {
        this.val = val;
    }
}
