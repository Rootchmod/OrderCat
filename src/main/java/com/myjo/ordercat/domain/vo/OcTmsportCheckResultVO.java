package com.myjo.ordercat.domain.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * Created by lee5hx on 17/6/14.
 */
public class OcTmsportCheckResultVO {

    private long id;
    private String tmOuterOrderId;
    private Long tmOrderNum;
    private Long tmNum;
    private Long tbOrderNum;
    private Long tbNum;

    private Date tbCreated;
    private Date tbPaytime;
    private BigDecimal tbPrice;
    private BigDecimal tbPayment;
    private BigDecimal tbDiscountFee;
    private BigDecimal tbTotalFee;
    private String dzStatus;
    private String dzDetailsMessage;
    private String remarks;
    private Date addTime;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTmOuterOrderId() {
        return tmOuterOrderId;
    }

    public void setTmOuterOrderId(String tmOuterOrderId) {
        this.tmOuterOrderId = tmOuterOrderId;
    }

    public Long getTmOrderNum() {
        return tmOrderNum;
    }

    public void setTmOrderNum(Long tmOrderNum) {
        this.tmOrderNum = tmOrderNum;
    }

    public Long getTmNum() {
        return tmNum;
    }

    public void setTmNum(Long tmNum) {
        this.tmNum = tmNum;
    }

    public Long getTbOrderNum() {
        return tbOrderNum;
    }

    public void setTbOrderNum(Long tbOrderNum) {
        this.tbOrderNum = tbOrderNum;
    }

    public Long getTbNum() {
        return tbNum;
    }

    public void setTbNum(Long tbNum) {
        this.tbNum = tbNum;
    }

    @JsonSerialize(using = DateTimeSerializer.class)
    public Date getTbCreated() {
        return tbCreated;
    }

    public void setTbCreated(Date tbCreated) {
        this.tbCreated = tbCreated;
    }

    @JsonSerialize(using = DateTimeSerializer.class)
    public Date getTbPaytime() {
        return tbPaytime;
    }

    public void setTbPaytime(Date tbPaytime) {
        this.tbPaytime = tbPaytime;
    }

    public BigDecimal getTbPrice() {
        return tbPrice;
    }

    public void setTbPrice(BigDecimal tbPrice) {
        this.tbPrice = tbPrice;
    }

    public BigDecimal getTbPayment() {
        return tbPayment;
    }

    public void setTbPayment(BigDecimal tbPayment) {
        this.tbPayment = tbPayment;
    }

    public BigDecimal getTbDiscountFee() {
        return tbDiscountFee;
    }

    public void setTbDiscountFee(BigDecimal tbDiscountFee) {
        this.tbDiscountFee = tbDiscountFee;
    }

    public BigDecimal getTbTotalFee() {
        return tbTotalFee;
    }

    public void setTbTotalFee(BigDecimal tbTotalFee) {
        this.tbTotalFee = tbTotalFee;
    }

    public String getDzStatus() {
        return dzStatus;
    }

    public void setDzStatus(String dzStatus) {
        this.dzStatus = dzStatus;
    }

    public String getDzDetailsMessage() {
        return dzDetailsMessage;
    }

    public void setDzDetailsMessage(String dzDetailsMessage) {
        this.dzDetailsMessage = dzDetailsMessage;
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
