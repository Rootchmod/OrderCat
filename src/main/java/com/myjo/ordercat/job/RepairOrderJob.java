package com.myjo.ordercat.job;

import com.myjo.ordercat.handle.ExecuteHandle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobDataMap;
import org.quartz.PersistJobDataAfterExecution;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class RepairOrderJob extends OcBaseJob {

    private static final Logger Logger = LogManager.getLogger(RepairOrderJob.class);

    @Override
    protected ExecuteHandle execHandle(JobDataMap map) {
        Logger.info("RepairOrderJob.execHandle");
        return (ExecuteHandle) map.get("RepairOrderHandle");
    }
}