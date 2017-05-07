package com.myjo.ordercat.job;

import com.myjo.ordercat.handle.ExecuteHandle;
import com.myjo.ordercat.http.TianmaSportHttp;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.*;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class SyncSalesInfoJob implements Job {



    private static final Logger Logger = LogManager.getLogger(SyncSalesInfoJob.class);


    public SyncSalesInfoJob() {
    }

    public void execute(JobExecutionContext context)
            throws JobExecutionException {
        JobDataMap map = context.getJobDetail().getJobDataMap();
        ExecuteHandle eh = (ExecuteHandle) map.get("SyncSalesInfoHandle");


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