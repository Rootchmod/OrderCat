package com.myjo.ordercat.handle;

import com.myjo.ordercat.config.OrderCatConfig;
import com.myjo.ordercat.domain.OrderStatus;
import com.myjo.ordercat.domain.ReturnResult;
import com.myjo.ordercat.domain.TianmaOrder;
import com.myjo.ordercat.exception.OCException;
import com.myjo.ordercat.http.TaoBaoHttp;
import com.myjo.ordercat.http.TianmaSportHttp;
import com.myjo.ordercat.spm.ordercat.ordercat.oc_job_exec_info.OcJobExecInfoManager;
import com.myjo.ordercat.spm.ordercat.ordercat.oc_logistics_companies_info.OcLogisticsCompaniesInfo;
import com.myjo.ordercat.spm.ordercat.ordercat.oc_logistics_companies_info.OcLogisticsCompaniesInfoImpl;
import com.myjo.ordercat.spm.ordercat.ordercat.oc_logistics_companies_info.OcLogisticsCompaniesInfoManager;
import com.myjo.ordercat.utils.OcDateTimeUtils;
import com.taobao.api.domain.LogisticsCompany;
import com.taobao.api.domain.Shipping;
import com.taobao.api.domain.Trade;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 自动发货相关
 * Created by lee5hx on 17/5/8.
 */
public class SendGoods {

    private static final Logger Logger = LogManager.getLogger(SendGoods.class);

    private TianmaSportHttp tianmaSportHttp;

    private TaoBaoHttp taoBaoHttp;


    private OcLogisticsCompaniesInfoManager ocLogisticsCompaniesInfoManager;

    private OcJobExecInfoManager ocJobExecInfoManager;


    public SendGoods(TianmaSportHttp tianmaSportHttp, TaoBaoHttp taoBaoHttp) {
        this.tianmaSportHttp = tianmaSportHttp;
        this.taoBaoHttp = taoBaoHttp;
    }

