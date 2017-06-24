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
            @ApiParam(required = true,name = "payPwd", value = "密码") @FormParam("payPwd") String payPwd
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

//        Logger.info(String.format("tm_outer_order_id:%s,dz_status:%s,page_size:%d,page:%d",
//                tm_outer_order_id,
//                dz_status,
//                page_size,
//                page
//        ));
        PageResult<OcTmsportCheckResultVO> pageResult = new PageResult<>();

        OcTmsportCheckResultManager ocTmsportCheckResultManager = OrderCatContext.getOcTmsportCheckResultManager();


        List<Predicate<OcTmsportCheckResult>> predicateList = new ArrayList<>();

//        if (tm_outer_order_id != null) {
//            predicateList.add(OcTmsportCheckResultImpl.TM_OUTER_ORDER_ID.equal(tm_outer_order_id));
//        }
//
//        if (dz_status != null) {
//            predicateList.add(OcTmsportCheckResultImpl.DZ_STATUS.equal(dz_status));
//        }

        Predicate<OcTmsportCheckResult> p1 = ocTmsportCheckResult -> true;

        for (Predicate<OcTmsportCheckResult> p : predicateList) {
            p1 = p1.and(p);
        }
        //Stream<OcTmsportCheckResult> stream = ;

        long count = ocTmsportCheckResultManager.stream()
                .filter(p1)
                .count();
        pageResult.setTotal(count);


        List<OcTmsportCheckResult> list = ocTmsportCheckResultManager.stream()
                .filter(p1)
                .sorted(OcTmsportCheckResultImpl.ADD_TIME.comparator())
                .skip((page - 1) * page_size)
                .limit(page_size)
                .collect(Collectors.toList());

        List<OcTmsportCheckResultVO> tianmaCheckResultList = list.parallelStream()
                .map(o -> {
                    OcTmsportCheckResultVO tianmaCheckResult = new OcTmsportCheckResultVO();
                    tianmaCheckResult.setId(o.getId());
                    tianmaCheckResult.setTmOuterOrderId(o.getTmOuterOrderId().get());
                    tianmaCheckResult.setTmOrderNum(o.getTmOrderNum().getAsLong());
                    tianmaCheckResult.setTmNum(o.getTmNum().getAsLong());
                    tianmaCheckResult.setTbOrderNum(o.getTbOrderNum().getAsLong());
                    tianmaCheckResult.setTbNum(o.getTbNum().getAsLong());
                    tianmaCheckResult.setTbCreated(OcDateTimeUtils.localDateTime2Date(o.getTbCreated().get()));
                    tianmaCheckResult.setTbPaytime(OcDateTimeUtils.localDateTime2Date(o.getTbPaytime().get()));
                    tianmaCheckResult.setTbPrice(o.getTbPrice().get());
                    tianmaCheckResult.setTbPayment(o.getTbPayment().get());
                    tianmaCheckResult.setTbDiscountFee(o.getTbDiscountFee().get());
                    tianmaCheckResult.setTbTotalFee(o.getTbTotalFee().get());
                    tianmaCheckResult.setDzStatus(o.getDzStatus().get());
                    tianmaCheckResult.setDzDetailsMessage(o.getDzDetailsMessage().get());
                    tianmaCheckResult.setRemarks(o.getRemarks().isPresent() ? o.getRemarks().get() : "");
                    tianmaCheckResult.setAddTime(OcDateTimeUtils.localDateTime2Date(o.getAddTime()));
                    return tianmaCheckResult;
                })
                .collect(Collectors.toList());
        pageResult.setRows(tianmaCheckResultList);


        return pageResult;
    }




}