package com.myjo.ordercat.domain;

import com.myjo.ordercat.domain.constant.RefundOperateType;

/**
 * Created by lee5hx on 2017/8/12.
 */
public class RefundOperateRecord {

    private Long refundId;
    private Long tid;
    private String status;
    private String reason;
    private Boolean isDaixiao;
    private String operateDetail;
    private String sid;
    private String companyName;
    private RefundOperateType operateType;

    private String refundAmount;
    private Long refundVersion;
    private String refundPhase;


    private String operateResult;
    private Boolean isPersist;

    private String tmOrderId;

    private String warehouseName;
    private String delivery;
    private String marketPrice;
    private String discount;



    private Long purchaseOrderId;
    private String purchaseOrderStatus;
    private String purchaseBuyerPayment;



    public Long getRefundId() {
        return refundId;
    }

    public void setRefundId(Long refundId) {
        this.refundId = refundId;
    }

    public Long getTid() {
        return tid;
    }

    public void setTid(Long tid) {
        this.tid = tid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Boolean getDaixiao() {
        return isDaixiao;
    }

    public void setDaixiao(Boolean daixiao) {
        isDaixiao = daixiao;
    }

    public String getOperateDetail() {
        return operateDetail;
    }

    public void setOperateDetail(String operateDetail) {
        this.operateDetail = operateDetail;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public RefundOperateType getOperateType() {
        return operateType;
    }

    public void setOperateType(RefundOperateType operateType) {
        this.operateType = operateType;
    }

    public String getOperateResult() {
        return operateResult;
    }

    public void setOperateResult(String operateResult) {
        this.operateResult = operateResult;
    }


    public Boolean getPersist() {
        return isPersist;
    }

    public void setPersist(Boolean persist) {
        isPersist = persist;
    }

    public String getRefundAmount() {
        return refundAmount;
    }

    public void setRefundAmount(String refundAmount) {
        this.refundAmount = refundAmount;
    }

    public Long getRefundVersion() {
        return refundVersion;
    }

    public void setRefundVersion(Long refundVersion) {
        this.refundVersion = refundVersion;
    }

    public String getRefundPhase() {
        return refundPhase;
    }

    public void setRefundPhase(String refundPhase) {
        this.refundPhase = refundPhase;
    }

    public String getTmOrderId() {
        return tmOrderId;
    }

    public void setTmOrderId(String tmOrderId) {
        this.tmOrderId = tmOrderId;
    }

    public String getWarehouseName() {
        return warehouseName;
    }

    public void setWarehouseName(String warehouseName) {
        this.warehouseName = warehouseName;
    }

    public String getDelivery() {
        return delivery;
    }

    public void setDelivery(String delivery) {
        this.delivery = delivery;
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


    public Long getPurchaseOrderId() {
        return purchaseOrderId;
    }

    public void setPurchaseOrderId(Long purchaseOrderId) {
        this.purchaseOrderId = purchaseOrderId;
    }

    public String getPurchaseOrderStatus() {
        return purchaseOrderStatus;
    }

    public void setPurchaseOrderStatus(String purchaseOrderStatus) {
        this.purchaseOrderStatus = purchaseOrderStatus;
    }

    public String getPurchaseBuyerPayment() {
        return purchaseBuyerPayment;
    }

    public void setPurchaseBuyerPayment(String purchaseBuyerPayment) {
        this.purchaseBuyerPayment = purchaseBuyerPayment;
    }
}
