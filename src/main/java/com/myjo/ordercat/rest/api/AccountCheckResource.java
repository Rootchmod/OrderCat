package com.myjo.ordercat.rest.api;

import com.aol.micro.server.auto.discovery.Rest;
import com.myjo.ordercat.context.OrderCatContext;
import com.myjo.ordercat.domain.PageResult;
import com.myjo.ordercat.domain.vo.OcFenxiaoCheckResultVO;
import com.myjo.ordercat.domain.vo.OcTmsportCheckResultVO;
import com.myjo.ordercat.handle.OrderOperate;
import com.myjo.ordercat.spm.ordercat.ordercat.oc_fenxiao_check_result.OcFenxiaoCheckResult;
import com.myjo.ordercat.spm.ordercat.ordercat.oc_fenxiao_check_result.OcFenxiaoCheckResultImpl;
import com.myjo.ordercat.spm.ordercat.ordercat.oc_fenxiao_check_result.OcFenxiaoCheckResultManager;
import com.myjo.ordercat.spm.ordercat.ordercat.oc_tmsport_check_result.OcTmsportCheckResult;
import com.myjo.ordercat.spm.ordercat.ordercat.oc_tmsport_check_result.OcTmsportCheckResultImpl;
import com.myjo.ordercat.spm.ordercat.ordercat.oc_tmsport_check_result.OcTmsportCheckResultManager;
import com.myjo.ordercat.utils.OcDateTimeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ws.rs.*;
import java.math.BigDecimal;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Rest
@Path("/account-check")
public class AccountCheckResource {


    //private final static int PAGE_SIZE = 50;

    private static final Logger Logger = LogManager.getLogger(OrderOperate.class);


    @GET
    @Produces("application/json;charset=utf-8")
    @Path("/tmsport/check/list")

