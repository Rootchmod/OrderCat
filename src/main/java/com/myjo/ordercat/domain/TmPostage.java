package com.myjo.ordercat.domain;

import java.math.BigDecimal;

/**
 * Created by lee5hx on 17/6/15.
 */
public class TmPostage{
    private String supplierId;
    private String status;
    private String remark;
    private String checkContinuePostage;
    private String continuePostage;
    private String firsPostage;
    private String type;
    private String id;
    private String checkFirsPostage;
    private String wareHouseName;
    private String province;
    private String expressName;
    private String wareHouseId;
    private BigDecimal kdCost;


    public String getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(String supplierId) {
        this.supplierId = supplierId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getCheckContinuePostage() {
        return checkContinuePostage;
    }

    public void setCheckContinuePostage(String checkContinuePostage) {
        this.checkContinuePostage = checkContinuePostage;
    }

    public String getContinuePostage() {
        return continuePostage;
    }

    public void setContinuePostage(String continuePostage) {
        this.continuePostage = continuePostage;
    }

    public String getFirsPostage() {
        return firsPostage;
    }

    public void setFirsPostage(String firsPostage) {
        this.firsPostage = firsPostage;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCheckFirsPostage() {
        return checkFirsPostage;
    }

    public void setCheckFirsPostage(String checkFirsPostage) {
        this.checkFirsPostage = checkFirsPostage;
    }

    public String getWareHouseName() {
        return wareHouseName;
    }

    public void setWareHouseName(String wareHouseName) {
        this.wareHouseName = wareHouseName;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getExpressName() {
        return expressName;
    }

    public void setExpressName(String expressName) {
        this.expressName = expressName;
    }

    public String getWareHouseId() {
        return wareHouseId;
    }

    public void setWareHouseId(String wareHouseId) {
        this.wareHouseId = wareHouseId;
    }

    public BigDecimal getKdCost() {
        return kdCost;
    }

    public void setKdCost(BigDecimal kdCost) {
        this.kdCost = kdCost;
    }
}
