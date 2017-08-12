package com.myjo.ordercat.spm.ordercat.ordercat.oc_refund_operate_record.generated;

import com.myjo.ordercat.spm.ordercat.ordercat.oc_refund_operate_record.OcRefundOperateRecord;
import com.myjo.ordercat.spm.ordercat.ordercat.oc_refund_operate_record.OcRefundOperateRecordImpl;
import com.speedment.common.annotation.GeneratedCode;
import com.speedment.common.injector.annotation.ExecuteBefore;
import com.speedment.common.injector.annotation.WithState;
import com.speedment.runtime.config.Project;
import com.speedment.runtime.config.identifier.TableIdentifier;
import com.speedment.runtime.core.component.ProjectComponent;
import com.speedment.runtime.core.component.sql.SqlPersistenceComponent;
import com.speedment.runtime.core.component.sql.SqlStreamSupplierComponent;
import com.speedment.runtime.core.component.sql.SqlTypeMapperHelper;
import com.speedment.runtime.core.exception.SpeedmentException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import static com.speedment.common.injector.State.RESOLVED;
import static com.speedment.runtime.core.internal.util.sql.ResultSetUtil.*;

/**
 * The generated Sql Adapter for a {@link
 * com.myjo.ordercat.spm.ordercat.ordercat.oc_refund_operate_record.OcRefundOperateRecord}
 * entity.
 * <p>
 * This file has been automatically generated by Speedment. Any changes made to
 * it will be overwritten.
 * 
 * @author Speedment
 */
@GeneratedCode("Speedment")
public abstract class GeneratedOcRefundOperateRecordSqlAdapter {
    
    private final TableIdentifier<OcRefundOperateRecord> tableIdentifier;
    private SqlTypeMapperHelper<Timestamp, LocalDateTime> addTimeHelper;
    
    protected GeneratedOcRefundOperateRecordSqlAdapter() {
        this.tableIdentifier = TableIdentifier.of("ordercat", "ordercat", "oc_refund_operate_record");
    }
    
    @ExecuteBefore(RESOLVED)
    void installMethodName(@WithState(RESOLVED) SqlStreamSupplierComponent streamSupplierComponent,
            @WithState(RESOLVED) SqlPersistenceComponent persistenceComponent) {
        streamSupplierComponent.install(tableIdentifier, this::apply);
        persistenceComponent.install(tableIdentifier);
    }
    
    protected OcRefundOperateRecord apply(ResultSet resultSet) throws SpeedmentException {
        final OcRefundOperateRecord entity = createEntity();
        try {
            entity.setId(            resultSet.getLong(1)                            );
            entity.setRefundId(      getLong(resultSet, 2)                           );
            entity.setTid(           getLong(resultSet, 3)                           );
            entity.setIsDaixiao(     getShort(resultSet, 4)                          );
            entity.setStatus(        resultSet.getString(5)                          );
            entity.setReason(        resultSet.getString(6)                          );
            entity.setSid(           resultSet.getString(7)                          );
            entity.setCompanyName(   resultSet.getString(8)                          );
            entity.setOperateDetail( resultSet.getString(9)                          );
            entity.setAddTime(       addTimeHelper.apply(resultSet.getTimestamp(10)) );
        } catch (final SQLException sqle) {
            throw new SpeedmentException(sqle);
        }
        return entity;
    }
    
    protected OcRefundOperateRecordImpl createEntity() {
        return new OcRefundOperateRecordImpl();
    }
    
    @ExecuteBefore(RESOLVED)
    void createHelpers(ProjectComponent projectComponent) {
        final Project project = projectComponent.getProject();
        addTimeHelper = SqlTypeMapperHelper.create(project, OcRefundOperateRecord.ADD_TIME, OcRefundOperateRecord.class);
    }
}