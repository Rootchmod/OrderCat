package com.myjo.ordercat.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Created by lee5hx on 17/4/27.
 */
public class GoodsInventoryInfo {
    private String goodsNo;//商品货号
    private String wareHouseID;//货源ID
    private LocalDateTime updateTime;//库存更新时间
    private int pickRate;//配货率
    private int quantity;//库存数量
    private BigDecimal marketPrice;//市场价格
    private BigDecimal price;//价格
    private BigDecimal avgPrice;//平均价格(根据仓库取平均)
    private int sizeCount;//尺码数

    public String getGoodsNo() {
        return goodsNo;
    }

    public void setGoodsNo(String goodsNo) {
        this.goodsNo = goodsNo;
    }

    public String getWareHouseID() {
        return wareHouseID;
    }

    public void setWareHouseID(String wareHouseID) {
        this.wareHouseID = wareHouseID;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    public int getPickRate() {
        return pickRate;
    }

    public void setPickRate(int pickRate) {
        this.pickRate = pickRate;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getAvgPrice() {
        return avgPrice;
    }

    public void setAvgPrice(BigDecimal avgPrice) {
        this.avgPrice = avgPrice;
    }

    public int getSizeCount() {
        return sizeCount;
    }

    public void setSizeCount(int sizeCount) {
        this.sizeCount = sizeCount;
    }


    public BigDecimal getMarketPrice() {
        return marketPrice;
    }

    public void setMarketPrice(BigDecimal marketPrice) {
        this.marketPrice = marketPrice;
    }
}
