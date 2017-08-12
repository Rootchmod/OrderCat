package com.myjo.ordercat.rest.api;

import com.aol.micro.server.auto.discovery.Rest;
import com.myjo.ordercat.context.OrderCatContext;
import com.myjo.ordercat.domain.PageResult;
import com.myjo.ordercat.domain.vo.RefundOperateRecordVO;
import com.myjo.ordercat.handle.OrderOperate;
import com.myjo.ordercat.spm.ordercat.ordercat.oc_refund_operate_record.OcRefundOperateRecord;
import com.myjo.ordercat.spm.ordercat.ordercat.oc_refund_operate_record.OcRefundOperateRecordManager;
import com.myjo.ordercat.spm.ordercat.ordercat.oc_tm_order_records.OcTmOrderRecordsImpl;
import com.myjo.ordercat.utils.OcDateTimeUtils;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Rest
@Path("/refund-operate")
@Api(value = "/refund-operate", description = "自动退款接口")
public class RefundOperateResource {

    private static final Logger Logger = LogManager.getLogger(OrderOperate.class);

    @GET
    @Produces("application/json;charset=utf-8")
    @Path("/list")
    @ApiOperation(value = "获取自动退款操作日志", response = PageResult.class)
    public Map<String, Object> list(
            @ApiParam(required = true, name = "refundId", value = "退款ID") @QueryParam("refundId") String refundId) {
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
                    return refundOperateRecordVO;
                }).collect(Collectors.toList());
        success = true;
        result = list;
        rt.put("success", success);
        rt.put("result", result);
        return rt;

    }


}