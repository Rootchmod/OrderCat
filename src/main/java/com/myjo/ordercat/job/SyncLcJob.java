package com.myjo.ordercat.job;

import com.myjo.ordercat.handle.ExecuteHandle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.*;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class SyncLcJob extends OcBaseJob {

    private static final Logger Logger = LogManager.getLogger(SyncLcJob.class);

    @Override
    protected ExecuteHandle execHandle(JobDataMap map) {
        Logger.info("SyncLcJob.execHandle");
        return (ExecuteHandle) map.get("SyncLcHandle");
    }
}