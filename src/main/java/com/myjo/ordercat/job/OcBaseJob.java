package com.myjo.ordercat.job;

import com.myjo.ordercat.handle.ExecuteHandle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * Created by lee5hx on 17/5/11.
 */
public abstract class OcBaseJob implements Job {

    private static final Logger Logger = LogManager.getLogger(SyncLcJob.class);

    protected abstract ExecuteHandle execHandle(JobDataMap map);

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {

        ExecuteHandle eh = null;
        try {
            JobDataMap map = context.getJobDetail().getJobDataMap();
            eh = execHandle(map);
            eh.exec();
        } catch (Exception e) {
            Logger.error("--- Error in job:"+eh.getJobName(),e);
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
