package com.myjo.ordercat.job;

import com.myjo.ordercat.handle.ExecuteHandle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.*;

import java.util.Date;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class SyncWarehouseJob extends OcBaseJob {

    private static final Logger Logger = LogManager.getLogger(SyncWarehouseJob.class);

    @Override
    protected ExecuteHandle execHandle(JobDataMap map) {
        Logger.info("SyncWarehouseJob.execHandle");
        return (ExecuteHandle) map.get("SyncWarehouseHandle");
    }
}