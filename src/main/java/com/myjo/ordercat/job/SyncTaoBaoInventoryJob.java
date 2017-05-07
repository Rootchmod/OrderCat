package com.myjo.ordercat.job;

import com.myjo.ordercat.handle.ExecuteHandle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.*;

import java.util.Date;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class SyncTaoBaoInventoryJob implements Job {


    private static final Logger Logger = LogManager.getLogger(SyncSalesInfoJob.class);

    public SyncTaoBaoInventoryJob() {
    }
    public void execute(JobExecutionContext context)
        throws JobExecutionException {
        JobDataMap map = context.getJobDetail().getJobDataMap();
        ExecuteHandle eh =  (ExecuteHandle) map.get("SyncTaoBaoInventoryHandle");
        try {
            eh.exec();
        } catch (Exception e) {
            Logger.error("--- Error in job!");
            JobExecutionException e2 =
                    new JobExecutionException(e);
            // Quartz will automatically unschedule
            // all triggers associated with this job
            // so that it does not run again
            e2.setUnscheduleAllTriggers(true);
            throw e2;
        }
    }
}