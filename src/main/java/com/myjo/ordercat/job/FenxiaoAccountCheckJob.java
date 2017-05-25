package com.myjo.ordercat.job;

import com.myjo.ordercat.handle.ExecuteHandle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobDataMap;
import org.quartz.PersistJobDataAfterExecution;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class FenxiaoAccountCheckJob extends OcBaseJob {

    private static final Logger Logger = LogManager.getLogger(FenxiaoAccountCheckJob.class);

    @Override
    protected ExecuteHandle execHandle(JobDataMap map) {
        Logger.info("FenxiaoAccountCheckJob.execHandle");
        return (ExecuteHandle) map.get("FenXiaoAcHandle");
    }
}