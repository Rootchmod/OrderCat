package com.myjo.ordercat.handle;

import com.myjo.ordercat.domain.JobStatus;
import com.myjo.ordercat.spm.ordercat.ordercat.oc_job_exec_info.OcJobExecInfo;
import com.myjo.ordercat.spm.ordercat.ordercat.oc_job_exec_info.OcJobExecInfoImpl;
import com.myjo.ordercat.spm.ordercat.ordercat.oc_job_exec_info.OcJobExecInfoManager;
import org.apache.commons.io.IOUtils;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Created by lee5hx on 17/4/30.
 */
public abstract class ExecuteHandle {

    private OcJobExecInfoManager ocJobExecInfoManager;

    public abstract void exec(Long execJobId) throws Exception;

    protected String jobName = "";

    public void exec(){

        //String jobName =  "SyncWarehouseJob";
        Long execJobId ;
        LocalDateTime begin = LocalDateTime.now();
        LocalDateTime end;

        OcJobExecInfo ocJobExecInfo = new OcJobExecInfoImpl();
        ocJobExecInfo.setJobName(jobName);
        ocJobExecInfo.setBeginTime(begin);
        ocJobExecInfo.setStatus(JobStatus.RUNNING.toString());
        ocJobExecInfoManager.persist(ocJobExecInfo);
        execJobId = ocJobExecInfo.getId();

        try {
            exec(execJobId);
            end = LocalDateTime.now();
            long elapsed = Duration.between(begin,end).getSeconds();
            ocJobExecInfoManager.stream()
                    .filter(OcJobExecInfo.ID.equal(execJobId))
                    .map(OcJobExecInfo.STATUS
                            .setTo(JobStatus.SUCCESS.toString())
                            .andThen(OcJobExecInfo.END_TIME.setTo(end))
                            .andThen(OcJobExecInfo.ELAPSED.setTo(elapsed)))
                    .forEach(ocJobExecInfoManager.updater());
        } catch (Exception e) {

            end = LocalDateTime.now();
            long elapsed = Duration.between(begin,end).getSeconds();

            e.printStackTrace();

            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);


            ocJobExecInfoManager.stream()
                    .filter(OcJobExecInfo.ID.equal(execJobId))
                    .map(OcJobExecInfo.STATUS
                            .setTo(JobStatus.FAILURE.toString())
                            .andThen(OcJobExecInfo.END_TIME.setTo(end))
                            .andThen(OcJobExecInfo.ERROR_MESSAGE.setTo(sw.toString()))
                            .andThen(OcJobExecInfo.ELAPSED.setTo(elapsed)))
                    .forEach(ocJobExecInfoManager.updater());
        }finally {

        }
    }

    public OcJobExecInfoManager getOcJobExecInfoManager() {
        return ocJobExecInfoManager;
    }

    public void setOcJobExecInfoManager(OcJobExecInfoManager ocJobExecInfoManager) {
        this.ocJobExecInfoManager = ocJobExecInfoManager;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }
}
