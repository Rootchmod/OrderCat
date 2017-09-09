package com.myjo.ordercat.rest.api;

import com.aol.micro.server.auto.discovery.Rest;
import com.myjo.ordercat.context.OrderCatContext;
import com.myjo.ordercat.domain.PageResult;
import com.myjo.ordercat.domain.vo.ParamsVO;
import com.myjo.ordercat.spm.ordercat.ordercat.oc_job_exec_info.OcJobExecInfo;
import com.myjo.ordercat.spm.ordercat.ordercat.oc_job_exec_info.OcJobExecInfoManager;
import com.myjo.ordercat.spm.ordercat.ordercat.oc_params.OcParams;
import com.myjo.ordercat.spm.ordercat.ordercat.oc_params.OcParamsImpl;
import com.myjo.ordercat.spm.ordercat.ordercat.oc_params.OcParamsManager;
import com.myjo.ordercat.utils.OcDateTimeUtils;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import cyclops.monads.Witness;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ws.rs.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Rest
@Api(value = "/jobs", description = "任务接口")
@Path("/jobs")
public class JobResource {

    private static final Logger Logger = LogManager.getLogger(JobResource.class);

//    @GET
//    @Produces("application/json;charset=utf-8")
//    @Path("/list")
//    @ApiOperation(value = "获取JOB执行列表", response = PageResult.class)
//    public Map<String, Object> list(
//            @ApiParam(required = true, name = "page_size", value = "分页大小") @QueryParam("page_size") int page_size,
//            @ApiParam(required = true, name = "page", value = "当前页") @QueryParam("page") int page
//    ) {
//
//        Map<String, Object> rt = new HashMap<>();
//        boolean success;
//        Object result;
//        OcJobExecInfoManager ocJobExecInfoManager = OrderCatContext.getOcJobExecInfoManager();
//
//        List<OcJobExecInfo> list = ocJobExecInfoManager.stream()
//                .collect(Collectors.toList());
//
//        List<ParamsVO> voList = Witness.list.parallelStream()
//                .map(params -> {
//                    ParamsVO paramsVO = new ParamsVO();
//                    paramsVO.setId(params.getId());
//                    paramsVO.setKey(params.getPkey());
//                    paramsVO.setValue(params.getPvalue());
//                    paramsVO.setAddTime(OcDateTimeUtils.localDateTime2Date(params.getAddTime()));
//                    return paramsVO;
//                }).collect(Collectors.toList());
//        success = true;
//        result = voList;
//        rt.put("success", success);
//        rt.put("result", result);
//        return rt;
//    }


    


}