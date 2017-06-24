package com.myjo.ordercat.job;

import com.myjo.ordercat.handle.ExecuteHandle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobDataMap;
import org.quartz.PersistJobDataAfterExecution;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class AutoSendGoodsJob extends OcBaseJob {

    private static final Logger Logger = LogManager.getLogger(AutoSendGoodsJob.class);

    @Override
    protected ExecuteHandle execHandle(JobDataMap map) {
        Logger.info("AutoSendGoodsJob.execHandle");
        return (ExecuteHandle) map.get("AutoSendHandle");
    }
}