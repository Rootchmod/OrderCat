package com.myjo.ordercat.job;

import com.myjo.ordercat.domain.LogisticsCompany;
import com.myjo.ordercat.handle.ExecuteHandle;
import com.myjo.ordercat.http.TianmaSportHttp;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.Optional;
import java.util.Random;

/**
 * Created by lee5hx on 17/5/11.
 */
public class GuessMailNoKeepJob implements Job {

    private static final Logger Logger = LogManager.getLogger(SyncLcJob.class);

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobDataMap map = context.getJobDetail().getJobDataMap();
        TianmaSportHttp tianmaSportHttp = (TianmaSportHttp) map.get("TianmaSportHttp");

        try {

            Random random = new Random();

            long mailNo = random.nextLong();
            if(mailNo<0){
                mailNo = mailNo*-1;
            }
            long tradeId = random.nextLong();
            if(tradeId<0){
                tradeId = tradeId*-1;
            }

            Optional<LogisticsCompany> logisticsCompany =
                    tianmaSportHttp.ajaxGuessMailNoRequest(String.valueOf(mailNo), String.valueOf(tradeId));
            if (logisticsCompany.isPresent()) {
                Logger.info(String.format("淘宝快递公司猜测接口-可用性维持[%s]-[%s]-[%s]", String.valueOf(mailNo), String.valueOf(tradeId), logisticsCompany.get().getCode()));
            }
        }catch (Exception e){
            Logger.error(e);
        }
    }
}
