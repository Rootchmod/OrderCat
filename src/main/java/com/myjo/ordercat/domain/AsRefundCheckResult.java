package com.myjo.ordercat.domain;

/**
 * Created by lee5hx on 17/5/22.
 */
public class AsRefundCheckResult {


    private Long refundId;
    private Long tid;
    private Boolean isDaixiao;
    private String buyerNick;
    private Integer ordersCount;
    private Integer num;
    private String refundStatus;
    private String refundFee;
    private String totalFee;
    private String refundPhase;




    private AsRefundCheckStatus dzStatus; //对账状态

    private String failureReason;




    public AsRefundCheckStatus getDzStatus() {
        return dzStatus;
    }

    public void setDzStatus(AsRefundCheckStatus dzStatus) {
        this.dzStatus = dzStatus;
    }


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

    public Boolean getIsDaixiao() {
        return isDaixiao;
    }

    public void setIsDaixiao(Boolean daixiao) {
        isDaixiao = daixiao;
    }

    public String getBuyerNick() {
        return buyerNick;
    }

    public void setBuyerNick(String buyerNick) {
        this.buyerNick = buyerNick;
    }

    public Integer getOrdersCount() {
        return ordersCount;
    }

    public void setOrdersCount(Integer ordersCount) {
        this.ordersCount = ordersCount;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public String getRefundFee() {
        return refundFee;
    }

    public void setRefundFee(String refundFee) {
        this.refundFee = refundFee;
    }

    public String getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(String totalFee) {
        this.totalFee = totalFee;
    }

    public String getRefundStatus() {
        return refundStatus;
    }

    public void setRefundStatus(String refundStatus) {
        this.refundStatus = refundStatus;
    }


    public String getFailureReason() {
        return failureReason;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }

    public String getRefundPhase() {
        return refundPhase;
    }

    public void setRefundPhase(String refundPhase) {
        this.refundPhase = refundPhase;
    }
}
