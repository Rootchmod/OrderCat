package com.myjo.ordercat.job;

import com.myjo.ordercat.handle.ExecuteHandle;
import org.quartz.*;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class SyncSalesInfoJob implements Job {


    public SyncSalesInfoJob() {
    }

    public void execute(JobExecutionContext context)
            throws JobExecutionException {
        JobDataMap map = context.getJobDetail().getJobDataMap();
        ExecuteHandle eh = (ExecuteHandle) map.get("SyncSalesInfoHandle");
        eh.exec();
    }
}