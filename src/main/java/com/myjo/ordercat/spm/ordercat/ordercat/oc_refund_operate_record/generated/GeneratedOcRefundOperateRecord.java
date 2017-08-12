package com.myjo.ordercat.spm.ordercat.ordercat.oc_refund_operate_record.generated;

import com.myjo.ordercat.spm.ordercat.ordercat.oc_refund_operate_record.OcRefundOperateRecord;
import com.speedment.common.annotation.GeneratedCode;
import com.speedment.runtime.config.identifier.ColumnIdentifier;
import com.speedment.runtime.config.identifier.TableIdentifier;
import com.speedment.runtime.core.util.OptionalUtil;
import com.speedment.runtime.field.ComparableField;
import com.speedment.runtime.field.LongField;
import com.speedment.runtime.field.StringField;
import com.speedment.runtime.typemapper.TypeMapper;
import com.speedment.runtime.typemapper.time.TimestampToLocalDateTimeMapper;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.OptionalLong;

/**
 * The generated base for the {@link
 * com.myjo.ordercat.spm.ordercat.ordercat.oc_refund_operate_record.OcRefundOperateRecord}-interface
 * representing entities of the {@code oc_refund_operate_record}-table in the
 * database.
 * <p>
 * This file has been automatically generated by Speedment. Any changes made to
 * it will be overwritten.
 * 
 * @author Speedment
 */
@GeneratedCode("Speedment")
public interface GeneratedOcRefundOperateRecord {
    
    /**
     * This Field corresponds to the {@link OcRefundOperateRecord} field that
     * can be obtained using the {@link OcRefundOperateRecord#getId()} method.
     */
    LongField<OcRefundOperateRecord, Long> ID = LongField.create(
        Identifier.ID,
        OcRefundOperateRecord::getId,
        OcRefundOperateRecord::setId,
        TypeMapper.primitive(), 
        true
    );
    /**
     * This Field corresponds to the {@link OcRefundOperateRecord} field that
     * can be obtained using the {@link OcRefundOperateRecord#getRefundId()}
     * method.
     */
    ComparableField<OcRefundOperateRecord, Long, Long> REFUND_ID = ComparableField.create(
        Identifier.REFUND_ID,
        o -> OptionalUtil.unwrap(o.getRefundId()),
        OcRefundOperateRecord::setRefundId,
        TypeMapper.identity(), 
        false
    );
    /**
     * This Field corresponds to the {@link OcRefundOperateRecord} field that
     * can be obtained using the {@link OcRefundOperateRecord#getTid()} method.
     */
    ComparableField<OcRefundOperateRecord, Long, Long> TID = ComparableField.create(
        Identifier.TID,
        o -> OptionalUtil.unwrap(o.getTid()),
        OcRefundOperateRecord::setTid,
        TypeMapper.identity(), 
        false
    );
    /**
     * This Field corresponds to the {@link OcRefundOperateRecord} field that
     * can be obtained using the {@link OcRefundOperateRecord#getIsDaixiao()}
     * method.
     */
    ComparableField<OcRefundOperateRecord, Short, Short> IS_DAIXIAO = ComparableField.create(
        Identifier.IS_DAIXIAO,
        o -> OptionalUtil.unwrap(o.getIsDaixiao()),
        OcRefundOperateRecord::setIsDaixiao,
        TypeMapper.identity(), 
        false
    );
    /**
     * This Field corresponds to the {@link OcRefundOperateRecord} field that
     * can be obtained using the {@link OcRefundOperateRecord#getStatus()}
     * method.
     */
    StringField<OcRefundOperateRecord, String> STATUS = StringField.create(
        Identifier.STATUS,
        o -> OptionalUtil.unwrap(o.getStatus()),
        OcRefundOperateRecord::setStatus,
        TypeMapper.identity(), 
        false
    );
    /**
     * This Field corresponds to the {@link OcRefundOperateRecord} field that
     * can be obtained using the {@link OcRefundOperateRecord#getReason()}
     * method.
     */
    StringField<OcRefundOperateRecord, String> REASON = StringField.create(
        Identifier.REASON,
        o -> OptionalUtil.unwrap(o.getReason()),
        OcRefundOperateRecord::setReason,
        TypeMapper.identity(), 
        false
    );
    /**
     * This Field corresponds to the {@link OcRefundOperateRecord} field that
     * can be obtained using the {@link OcRefundOperateRecord#getSid()} method.
     */
    StringField<OcRefundOperateRecord, String> SID = StringField.create(
        Identifier.SID,
        o -> OptionalUtil.unwrap(o.getSid()),
        OcRefundOperateRecord::setSid,
        TypeMapper.identity(), 
        false
    );
    /**
     * This Field corresponds to the {@link OcRefundOperateRecord} field that
     * can be obtained using the {@link OcRefundOperateRecord#getCompanyName()}
     * method.
     */
    StringField<OcRefundOperateRecord, String> COMPANY_NAME = StringField.create(
        Identifier.COMPANY_NAME,
        o -> OptionalUtil.unwrap(o.getCompanyName()),
        OcRefundOperateRecord::setCompanyName,
        TypeMapper.identity(), 
        false
    );
    /**
     * This Field corresponds to the {@link OcRefundOperateRecord} field that
     * can be obtained using the {@link
     * OcRefundOperateRecord#getOperateDetail()} method.
     */
    StringField<OcRefundOperateRecord, String> OPERATE_DETAIL = StringField.create(
        Identifier.OPERATE_DETAIL,
        o -> OptionalUtil.unwrap(o.getOperateDetail()),
        OcRefundOperateRecord::setOperateDetail,
        TypeMapper.identity(), 
        false
    );
    /**
     * This Field corresponds to the {@link OcRefundOperateRecord} field that
     * can be obtained using the {@link OcRefundOperateRecord#getAddTime()}
     * method.
     */
    ComparableField<OcRefundOperateRecord, Timestamp, LocalDateTime> ADD_TIME = ComparableField.create(
        Identifier.ADD_TIME,
        OcRefundOperateRecord::getAddTime,
        OcRefundOperateRecord::setAddTime,
        new TimestampToLocalDateTimeMapper(), 
        false
    );
    
