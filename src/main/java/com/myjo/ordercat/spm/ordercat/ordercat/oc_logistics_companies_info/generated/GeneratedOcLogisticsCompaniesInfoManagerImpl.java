package com.myjo.ordercat.spm.ordercat.ordercat.oc_logistics_companies_info.generated;

import com.myjo.ordercat.spm.ordercat.ordercat.oc_logistics_companies_info.OcLogisticsCompaniesInfo;
import com.speedment.runtime.config.identifier.TableIdentifier;
import com.speedment.runtime.core.annotation.GeneratedCode;
import com.speedment.runtime.core.manager.AbstractManager;
import com.speedment.runtime.field.Field;
import java.util.stream.Stream;

/**
 * The generated base implementation for the manager of every {@link
 * com.myjo.ordercat.spm.ordercat.ordercat.oc_logistics_companies_info.OcLogisticsCompaniesInfo}
 * entity.
 * <p>
 * This file has been automatically generated by Speedment. Any changes made to
 * it will be overwritten.
 * 
 * @author Speedment
 */
@GeneratedCode("Speedment")
public abstract class GeneratedOcLogisticsCompaniesInfoManagerImpl extends AbstractManager<OcLogisticsCompaniesInfo> implements GeneratedOcLogisticsCompaniesInfoManager {
    
    private final TableIdentifier<OcLogisticsCompaniesInfo> tableIdentifier;
    
    protected GeneratedOcLogisticsCompaniesInfoManagerImpl() {
        this.tableIdentifier = TableIdentifier.of("ordercat", "ordercat", "oc_logistics_companies_info");
    }
    
    @Override
    public TableIdentifier<OcLogisticsCompaniesInfo> getTableIdentifier() {
        return tableIdentifier;
    }
    
    @Override
    public Stream<Field<OcLogisticsCompaniesInfo>> fields() {
        return Stream.of(
            OcLogisticsCompaniesInfo.ID,
            OcLogisticsCompaniesInfo.LC_ID,
            OcLogisticsCompaniesInfo.LC_CODE,
            OcLogisticsCompaniesInfo.LC_NAME,
            OcLogisticsCompaniesInfo.LC_REG_MAIL_NO,
            OcLogisticsCompaniesInfo.EXEC_JOB_ID,
            OcLogisticsCompaniesInfo.ADD_TIME
        );
    }
    
    @Override
    public Stream<Field<OcLogisticsCompaniesInfo>> primaryKeyFields() {
        return Stream.of(
            OcLogisticsCompaniesInfo.ID
        );
    }
}