    public void autoSend(Long execJobId) throws Exception {
        Logger.info(String.format("开始进行自动发货,execJobId:[%d]", execJobId.longValue()));


        LocalDateTime lend = LocalDateTime.now();
        LocalDateTime lbegin = lend.minusDays(OrderCatConfig.getAsdOrderDateIntervalDay());

        String startTime = OcDateTimeUtils.localDateTime2String(lbegin, OcDateTimeUtils.OC_DATE);
        String endTime = OcDateTimeUtils.localDateTime2String(lend, OcDateTimeUtils.OC_DATE);
        //查询反馈成功的订单
        List<TianmaOrder> tianmaOrders = tianmaSportHttp.tradeOrderDataList(startTime, endTime, OrderStatus.FEEDBACK_SUCCESS);
        Logger.info(String.format("查询[%s]-[%s]区间,status=[%s]的订单.size:[%d]",
                startTime,
                endTime,
                OrderStatus.FEEDBACK_SUCCESS.toString(),
                tianmaOrders.size()
        ));
        if (tianmaOrders.size() == 0) {
            throw new OCException("天马反馈成功的订单不能为空!请检查!");
        }


        Optional<com.myjo.ordercat.domain.LogisticsCompany> logisticsCompany = tianmaSportHttp.ajaxGuessMailNoRequest("885214803258033378", "23134990467245578");
        if (logisticsCompany.isPresent()) {
            Logger.info(String.format("淘宝快递公司猜测接口-可用性维持[%s]-[%s]-[%s]", "885214803258033378", "23134990467245578", logisticsCompany.get().getCode()));
        }


        tianmaOrders.parallelStream()
                .filter(tianmaOrder -> tianmaOrder.getOuterOrderId().indexOf("麦巨") == -1)
                //.filter(tianmaOrder -> tianmaOrder.getOuterOrderId().equals("24082990247785463"))
                .forEach(tianmaOrder -> {
                    //System.out.println(tianmaOrder.getDeliveryNo()+":"+tianmaOrder.getNoShipmentRemark());
                    try {
                        Optional<Trade> trade = taoBaoHttp.getTaobaoTrade(Long.valueOf(tianmaOrder.getOuterOrderId()));

                        if (trade.isPresent() && "WAIT_SELLER_SEND_GOODS".equals(trade.get().getStatus()) && trade.get().getOrders().size() == 1) {//WAIT_SELLER_SEND_GOODS(等待卖家发货,即:买家已付款)

                            Optional<com.myjo.ordercat.domain.LogisticsCompany> lc = tianmaSportHttp.ajaxGuessMailNoRequest(tianmaOrder.getDeliveryNo(), tianmaOrder.getOuterOrderId());
                            if (lc.isPresent()) {
                                String companyCode = lc.get().getCode().get();
                                Logger.info(String.format("自动发货订单[%s],快递单号[%s]-快递公司编码[%s].",
                                        tianmaOrder.getOuterOrderId(),
                                        tianmaOrder.getDeliveryNo(),
                                        companyCode));
                                Optional<ReturnResult<Shipping>> brt = taoBaoHttp.sendTaobaoLogisticsOffline(Long.valueOf(tianmaOrder.getOuterOrderId()), tianmaOrder.getDeliveryNo(), companyCode);

                                if (brt.isPresent()) {
                                    if (brt.get().isSuccess() == true) {
                                        tianmaSportHttp.addOrderRemark(tianmaOrder.getOrderId(), String.format("OC-已自动发货"));
                                    } else {
                                        tianmaSportHttp.addOrderRemark(tianmaOrder.getOrderId(), String.format("OC-发货错误:[%s]-[%s]", brt.get().getErrorCode(), brt.get().getErrorMessages()));
                                    }
                                }

                            } else {
                                Logger.error(String.format("自动发货订单[%s],快递单号[%s]-没有匹配到物流公司编码!",
                                        tianmaOrder.getOuterOrderId(),
                                        tianmaOrder.getDeliveryNo()));
                            }
                        } else {
                            if (trade.isPresent()) {
                                Logger.debug(String.format("订单[%s]-订单状态为[%s]-order.size[%d]-([已付款,等待发货]的订单并且订单只有一笔时才会发货.)",
                                        tianmaOrder.getOuterOrderId(),
                                        trade.get().getStatus(),
                                        trade.get().getOrders().size()));
                            }
                        }

                    } catch (Exception e) {
                        //e.printStackTrace();
                        Logger.error(e);
                    }
                });

        Logger.info(String.format("自动发货-运行结束"));
    }


    public void syncLogisticsCompanies(Long execJobId) throws Exception {

        Logger.info(String.format("同步物流公司信息,execJobId:[%d]", execJobId.longValue()));
        List<LogisticsCompany> list = taoBaoHttp.getTaoBaoLogisticsCompanies();
        if (list.size() == 0) {
            throw new OCException("淘宝物流公司列表获取接口为空!请检查!");
        }

        OcLogisticsCompaniesInfo ocLogisticsCompaniesInfo;
        for (LogisticsCompany logisticsCompany : list) {
            ocLogisticsCompaniesInfo = new OcLogisticsCompaniesInfoImpl();
            ocLogisticsCompaniesInfo.setLcId(logisticsCompany.getId());
            ocLogisticsCompaniesInfo.setLcCode(logisticsCompany.getCode());
            ocLogisticsCompaniesInfo.setLcRegMailNo(logisticsCompany.getRegMailNo());
            ocLogisticsCompaniesInfo.setExecJobId(execJobId);
            ocLogisticsCompaniesInfo.setLcName(logisticsCompany.getName());
            ocLogisticsCompaniesInfo.setAddTime(LocalDateTime.now());

            ocLogisticsCompaniesInfoManager.persist(ocLogisticsCompaniesInfo);

        }
        Logger.info(String.format("同步物流公司信息-运行结束"));
    }

    public void setOcLogisticsCompaniesInfoManager(OcLogisticsCompaniesInfoManager ocLogisticsCompaniesInfoManager) {
        this.ocLogisticsCompaniesInfoManager = ocLogisticsCompaniesInfoManager;
    }

    public void setOcJobExecInfoManager(OcJobExecInfoManager ocJobExecInfoManager) {
        this.ocJobExecInfoManager = ocJobExecInfoManager;
    }
}
