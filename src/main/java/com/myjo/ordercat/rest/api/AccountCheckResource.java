package com.myjo.ordercat.rest.api;

import com.alibaba.fastjson.JSON;
import com.aol.micro.server.auto.discovery.Rest;
import com.myjo.ordercat.context.OrderCatContext;
import com.myjo.ordercat.domain.PageResult;
import com.myjo.ordercat.domain.vo.OcFenxiaoCheckResultVO;
import com.myjo.ordercat.domain.vo.OcTmsportCheckResultVO;
import com.myjo.ordercat.domain.vo.RemarkJson;
import com.myjo.ordercat.handle.OrderOperate;
import com.myjo.ordercat.spm.ordercat.ordercat.oc_fenxiao_check_result.OcFenxiaoCheckResult;
import com.myjo.ordercat.spm.ordercat.ordercat.oc_fenxiao_check_result.OcFenxiaoCheckResultImpl;
import com.myjo.ordercat.spm.ordercat.ordercat.oc_fenxiao_check_result.OcFenxiaoCheckResultManager;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Rest
@Path("/account-check")
@Api(value = "/account-check", description = "对账信息接口")
public class AccountCheckResource {


    //private final static int PAGE_SIZE = 50;

    private static final Logger Logger = LogManager.getLogger(OrderOperate.class);

   //docker run --name oc-mysql -v /Users/lee5hx/docker/mysql/data:/var/lib/mysql -v /Users/lee5hx/docker/mysql/conf:/etc/mysql -e MYSQL_ROOT_PASSWORD=123456 -d mysql:5.7





