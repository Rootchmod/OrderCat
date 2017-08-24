package com.myjo.ordercat.domain.constant;

/**
 * Created by lee5hx on 17/6/16.
 */
public enum RefundOperateType {
    TM_OP_YFKQX("TM_OP_YFKQX"),//天马操作已付款取消
    TM_OP_WCZ("TM_OP_WCZ"),//天马没有操作
    TM_OP_SQXG_QXDD("TM_OP_SQXG_QXDD"),//申请修改-取消订单
    TM_OP_SQSH("TM_OP_SQSH"),//申请售后
    TM_OP_HTKDDH("TM_OP_HTKDDH"),//回填快递单号
    TB_OP_TYTK("TB_OP_TYTK"),//同意退款
    GX_OP_SQTK("GX_OP_SQTK");//申请退款

    private String v;



    private final static RefundOperateType[] REFUND_OPERATE_TYPES = RefundOperateType.values();

    RefundOperateType(String v) {
        this.v = v;
    }

    public String getValue() {
        return v;
    }

    public static RefundOperateType valueOfs(String value) {

        RefundOperateType refundOperateType = null;
        for (RefundOperateType rot : REFUND_OPERATE_TYPES) {
            if (rot.getValue().equals(value)) {
                refundOperateType = rot;
                break;
            }
        }
        return refundOperateType;
    }

}
