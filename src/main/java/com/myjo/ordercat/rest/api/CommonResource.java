package com.myjo.ordercat.rest.api;

import com.aol.micro.server.auto.discovery.Rest;
import com.myjo.ordercat.context.OrderCatContext;
import com.myjo.ordercat.domain.JobName;
import com.myjo.ordercat.spm.ordercat.ordercat.oc_job_exec_info.OcJobExecInfo;
import com.myjo.ordercat.spm.ordercat.ordercat.oc_job_exec_info.OcJobExecInfoManager;
import com.myjo.ordercat.spm.ordercat.ordercat.oc_warehouse_info.OcWarehouseInfo;
import com.myjo.ordercat.spm.ordercat.ordercat.oc_warehouse_info.OcWarehouseInfoImpl;
import com.myjo.ordercat.spm.ordercat.ordercat.oc_warehouse_info.OcWarehouseInfoManager;
import com.myjo.ordercat.utils.OcJobUtils;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by lee5hx on 17/6/19.
 */

@Rest
@Path("/common")
public class CommonResource {
    @GET
    @Produces("application/json;charset=utf-8")
    @Path("/wh-list")
    public List<OcWarehouseInfo> whlist() {
        OcWarehouseInfoManager ocWarehouseInfoManager = OrderCatContext.getOcWarehouseInfoManager();
        OcJobExecInfoManager ocJobExecInfoManager = OrderCatContext.getOcJobExecInfoManager();
        Integer jobID = OcJobUtils.getLastSuccessJobID(ocJobExecInfoManager,JobName.SYNC_WAREHOUSE_JOB.getValue());
        List<OcWarehouseInfo> warehouseInfoList = ocWarehouseInfoManager.stream()
                .filter(OcWarehouseInfo.EXEC_JOB_ID.equal(jobID)
                        .and(OcWarehouseInfo.WAREHOUSE_ID.isNotNull()))
                .collect(
                        Collectors.toList());

        return warehouseInfoList;
    }
}
