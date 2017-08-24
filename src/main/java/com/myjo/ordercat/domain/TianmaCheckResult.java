package com.myjo.ordercat.domain;

import com.myjo.ordercat.domain.constant.TianmaCheckStatus;
import com.taobao.api.domain.Order;
import com.taobao.api.domain.Trade;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by lee5hx on 17/5/22.
 */
public class TianmaCheckResult {
    private String tmOuterOrderId;
    private List<TianmaOrder> tmOrders;
    private List<Order> tbOrders;
    private Trade trade;
    private Long tmNum;
    private Long tmOrderNum;
    private Long tbNum;
    private Long tbOrderNum;
    private LocalDateTime tbCreated;
    private LocalDateTime tbPayTime;
    private BigDecimal tbPayment;
    private BigDecimal tbPrice;
    private BigDecimal tbDiscountFee;
    private BigDecimal tbTotalFee;

    private String tbTitle;
    private String tbNickName;


    private TianmaCheckStatus dzStatus;
    private String dzDetailsMessage;
    private String remarks;
    private LocalDateTime addTime;

    public String getTmOuterOrderId() {
        return tmOuterOrderId;
    }

    public void setTmOuterOrderId(String tmOuterOrderId) {
        this.tmOuterOrderId = tmOuterOrderId;
    }

    public List<TianmaOrder> getTmOrders() {
        return tmOrders;
    }

    public void setTmOrders(List<TianmaOrder> tmOrders) {
        this.tmOrders = tmOrders;
    }

    public List<Order> getTbOrders() {
        return tbOrders;
    }

    public void setTbOrders(List<Order> tbOrders) {
        this.tbOrders = tbOrders;
    }

    public Long getTmNum() {
        return tmNum;
    }

    public void setTmNum(Long tmNum) {
        this.tmNum = tmNum;
    }

    public Long getTmOrderNum() {
        return tmOrderNum;
    }

    public void setTmOrderNum(Long tmOrderNum) {
        this.tmOrderNum = tmOrderNum;
    }

    public Long getTbNum() {
        return tbNum;
    }

    public void setTbNum(Long tbNum) {
        this.tbNum = tbNum;
    }

    public Long getTbOrderNum() {
        return tbOrderNum;
    }

    public void setTbOrderNum(Long tbOrderNum) {
        this.tbOrderNum = tbOrderNum;
    }


    public TianmaCheckStatus getDzStatus() {
        return dzStatus;
    }

    public void setDzStatus(TianmaCheckStatus dzStatus) {
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

    public LocalDateTime getAddTime() {
        return addTime;
    }

    public void setAddTime(LocalDateTime addTime) {
        this.addTime = addTime;
    }


    public LocalDateTime getTbCreated() {
        return tbCreated;
    }

    public void setTbCreated(LocalDateTime tbCreated) {
        this.tbCreated = tbCreated;
    }

    public LocalDateTime getTbPayTime() {
        return tbPayTime;
    }

    public void setTbPayTime(LocalDateTime tbPayTime) {
        this.tbPayTime = tbPayTime;
    }

    public BigDecimal getTbPrice() {
        return tbPrice;
    }

    public void setTbPrice(BigDecimal tbPrice) {
        this.tbPrice = tbPrice;
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


    public Trade getTrade() {
        return trade;
    }

    public void setTrade(Trade trade) {
        this.trade = trade;
    }

    public BigDecimal getTbPayment() {
        return tbPayment;
    }

    public void setTbPayment(BigDecimal tbPayment) {
        this.tbPayment = tbPayment;
    }

    public String getTbTitle() {
        return tbTitle;
    }

    public void setTbTitle(String tbTitle) {
        this.tbTitle = tbTitle;
    }

    public String getTbNickName() {
        return tbNickName;
    }

    public void setTbNickName(String tbNickName) {
        this.tbNickName = tbNickName;
    }
}
