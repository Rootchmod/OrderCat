package com.myjo.ordercat.domain.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * Created by lee5hx on 2017/6/24.
 */
public class OcTmOrderRecordsVO {


    private long id; //序号
    private String tid; //'淘宝订单ID'
    private String tmOrderId;//'天马订单ID'
    private String goodsNo;//'商品货号'
    private String size;//尺码
    private String freightPriceStr;//运费
    private Integer whId;//下单仓库
    private String whName;//下单仓库名称
    private Integer whPickRate;//下单仓库配货率,单位:百分比
    private BigDecimal whProxyPrice;//下单仓库价格
    private Date whUpdateTime;//下单仓库库存更新时间
    private Integer whInventoryCount;//下单仓库库存数
    private String type;//下单类型：手工补单，自动下单
    private BigDecimal tbPayAmount;//淘宝订单支付金额
    private String status;//下单状态：成功或失败
    private String orderInfo;//订单信息-json
    private String failCause;//失败原因
    private BigDecimal breakEvenPrice;//保本价(自动机器下单时，才会有数据)
    private String whSnapshotData;//仓库快照数据
    private String machineCid;//下单机器CID
    private long elapsed;//执行耗时,单位:毫秒
    private Date addTime;//下单时间


    private String tmSkuId;
    private String tmSizeInfoStr;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public String getTmOrderId() {
        return tmOrderId;
    }

    public void setTmOrderId(String tmOrderId) {
        this.tmOrderId = tmOrderId;
    }

    public String getGoodsNo() {
        return goodsNo;
    }

    public void setGoodsNo(String goodsNo) {
        this.goodsNo = goodsNo;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getFreightPriceStr() {
        return freightPriceStr;
    }

    public void setFreightPriceStr(String freightPriceStr) {
        this.freightPriceStr = freightPriceStr;
    }

    public Integer getWhId() {
        return whId;
    }

    public void setWhId(Integer whId) {
        this.whId = whId;
    }

    public String getWhName() {
        return whName;
    }

    public void setWhName(String whName) {
        this.whName = whName;
    }

    public Integer getWhPickRate() {
        return whPickRate;
    }

    public void setWhPickRate(Integer whPickRate) {
        this.whPickRate = whPickRate;
    }

    public BigDecimal getWhProxyPrice() {
        return whProxyPrice;
    }

    public void setWhProxyPrice(BigDecimal whProxyPrice) {
        this.whProxyPrice = whProxyPrice;
    }
    @JsonSerialize(using = DateTimeSerializer.class)
    public Date getWhUpdateTime() {
        return whUpdateTime;
    }

    public void setWhUpdateTime(Date whUpdateTime) {
        this.whUpdateTime = whUpdateTime;
    }

    public Integer getWhInventoryCount() {
        return whInventoryCount;
    }

    public void setWhInventoryCount(Integer whInventoryCount) {
        this.whInventoryCount = whInventoryCount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public BigDecimal getTbPayAmount() {
        return tbPayAmount;
    }

    public void setTbPayAmount(BigDecimal tbPayAmount) {
        this.tbPayAmount = tbPayAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOrderInfo() {
        return orderInfo;
    }

    public void setOrderInfo(String orderInfo) {
        this.orderInfo = orderInfo;
    }

    public String getFailCause() {
        return failCause;
    }

    public void setFailCause(String failCause) {
        this.failCause = failCause;
    }

    public BigDecimal getBreakEvenPrice() {
        return breakEvenPrice;
    }

    public void setBreakEvenPrice(BigDecimal breakEvenPrice) {
        this.breakEvenPrice = breakEvenPrice;
    }

    public String getWhSnapshotData() {
        return whSnapshotData;
    }

    public void setWhSnapshotData(String whSnapshotData) {
        this.whSnapshotData = whSnapshotData;
    }

    public String getMachineCid() {
        return machineCid;
    }

    public void setMachineCid(String machineCid) {
        this.machineCid = machineCid;
    }

    public long getElapsed() {
        return elapsed;
    }

    public void setElapsed(long elapsed) {
        this.elapsed = elapsed;
    }
    @JsonSerialize(using = DateTimeSerializer.class)
    public Date getAddTime() {
        return addTime;
    }

    public void setAddTime(Date addTime) {
        this.addTime = addTime;
    }

    public String getTmSkuId() {
        return tmSkuId;
    }

    public void setTmSkuId(String tmSkuId) {
        this.tmSkuId = tmSkuId;
    }

    public String getTmSizeInfoStr() {
        return tmSizeInfoStr;
    }

    public void setTmSizeInfoStr(String tmSizeInfoStr) {
        this.tmSizeInfoStr = tmSizeInfoStr;
    }
}
