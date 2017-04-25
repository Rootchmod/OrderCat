package com.myjo.ordercat.bean;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Created by lee5hx on 17/4/24.
 */
public class InventoryInfo {

    //商品货号	货源	中国尺码	外国尺码	品牌	市场价	库存数量	类别	小类	性别	季节	折扣
    private String goodsNo;//商品货号
    private String wareHouseID;//货源ID
    private String warehouseName;//货源
    private String size1;//中国尺码
    private String size2;//外国尺码
    private String brandName;//品牌
    private String marketprice;//市场价
    private String num2;//库存数量
    private String division;//类别
    private String cate;//小类
    private String sex;//性别
    private String quarter;//季节
    private String discount;//折扣
    private String pickRate;//配货率
    private String updateTime;//库存更新时间
    private String thedtime;

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

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
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

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
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

    public String getThedtime() {
        return thedtime;
    }

    public void setThedtime(String thedtime) {
        this.thedtime = thedtime;
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

    //按照仓库去重复`
    public static <T> Predicate<T> distinctByField(Function<? super T, Object> keyExtractor) {
        Map<Object, Boolean> map = new ConcurrentHashMap<>();
        return t -> map.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }




    public String getWareHouseID() {
        return wareHouseID;
    }

    public void setWareHouseID(String wareHouseID) {
        this.wareHouseID = wareHouseID;
    }

    public String getPickRate() {
        return pickRate;
    }

    public void setPickRate(String pickRate) {
        this.pickRate = pickRate;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }
}
