package com.myjo.ordercat.spm.ordercat.ordercat.oc_warehouse_info.generated;

import com.myjo.ordercat.spm.ordercat.ordercat.oc_warehouse_info.OcWarehouseInfo;
import com.speedment.common.annotation.GeneratedCode;
import com.speedment.runtime.core.manager.Manager;

/**
 * The generated base interface for the manager of every {@link
 * com.myjo.ordercat.spm.ordercat.ordercat.oc_warehouse_info.OcWarehouseInfo}
 * entity.
 * <p>
 * This file has been automatically generated by Speedment. Any changes made to
 * it will be overwritten.
 * 
 * @author Speedment
 */
@GeneratedCode("Speedment")
public interface GeneratedOcWarehouseInfoManager extends Manager<OcWarehouseInfo> {
    
    @Override
    default Class<OcWarehouseInfo> getEntityClass() {
        return OcWarehouseInfo.class;
    }
}