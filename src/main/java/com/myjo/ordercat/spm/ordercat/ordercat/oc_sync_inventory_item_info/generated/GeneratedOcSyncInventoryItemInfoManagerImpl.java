package com.myjo.ordercat.spm.ordercat.ordercat.oc_sync_inventory_item_info.generated;

import com.myjo.ordercat.spm.ordercat.ordercat.oc_sync_inventory_item_info.OcSyncInventoryItemInfo;
import com.speedment.common.annotation.GeneratedCode;
import com.speedment.runtime.config.identifier.TableIdentifier;
import com.speedment.runtime.core.manager.AbstractManager;
import com.speedment.runtime.field.Field;
import java.util.stream.Stream;

/**
 * The generated base implementation for the manager of every {@link
 * com.myjo.ordercat.spm.ordercat.ordercat.oc_sync_inventory_item_info.OcSyncInventoryItemInfo}
 * entity.
 * <p>
 * This file has been automatically generated by Speedment. Any changes made to
 * it will be overwritten.
 * 
 * @author Speedment
 */
@GeneratedCode("Speedment")
public abstract class GeneratedOcSyncInventoryItemInfoManagerImpl 
extends AbstractManager<OcSyncInventoryItemInfo> 
implements GeneratedOcSyncInventoryItemInfoManager {
    
    private final TableIdentifier<OcSyncInventoryItemInfo> tableIdentifier;
    
    protected GeneratedOcSyncInventoryItemInfoManagerImpl() {
        this.tableIdentifier = TableIdentifier.of("ordercat", "ordercat", "oc_sync_inventory_item_info");
    }
    
    @Override
    public TableIdentifier<OcSyncInventoryItemInfo> getTableIdentifier() {
        return tableIdentifier;
    }
    
    @Override
    public Stream<Field<OcSyncInventoryItemInfo>> fields() {
        return Stream.of(
            OcSyncInventoryItemInfo.ID,
            OcSyncInventoryItemInfo.NUM_IID,
            OcSyncInventoryItemInfo.STATUS,
            OcSyncInventoryItemInfo.ADD_TIME
        );
    }
    
    @Override
    public Stream<Field<OcSyncInventoryItemInfo>> primaryKeyFields() {
        return Stream.of(
            OcSyncInventoryItemInfo.ID
        );
    }
}