    @GET
    @Produces("application/json;charset=utf-8")
    @Path("/tmsport/check/list")
    @ApiOperation(value = "天马对账信息查询", response = PageResult.class)
    public PageResult<OcTmsportCheckResultVO> tmsportCheckList(
            @ApiParam(name = "tm_outer_order_id", value = "淘宝订单ID") @QueryParam("tm_outer_order_id") String tm_outer_order_id,
            @ApiParam(name = "dz_status", value = "对账状态") @QueryParam("dz_status") String dz_status,
            @ApiParam(name = "begin_time", value = "开始时间") @QueryParam("begin_time") String begin_time,
            @ApiParam(name = "end_time", value = "结束时间") @QueryParam("end_time") String end_time,
            @ApiParam(name = "order_begin_time", value = "开始时间") @QueryParam("order_begin_time") String order_begin_time,
            @ApiParam(name = "order_end_time", value = "结束时间") @QueryParam("order_end_time") String order_end_time,
            @ApiParam(name = "is_dz_success", value = "是否过滤对账成功") @QueryParam("is_dz_success") String is_dz_success,
            @ApiParam(name = "labour_status", value = "人工对账状态") @QueryParam("labour_status") String labour_status,
            @ApiParam(required = true, name = "page_size", value = "分页大小") @QueryParam("page_size") int page_size,
            @ApiParam(required = true, name = "page", value = "当前页") @QueryParam("page") int page
    ) {
        if (page_size > 200) {
            page_size = 200;
        }

        Logger.info(String.format("tm_outer_order_id:%s,dz_status:%s,begin_time:%s,end_time:%s,page_size:%d,page:%d",
                tm_outer_order_id,
                dz_status,
                begin_time,
                end_time,
                page_size,
                page
        ));
        PageResult<OcTmsportCheckResultVO> pageResult = new PageResult<>();

        OcTmsportCheckResultManager ocTmsportCheckResultManager = OrderCatContext.getOcTmsportCheckResultManager();


        List<Predicate<OcTmsportCheckResult>> predicateList = new ArrayList<>();



        if(labour_status!=null){
            predicateList.add(OcTmsportCheckResultImpl.LABOUR_STATUS.equal(labour_status));
        }

        if(is_dz_success!=null){
            predicateList.add(OcTmsportCheckResultImpl.DZ_STATUS.notEqual("DZ_SUCCESS"));
        }

        if(begin_time!=null){
            predicateList.add(OcTmsportCheckResultImpl.ADD_TIME.greaterOrEqual(OcDateTimeUtils.string2LocalDateTime(begin_time)));
        }

        if(end_time!=null){
            predicateList.add(OcTmsportCheckResultImpl.ADD_TIME.lessOrEqual(OcDateTimeUtils.string2LocalDateTime(end_time)));
        }

        if(order_begin_time!=null){
            predicateList.add(OcTmsportCheckResultImpl.TB_CREATED.greaterOrEqual(OcDateTimeUtils.string2LocalDateTime(order_begin_time)));
        }

        if(order_end_time!=null){
            predicateList.add(OcTmsportCheckResultImpl.TB_CREATED.lessOrEqual(OcDateTimeUtils.string2LocalDateTime(order_end_time)));
        }

        if (tm_outer_order_id != null) {
            predicateList.add(OcTmsportCheckResultImpl.TM_OUTER_ORDER_ID.equal(tm_outer_order_id));
        }

        if (dz_status != null) {
            predicateList.add(OcTmsportCheckResultImpl.DZ_STATUS.equal(dz_status));
        }

        Predicate<OcTmsportCheckResult> p1 = null;
        long count;
        List<OcTmsportCheckResult> list;
        if(predicateList.size() == 0){
            count = ocTmsportCheckResultManager.stream()
                    .count();
            list = ocTmsportCheckResultManager.stream()
                    .sorted(OcTmsportCheckResultImpl.ADD_TIME.comparator())
                    .skip((page - 1) * page_size)
                    .limit(page_size)
                    .collect(Collectors.toList());
        }else {
            for (Predicate<OcTmsportCheckResult> p : predicateList) {
                if(p1 ==null){
                    p1 = p;
                }else {
                    p1 = p1.and(p);
                }
            }
            count = ocTmsportCheckResultManager.stream()
                    .filter(p1)
                    .count();
            list = ocTmsportCheckResultManager.stream()
                    .filter(p1)
                    .sorted(OcTmsportCheckResultImpl.ADD_TIME.comparator())
                    .skip((page - 1) * page_size)
                    .limit(page_size)
                    .collect(Collectors.toList());
        }


        for (Predicate<OcTmsportCheckResult> p : predicateList) {
            p1 = p1.and(p);
        }
        pageResult.setTotal(count);
        List<OcTmsportCheckResultVO> tianmaCheckResultList = list.parallelStream()
                .map(o -> {
                    OcTmsportCheckResultVO tianmaCheckResult = new OcTmsportCheckResultVO();
                    tianmaCheckResult.setId(o.getId());
                    tianmaCheckResult.setTmOuterOrderId(o.getTmOuterOrderId().orElse(""));
                    tianmaCheckResult.setTmOrderIds(o.getTmOrderIds().orElse(""));
                    tianmaCheckResult.setTmOrderNum(o.getTmOrderNum().orElse(0));
                    tianmaCheckResult.setTmNum(o.getTmNum().orElse(0));
                    tianmaCheckResult.setTbOrderNum(o.getTbOrderNum().orElse(0));
                    tianmaCheckResult.setTbTitle(o.getTbTitle().orElse(""));
                    tianmaCheckResult.setTbNickName(o.getTbNickname().orElse(""));
                    tianmaCheckResult.setLabourStatus(o.getLabourStatus().orElse(""));
                    tianmaCheckResult.setTbNum(o.getTbNum().orElse(0));
                    tianmaCheckResult.setTbCreated(o.getTbCreated().isPresent()?OcDateTimeUtils.localDateTime2Date(o.getTbCreated().get()):null);
                    tianmaCheckResult.setTbPaytime(o.getTbPaytime().isPresent()?OcDateTimeUtils.localDateTime2Date(o.getTbPaytime().get()):null);
                    tianmaCheckResult.setTbPrice(o.getTbPrice().orElse(BigDecimal.ZERO));
                    tianmaCheckResult.setTbPayment(o.getTbPayment().orElse(BigDecimal.ZERO));
                    tianmaCheckResult.setTbDiscountFee(o.getTbDiscountFee().orElse(BigDecimal.ZERO));
                    tianmaCheckResult.setTbTotalFee(o.getTbTotalFee().orElse(BigDecimal.ZERO));
                    tianmaCheckResult.setDzStatus(o.getDzStatus().orElse(""));
                    tianmaCheckResult.setDzDetailsMessage(o.getDzDetailsMessage().orElse(""));
                    tianmaCheckResult.setLabourStatus(o.getLabourStatus().orElse(""));
                    tianmaCheckResult.setRemarks(o.getRemarks().orElse(""));
                    tianmaCheckResult.setAddTime(OcDateTimeUtils.localDateTime2Date(o.getAddTime()));
                    return tianmaCheckResult;
                })
                .collect(Collectors.toList());
        pageResult.setRows(tianmaCheckResultList);
        return pageResult;
    }


