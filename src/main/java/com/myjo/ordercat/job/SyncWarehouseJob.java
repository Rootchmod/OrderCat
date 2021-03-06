package com.myjo.ordercat.job;

import com.myjo.ordercat.handle.ExecuteHandle;
import org.quartz.*;

import java.util.Date;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class SyncWarehouseJob implements Job {


    public SyncWarehouseJob() {
    }
    public void execute(JobExecutionContext context)
        throws JobExecutionException {
        JobDataMap map = context.getJobDetail().getJobDataMap();
        ExecuteHandle eh =  (ExecuteHandle) map.get("SyncWarehouseHandle");
        eh.exec();
    }
}