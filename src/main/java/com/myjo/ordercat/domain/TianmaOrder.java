package com.myjo.ordercat.domain;

import com.myjo.ordercat.domain.constant.TianmaOrderStatus;

import java.math.BigDecimal;

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

    private String size1;
    private String size2;

    private Integer warehouseId;
    private String warehouseName;

    private BigDecimal payPrice;

    private BigDecimal postFee;

    private TianmaOrderStatus status;

    //订单日期
    private String created;

    //feed_back_time 反馈日期
    private String feedBackTime;


    private String goodsNo;

    private String tid;



    private String marketPrice;
    private String discount;

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


    public String getSize1() {
        return size1;
    }

    public void setSize1(String size1) {
        this.size1 = size1;
    }

    public String getSize2() {
        return size2;
    }

    public void setSize2(String size2) {
        this.size2 = size2;
    }

    public Integer getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(Integer warehouseId) {
        this.warehouseId = warehouseId;
    }

    public String getWarehouseName() {
        return warehouseName;
    }

    public void setWarehouseName(String warehouseName) {
        this.warehouseName = warehouseName;
    }

    public BigDecimal getPayPrice() {
        return payPrice;
    }

    public void setPayPrice(BigDecimal payPrice) {
        this.payPrice = payPrice;
    }

    public BigDecimal getPostFee() {
        return postFee;
    }

    public void setPostFee(BigDecimal postFee) {
        this.postFee = postFee;
    }

    public TianmaOrderStatus getStatus() {
        return status;
    }

    public void setStatus(TianmaOrderStatus status) {
        this.status = status;
    }

    public String getGoodsNo() {
        return goodsNo;
    }

    public void setGoodsNo(String goodsNo) {
        this.goodsNo = goodsNo;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }


    public String getMarketPrice() {
        return marketPrice;
    }

    public void setMarketPrice(String marketPrice) {
        this.marketPrice = marketPrice;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }
}



