package com.myjo.ordercat.spm.ordercat.ordercat.oc_tmsport_check_result.generated;

import com.myjo.ordercat.spm.ordercat.ordercat.oc_tmsport_check_result.OcTmsportCheckResult;
import com.myjo.ordercat.spm.ordercat.ordercat.oc_tmsport_check_result.OcTmsportCheckResultImpl;
import com.speedment.common.injector.annotation.ExecuteBefore;
import com.speedment.common.injector.annotation.WithState;
import com.speedment.runtime.config.Project;
import com.speedment.runtime.config.identifier.TableIdentifier;
import com.speedment.runtime.core.annotation.GeneratedCode;
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
 * com.myjo.ordercat.spm.ordercat.ordercat.oc_tmsport_check_result.OcTmsportCheckResult}
 * entity.
 * <p>
 * This file has been automatically generated by Speedment. Any changes made to
 * it will be overwritten.
 * 
 * @author Speedment
 */
@GeneratedCode("Speedment")
public abstract class GeneratedOcTmsportCheckResultSqlAdapter {
    
    private final TableIdentifier<OcTmsportCheckResult> tableIdentifier;
    private SqlTypeMapperHelper<Timestamp, LocalDateTime> tbCreatedHelper;
    private SqlTypeMapperHelper<Timestamp, LocalDateTime> tbPaytimeHelper;
    private SqlTypeMapperHelper<Timestamp, LocalDateTime> addTimeHelper;
    
    protected GeneratedOcTmsportCheckResultSqlAdapter() {
        this.tableIdentifier = TableIdentifier.of("ordercat", "ordercat", "oc_tmsport_check_result");
    }
    
    @ExecuteBefore(RESOLVED)
    void installMethodName(@WithState(RESOLVED) SqlStreamSupplierComponent streamSupplierComponent, @WithState(RESOLVED) SqlPersistenceComponent persistenceComponent) {
        streamSupplierComponent.install(tableIdentifier, this::apply);
        persistenceComponent.install(tableIdentifier);
    }
    
    protected OcTmsportCheckResult apply(ResultSet resultSet) throws SpeedmentException {
        final OcTmsportCheckResult entity = createEntity();
        try {
            entity.setId(               resultSet.getLong(1)                             );
            entity.setTmOuterOrderId(   resultSet.getString(2)                           );
            entity.setTmOrderNum(       getLong(resultSet, 3)                            );
            entity.setTmNum(            getLong(resultSet, 4)                            );
            entity.setTbOrderNum(       getLong(resultSet, 5)                            );
            entity.setTbNum(            getLong(resultSet, 6)                            );
            entity.setTbCreated(        tbCreatedHelper.apply(resultSet.getTimestamp(7)) );
            entity.setTbPaytime(        tbPaytimeHelper.apply(resultSet.getTimestamp(8)) );
            entity.setTbPrice(          resultSet.getBigDecimal(9)                       );
            entity.setTbPayment(        resultSet.getBigDecimal(10)                      );
            entity.setTbDiscountFee(    resultSet.getBigDecimal(11)                      );
            entity.setTbTotalFee(       resultSet.getBigDecimal(12)                      );
            entity.setDzStatus(         resultSet.getString(13)                          );
            entity.setDzDetailsMessage( resultSet.getString(14)                          );
            entity.setRemarks(          resultSet.getString(15)                          );
            entity.setAddTime(          addTimeHelper.apply(resultSet.getTimestamp(16))  );
        } catch (final SQLException sqle) {
            throw new SpeedmentException(sqle);
        }
        return entity;
    }
    
    protected OcTmsportCheckResultImpl createEntity() {
        return new OcTmsportCheckResultImpl();
    }
    
    @ExecuteBefore(RESOLVED)
    void createHelpers(ProjectComponent projectComponent) {
        final Project project = projectComponent.getProject();
        tbCreatedHelper = SqlTypeMapperHelper.create(project, OcTmsportCheckResult.TB_CREATED, OcTmsportCheckResult.class);
        tbPaytimeHelper = SqlTypeMapperHelper.create(project, OcTmsportCheckResult.TB_PAYTIME, OcTmsportCheckResult.class);
        addTimeHelper = SqlTypeMapperHelper.create(project, OcTmsportCheckResult.ADD_TIME, OcTmsportCheckResult.class);
    }
}