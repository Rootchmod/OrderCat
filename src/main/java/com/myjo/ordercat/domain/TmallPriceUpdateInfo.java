package com.myjo.ordercat.domain;

import java.math.BigDecimal;

/**
 * Created by lee5hx on 17/5/23.
 */
public class TmallPriceUpdateInfo{

    private Long skuId;
    private String outerId;
    private BigDecimal salesPrice;


    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }

    public String getOuterId() {
        return outerId;
    }

    public void setOuterId(String outerId) {
        this.outerId = outerId;
    }

    public BigDecimal getSalesPrice() {
        return salesPrice;
    }

    public void setSalesPrice(BigDecimal salesPrice) {
        this.salesPrice = salesPrice;
    }
}
