package com.myjo.ordercat.spm.ordercat.ordercat.oc_job_exec_info.generated;

import com.myjo.ordercat.spm.ordercat.ordercat.oc_job_exec_info.OcJobExecInfo;
import com.speedment.runtime.core.annotation.GeneratedCode;
import com.speedment.runtime.core.manager.Manager;

/**
 * The generated base interface for the manager of every {@link
 * com.myjo.ordercat.spm.ordercat.ordercat.oc_job_exec_info.OcJobExecInfo}
 * entity.
 * <p>
 * This file has been automatically generated by Speedment. Any changes made to
 * it will be overwritten.
 * 
 * @author Speedment
 */
@GeneratedCode("Speedment")
public interface GeneratedOcJobExecInfoManager extends Manager<OcJobExecInfo> {
    
    @Override
    default Class<OcJobExecInfo> getEntityClass() {
        return OcJobExecInfo.class;
    }
}