    public PageResult<OcTmsportCheckResultVO> tmsportCheckList(
            @QueryParam("tm_outer_order_id") String tm_outer_order_id,
            @QueryParam("dz_status") String dz_status,
            @QueryParam("page_size") int page_size,
            @QueryParam("page") int page
    ) {

        Logger.info(String.format("tm_outer_order_id:%s,dz_status:%s,page_size:%d,page:%d",
                tm_outer_order_id,
                dz_status,
                page_size,
                page
        ));
        PageResult<OcTmsportCheckResultVO> pageResult = new PageResult<>();

        OcTmsportCheckResultManager ocTmsportCheckResultManager = OrderCatContext.getOcTmsportCheckResultManager();


        List<Predicate<OcTmsportCheckResult>> predicateList = new ArrayList<>();

        if (tm_outer_order_id != null) {
            predicateList.add(OcTmsportCheckResultImpl.TM_OUTER_ORDER_ID.equal(tm_outer_order_id));
        }

        if (dz_status != null) {
            predicateList.add(OcTmsportCheckResultImpl.DZ_STATUS.equal(dz_status));
        }

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
                .skip((page-1) * page_size)
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


    @POST
    @Produces("application/json;charset=utf-8")
    @Path("/tmsport/check/addRemark")
    public Map<String, Object> tmsportCheckAddRemark(
            @FormParam("id") String id,
            @FormParam("remark") String remark) {


        Logger.info(String.format("id:%s,remark:%s", id, remark));
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
            ocTmsportCheckResult.setRemarks(remark);
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


    @GET
    @Produces("application/json;charset=utf-8")
    @Path("/fenxiao/check/list")
    public PageResult<OcFenxiaoCheckResultVO> fenxiaoCheckList(
            @QueryParam("status") String status,
            @QueryParam("page_size") int page_size,
            @QueryParam("page") int page
    ) {
        PageResult<OcFenxiaoCheckResultVO> pageResult = new PageResult<>();
        OcFenxiaoCheckResultManager ocFenxiaoCheckResultManager = OrderCatContext.getOcFenxiaoCheckResultManager();
        List<Predicate<OcFenxiaoCheckResult>> predicateList = new ArrayList<>();

        if (status != null) {
            predicateList.add(OcFenxiaoCheckResultImpl.STATUS.equal(status));
        }

        Predicate<OcFenxiaoCheckResult> p1 = ocFenxiaoCheckResult -> true;
        for (Predicate<OcFenxiaoCheckResult> p : predicateList) {
            p1 = p1.and(p);
        }

        long count = ocFenxiaoCheckResultManager.stream()
                .filter(p1)
                .count();
        pageResult.setTotal(count);


        List<OcFenxiaoCheckResult> list = ocFenxiaoCheckResultManager.stream()
                .filter(p1)
                .sorted(OcFenxiaoCheckResultImpl.ADD_TIME.comparator())
                .skip((page - 1) * page_size)
                .limit(page_size)
                .collect(Collectors.toList());


        List<OcFenxiaoCheckResultVO> ocFenxiaoCheckResultList = list.parallelStream()
                .map(o -> {
                    OcFenxiaoCheckResultVO fenxiaoCheckResult = new OcFenxiaoCheckResultVO();
                    fenxiaoCheckResult.setId(o.getId());
                    fenxiaoCheckResult.setTid(o.getTid().isPresent()?o.getTid().getAsLong():0);
                    fenxiaoCheckResult.setOrderStatus(o.getOrderStatus().isPresent()?o.getOrderStatus().get():"");
                    fenxiaoCheckResult.setRefundId(o.getRefundId().isPresent()?o.getRefundId().getAsLong():0);
                    fenxiaoCheckResult.setNumIid(o.getNumIid().isPresent()?o.getNumIid().getAsLong():0);
                    fenxiaoCheckResult.setTitle(o.getTitle());
                    fenxiaoCheckResult.setFenxiaoId(o.getFenxiaoId().isPresent()?o.getFenxiaoId().getAsLong():0);
                    fenxiaoCheckResult.setSupplierNick(o.getSupplierNick().isPresent()?o.getSupplierNick().get():"");
                    fenxiaoCheckResult.setDistributorNick(o.getDistributorNick().isPresent()?o.getDistributorNick().get():"");
                    fenxiaoCheckResult.setFenxiaoRefundStatus(o.getFenxiaoRefundStatus().isPresent()?o.getFenxiaoRefundStatus().get():"");
                    fenxiaoCheckResult.setFenxiaoRefundFee(o.getFenxiaoRefundFee().isPresent()?o.getFenxiaoRefundFee().get(): BigDecimal.ZERO);
                    fenxiaoCheckResult.setFenxiaoPaySupFee(o.getFenxiaoPaySupFee().isPresent()?o.getFenxiaoPaySupFee().get(): BigDecimal.ZERO);
                    fenxiaoCheckResult.setFenxiaoRefundDesc(o.getFenxiaoRefundDesc().isPresent()?o.getFenxiaoRefundDesc().get():"");
                    fenxiaoCheckResult.setFenxiaoRefundReason(o.getFenxiaoRefundReason().isPresent()?o.getFenxiaoRefundReason().get():"");
                    fenxiaoCheckResult.setStatus(o.getStatus().isPresent()?o.getStatus().get():"");
                    fenxiaoCheckResult.setRemarks(o.getRemarks().isPresent() ? o.getRemarks().get() : "");
                    fenxiaoCheckResult.setAddTime(OcDateTimeUtils.localDateTime2Date(o.getAddTime()));
                    return fenxiaoCheckResult;
                })
                .collect(Collectors.toList());
        pageResult.setRows(ocFenxiaoCheckResultList);
        return pageResult;
    }


    @POST
    @Produces("application/json;charset=utf-8")
    @Path("/fenxiao/check/addRemark")
    public Map<String, Object> fenxiaoCheckAddRemark(
            @FormParam("id") String id,
            @FormParam("remark") String remark) {

        Logger.info(String.format("id:%s,remark:%s", id, remark));

        Map<String, Object> rt = new HashMap<>();

        OcFenxiaoCheckResultManager ocFenxiaoCheckResultManager = OrderCatContext.getOcFenxiaoCheckResultManager();
        boolean success;
        String message;

        Optional<OcFenxiaoCheckResult> o = ocFenxiaoCheckResultManager.stream()
                .filter(OcFenxiaoCheckResultImpl.ID.equal(Long.valueOf(id)))
                .findAny();
        OcFenxiaoCheckResult ocFenxiaoCheckResult;
        if (o.isPresent()) {
            ocFenxiaoCheckResult = o.get();
            ocFenxiaoCheckResult.setRemarks(remark);

            ocFenxiaoCheckResultManager.update(ocFenxiaoCheckResult);
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


}