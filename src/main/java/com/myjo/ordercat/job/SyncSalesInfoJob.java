package com.myjo.ordercat.job;

import com.myjo.ordercat.handle.ExecuteHandle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.*;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class SyncSalesInfoJob extends OcBaseJob {

    private static final Logger Logger = LogManager.getLogger(SyncSalesInfoJob.class);

    @Override
    protected ExecuteHandle execHandle(JobDataMap map) {
        Logger.info("SyncSalesInfoJob.execHandle");
        return (ExecuteHandle) map.get("SyncSalesInfoHandle");
    }
}