    /**
     * Returns the id of this OcRefundOperateRecord. The id field corresponds to
     * the database column ordercat.ordercat.oc_refund_operate_record.id.
     * 
     * @return the id of this OcRefundOperateRecord
     */
    long getId();
    
    /**
     * Returns the refundId of this OcRefundOperateRecord. The refundId field
     * corresponds to the database column
     * ordercat.ordercat.oc_refund_operate_record.refund_id.
     * 
     * @return the refundId of this OcRefundOperateRecord
     */
    OptionalLong getRefundId();
    
    /**
     * Returns the tid of this OcRefundOperateRecord. The tid field corresponds
     * to the database column ordercat.ordercat.oc_refund_operate_record.tid.
     * 
     * @return the tid of this OcRefundOperateRecord
     */
    OptionalLong getTid();
    
    /**
     * Returns the isDaixiao of this OcRefundOperateRecord. The isDaixiao field
     * corresponds to the database column
     * ordercat.ordercat.oc_refund_operate_record.is_daixiao.
     * 
     * @return the isDaixiao of this OcRefundOperateRecord
     */
    Optional<Short> getIsDaixiao();
    
    /**
     * Returns the status of this OcRefundOperateRecord. The status field
     * corresponds to the database column
     * ordercat.ordercat.oc_refund_operate_record.status.
     * 
     * @return the status of this OcRefundOperateRecord
     */
    Optional<String> getStatus();
    
    /**
     * Returns the reason of this OcRefundOperateRecord. The reason field
     * corresponds to the database column
     * ordercat.ordercat.oc_refund_operate_record.reason.
     * 
     * @return the reason of this OcRefundOperateRecord
     */
    Optional<String> getReason();
    
    /**
     * Returns the sid of this OcRefundOperateRecord. The sid field corresponds
     * to the database column ordercat.ordercat.oc_refund_operate_record.sid.
     * 
     * @return the sid of this OcRefundOperateRecord
     */
    Optional<String> getSid();
    
    /**
     * Returns the companyName of this OcRefundOperateRecord. The companyName
     * field corresponds to the database column
     * ordercat.ordercat.oc_refund_operate_record.company_name.
     * 
     * @return the companyName of this OcRefundOperateRecord
     */
    Optional<String> getCompanyName();
    
    /**
     * Returns the operateDetail of this OcRefundOperateRecord. The
     * operateDetail field corresponds to the database column
     * ordercat.ordercat.oc_refund_operate_record.operate_detail.
     * 
     * @return the operateDetail of this OcRefundOperateRecord
     */
    Optional<String> getOperateDetail();
    
    /**
     * Returns the addTime of this OcRefundOperateRecord. The addTime field
     * corresponds to the database column
     * ordercat.ordercat.oc_refund_operate_record.add_time.
     * 
     * @return the addTime of this OcRefundOperateRecord
     */
    LocalDateTime getAddTime();
    
    /**
     * Sets the id of this OcRefundOperateRecord. The id field corresponds to
     * the database column ordercat.ordercat.oc_refund_operate_record.id.
     * 
     * @param id to set of this OcRefundOperateRecord
     * @return   this OcRefundOperateRecord instance
     */
    OcRefundOperateRecord setId(long id);
    
