package com.myjo.ordercat.spm.ordercat.ordercat.oc_inventory_info.generated;

import com.myjo.ordercat.spm.ordercat.ordercat.oc_inventory_info.OcInventoryInfo;
import com.speedment.common.annotation.GeneratedCode;
import com.speedment.runtime.core.manager.Manager;

/**
 * The generated base interface for the manager of every {@link
 * com.myjo.ordercat.spm.ordercat.ordercat.oc_inventory_info.OcInventoryInfo}
 * entity.
 * <p>
 * This file has been automatically generated by Speedment. Any changes made to
 * it will be overwritten.
 * 
 * @author Speedment
 */
@GeneratedCode("Speedment")
public interface GeneratedOcInventoryInfoManager extends Manager<OcInventoryInfo> {
    
    @Override
    default Class<OcInventoryInfo> getEntityClass() {
        return OcInventoryInfo.class;
    }
}