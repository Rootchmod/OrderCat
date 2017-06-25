package com.myjo.ordercat.spm.ordercat.ordercat.oc_tm_order_records.generated;

import com.myjo.ordercat.spm.ordercat.ordercat.oc_tm_order_records.OcTmOrderRecords;
import com.speedment.common.annotation.GeneratedCode;
import com.speedment.runtime.core.util.OptionalUtil;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;

/**
 * The generated base implementation of the {@link
 * com.myjo.ordercat.spm.ordercat.ordercat.oc_tm_order_records.OcTmOrderRecords}-interface.
 * <p>
 * This file has been automatically generated by Speedment. Any changes made to
 * it will be overwritten.
 * 
 * @author Speedment
 */
@GeneratedCode("Speedment")
public abstract class GeneratedOcTmOrderRecordsImpl implements OcTmOrderRecords {
    
    private long id;
    private String tid;
    private String type;
    private String status;
    private String orderInfo;
    private String failCause;
    private String whSnapshotData;
    private String machineCid;
    private LocalDateTime addTime;
    
    protected GeneratedOcTmOrderRecordsImpl() {
        
    }
    
    @Override
    public long getId() {
        return id;
    }
    
    @Override
    public Optional<String> getTid() {
        return Optional.ofNullable(tid);
    }
    
    @Override
    public Optional<String> getType() {
        return Optional.ofNullable(type);
    }
    
    @Override
    public Optional<String> getStatus() {
        return Optional.ofNullable(status);
    }
    
    @Override
    public Optional<String> getOrderInfo() {
        return Optional.ofNullable(orderInfo);
    }
    
    @Override
    public Optional<String> getFailCause() {
        return Optional.ofNullable(failCause);
    }
    
    @Override
    public Optional<String> getWhSnapshotData() {
        return Optional.ofNullable(whSnapshotData);
    }
    
    @Override
    public Optional<String> getMachineCid() {
        return Optional.ofNullable(machineCid);
    }
    
    @Override
    public LocalDateTime getAddTime() {
        return addTime;
    }
    
    @Override
    public OcTmOrderRecords setId(long id) {
        this.id = id;
        return this;
    }
    
    @Override
    public OcTmOrderRecords setTid(String tid) {
        this.tid = tid;
        return this;
    }
    
    @Override
    public OcTmOrderRecords setType(String type) {
        this.type = type;
        return this;
    }
    
    @Override
    public OcTmOrderRecords setStatus(String status) {
        this.status = status;
        return this;
    }
    
    @Override
    public OcTmOrderRecords setOrderInfo(String orderInfo) {
        this.orderInfo = orderInfo;
        return this;
    }
    
    @Override
    public OcTmOrderRecords setFailCause(String failCause) {
        this.failCause = failCause;
        return this;
    }
    
    @Override
    public OcTmOrderRecords setWhSnapshotData(String whSnapshotData) {
        this.whSnapshotData = whSnapshotData;
        return this;
    }
    
    @Override
    public OcTmOrderRecords setMachineCid(String machineCid) {
        this.machineCid = machineCid;
        return this;
    }
    
    @Override
    public OcTmOrderRecords setAddTime(LocalDateTime addTime) {
        this.addTime = addTime;
        return this;
    }
    
    @Override
    public String toString() {
        final StringJoiner sj = new StringJoiner(", ", "{ ", " }");
        sj.add("id = "             + Objects.toString(getId()));
        sj.add("tid = "            + Objects.toString(OptionalUtil.unwrap(getTid())));
        sj.add("type = "           + Objects.toString(OptionalUtil.unwrap(getType())));
        sj.add("status = "         + Objects.toString(OptionalUtil.unwrap(getStatus())));
        sj.add("orderInfo = "      + Objects.toString(OptionalUtil.unwrap(getOrderInfo())));
        sj.add("failCause = "      + Objects.toString(OptionalUtil.unwrap(getFailCause())));
        sj.add("whSnapshotData = " + Objects.toString(OptionalUtil.unwrap(getWhSnapshotData())));
        sj.add("machineCid = "     + Objects.toString(OptionalUtil.unwrap(getMachineCid())));
        sj.add("addTime = "        + Objects.toString(getAddTime()));
        return "OcTmOrderRecordsImpl " + sj.toString();
    }
    
    @Override
    public boolean equals(Object that) {
        if (this == that) { return true; }
        if (!(that instanceof OcTmOrderRecords)) { return false; }
        final OcTmOrderRecords thatOcTmOrderRecords = (OcTmOrderRecords)that;
        if (this.getId() != thatOcTmOrderRecords.getId()) {return false; }
        if (!Objects.equals(this.getTid(), thatOcTmOrderRecords.getTid())) {return false; }
        if (!Objects.equals(this.getType(), thatOcTmOrderRecords.getType())) {return false; }
        if (!Objects.equals(this.getStatus(), thatOcTmOrderRecords.getStatus())) {return false; }
        if (!Objects.equals(this.getOrderInfo(), thatOcTmOrderRecords.getOrderInfo())) {return false; }
        if (!Objects.equals(this.getFailCause(), thatOcTmOrderRecords.getFailCause())) {return false; }
        if (!Objects.equals(this.getWhSnapshotData(), thatOcTmOrderRecords.getWhSnapshotData())) {return false; }
        if (!Objects.equals(this.getMachineCid(), thatOcTmOrderRecords.getMachineCid())) {return false; }
        if (!Objects.equals(this.getAddTime(), thatOcTmOrderRecords.getAddTime())) {return false; }
        return true;
    }
    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + Long.hashCode(getId());
        hash = 31 * hash + Objects.hashCode(getTid());
        hash = 31 * hash + Objects.hashCode(getType());
        hash = 31 * hash + Objects.hashCode(getStatus());
        hash = 31 * hash + Objects.hashCode(getOrderInfo());
        hash = 31 * hash + Objects.hashCode(getFailCause());
        hash = 31 * hash + Objects.hashCode(getWhSnapshotData());
        hash = 31 * hash + Objects.hashCode(getMachineCid());
        hash = 31 * hash + Objects.hashCode(getAddTime());
        return hash;
    }
}