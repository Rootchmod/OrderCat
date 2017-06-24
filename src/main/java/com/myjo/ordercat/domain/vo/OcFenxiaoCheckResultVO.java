package com.myjo.ordercat.domain.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by lee5hx on 17/6/14.
 */
public class OcFenxiaoCheckResultVO {

    private long id;
    private Long tid;
    private String orderStatus;
    private Long refundId;
    private Long numIid;
    private String title;
    private Long fenxiaoId;
    private String supplierNick;
    private String distributorNick;
    private String fenxiaoRefundStatus;
    private BigDecimal fenxiaoRefundFee;
    private BigDecimal fenxiaoPaySupFee;
    private String fenxiaoRefundDesc;
    private String fenxiaoRefundReason;
    private String status;
    private String remarks;
    private Date addTime;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Long getTid() {
        return tid;
    }

    public void setTid(Long tid) {
        this.tid = tid;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public Long getRefundId() {
        return refundId;
    }

    public void setRefundId(Long refundId) {
        this.refundId = refundId;
    }

    public Long getNumIid() {
        return numIid;
    }

    public void setNumIid(Long numIid) {
        this.numIid = numIid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getFenxiaoId() {
        return fenxiaoId;
    }

    public void setFenxiaoId(Long fenxiaoId) {
        this.fenxiaoId = fenxiaoId;
    }

    public String getSupplierNick() {
        return supplierNick;
    }

    public void setSupplierNick(String supplierNick) {
        this.supplierNick = supplierNick;
    }

    public String getDistributorNick() {
        return distributorNick;
    }

    public void setDistributorNick(String distributorNick) {
        this.distributorNick = distributorNick;
    }

    public String getFenxiaoRefundStatus() {
        return fenxiaoRefundStatus;
    }

    public void setFenxiaoRefundStatus(String fenxiaoRefundStatus) {
        this.fenxiaoRefundStatus = fenxiaoRefundStatus;
    }

    public BigDecimal getFenxiaoRefundFee() {
        return fenxiaoRefundFee;
    }

    public void setFenxiaoRefundFee(BigDecimal fenxiaoRefundFee) {
        this.fenxiaoRefundFee = fenxiaoRefundFee;
    }

    public BigDecimal getFenxiaoPaySupFee() {
        return fenxiaoPaySupFee;
    }

    public void setFenxiaoPaySupFee(BigDecimal fenxiaoPaySupFee) {
        this.fenxiaoPaySupFee = fenxiaoPaySupFee;
    }

    public String getFenxiaoRefundDesc() {
        return fenxiaoRefundDesc;
    }

    public void setFenxiaoRefundDesc(String fenxiaoRefundDesc) {
        this.fenxiaoRefundDesc = fenxiaoRefundDesc;
    }

    public String getFenxiaoRefundReason() {
        return fenxiaoRefundReason;
    }

    public void setFenxiaoRefundReason(String fenxiaoRefundReason) {
        this.fenxiaoRefundReason = fenxiaoRefundReason;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    @JsonSerialize(using = DateTimeSerializer.class)
    public Date getAddTime() {
        return addTime;
    }

    public void setAddTime(Date addTime) {
        this.addTime = addTime;
    }
}