    /**
     * Sets the refundId of this OcRefundOperateRecord. The refundId field
     * corresponds to the database column
     * ordercat.ordercat.oc_refund_operate_record.refund_id.
     * 
     * @param refundId to set of this OcRefundOperateRecord
     * @return         this OcRefundOperateRecord instance
     */
    OcRefundOperateRecord setRefundId(Long refundId);
    
    /**
     * Sets the tid of this OcRefundOperateRecord. The tid field corresponds to
     * the database column ordercat.ordercat.oc_refund_operate_record.tid.
     * 
     * @param tid to set of this OcRefundOperateRecord
     * @return    this OcRefundOperateRecord instance
     */
    OcRefundOperateRecord setTid(Long tid);
    
    /**
     * Sets the isDaixiao of this OcRefundOperateRecord. The isDaixiao field
     * corresponds to the database column
     * ordercat.ordercat.oc_refund_operate_record.is_daixiao.
     * 
     * @param isDaixiao to set of this OcRefundOperateRecord
     * @return          this OcRefundOperateRecord instance
     */
    OcRefundOperateRecord setIsDaixiao(Short isDaixiao);
    
    /**
     * Sets the status of this OcRefundOperateRecord. The status field
     * corresponds to the database column
     * ordercat.ordercat.oc_refund_operate_record.status.
     * 
     * @param status to set of this OcRefundOperateRecord
     * @return       this OcRefundOperateRecord instance
     */
    OcRefundOperateRecord setStatus(String status);
    
    /**
     * Sets the reason of this OcRefundOperateRecord. The reason field
     * corresponds to the database column
     * ordercat.ordercat.oc_refund_operate_record.reason.
     * 
     * @param reason to set of this OcRefundOperateRecord
     * @return       this OcRefundOperateRecord instance
     */
    OcRefundOperateRecord setReason(String reason);
    
    /**
     * Sets the sid of this OcRefundOperateRecord. The sid field corresponds to
     * the database column ordercat.ordercat.oc_refund_operate_record.sid.
     * 
     * @param sid to set of this OcRefundOperateRecord
     * @return    this OcRefundOperateRecord instance
     */
    OcRefundOperateRecord setSid(String sid);
    
    /**
     * Sets the companyName of this OcRefundOperateRecord. The companyName field
     * corresponds to the database column
     * ordercat.ordercat.oc_refund_operate_record.company_name.
     * 
     * @param companyName to set of this OcRefundOperateRecord
     * @return            this OcRefundOperateRecord instance
     */
    OcRefundOperateRecord setCompanyName(String companyName);
    
    /**
     * Sets the operateDetail of this OcRefundOperateRecord. The operateDetail
     * field corresponds to the database column
     * ordercat.ordercat.oc_refund_operate_record.operate_detail.
     * 
     * @param operateDetail to set of this OcRefundOperateRecord
     * @return              this OcRefundOperateRecord instance
     */
    OcRefundOperateRecord setOperateDetail(String operateDetail);
    
    /**
     * Sets the addTime of this OcRefundOperateRecord. The addTime field
     * corresponds to the database column
     * ordercat.ordercat.oc_refund_operate_record.add_time.
     * 
     * @param addTime to set of this OcRefundOperateRecord
     * @return        this OcRefundOperateRecord instance
     */
    OcRefundOperateRecord setAddTime(LocalDateTime addTime);
    
    enum Identifier implements ColumnIdentifier<OcRefundOperateRecord> {
        
        ID             ("id"),
        REFUND_ID      ("refund_id"),
        TID            ("tid"),
        IS_DAIXIAO     ("is_daixiao"),
        STATUS         ("status"),
        REASON         ("reason"),
        SID            ("sid"),
        COMPANY_NAME   ("company_name"),
        OPERATE_DETAIL ("operate_detail"),
        ADD_TIME       ("add_time");
        
        private final String columnName;
        private final TableIdentifier<OcRefundOperateRecord> tableIdentifier;
        
        Identifier(String columnName) {
            this.columnName      = columnName;
            this.tableIdentifier = TableIdentifier.of(    getDbmsName(), 
                getSchemaName(), 
                getTableName());
        }
        
        @Override
        public String getDbmsName() {
            return "ordercat";
        }
        
        @Override
        public String getSchemaName() {
            return "ordercat";
        }
        
        @Override
        public String getTableName() {
            return "oc_refund_operate_record";
        }
        
        @Override
        public String getColumnName() {
            return this.columnName;
        }
        
        @Override
        public TableIdentifier<OcRefundOperateRecord> asTableIdentifier() {
            return this.tableIdentifier;
        }
    }
}