    @POST
    @Produces("application/json;charset=utf-8")
    @Path("/tmsport/check/addRemark")
    @ApiOperation(value = "添加天马对账备注", response = Map.class)
    public Map<String, Object> tmsportCheckAddRemark(
            @ApiParam(required = true,name = "id", value = "对账ID") @FormParam("id") String id,
            @ApiParam(required = true,name = "remark", value = "备注") @FormParam("remark") String remark) {


        Logger.info(String.format("id:%s,remark:%s", id, remark));
        Map<String, Object> rt = new HashMap<>();

        OcTmsportCheckResultManager ocTmsportCheckResultManager = OrderCatContext.getOcTmsportCheckResultManager();
        boolean success;
        String message;

        Optional<OcTmsportCheckResult> o = ocTmsportCheckResultManager.stream()
                .filter(OcTmsportCheckResultImpl.ID.equal(Long.valueOf(id)))
                .findAny();
        //com.alibaba.fastjson.JSONObject object = JSON.parseObject(jsonstr);

        RemarkJson remarkJson = new RemarkJson();
        remarkJson.setAddTime(OcDateTimeUtils.localDateTime2String(LocalDateTime.now()));
        remarkJson.setRemark(remark);

        OcTmsportCheckResult ocTmsportCheckResult;
        if (o.isPresent()) {
            ocTmsportCheckResult = o.get();
            List<RemarkJson> remarkJsons;
            if(ocTmsportCheckResult.getRemarks().isPresent()){
                remarkJsons = (List<RemarkJson>)JSON.parse(ocTmsportCheckResult.getRemarks().get());
                remarkJsons.add(remarkJson);
            }else {
                remarkJsons = new ArrayList<>();
                remarkJsons.add(remarkJson);
            }
            ocTmsportCheckResult.setRemarks( JSON.toJSONString(remarkJsons));
            ocTmsportCheckResultManager.update(ocTmsportCheckResult);
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
    @Path("/tmsport/check/addLbourStatuLs")
    @ApiOperation(value = "添加天马对账人工状态", response = Map.class)
    public Map<String, Object> tmsportCheckAddLabourStatus(
            @ApiParam(required = true,name = "id", value = "对账ID") @FormParam("id") String id,
            @ApiParam(required = true,name = "labourStatus", value = "备注") @FormParam("labourStatus") String labourStatus) {


        Logger.info(String.format("id:%s,LabourStatus:%s", id, labourStatus));
        Map<String, Object> rt = new HashMap<>();

        OcTmsportCheckResultManager ocTmsportCheckResultManager = OrderCatContext.getOcTmsportCheckResultManager();
        boolean success;
        String message;

        Optional<OcTmsportCheckResult> o = ocTmsportCheckResultManager.stream()
                .filter(OcTmsportCheckResultImpl.ID.equal(Long.valueOf(id)))
                .findAny();
        OcTmsportCheckResult ocTmsportCheckResult;
        if (o.isPresent()) {
            ocTmsportCheckResult = o.get();
            ocTmsportCheckResult.setLabourStatus(labourStatus);
            ocTmsportCheckResultManager.update(ocTmsportCheckResult);
            success = true;
            message = String.format("人工状态设置成功-[%s]!",labourStatus);
        } else {
            success = false;
            message = String.format("[%s]-ID不存在记录!", id);
        }
        rt.put("success", success);
        rt.put("message", message);
        return rt;
    }



}