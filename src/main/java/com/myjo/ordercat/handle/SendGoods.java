package com.myjo.ordercat.handle;

import com.myjo.ordercat.domain.JobName;
import com.myjo.ordercat.domain.OrderStatus;
import com.myjo.ordercat.domain.TianmaOrder;
import com.myjo.ordercat.exception.OCException;
import com.myjo.ordercat.http.TaoBaoHttp;
import com.myjo.ordercat.http.TianmaSportHttp;
import com.myjo.ordercat.utils.OcDateTimeUtils;
import com.myjo.ordercat.utils.OcJobUtils;
import com.myjo.ordercat.spm.ordercat.ordercat.oc_job_exec_info.OcJobExecInfoManager;
import com.myjo.ordercat.spm.ordercat.ordercat.oc_logistics_companies_info.OcLogisticsCompaniesInfo;
import com.myjo.ordercat.spm.ordercat.ordercat.oc_logistics_companies_info.OcLogisticsCompaniesInfoImpl;
import com.myjo.ordercat.spm.ordercat.ordercat.oc_logistics_companies_info.OcLogisticsCompaniesInfoManager;
import com.myjo.ordercat.utils.OcLcUtils;
import com.myjo.ordercat.utils.OcStringUtils;
import com.taobao.api.domain.LogisticsCompany;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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

    public void autoSend(Long execJobId) throws Exception{
        Logger.info(String.format("开始进行自动发货,execJobId:[%d]",execJobId.longValue()));


        LocalDateTime lend = LocalDateTime.now();
        LocalDateTime lbegin = lend.minusDays(10);

        String startTime = OcDateTimeUtils.localDateTime2String(lbegin, OcDateTimeUtils.OC_DATE);
        String endTime = OcDateTimeUtils.localDateTime2String(lend, OcDateTimeUtils.OC_DATE);
        //查询反馈成功的订单
        List<TianmaOrder> tianmaOrders = tianmaSportHttp.tradeOrderDataList(startTime,endTime, OrderStatus.FEEDBACK_SUCCESS);
        Logger.info(String.format("查询[%s]-[%s]区间,status=[%s]的订单.size:[%d]",
                startTime,
                endTime,
                OrderStatus.FEEDBACK_SUCCESS.toString(),
                tianmaOrders.size()
        ));
        if(tianmaOrders.size()==0){
            throw new OCException("天马反馈成功的订单不能为空!请检查!");
        }
        Integer lcJobId = OcJobUtils.getLastSuccessJobID(ocJobExecInfoManager, JobName.SYNC_LC_JOB.getValue());
        Logger.info(String.format("同步物流公司信息最后一次执行ID:[%d]", lcJobId.intValue()));


        List<OcLogisticsCompaniesInfo> oclcList = ocLogisticsCompaniesInfoManager.stream()
                .filter(OcLogisticsCompaniesInfo.EXEC_JOB_ID.equal(lcJobId.longValue())
                        .and(OcLogisticsCompaniesInfo.LC_REG_MAIL_NO.isNotNull()))
                .collect(Collectors.toList());

        List<String> lcCodelist;
        for(TianmaOrder tianmaOrder:tianmaOrders){

            lcCodelist = OcLcUtils.getLogisticsCompaniesCode(oclcList,"667551798847");
            System.out.println(lcCodelist.size());
        }












        Logger.info(String.format("自动发货-运行结束"));
    }


    public void syncLogisticsCompanies(Long execJobId) throws Exception{

        Logger.info(String.format("同步物流公司信息,execJobId:[%d]",execJobId.longValue()));
        List<LogisticsCompany> list = taoBaoHttp.getTaoBaoLogisticsCompanies();
        if(list.size()==0){
            throw new OCException("淘宝物流公司列表获取接口为空!请检查!");
        }

        OcLogisticsCompaniesInfo ocLogisticsCompaniesInfo;
        for(LogisticsCompany logisticsCompany : list){
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
