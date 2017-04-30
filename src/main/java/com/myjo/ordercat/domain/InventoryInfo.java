package com.myjo.ordercat.domain;

import com.taobao.api.domain.Sku;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Created by lee5hx on 17/4/24.
 */
public class InventoryInfo {

    private String goodsNo;//商品货号
    private Integer wareHouseID;//货源ID
    private String warehouseName;//货源
    private String size1;//中国尺码
    private String size2;//外国尺码
    private Brand brand;//品牌
    private String marketprice;//市场价
    private String num2;//库存数量
    private String division;//类别
    private String cate;//小类
    private Sex sex;//性别
    private String quarter;//季节
    private String discount;//折扣
    private BigDecimal bdiscount;
    private int pickRate;//配货率
    private LocalDateTime updateTime;//库存更新时间
    private PickDate pickDate;//配货时间
    private String thedtime;
    private BigDecimal proxyPrice;
    private BigDecimal purchasePrice;
    private Long salesCount;
    private String expressName;
    private String retrunDesc;
    private int returnRate;
    private String endT;
    private String mark;
    private Long numIid;

    //按照仓库去重复`
    public static <T> Predicate<T> distinctByField(Function<? super T, Object> keyExtractor) {
        Map<Object, Boolean> map = new ConcurrentHashMap<>();
        return t -> map.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

    //根据SKU信息去重复
    public static <T> Predicate<T> distinctBySkusMap(Function<? super T, String> keyExtractor, Map<String, List<Sku>> map) {
        //return t -> map.putIfAbsent(keyExtractor.apply(t), null) == null;
        return t -> map.containsKey(keyExtractor.apply(t)) == true;
    }

    public String getGoodsNo() {
        return goodsNo;
    }

    public void setGoodsNo(String goodsNo) {
        this.goodsNo = goodsNo;
    }

    public String getWarehouseName() {
        return warehouseName;
    }

    public void setWarehouseName(String warehouseName) {
        this.warehouseName = warehouseName;
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

    public Brand getBrand() {
        return brand;
    }

    public void setBrand(Brand brand) {
        this.brand = brand;
    }

    public String getMarketprice() {
        return marketprice;
    }

    public void setMarketprice(String marketprice) {
        this.marketprice = marketprice;
    }

    public String getNum2() {
        return num2;
    }

    public void setNum2(String num2) {
        this.num2 = num2;
    }

    public String getDivision() {
        return division;
    }

    public void setDivision(String division) {
        this.division = division;
    }

    public String getCate() {
        return cate;
    }

    public void setCate(String cate) {
        this.cate = cate;
    }

    public Sex getSex() {
        return sex;
    }

    public void setSex(Sex sex) {
        this.sex = sex;
    }

    public String getQuarter() {
        return quarter;
    }

    public void setQuarter(String quarter) {
        this.quarter = quarter;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    //    "warehouse_name": "广东特价渠道",
//            "hide_flag": null,
//            "sex": "男",
//            "warehouse_big_id": null,
//            "cate": "短裤/中长裤",
//            "sort": 0,
//            "pick_rate": null,
//            "goods_no": "631065-658",
//            "product_id": null,
//            "marketprice": 199,
//            "out_sku": null,
//            "search_goods_no": null,
//            "warehouse_mark": null,
//            "warehouse_goods_no": "631065-658",
//            "brand_name": "耐克",
//            "size2": "M",
//            "size1": "M",
//            "warehouse_size": null,
//            "id": null,
//            "division": "服",
//            "created": null,
//            "expressName": null,
//            "paperDeadLine": null,
//            "sku_id": null,
//            "return_desc": null,
//            "warehouse_price": null,
//            "pick_date": null,
//            "return_rate": null,
//            "supplier_id": null,
//            "post": null,
//            "warehouse_id": 485,
//            "supplier_name": null,
//            "brand_id": null,
//            "barcode": null,
//            "onsale_date": null,
//            "cid": null,
//            "on_list": null,
//            "discount": 7.77,
//            "update_time": null,
//            "pick_str": null,
//            "month": null,
//            "num2": 16,
//            "warehouse_des": null,
//            "num1": null,
//            "quarter": "17Q2"

    public String getThedtime() {
        return thedtime;
    }

    public void setThedtime(String thedtime) {
        this.thedtime = thedtime;
    }

    public int getPickRate() {
        return pickRate;
    }

    public void setPickRate(int pickRate) {
        this.pickRate = pickRate;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    public PickDate getPickDate() {
        return pickDate;
    }

    public void setPickDate(PickDate pickDate) {
        this.pickDate = pickDate;
    }


    public String getExpressName() {
        return expressName;
    }

    public void setExpressName(String expressName) {
        this.expressName = expressName;
    }

    public String getRetrunDesc() {
        return retrunDesc;
    }

    public void setRetrunDesc(String retrunDesc) {
        this.retrunDesc = retrunDesc;
    }

    public int getReturnRate() {
        return returnRate;
    }

    public void setReturnRate(int returnRate) {
        this.returnRate = returnRate;
    }

    public String getEndT() {
        return endT;
    }

    public void setEndT(String endT) {
        this.endT = endT;
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

    public Integer getWareHouseID() {
        return wareHouseID;
    }

    public void setWareHouseID(Integer wareHouseID) {
        this.wareHouseID = wareHouseID;
    }


    public BigDecimal getBdiscount() {
        return bdiscount;
    }

    public void setBdiscount(BigDecimal bdiscount) {
        this.bdiscount = bdiscount;
    }


    public BigDecimal getProxyPrice() {
        return proxyPrice;
    }

    public void setProxyPrice(BigDecimal proxyPrice) {
        this.proxyPrice = proxyPrice;
    }


    public Long getNumIid() {
        return numIid;
    }

    public void setNumIid(Long numIid) {
        this.numIid = numIid;
    }

    public BigDecimal getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(BigDecimal purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public Long getSalesCount() {
        return salesCount;
    }

    public void setSalesCount(Long salesCount) {
        this.salesCount = salesCount;
    }
}
