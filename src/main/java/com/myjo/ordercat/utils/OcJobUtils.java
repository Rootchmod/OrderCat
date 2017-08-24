package com.myjo.ordercat.utils;

import com.myjo.ordercat.domain.constant.JobStatus;
import com.myjo.ordercat.exception.OCException;
import com.myjo.ordercat.spm.ordercat.ordercat.oc_job_exec_info.OcJobExecInfo;
import com.myjo.ordercat.spm.ordercat.ordercat.oc_job_exec_info.OcJobExecInfoManager;

import java.util.Optional;

/**
 * Created by lee5hx on 17/5/14.
 */
public class OcJobUtils {



    public static Integer getLastSuccessJobID(OcJobExecInfoManager ocJobExecInfoManager, String jobName) {
        Integer execJobId;
        Optional<OcJobExecInfo> oexecJob = ocJobExecInfoManager.stream()
                .filter(OcJobExecInfo.STATUS.equal(JobStatus.SUCCESS.toString())
                        .and(OcJobExecInfo.JOB_NAME.equal(jobName))
                ).sorted(OcJobExecInfo.ID.comparator().reversed()).findFirst();

        if (oexecJob.isPresent()) {
            execJobId = (int) oexecJob.get().getId();
        } else {
            throw new OCException(String.format("对不起,没有找到对应的执行信息信息[%s]!", jobName));
        }
        return execJobId;
    }
}
