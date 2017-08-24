package com.myjo.ordercat.rest.api;

import com.aol.micro.server.auto.discovery.Rest;
import com.myjo.ordercat.context.OrderCatContext;
import com.myjo.ordercat.domain.PageResult;
import com.myjo.ordercat.domain.vo.RefundOperateRecordVO;
import com.myjo.ordercat.handle.OrderOperate;
import com.myjo.ordercat.spm.ordercat.ordercat.oc_refund_operate_record.OcRefundOperateRecord;
import com.myjo.ordercat.spm.ordercat.ordercat.oc_refund_operate_record.OcRefundOperateRecordImpl;
import com.myjo.ordercat.spm.ordercat.ordercat.oc_refund_operate_record.OcRefundOperateRecordManager;
import com.myjo.ordercat.utils.OcDateTimeUtils;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ws.rs.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Rest
@Path("/auto-refund")
@Api(value = "/auto-refund", description = "自动退款接口")
public class RefundOperateResource {

    private static final Logger Logger = LogManager.getLogger(OrderOperate.class);

    @GET
    @Produces("application/json;charset=utf-8")
    @Path("/operate-records/${refundId}")
    @ApiOperation(value = "获取自动退款操作日志", response = PageResult.class)
    public Map<String, Object> operateRecordsByRefundId(
            @ApiParam(required = true, name = "refundId", value = "退款ID") @PathParam(value = "refundId") String refundId) {
        Map<String, Object> rt = new HashMap<>();
        boolean success;
        Object result;

        OcRefundOperateRecordManager ocRefundOperateRecordManager = OrderCatContext.getOcRefundOperateRecordManager();


        //Integer jobId = OcJobUtils.getLastSuccessJobID(ocJobExecInfoManager, JobName.SYNC_WAREHOUSE_JOB.getValue());
        List<OcRefundOperateRecord> list1 = ocRefundOperateRecordManager.stream()
                .filter(OcRefundOperateRecord.REFUND_ID.equal(Long.valueOf(refundId)))
                .sorted(OcRefundOperateRecord.ADD_TIME.comparator().reversed())
                .collect(Collectors.toList());


        List<RefundOperateRecordVO> list = list1.parallelStream()
                .map(refundOperateRecord -> {
                    RefundOperateRecordVO refundOperateRecordVO = new RefundOperateRecordVO();
                    refundOperateRecordVO.setAddTime(OcDateTimeUtils.localDateTime2Date(refundOperateRecord.getAddTime()));
                    refundOperateRecordVO.setId(refundOperateRecord.getId());
                    refundOperateRecordVO.setCompanyName(refundOperateRecord.getCompanyName().orElse(""));
                    refundOperateRecordVO.setSid(refundOperateRecord.getSid().orElse(""));
                    refundOperateRecordVO.setIsDaixiao(refundOperateRecord.getIsDaixiao().orElse(null));
                    refundOperateRecordVO.setTid(refundOperateRecord.getTid().orElse(0l));
                    refundOperateRecordVO.setRefundId(refundOperateRecord.getRefundId().orElse(0l));
                    refundOperateRecordVO.setReason(refundOperateRecord.getReason().orElse(""));
                    refundOperateRecordVO.setStatus(refundOperateRecord.getStatus().orElse(""));
                    refundOperateRecordVO.setOperateDetail(refundOperateRecord.getOperateDetail().orElse(""));
                    refundOperateRecordVO.setOperateResult(refundOperateRecord.getOperateResult().orElse(""));
                    refundOperateRecordVO.setOperateType(refundOperateRecord.getOperateType().orElse(""));
                    return refundOperateRecordVO;
                }).collect(Collectors.toList());
        success = true;
        result = list;
        rt.put("success", success);
        rt.put("result", result);
        return rt;
    }


