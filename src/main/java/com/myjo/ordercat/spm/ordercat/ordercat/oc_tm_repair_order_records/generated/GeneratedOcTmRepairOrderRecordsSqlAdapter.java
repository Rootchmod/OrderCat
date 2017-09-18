package com.myjo.ordercat.spm.ordercat.ordercat.oc_tm_repair_order_records.generated;

import com.myjo.ordercat.spm.ordercat.ordercat.oc_tm_repair_order_records.OcTmRepairOrderRecords;
import com.myjo.ordercat.spm.ordercat.ordercat.oc_tm_repair_order_records.OcTmRepairOrderRecordsImpl;
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
 * com.myjo.ordercat.spm.ordercat.ordercat.oc_tm_repair_order_records.OcTmRepairOrderRecords}
 * entity.
 * <p>
 * This file has been automatically generated by Speedment. Any changes made to
 * it will be overwritten.
 * 
 * @author Speedment
 */
@GeneratedCode("Speedment")
public abstract class GeneratedOcTmRepairOrderRecordsSqlAdapter {
    
    private final TableIdentifier<OcTmRepairOrderRecords> tableIdentifier;
    private SqlTypeMapperHelper<Timestamp, LocalDateTime> ptcOdealDateHelper;
    private SqlTypeMapperHelper<Timestamp, LocalDateTime> roWhUpdateTimeHelper;
    private SqlTypeMapperHelper<Timestamp, LocalDateTime> addTimeHelper;
    
    protected GeneratedOcTmRepairOrderRecordsSqlAdapter() {
        this.tableIdentifier = TableIdentifier.of("ordercat", "ordercat", "oc_tm_repair_order_records");
    }
    
    @ExecuteBefore(RESOLVED)
    void installMethodName(@WithState(RESOLVED) SqlStreamSupplierComponent streamSupplierComponent,
            @WithState(RESOLVED) SqlPersistenceComponent persistenceComponent) {
        streamSupplierComponent.install(tableIdentifier, this::apply);
        persistenceComponent.install(tableIdentifier);
    }
    
    protected OcTmRepairOrderRecords apply(ResultSet resultSet) throws SpeedmentException {
        final OcTmRepairOrderRecords entity = createEntity();
        try {
            entity.setId(                 resultSet.getLong(1)                                   );
            entity.setOuterOrderId(       resultSet.getString(2)                                 );
            entity.setPctTmOrderId(       resultSet.getString(3)                                 );
            entity.setPtcOdealDescr(      resultSet.getString(4)                                 );
            entity.setPtcOdealDate(       ptcOdealDateHelper.apply(resultSet.getTimestamp(5))    );
            entity.setPtcTmTradeRemark(   resultSet.getString(6)                                 );
            entity.setGoodsNo(            resultSet.getString(7)                                 );
            entity.setSize1(              resultSet.getString(8)                                 );
            entity.setSize2(              resultSet.getString(9)                                 );
            entity.setPtcOrderStatus(     resultSet.getString(10)                                );
            entity.setCustomerName(       resultSet.getString(11)                                );
            entity.setPayPrice(           resultSet.getBigDecimal(12)                            );
            entity.setPtcMwhId(           getInt(resultSet, 13)                                  );
            entity.setPtcMwhName(         resultSet.getString(14)                                );
            entity.setRoWhSnapshotData(   resultSet.getString(15)                                );
            entity.setRoTmSizeInfoStr(    resultSet.getString(16)                                );
            entity.setRoTmSkuId(          resultSet.getString(17)                                );
            entity.setRoWhId(             getInt(resultSet, 18)                                  );
            entity.setRoWhName(           resultSet.getString(19)                                );
            entity.setRoWhPickRate(       getInt(resultSet, 20)                                  );
            entity.setRoWhProxyPrice(     resultSet.getBigDecimal(21)                            );
            entity.setRoWhUpdateTime(     roWhUpdateTimeHelper.apply(resultSet.getTimestamp(22)) );
            entity.setRoWhInventoryCount( getInt(resultSet, 23)                                  );
            entity.setRoFreightPriceStr(  resultSet.getString(24)                                );
            entity.setRoStatus(           resultSet.getString(25)                                );
            entity.setRoOrderInfo(        resultSet.getString(26)                                );
            entity.setRoTmOrderId(        resultSet.getString(27)                                );
            entity.setRoFailCause(        resultSet.getString(28)                                );
            entity.setRoBreakEvenPrice(   resultSet.getBigDecimal(29)                            );
            entity.setAddTime(            addTimeHelper.apply(resultSet.getTimestamp(30))        );
        } catch (final SQLException sqle) {
            throw new SpeedmentException(sqle);
        }
        return entity;
    }
    
    protected OcTmRepairOrderRecordsImpl createEntity() {
        return new OcTmRepairOrderRecordsImpl();
    }
    
    @ExecuteBefore(RESOLVED)
    void createHelpers(ProjectComponent projectComponent) {
        final Project project = projectComponent.getProject();
        ptcOdealDateHelper = SqlTypeMapperHelper.create(project, OcTmRepairOrderRecords.PTC_ODEAL_DATE, OcTmRepairOrderRecords.class);
        roWhUpdateTimeHelper = SqlTypeMapperHelper.create(project, OcTmRepairOrderRecords.RO_WH_UPDATE_TIME, OcTmRepairOrderRecords.class);
        addTimeHelper = SqlTypeMapperHelper.create(project, OcTmRepairOrderRecords.ADD_TIME, OcTmRepairOrderRecords.class);
    }
}