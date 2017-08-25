package com.myjo.ordercat.rest.api;

import com.aol.micro.server.auto.discovery.Rest;
import com.myjo.ordercat.context.OrderCatContext;
import com.myjo.ordercat.domain.PageResult;
import com.myjo.ordercat.domain.vo.ParamsVO;
import com.myjo.ordercat.spm.ordercat.ordercat.oc_params.OcParams;
import com.myjo.ordercat.spm.ordercat.ordercat.oc_params.OcParamsImpl;
import com.myjo.ordercat.spm.ordercat.ordercat.oc_params.OcParamsManager;
import com.myjo.ordercat.utils.OcDateTimeUtils;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Rest
@Api(value = "/params", description = "参数接口")
@Path("/params")
public class ParamsResource {

    private static final Logger Logger = LogManager.getLogger(ParamsResource.class);

    @GET
    @Produces("application/json;charset=utf-8")
    @Path("/list")
    @ApiOperation(value = "获取参数列表", response = PageResult.class)
    public Map<String, Object> list() {
        Map<String, Object> rt = new HashMap<>();
        boolean success;
        Object result;
        OcParamsManager ocParamsManager = OrderCatContext.getOcParamsManager();

        List<OcParams> list = ocParamsManager.stream()
                .collect(Collectors.toList());

        List<ParamsVO> voList = list.parallelStream()
                .map(params -> {
                    ParamsVO paramsVO = new ParamsVO();
                    paramsVO.setId(params.getId());
                    paramsVO.setKey(params.getPkey());
                    paramsVO.setValue(params.getPvalue());
                    paramsVO.setAddTime(OcDateTimeUtils.localDateTime2Date(params.getAddTime()));
                    return paramsVO;
                }).collect(Collectors.toList());
        success = true;
        result = voList;
        rt.put("success", success);
        rt.put("result", result);
        return rt;
    }


    @POST
    @Produces("application/json;charset=utf-8")
    @Path("/add")
    @ApiOperation(value = "添加参数", response = Map.class)
    public Map<String, Object> add(
            @ApiParam(required = true, name = "key", value = "键") @FormParam("key") String key,
            @ApiParam(required = true, name = "value", value = "值") @FormParam("value") String value) {


        Logger.info(String.format("key:%s,value:%s", key, value));
        Map<String, Object> rt = new HashMap<>();

        OcParamsManager ocParamsManager = OrderCatContext.getOcParamsManager();
        boolean success;
        String message;
        OcParams ocParams = new OcParamsImpl();
        ocParams.setPkey(key);
        ocParams.setPvalue(value);
        ocParams.setAddTime(LocalDateTime.now());
        ocParamsManager.persist(ocParams);
        success = true;
        message = String.format("key:[%s],value[%s]-新增成功!", key,value);
        rt.put("success", success);
        rt.put("message", message);
        return rt;
    }


    @POST
    @Produces("application/json;charset=utf-8")
    @Path("/edit")
    @ApiOperation(value = "编辑参数", response = Map.class)
    public Map<String, Object> edit(
            @ApiParam(required = true, name = "key", value = "键") @FormParam("key") String key,
            @ApiParam(required = true, name = "value", value = "值") @FormParam("value") String value) {
        Logger.info(String.format("key:%s,value:%s", key, value));
        Map<String, Object> rt = new HashMap<>();

        OcParamsManager ocParamsManager = OrderCatContext.getOcParamsManager();

        Optional<OcParams> opt = ocParamsManager.stream()
                .filter(OcParams.PKEY.equal(key))
                .findFirst();

        boolean success;
        String message;
        if(opt.isPresent()){
            OcParams ocParams = opt.get();
            ocParams.setPvalue(value);
            ocParamsManager.update(ocParams);

            success = true;
            message = String.format("Key[%s],value[%s]-修改成功!",key,value);
        }else {
            success = false;
            message = String.format("Key值[%s],不存在!",key);
        }

        rt.put("success", success);
        rt.put("message", message);
        return rt;
    }


}