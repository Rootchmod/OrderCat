package com.myjo.ordercat.spm.ordercat.ordercat.oc_sync_inventory_item_info.generated;

import com.myjo.ordercat.spm.ordercat.ordercat.oc_sync_inventory_item_info.OcSyncInventoryItemInfo;
import com.speedment.runtime.core.annotation.GeneratedCode;
import com.speedment.runtime.core.manager.Manager;

/**
 * The generated base interface for the manager of every {@link
 * com.myjo.ordercat.spm.ordercat.ordercat.oc_sync_inventory_item_info.OcSyncInventoryItemInfo}
 * entity.
 * <p>
 * This file has been automatically generated by Speedment. Any changes made to
 * it will be overwritten.
 * 
 * @author Speedment
 */
@GeneratedCode("Speedment")
public interface GeneratedOcSyncInventoryItemInfoManager extends Manager<OcSyncInventoryItemInfo> {
    
    @Override
    default Class<OcSyncInventoryItemInfo> getEntityClass() {
        return OcSyncInventoryItemInfo.class;
    }
}