package com.myjo.ordercat.rest.api;

import com.aol.micro.server.auto.discovery.Rest;
import com.myjo.ordercat.context.OrderCatContext;
import com.myjo.ordercat.domain.PageResult;
import com.myjo.ordercat.domain.ReturnResult;
import com.myjo.ordercat.domain.vo.RefundOperateRecordVO;
import com.myjo.ordercat.domain.vo.RemarkJson;
import com.myjo.ordercat.handle.OrderOperate;
import com.myjo.ordercat.http.TaoBaoHttp;
import com.myjo.ordercat.spm.ordercat.ordercat.oc_refund_operate_record.OcRefundOperateRecord;
import com.myjo.ordercat.spm.ordercat.ordercat.oc_refund_operate_record.OcRefundOperateRecordImpl;
import com.myjo.ordercat.spm.ordercat.ordercat.oc_refund_operate_record.OcRefundOperateRecordManager;
import com.myjo.ordercat.utils.OcDateTimeUtils;
import com.taobao.api.domain.RefundMappingResult;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.alibaba.fastjson.JSON;

import javax.ws.rs.*;
import java.time.LocalDateTime;
import java.util.*;
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
                .filter(OcRefundOperateRecord.REFUND_ID.equal(Long.valueOf(refundId))
                        .and(OcRefundOperateRecordImpl.IS_DELETE.equal((short) 0)))
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
                    refundOperateRecordVO.setTid(String.valueOf(refundOperateRecord.getTid().orElse(0l)));
                    refundOperateRecordVO.setRefundId(String.valueOf(refundOperateRecord.getRefundId().orElse(0l)));
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
            @ApiParam(name = "operateType", value = "操作类型") @QueryParam(value = "operateType") String operateType,
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
        predicateList.add(OcRefundOperateRecordImpl.IS_DELETE.equal((short) 0));



        if (refundId != null) {
            predicateList.add(OcRefundOperateRecordImpl.REFUND_ID.equal(Long.valueOf(refundId)));
        }

        if (operateType != null) {
            predicateList.add(OcRefundOperateRecordImpl.OPERATE_TYPE.equal(operateType));
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
                    refundOperateRecordVO.setTid(String.valueOf(refundOperateRecord.getTid().orElse(0l)));
                    refundOperateRecordVO.setRefundId(String.valueOf(refundOperateRecord.getRefundId().orElse(0l)));
                    refundOperateRecordVO.setReason(refundOperateRecord.getReason().orElse(""));
                    refundOperateRecordVO.setStatus(refundOperateRecord.getStatus().orElse(""));
                    refundOperateRecordVO.setOperateDetail(refundOperateRecord.getOperateDetail().orElse(""));
                    refundOperateRecordVO.setOperateResult(refundOperateRecord.getOperateResult().orElse(""));
                    refundOperateRecordVO.setOperateType(refundOperateRecord.getOperateType().orElse(""));
                    refundOperateRecordVO.setRemark(refundOperateRecord.getRemark().orElse(""));
                    return refundOperateRecordVO;
                }).collect(Collectors.toList());
        success = true;
        pageResult.setRows(list);


        result = pageResult;
        rt.put("success", success);
        rt.put("result", result);
        return rt;
    }


    @POST
    @Produces("application/json;charset=utf-8")
    @Path("/operate-records/addRemark")
    @ApiOperation(value = "添加对退款操作日志备注", response = Map.class)
    public Map<String, Object> addRemark(
            @ApiParam(required = true,name = "id", value = "ID") @FormParam("id") String id,
            @ApiParam(required = true,name = "remark", value = "备注") @FormParam("remark") String remark) {


        Logger.info(String.format("id:%s,remark:%s", id, remark));
        Map<String, Object> rt = new HashMap<>();

        OcRefundOperateRecordManager ocRefundOperateRecordManager = OrderCatContext.getOcRefundOperateRecordManager();
        boolean success;
        String message;

        Optional<OcRefundOperateRecord> o = ocRefundOperateRecordManager.stream()
                .filter(OcRefundOperateRecordImpl.ID.equal(Long.valueOf(id)))
                .findAny();
        //com.alibaba.fastjson.JSONObject object = JSON.parseObject(jsonstr);

        RemarkJson remarkJson = new RemarkJson();
        remarkJson.setAddTime(OcDateTimeUtils.localDateTime2String(LocalDateTime.now()));
        remarkJson.setRemark(remark);

        OcRefundOperateRecord ocRefundOperateRecord;
        if (o.isPresent()) {
            ocRefundOperateRecord = o.get();
            List<RemarkJson> remarkJsons;
            if(ocRefundOperateRecord.getRemark().isPresent()){
                remarkJsons = (List<RemarkJson>)JSON.parse(ocRefundOperateRecord.getRemark().get());
                remarkJsons.add(remarkJson);
            }else {
                remarkJsons = new ArrayList<>();
                remarkJsons.add(remarkJson);
            }
            ocRefundOperateRecord.setRemark( JSON.toJSONString(remarkJsons));
            ocRefundOperateRecordManager.update(ocRefundOperateRecord);
            success = true;
            message = String.format("备注成功!");
        } else {
            success = false;
            message = String.format("[%s]-ID不存在记录!", id);
        }
        rt.put("success", success);
        rt.put("message", message);
        return rt;
    }


    @POST
    @Produces("application/json;charset=utf-8")
    @Path("/operate-records/delete")
    @ApiOperation(value = "删除对退款操作日志备注", response = Map.class)
    public Map<String, Object> delete(
            @ApiParam(required = true,name = "id", value = "ID") @FormParam("id") String id) {

        Logger.info(String.format("id:%s", id));
        Map<String, Object> rt = new HashMap<>();

        OcRefundOperateRecordManager ocRefundOperateRecordManager = OrderCatContext.getOcRefundOperateRecordManager();
        boolean success;
        String message;

        Optional<OcRefundOperateRecord> o = ocRefundOperateRecordManager.stream()
                .filter(OcRefundOperateRecordImpl.ID.equal(Long.valueOf(id)))
                .findAny();
        //com.alibaba.fastjson.JSONObject object = JSON.parseObject(jsonstr);

        OcRefundOperateRecord ocRefundOperateRecord;
        if (o.isPresent()) {
            ocRefundOperateRecord = o.get();
            ocRefundOperateRecord.setIsDelete((short) 1);

            ocRefundOperateRecordManager.update(ocRefundOperateRecord);
            success = true;
            message = String.format("删除成功!");
        } else {
            success = false;
            message = String.format("[%s]-ID不存在记录!", id);
        }
        rt.put("success", success);
        rt.put("message", message);
        return rt;
    }



    @POST
    @Produces("application/json;charset=utf-8")
    @Path("/operate-records/agreeTaobaoRpRefunds")
    @ApiOperation(value = "批量同意退款", response = Map.class)
    public Map<String, Object> agreeTaobaoRpRefunds(
            @ApiParam(required = true,name = "refundInfos", value = "退款明细") @FormParam("refundInfos") String refundInfos,
            @ApiParam(required = true,name = "sessionKey", value = "sessionKey") @FormParam("sessionKey") String sessionKey,
            @ApiParam(required = true,name = "code", value = "短信验证码") @FormParam("code") String code) {

        Logger.info(String.format("refundInfos:%s", refundInfos));
        Logger.info(String.format("sessionKey:%s", sessionKey));
        Logger.info(String.format("code:%s", code));
        Map<String, Object> rt = new HashMap<>();

        TaoBaoHttp taoBaoHttp = OrderCatContext.getTaoBaoHttp();
        ReturnResult<RefundMappingResult> reto = taoBaoHttp.agreeTaobaoRpRefunds(refundInfos, sessionKey, code);
        boolean success;
        String message;
        //com.alibaba.fastjson.JSONObject object = JSON.parseObject(jsonstr);
        if (reto.isSuccess()) {
            success = true;
            message = String.format("退款成功!");
        } else {
            success = false;
            message = String.format(reto.getErrorMessages());
        }
        rt.put("success", success);
        rt.put("message", message);
        return rt;
    }


}