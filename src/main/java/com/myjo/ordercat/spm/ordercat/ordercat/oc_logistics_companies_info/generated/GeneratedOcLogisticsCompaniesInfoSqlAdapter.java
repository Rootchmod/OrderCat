package com.myjo.ordercat.spm.ordercat.ordercat.oc_logistics_companies_info.generated;

import com.myjo.ordercat.spm.ordercat.ordercat.oc_logistics_companies_info.OcLogisticsCompaniesInfo;
import com.myjo.ordercat.spm.ordercat.ordercat.oc_logistics_companies_info.OcLogisticsCompaniesInfoImpl;
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
 * com.myjo.ordercat.spm.ordercat.ordercat.oc_logistics_companies_info.OcLogisticsCompaniesInfo}
 * entity.
 * <p>
 * This file has been automatically generated by Speedment. Any changes made to
 * it will be overwritten.
 * 
 * @author Speedment
 */
@GeneratedCode("Speedment")
public abstract class GeneratedOcLogisticsCompaniesInfoSqlAdapter {
    
    private final TableIdentifier<OcLogisticsCompaniesInfo> tableIdentifier;
    private SqlTypeMapperHelper<Timestamp, LocalDateTime> addTimeHelper;
    
    protected GeneratedOcLogisticsCompaniesInfoSqlAdapter() {
        this.tableIdentifier = TableIdentifier.of("ordercat", "ordercat", "oc_logistics_companies_info");
    }
    
    @ExecuteBefore(RESOLVED)
    void installMethodName(@WithState(RESOLVED) SqlStreamSupplierComponent streamSupplierComponent, @WithState(RESOLVED) SqlPersistenceComponent persistenceComponent) {
        streamSupplierComponent.install(tableIdentifier, this::apply);
        persistenceComponent.install(tableIdentifier);
    }
    
    protected OcLogisticsCompaniesInfo apply(ResultSet resultSet) throws SpeedmentException {
        final OcLogisticsCompaniesInfo entity = createEntity();
        try {
            entity.setId(          resultSet.getLong(1)                           );
            entity.setLcId(        getLong(resultSet, 2)                          );
            entity.setLcCode(      resultSet.getString(3)                         );
            entity.setLcName(      resultSet.getString(4)                         );
            entity.setLcRegMailNo( resultSet.getString(5)                         );
            entity.setExecJobId(   getLong(resultSet, 6)                          );
            entity.setAddTime(     addTimeHelper.apply(resultSet.getTimestamp(7)) );
        } catch (final SQLException sqle) {
            throw new SpeedmentException(sqle);
        }
        return entity;
    }
    
    protected OcLogisticsCompaniesInfoImpl createEntity() {
        return new OcLogisticsCompaniesInfoImpl();
    }
    
    @ExecuteBefore(RESOLVED)
    void createHelpers(ProjectComponent projectComponent) {
        final Project project = projectComponent.getProject();
        addTimeHelper = SqlTypeMapperHelper.create(project, OcLogisticsCompaniesInfo.ADD_TIME, OcLogisticsCompaniesInfo.class);
    }
}