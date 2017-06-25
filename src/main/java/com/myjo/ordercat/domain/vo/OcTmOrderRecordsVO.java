package com.myjo.ordercat.domain.vo;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * Created by lee5hx on 2017/6/24.
 */
public class OcTmOrderRecordsVO {


    private long id;
    private String tid;
    private String type;
    private String status;
    private String orderInfo;
    private String failCause;
    private String whSnapshotData;
    private String machineCid;
    private Date addTime;


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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public Date getAddTime() {
        return addTime;
    }

    public void setAddTime(Date addTime) {
        this.addTime = addTime;
    }
}
