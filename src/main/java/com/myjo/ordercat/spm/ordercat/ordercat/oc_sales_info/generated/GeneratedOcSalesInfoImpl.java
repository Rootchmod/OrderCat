package com.myjo.ordercat.spm.ordercat.ordercat.oc_sales_info.generated;

import com.myjo.ordercat.spm.ordercat.ordercat.oc_sales_info.OcSalesInfo;
import com.speedment.runtime.core.annotation.GeneratedCode;
import com.speedment.runtime.core.util.OptionalUtil;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.StringJoiner;

/**
 * The generated base implementation of the {@link
 * com.myjo.ordercat.spm.ordercat.ordercat.oc_sales_info.OcSalesInfo}-interface.
 * <p>
 * This file has been automatically generated by Speedment. Any changes made to
 * it will be overwritten.
 * 
 * @author Speedment
 */
@GeneratedCode("Speedment")
public abstract class GeneratedOcSalesInfoImpl implements OcSalesInfo {
    
    private long id;
    private String numIid;
    private Integer salesCount;
    private Integer execJobId;
    private LocalDateTime addTime;
    
    protected GeneratedOcSalesInfoImpl() {
        
    }
    
    @Override
    public long getId() {
        return id;
    }
    
    @Override
    public Optional<String> getNumIid() {
        return Optional.ofNullable(numIid);
    }
    
    @Override
    public OptionalInt getSalesCount() {
        return OptionalUtil.ofNullable(salesCount);
    }
    
    @Override
    public OptionalInt getExecJobId() {
        return OptionalUtil.ofNullable(execJobId);
    }
    
    @Override
    public LocalDateTime getAddTime() {
        return addTime;
    }
    
    @Override
    public OcSalesInfo setId(long id) {
        this.id = id;
        return this;
    }
    
    @Override
    public OcSalesInfo setNumIid(String numIid) {
        this.numIid = numIid;
        return this;
    }
    
    @Override
    public OcSalesInfo setSalesCount(Integer salesCount) {
        this.salesCount = salesCount;
        return this;
    }
    
    @Override
    public OcSalesInfo setExecJobId(Integer execJobId) {
        this.execJobId = execJobId;
        return this;
    }
    
    @Override
    public OcSalesInfo setAddTime(LocalDateTime addTime) {
        this.addTime = addTime;
        return this;
    }
    
    @Override
    public String toString() {
        final StringJoiner sj = new StringJoiner(", ", "{ ", " }");
        sj.add("id = "         + Objects.toString(getId()));
        sj.add("numIid = "     + Objects.toString(OptionalUtil.unwrap(getNumIid())));
        sj.add("salesCount = " + Objects.toString(OptionalUtil.unwrap(getSalesCount())));
        sj.add("execJobId = "  + Objects.toString(OptionalUtil.unwrap(getExecJobId())));
        sj.add("addTime = "    + Objects.toString(getAddTime()));
        return "OcSalesInfoImpl " + sj.toString();
    }
    
    @Override
    public boolean equals(Object that) {
        if (this == that) { return true; }
        if (!(that instanceof OcSalesInfo)) { return false; }
        final OcSalesInfo thatOcSalesInfo = (OcSalesInfo)that;
        if (this.getId() != thatOcSalesInfo.getId()) {return false; }
        if (!Objects.equals(this.getNumIid(), thatOcSalesInfo.getNumIid())) {return false; }
        if (!Objects.equals(this.getSalesCount(), thatOcSalesInfo.getSalesCount())) {return false; }
        if (!Objects.equals(this.getExecJobId(), thatOcSalesInfo.getExecJobId())) {return false; }
        if (!Objects.equals(this.getAddTime(), thatOcSalesInfo.getAddTime())) {return false; }
        return true;
    }
    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + Long.hashCode(getId());
        hash = 31 * hash + Objects.hashCode(getNumIid());
        hash = 31 * hash + Objects.hashCode(getSalesCount());
        hash = 31 * hash + Objects.hashCode(getExecJobId());
        hash = 31 * hash + Objects.hashCode(getAddTime());
        return hash;
    }
}