package com.myjo.ordercat.domain;

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
}
