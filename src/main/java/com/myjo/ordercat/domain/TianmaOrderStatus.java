package com.myjo.ordercat.domain;

/**
 * Created by lee5hx on 17/4/26.
 */
public enum TianmaOrderStatus {

    WAITING_PAYMENT("0"),//待付款
    PAYMENT_HAS_BEEN("10"),//已付款
    NIL20("20"),//
    PICKING_30("30"),//配货中30
    PICKING_40("40"),//配货中40
    FEEDBACK_SUCCESS("50"),//反馈成功
    FEEDBACK_FAILURE("60"),//已退货
    WAITING_RETURN("70"),//待退货
    RETURNED("80"),//已退货
    WAITING_REFUNDED("90"),//代退款
    REFUNDED("100");//已退款

    private String val;

    TianmaOrderStatus(String val){
        this.val = val;
    }

    private final static TianmaOrderStatus[] TIANMA_ORDER_STATUS_ARR = TianmaOrderStatus.values();

    public static TianmaOrderStatus valueOf1(String value) {
        TianmaOrderStatus tianmaOrderStatus = null;
        for (TianmaOrderStatus fa : TIANMA_ORDER_STATUS_ARR) {
            if (fa.getVal().equals(value)) {
                tianmaOrderStatus = fa;
                break;
            }
        }
        return tianmaOrderStatus;
    }

    //已付款 配货中 反馈成功
    public final static TianmaOrderStatus[] CHECK_TM_ORDER_STATUS = {
            PAYMENT_HAS_BEEN,//已付款
            PICKING_30,PICKING_40,//配货中
            FEEDBACK_SUCCESS
    };


    public String getVal() {
        return val;
    }

    public void setVal(String val) {
        this.val = val;
    }
}