    @GET
    @Produces("application/json;charset=utf-8")
    @Path("/operate-records")
    @ApiOperation(value = "获取自动退款操作日志", response = PageResult.class)
    public Map<String, Object> operateRecords(
            @ApiParam(name = "refundId", value = "退款ID") @QueryParam(value = "refundId") String refundId,
            @ApiParam(name = "operateType", value = "退款ID") @QueryParam(value = "operateType") String operateType,
            @ApiParam(required = true, name = "page_size", value = "分页大小") @QueryParam("page_size") int page_size,
            @ApiParam(required = true, name = "page", value = "当前页") @QueryParam("page") int page
    ) {
        Map<String, Object> rt = new HashMap<>();
        boolean success;
        Object result;


        PageResult<RefundOperateRecordVO> pageResult = new PageResult<>();
        OcRefundOperateRecordManager ocRefundOperateRecordManager = OrderCatContext.getOcRefundOperateRecordManager();
        List<Predicate<OcRefundOperateRecord>> predicateList = new ArrayList<>();


        predicateList.add(OcRefundOperateRecordImpl.IS_LATEST.equal((short) 1));


        if (refundId != null) {
            predicateList.add(OcRefundOperateRecordImpl.REFUND_ID.greaterOrEqual(Long.valueOf(refundId)));
        }

        if (operateType != null) {
            predicateList.add(OcRefundOperateRecordImpl.OPERATE_TYPE.greaterOrEqual(operateType));
        }

        Predicate<OcRefundOperateRecord> p1 = null;


        //Stream<OcTmsportCheckResult> stream = ;
        long count;
        List<OcRefundOperateRecord> list1;

        if (predicateList.size() == 0) {
            count = ocRefundOperateRecordManager.stream()
                    .count();
            list1 = ocRefundOperateRecordManager.stream()
                    .sorted(OcRefundOperateRecordImpl.ID.comparator().reversed())
                    .skip((page - 1) * page_size)
                    .limit(page_size)
                    .collect(Collectors.toList());
        } else {

            for (Predicate<OcRefundOperateRecord> p : predicateList) {
                if (p1 == null) {
                    p1 = p;
                } else {
                    p1 = p1.and(p);
                }

            }
            count = ocRefundOperateRecordManager.stream()
                    .filter(p1)
                    .count();
            list1 = ocRefundOperateRecordManager.stream()
                    .filter(p1)
                    .sorted(OcRefundOperateRecordImpl.ID.comparator().reversed())
                    .skip((page - 1) * page_size)
                    .limit(page_size)
                    .collect(Collectors.toList());
        }


        pageResult.setTotal(count);


        List<RefundOperateRecordVO> list = list1.parallelStream()
                .map(refundOperateRecord -> {
                    RefundOperateRecordVO refundOperateRecordVO = new RefundOperateRecordVO();
                    refundOperateRecordVO.setAddTime(OcDateTimeUtils.localDateTime2Date(refundOperateRecord.getAddTime()));
                    refundOperateRecordVO.setId(refundOperateRecord.getId());
                    refundOperateRecordVO.setCompanyName(refundOperateRecord.getCompanyName().orElse(""));
                    refundOperateRecordVO.setSid(refundOperateRecord.getSid().orElse(""));
                    refundOperateRecordVO.setIsDaixiao(refundOperateRecord.getIsDaixiao().orElse(null));
                    refundOperateRecordVO.setTid(refundOperateRecord.getTid().orElse(0l));
                    refundOperateRecordVO.setRefundId(refundOperateRecord.getRefundId().orElse(0l));
                    refundOperateRecordVO.setReason(refundOperateRecord.getReason().orElse(""));
                    refundOperateRecordVO.setStatus(refundOperateRecord.getStatus().orElse(""));
                    refundOperateRecordVO.setOperateDetail(refundOperateRecord.getOperateDetail().orElse(""));
                    refundOperateRecordVO.setOperateResult(refundOperateRecord.getOperateResult().orElse(""));
                    refundOperateRecordVO.setOperateType(refundOperateRecord.getOperateType().orElse(""));
                    return refundOperateRecordVO;
                }).collect(Collectors.toList());
        success = true;
        pageResult.setRows(list);


        result = pageResult;
        rt.put("success", success);
        rt.put("result", result);
        return rt;
    }


}