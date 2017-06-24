package com.myjo.ordercat.rest.api;

import com.aol.micro.server.auto.discovery.Rest;
import com.myjo.ordercat.context.OrderCatContext;
import com.myjo.ordercat.domain.PageResult;
import com.myjo.ordercat.domain.TmOrderRecordStatus;
import com.myjo.ordercat.domain.vo.OcFenxiaoCheckResultVO;
import com.myjo.ordercat.domain.vo.OcTmsportCheckResultVO;
import com.myjo.ordercat.handle.OrderOperate;
import com.myjo.ordercat.spm.ordercat.ordercat.oc_fenxiao_check_result.OcFenxiaoCheckResult;
import com.myjo.ordercat.spm.ordercat.ordercat.oc_fenxiao_check_result.OcFenxiaoCheckResultImpl;
import com.myjo.ordercat.spm.ordercat.ordercat.oc_fenxiao_check_result.OcFenxiaoCheckResultManager;
import com.myjo.ordercat.spm.ordercat.ordercat.oc_tm_order_records.OcTmOrderRecords;
import com.myjo.ordercat.spm.ordercat.ordercat.oc_tmsport_check_result.OcTmsportCheckResult;
import com.myjo.ordercat.spm.ordercat.ordercat.oc_tmsport_check_result.OcTmsportCheckResultImpl;
import com.myjo.ordercat.spm.ordercat.ordercat.oc_tmsport_check_result.OcTmsportCheckResultManager;
import com.myjo.ordercat.utils.OcDateTimeUtils;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ws.rs.*;
import java.math.BigDecimal;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Rest
@Path("/order-operate")
@Api(value = "/order-operate", description = "订单操作接口")
public class OrderOperateResource {
    //private final static int PAGE_SIZE = 50;
    private static final Logger Logger = LogManager.getLogger(OrderOperate.class);

    @POST
    @Produces("application/json;charset=utf-8")
    @Path("/manualOrder")
    @ApiOperation(value = "手工下单接口", response = Map.class)
    public Map<String, Object> fenxiaoCheckAddRemark(
            @ApiParam(required = true,name = "tid", value = "对账ID") @FormParam("tid") String tid,
            @ApiParam(required = true,name = "wareHouseId", value = "备注") @FormParam("wareHouseId") String wareHouseId,
            @ApiParam(required = true,name = "payPwd", value = "备注") @FormParam("payPwd") String payPwd
            ) {
        Logger.info(String.format("/order-operate/manualOrder tid:%s,wareHouseId:%s,payPwd:*******", tid, wareHouseId));
        Map<String, Object> rt = new HashMap<>();
        OrderOperate oop = OrderCatContext.getOrderOperate();
        OcTmOrderRecords ocTmOrderRecords = null;
        boolean success;
        String message;
        try{
            ocTmOrderRecords = oop.manualOrder(Long.valueOf(tid),wareHouseId,payPwd,null,null);
            if(TmOrderRecordStatus.FAILURE.getValue().equals(ocTmOrderRecords.getStatus().get())){
                success = false;
                message = "下单失败:"+ocTmOrderRecords.getFailCause().get();
            }else {
                success = true;
                message = "下单成功!";
            }
        }catch (Exception e){
            success = false;
            message = e.getMessage();
        }
        rt.put("success", success);
        rt.put("message", message);
        rt.put("ocTmOrderRecords", ocTmOrderRecords);
        return rt;
    }


    @GET
    @Produces("application/json;charset=utf-8")
    @Path("/orderRecords")
    @ApiOperation(value = "下单记录", response = PageResult.class)
    public PageResult<OcTmsportCheckResultVO> tmsportCheckList(
            @ApiParam(required = true, name = "page_size", value = "分页大小") @QueryParam("page_size") int page_size,
            @ApiParam(required = true, name = "page", value = "当前页") @QueryParam("page") int page
    ) {



        return null;
    }




}