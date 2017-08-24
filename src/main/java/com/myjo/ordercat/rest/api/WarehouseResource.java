package com.myjo.ordercat.rest.api;

import com.aol.micro.server.auto.discovery.Rest;
import com.myjo.ordercat.context.OrderCatContext;
import com.myjo.ordercat.domain.constant.JobName;
import com.myjo.ordercat.domain.PageResult;
import com.myjo.ordercat.domain.vo.WarehouseInfoVO;
import com.myjo.ordercat.handle.OrderOperate;
import com.myjo.ordercat.spm.ordercat.ordercat.oc_job_exec_info.OcJobExecInfoManager;
import com.myjo.ordercat.spm.ordercat.ordercat.oc_warehouse_info.OcWarehouseInfo;
import com.myjo.ordercat.spm.ordercat.ordercat.oc_warehouse_info.OcWarehouseInfoManager;
import com.myjo.ordercat.utils.OcDateTimeUtils;
import com.myjo.ordercat.utils.OcJobUtils;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Rest
@Path("/warehouse")
@Api(value = "/warehouse", description = "对账信息接口")
public class WarehouseResource {

    private static final Logger Logger = LogManager.getLogger(OrderOperate.class);

    @GET
    @Produces("application/json;charset=utf-8")
    @Path("/current/list")
    @ApiOperation(value = "获取仓库列表", response = PageResult.class)
    public Map<String, Object> list() {
        Map<String, Object> rt = new HashMap<>();
        boolean success;
        Object result;

        OcWarehouseInfoManager ocWarehouseInfoManager = OrderCatContext.getOcWarehouseInfoManager();
        OcJobExecInfoManager ocJobExecInfoManager = OrderCatContext.getOcJobExecInfoManager();


        Integer jobId = OcJobUtils.getLastSuccessJobID(ocJobExecInfoManager, JobName.SYNC_WAREHOUSE_JOB.getValue());
        List<OcWarehouseInfo> list1 = ocWarehouseInfoManager.stream()
                .filter(OcWarehouseInfo.EXEC_JOB_ID.equal(jobId)
                        .and(OcWarehouseInfo.WAREHOUSE_ID.isNotNull()))
                .collect(Collectors.toList());


        List<WarehouseInfoVO> list = list1.parallelStream()
                .map(ocWarehouseInfo -> {
                    WarehouseInfoVO warehouseInfoVO = new WarehouseInfoVO();
                    warehouseInfoVO.setId(ocWarehouseInfo.getId());
                    warehouseInfoVO.setAddTime(OcDateTimeUtils.localDateTime2Date(ocWarehouseInfo.getAddTime()));
                    warehouseInfoVO.setEndT(ocWarehouseInfo.getEndT().orElse(""));
                    warehouseInfoVO.setExecJobId(ocWarehouseInfo.getExecJobId().orElse(0));
                    warehouseInfoVO.setExpressName(ocWarehouseInfo.getExpressName().orElse(""));
                    warehouseInfoVO.setMark(ocWarehouseInfo.getMark().orElse(""));
                    warehouseInfoVO.setPickDate(ocWarehouseInfo.getPickDate().orElse(0));
                    warehouseInfoVO.setPickRate(ocWarehouseInfo.getPickRate().orElse(0));
                    warehouseInfoVO.setReturnDesc(ocWarehouseInfo.getRetrunDesc().orElse(""));
                    warehouseInfoVO.setReturnRate(ocWarehouseInfo.getReturnRate().orElse(0));
                    warehouseInfoVO.setUpdateWarehouseTime(ocWarehouseInfo.getUdpateWarehouseTime().isPresent() ? OcDateTimeUtils.localDateTime2Date(ocWarehouseInfo.getUdpateWarehouseTime().get()) : null);
                    warehouseInfoVO.setWarehouseId(ocWarehouseInfo.getWarehouseId().orElse(0));
                    warehouseInfoVO.setWarehouseName(ocWarehouseInfo.getWarehouseName());
                    warehouseInfoVO.setThedTime(ocWarehouseInfo.getThedTime().orElse(0));

                    return warehouseInfoVO;
                }).collect(Collectors.toList());
        success = true;
        result = list;
        rt.put("success", success);
        rt.put("result", result);
        return rt;

    }


}