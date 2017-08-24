package com.myjo.ordercat.domain.constant;

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
    FEEDBACK_FAILURE("60"),//反馈失败
    WAITING_RETURN("70"),//待退货
    RETURNED("80"),//已退货
    WAITING_REFUNDED("90"),//待退款
    REFUNDED("100");//已退款

    //已付款 配货中 反馈成功
    public final static TianmaOrderStatus[] CHECK_TM_ORDER_STATUS = {
            PAYMENT_HAS_BEEN,//已付款
            PICKING_30, PICKING_40,//配货中
            FEEDBACK_SUCCESS
    };
    private final static TianmaOrderStatus[] TIANMA_ORDER_STATUS_ARR = TianmaOrderStatus.values();
    private String val;

    TianmaOrderStatus(String val) {
        this.val = val;
    }

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

    public static String valueOf(TianmaOrderStatus status) {
        String tianmaOrderStatus = null;
        switch (status) {
            case WAITING_PAYMENT:
                tianmaOrderStatus = "0-待付款";
                break;
            case PAYMENT_HAS_BEEN:
                tianmaOrderStatus = "10-已付款";
                break;
            case NIL20:
                tianmaOrderStatus = "20-nil";
                break;
            case PICKING_30:
                tianmaOrderStatus = "30-配货中";
                break;
            case PICKING_40:
                tianmaOrderStatus = "40-配货中";
                break;
            case FEEDBACK_SUCCESS:
                tianmaOrderStatus = "50-反馈成功";
                break;
            case FEEDBACK_FAILURE:
                tianmaOrderStatus = "60-反馈失败";
                break;
            case WAITING_RETURN:
                tianmaOrderStatus = "70-待退货";
                break;
            case RETURNED:
                tianmaOrderStatus = "80-已退货";
                break;
            case WAITING_REFUNDED:
                tianmaOrderStatus = "90-代退款";
                break;
            case REFUNDED:
                tianmaOrderStatus = "100-已退款";
                break;
            default:
                break;

        }


        return tianmaOrderStatus;
    }

    public String getVal() {
        return val;
    }

    public void setVal(String val) {
        this.val = val;
    }
}
