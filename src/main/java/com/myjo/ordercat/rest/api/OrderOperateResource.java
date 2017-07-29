package com.myjo.ordercat.rest.api;

import com.aol.micro.server.auto.discovery.Rest;
import com.myjo.ordercat.context.OrderCatContext;
import com.myjo.ordercat.domain.PageResult;
import com.myjo.ordercat.domain.vo.OcTmOrderRecordsVO;
import com.myjo.ordercat.handle.OrderOperate;
import com.myjo.ordercat.spm.ordercat.ordercat.oc_tm_order_records.OcTmOrderRecords;
import com.myjo.ordercat.spm.ordercat.ordercat.oc_tm_order_records.OcTmOrderRecordsImpl;
import com.myjo.ordercat.spm.ordercat.ordercat.oc_tm_order_records.OcTmOrderRecordsManager;
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
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Rest
@Path("/order-operate")
@Api(value = "/order-operate", description = "订单操作接口")
public class OrderOperateResource {
    //private final static int PAGE_SIZE = 50;
    private static final Logger Logger = LogManager.getLogger(OrderOperate.class);


    @GET
    @Produces("application/json;charset=utf-8")
    @Path("/orderRecords")
    @ApiOperation(value = "下单记录", response = PageResult.class)
    public PageResult<OcTmOrderRecordsVO> tmsportCheckList(
            @ApiParam(name = "tid", value = "淘宝订单ID") @QueryParam("tid") String tid,
            @ApiParam(name = "status", value = "下单状态") @QueryParam("status") String status,
            @ApiParam(name = "order_begin_time", value = "下单开始时间") @QueryParam("order_begin_time") String order_begin_time,
            @ApiParam(name = "order_end_time", value = "下单结束时间") @QueryParam("order_end_time") String order_end_time,
            @ApiParam(required = true, name = "page_size", value = "分页大小") @QueryParam("page_size") int page_size,
            @ApiParam(required = true, name = "page", value = "当前页") @QueryParam("page") int page
    ) {
        PageResult<OcTmOrderRecordsVO> pageResult = new PageResult<>();
        OcTmOrderRecordsManager ocTmOrderRecordsManager = OrderCatContext.getOcTmOrderRecordsManager();
        List<Predicate<OcTmOrderRecords>> predicateList = new ArrayList<>();

        if (order_begin_time != null) {
            predicateList.add(OcTmOrderRecordsImpl.ADD_TIME.greaterOrEqual(OcDateTimeUtils.string2LocalDateTime(order_begin_time)));
        }

        if (order_end_time != null) {
            predicateList.add(OcTmOrderRecordsImpl.ADD_TIME.lessOrEqual(OcDateTimeUtils.string2LocalDateTime(order_end_time)));
        }

        if (tid != null) {
            predicateList.add(OcTmOrderRecordsImpl.TID.equal(tid));
        }

        if (status != null) {
            predicateList.add(OcTmOrderRecordsImpl.STATUS.equal(status));
        }

        Predicate<OcTmOrderRecords> p1 = ocTmOrderRecords -> true;

        for (Predicate<OcTmOrderRecords> p : predicateList) {
            p1 = p1.and(p);
        }
        //Stream<OcTmsportCheckResult> stream = ;

        long count = ocTmOrderRecordsManager.stream()
                .filter(p1)
                .count();
        pageResult.setTotal(count);


        List<OcTmOrderRecords> list = ocTmOrderRecordsManager.stream()
                .filter(p1)
                .sorted(OcTmOrderRecordsImpl.ADD_TIME.comparator().reversed())
                .skip((page - 1) * page_size)
                .limit(page_size)
                .collect(Collectors.toList());

        List<OcTmOrderRecordsVO> ocTmOrderRecordsVOS = list.parallelStream()
                .map(o -> {
                    OcTmOrderRecordsVO ocTmOrderRecordsVO = new OcTmOrderRecordsVO();
                    //序号
                    ocTmOrderRecordsVO.setId(o.getId());
                    //'淘宝订单ID'
                    ocTmOrderRecordsVO.setTid(o.getTid().orElse(""));
                    //'天马订单ID'
                    ocTmOrderRecordsVO.setTmOrderId(o.getTmOrderId().orElse(""));
                    //'商品货号'
                    ocTmOrderRecordsVO.setGoodsNo(o.getGoodsNo().orElse(""));
                    //尺码
                    ocTmOrderRecordsVO.setSize(o.getSize().orElse(""));
                    //运费
                    ocTmOrderRecordsVO.setFreightPriceStr(o.getFreightPriceStr().orElse(""));
                    //下单仓库
                    ocTmOrderRecordsVO.setWhId(o.getWhId().orElse(0));
                    //下单仓库名称
                    ocTmOrderRecordsVO.setWhName(o.getWhName().orElse(""));
                    //下单仓库配货率,单位:百分比
                    ocTmOrderRecordsVO.setWhPickRate(o.getWhPickRate().orElse(0));
                    //下单仓库价格
                    ocTmOrderRecordsVO.setWhProxyPrice(o.getWhProxyPrice().orElse(BigDecimal.ZERO));
                    //下单仓库库存更新时间
                    ocTmOrderRecordsVO.setWhUpdateTime(o.getWhUpdateTime().isPresent() ? OcDateTimeUtils.localDateTime2Date(o.getWhUpdateTime().get()) : null);
                    //下单仓库库存数
                    ocTmOrderRecordsVO.setWhInventoryCount(o.getWhInventoryCount().orElse(0));
                    //下单类型：手工补单，自动下单
                    ocTmOrderRecordsVO.setType(o.getType().orElse(""));
                    //淘宝订单支付金额
                    ocTmOrderRecordsVO.setTbPayAmount(o.getTbPayAmount().orElse(BigDecimal.ZERO));
                    //下单状态：成功或失败
                    ocTmOrderRecordsVO.setStatus(o.getStatus().orElse(""));
                    //订单信息-json
                    ocTmOrderRecordsVO.setOrderInfo(o.getOrderInfo().orElse(""));
                    //失败原因
                    ocTmOrderRecordsVO.setFailCause(o.getFailCause().orElse(""));
                    //保本价(自动机器下单时，才会有数据)
                    ocTmOrderRecordsVO.setBreakEvenPrice(o.getBreakEvenPrice().orElse(BigDecimal.ZERO));
                    //仓库快照数据
                    ocTmOrderRecordsVO.setWhSnapshotData(o.getWhSnapshotData().orElse(""));
                    //下单机器CID
                    ocTmOrderRecordsVO.setMachineCid(o.getMachineCid().orElse(""));
                    //执行耗时,单位:毫秒
                    ocTmOrderRecordsVO.setElapsed(o.getElapsed().orElse(0));
                    //下单时间
                    ocTmOrderRecordsVO.setAddTime(OcDateTimeUtils.localDateTime2Date(o.getAddTime()));
                    //天马尺码信息
                    ocTmOrderRecordsVO.setTmSizeInfoStr(o.getTmSizeInfoStr().orElse(""));
                    //天马SKUID
                    ocTmOrderRecordsVO.setTmSkuId(o.getTmSkuId().orElse(""));
                    
                    return ocTmOrderRecordsVO;
                })
                .collect(Collectors.toList());
        pageResult.setRows(ocTmOrderRecordsVOS);

        return pageResult;
    }


}