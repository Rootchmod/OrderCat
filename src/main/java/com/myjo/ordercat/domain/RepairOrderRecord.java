package com.myjo.ordercat.domain;

import com.alibaba.fastjson.JSONObject;
import com.myjo.ordercat.domain.constant.TianmaOrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by lee5hx on 2017/8/12.
 */
public class RepairOrderRecord {

    private String pctTmOrderid;//订单(原)
    private String ptcODealDescr;//待付款描述(原)
    private LocalDateTime ptcODealDate;//待付款时间(原)
    private String ptcTmTradeRemark;//天马订单备注(原)
    private String goodsNo;//货号
    private String size1;//尺码
    private String size2;//尺码
    private TianmaOrderStatus ptcOrderStatus; //订单状态(原)
    private String customerName; //客户名称
    private BigDecimal payPrice; //支付金额
    private String outerOrderId;//淘宝订单ID
    private String ptcMWarehouseName; //订单下单仓库ID(原)
    private Integer ptcMWarehouseId;//订单下单仓库名称(原)
    private List<JSONObject> roWhSnapshotData;
    private List<TmSizeInfo> roTmSizeInfos;
    private String roTmSkuId;
    private LocalDateTime nowDateTime;//待付款时间(原)
    private Integer roWhId;
    private String roWhName;
    private Integer roWhPickRate;
    private Integer roWhInventoryCount;
    private BigDecimal roWhProxyPrice;
    private LocalDateTime roWhUpdateTime;
    private String roTmOrderId;
    private String roFreightPriceStr;
    private String roStatus;
    private String roOrderInfo;
    private String roFailCause;

    private BigDecimal breakEvenPrice;

    public String getRoTmOrderId() {
        return roTmOrderId;
    }

    public void setRoTmOrderId(String roTmOrderId) {
        this.roTmOrderId = roTmOrderId;
    }

    public String getRoFreightPriceStr() {
        return roFreightPriceStr;
    }

    public void setRoFreightPriceStr(String roFreightPriceStr) {
        this.roFreightPriceStr = roFreightPriceStr;
    }

    public String getRoStatus() {
        return roStatus;
    }

    public void setRoStatus(String roStatus) {
        this.roStatus = roStatus;
    }

    public String getRoOrderInfo() {
        return roOrderInfo;
    }

    public void setRoOrderInfo(String roOrderInfo) {
        this.roOrderInfo = roOrderInfo;
    }

    public String getPctTmOrderid() {
        return pctTmOrderid;
    }

    public void setPctTmOrderid(String pctTmOrderid) {
        this.pctTmOrderid = pctTmOrderid;
    }

    public String getPtcODealDescr() {
        return ptcODealDescr;
    }

    public void setPtcODealDescr(String ptcODealDescr) {
        this.ptcODealDescr = ptcODealDescr;
    }

    public LocalDateTime getPtcODealDate() {
        return ptcODealDate;
    }

    public void setPtcODealDate(LocalDateTime ptcODealDate) {
        this.ptcODealDate = ptcODealDate;
    }

    public String getPtcTmTradeRemark() {
        return ptcTmTradeRemark;
    }

    public void setPtcTmTradeRemark(String ptcTmTradeRemark) {
        this.ptcTmTradeRemark = ptcTmTradeRemark;
    }

    public String getGoodsNo() {
        return goodsNo;
    }

    public void setGoodsNo(String goodsNo) {
        this.goodsNo = goodsNo;
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

    public TianmaOrderStatus getPtcOrderStatus() {
        return ptcOrderStatus;
    }

    public void setPtcOrderStatus(TianmaOrderStatus ptcOrderStatus) {
        this.ptcOrderStatus = ptcOrderStatus;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public BigDecimal getPayPrice() {
        return payPrice;
    }

    public void setPayPrice(BigDecimal payPrice) {
        this.payPrice = payPrice;
    }

    public String getOuterOrderId() {
        return outerOrderId;
    }

    public void setOuterOrderId(String outerOrderId) {
        this.outerOrderId = outerOrderId;
    }

    public String getPtcMWarehouseName() {
        return ptcMWarehouseName;
    }

    public void setPtcMWarehouseName(String ptcMWarehouseName) {
        this.ptcMWarehouseName = ptcMWarehouseName;
    }

    public Integer getPtcMWarehouseId() {
        return ptcMWarehouseId;
    }

    public void setPtcMWarehouseId(Integer ptcMWarehouseId) {
        this.ptcMWarehouseId = ptcMWarehouseId;
    }

    public LocalDateTime getNowDateTime() {
        return nowDateTime;
    }

    public void setNowDateTime(LocalDateTime nowDateTime) {
        this.nowDateTime = nowDateTime;
    }

    public List<JSONObject> getRoWhSnapshotData() {
        return roWhSnapshotData;
    }

    public void setRoWhSnapshotData(List<JSONObject> roWhSnapshotData) {
        this.roWhSnapshotData = roWhSnapshotData;
    }

    public List<TmSizeInfo> getRoTmSizeInfos() {
        return roTmSizeInfos;
    }

    public void setRoTmSizeInfos(List<TmSizeInfo> roTmSizeInfos) {
        this.roTmSizeInfos = roTmSizeInfos;
    }

    public String getRoTmSkuId() {
        return roTmSkuId;
    }

    public void setRoTmSkuId(String roTmSkuId) {
        this.roTmSkuId = roTmSkuId;
    }

    public Integer getRoWhId() {
        return roWhId;
    }

    public void setRoWhId(Integer roWhId) {
        this.roWhId = roWhId;
    }

    public String getRoWhName() {
        return roWhName;
    }

    public void setRoWhName(String roWhName) {
        this.roWhName = roWhName;
    }

    public Integer getRoWhPickRate() {
        return roWhPickRate;
    }

    public void setRoWhPickRate(Integer roWhPickRate) {
        this.roWhPickRate = roWhPickRate;
    }

    public Integer getRoWhInventoryCount() {
        return roWhInventoryCount;
    }

    public void setRoWhInventoryCount(Integer roWhInventoryCount) {
        this.roWhInventoryCount = roWhInventoryCount;
    }

    public BigDecimal getRoWhProxyPrice() {
        return roWhProxyPrice;
    }

    public void setRoWhProxyPrice(BigDecimal roWhProxyPrice) {
        this.roWhProxyPrice = roWhProxyPrice;
    }

    public LocalDateTime getRoWhUpdateTime() {
        return roWhUpdateTime;
    }

    public void setRoWhUpdateTime(LocalDateTime roWhUpdateTime) {
        this.roWhUpdateTime = roWhUpdateTime;
    }


    public String getRoFailCause() {
        return roFailCause;
    }

    public void setRoFailCause(String roFailCause) {
        this.roFailCause = roFailCause;
    }

    public BigDecimal getBreakEvenPrice() {
        return breakEvenPrice;
    }

    public void setBreakEvenPrice(BigDecimal breakEvenPrice) {
        this.breakEvenPrice = breakEvenPrice;
    }
}
