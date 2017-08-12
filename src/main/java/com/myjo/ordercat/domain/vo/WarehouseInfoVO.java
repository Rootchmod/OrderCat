package com.myjo.ordercat.domain.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import scala.Int;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by lee5hx on 17/6/14.
 */
public class WarehouseInfoVO {

    private long id;
    private Integer warehouseId;
    private String warehouseName;
    private Integer pickRate;
    private Integer thedTime;
    private Integer execJobId;
    private Integer pickDate;
    private Date updateWarehouseTime;
    private String mark;
    private String returnDesc;
    private Integer returnRate;
    private String expressName;
    private String endT;
    private Date addTime;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Integer getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(Integer warehouseId) {
        this.warehouseId = warehouseId;
    }

    public String getWarehouseName() {
        return warehouseName;
    }

    public void setWarehouseName(String warehouseName) {
        this.warehouseName = warehouseName;
    }

    public Integer getPickRate() {
        return pickRate;
    }

    public void setPickRate(Integer pickRate) {
        this.pickRate = pickRate;
    }


    public Integer getThedTime() {
        return thedTime;
    }

    public void setThedTime(Integer thedTime) {
        this.thedTime = thedTime;
    }

    public Integer getExecJobId() {
        return execJobId;
    }

    public void setExecJobId(Integer execJobId) {
        this.execJobId = execJobId;
    }

    public Integer getPickDate() {
        return pickDate;
    }

    public void setPickDate(Integer pickDate) {
        this.pickDate = pickDate;
    }
    @JsonSerialize(using = DateTimeSerializer.class)
    public Date getUpdateWarehouseTime() {
        return updateWarehouseTime;
    }

    public void setUpdateWarehouseTime(Date updateWarehouseTime) {
        this.updateWarehouseTime = updateWarehouseTime;
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

    public String getReturnDesc() {
        return returnDesc;
    }

    public void setReturnDesc(String returnDesc) {
        this.returnDesc = returnDesc;
    }

    public Integer getReturnRate() {
        return returnRate;
    }

    public void setReturnRate(Integer returnRate) {
        this.returnRate = returnRate;
    }

    public String getExpressName() {
        return expressName;
    }

    public void setExpressName(String expressName) {
        this.expressName = expressName;
    }

    public String getEndT() {
        return endT;
    }

    public void setEndT(String endT) {
        this.endT = endT;
    }
    @JsonSerialize(using = DateTimeSerializer.class)
    public Date getAddTime() {
        return addTime;
    }

    public void setAddTime(Date addTime) {
        this.addTime = addTime;
    }
}
