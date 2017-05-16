package com.myjo.ordercat.domain;

/**
 * Created by lee5hx on 17/5/10.
 */
public class TianmaOrder {


    //订单编码 outer_order_id
    private String outerOrderId;
    //订单ID order_id
    private String orderId;
    //p_delivery_no
    private String deliveryNo;
    //收件人
    private String name;
    //delivery
    private String deliveryName;
    //no_shipment_remark
    private String noShipmentRemark;

    //订单日期
    private String created;

    //feed_back_time 反馈日期
    private String feedBackTime;


    public String getOuterOrderId() {
        return outerOrderId;
    }

    public void setOuterOrderId(String outerOrderId) {
        this.outerOrderId = outerOrderId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getDeliveryNo() {
        return deliveryNo;
    }

    public void setDeliveryNo(String deliveryNo) {
        this.deliveryNo = deliveryNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDeliveryName() {
        return deliveryName;
    }

    public void setDeliveryName(String deliveryName) {
        this.deliveryName = deliveryName;
    }

    public String getNoShipmentRemark() {
        return noShipmentRemark;
    }

    public void setNoShipmentRemark(String noShipmentRemark) {
        this.noShipmentRemark = noShipmentRemark;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getFeedBackTime() {
        return feedBackTime;
    }

    public void setFeedBackTime(String feedBackTime) {
        this.feedBackTime = feedBackTime;
    }
}
