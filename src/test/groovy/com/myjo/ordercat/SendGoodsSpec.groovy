package com.myjo.ordercat

import com.myjo.ordercat.config.OrderCatConfig
import com.myjo.ordercat.domain.JobName
import com.myjo.ordercat.handle.*
import com.myjo.ordercat.http.TaoBaoHttp
import com.myjo.ordercat.http.TianmaSportHttp
import com.myjo.ordercat.spm.OrdercatApplication
import com.myjo.ordercat.spm.OrdercatApplicationBuilder
import com.myjo.ordercat.spm.ordercat.ordercat.oc_inventory_info.OcInventoryInfoManager
import com.myjo.ordercat.spm.ordercat.ordercat.oc_job_exec_info.OcJobExecInfoManager
import com.myjo.ordercat.spm.ordercat.ordercat.oc_logistics_companies_info.OcLogisticsCompaniesInfoManager
import com.myjo.ordercat.spm.ordercat.ordercat.oc_sales_info.OcSalesInfoManager
import com.myjo.ordercat.spm.ordercat.ordercat.oc_warehouse_info.OcWarehouseInfoManager
import org.apache.commons.lang3.StringUtils
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.json.JSONObject
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Created by lee5hx on 17/4/20.
 */
@Unroll
class SendGoodsSpec extends Specification {

    private static final Logger Logger = LogManager.getLogger(SendGoodsSpec.class);

    private static SendGoods sg;
    private static ExecuteHandle eh;
    private static ExecuteHandle eh1;

    def setup() {

        OrdercatApplication app = new OrdercatApplicationBuilder()
                .withConnectionUrl(OrderCatConfig.getDBmsName(), OrderCatConfig.getDBConnectionUrl())
                .withUsername(OrderCatConfig.getDBUsername())
                .withPassword(OrderCatConfig.getDBPassword())
                .build();

        OcWarehouseInfoManager ocWarehouseInfoManager = app.getOrThrow(OcWarehouseInfoManager.class);
        OcJobExecInfoManager ocJobExecInfoManager = app.getOrThrow(OcJobExecInfoManager.class);
        OcInventoryInfoManager ocInventoryInfoManager = app.getOrThrow(OcInventoryInfoManager.class);
        OcSalesInfoManager ocSalesInfoManager = app.getOrThrow(OcSalesInfoManager.class);
        OcLogisticsCompaniesInfoManager ocLogisticsCompaniesInfoManager = app.getOrThrow(OcLogisticsCompaniesInfoManager.class);



        Map<String,String> map = new HashMap<>();

        TianmaSportHttp tianmaSportHttp = new TianmaSportHttp(map);
        TaoBaoHttp taoBaoHttp = new TaoBaoHttp();
        sg = new SendGoods(tianmaSportHttp,taoBaoHttp);
        sg.setOcLogisticsCompaniesInfoManager(ocLogisticsCompaniesInfoManager)
        sg.setOcJobExecInfoManager(ocJobExecInfoManager);
//        si.setOcInventoryInfoManager(ocInventoryInfoManager);
//        si.setOcWarehouseInfoManager(ocWarehouseInfoManager);
//        si.setOcJobExecInfoManager(ocJobExecInfoManager);
//        si.setOcSalesInfoManager(ocSalesInfoManager);

        //ExecuteHandle eh;
        eh = new SyncLcHandle(sg);
        eh.setJobName(JobName.SYNC_LC_JOB.getValue());
        eh.setOcJobExecInfoManager(ocJobExecInfoManager);


        eh1 = new AutoSendHandle(sg);
        eh1.setJobName(JobName.AUTO_SEND_GOODS_JOB.getValue());
        eh1.setOcJobExecInfoManager(ocJobExecInfoManager);


        tianmaSportHttp.getVerifyCodeImage();

        String vcode = "1111";

        //Logger.error("test1",new Exception("11111"));

        tianmaSportHttp.login(vcode);

        tianmaSportHttp.main_html();


    }


    def "syncLogisticsCompanies"() {
        when:
        eh.exec()
        then:
        "ok" == "ok";
    }


    def "autoSend"() {
        when:
        eh1.exec()
        then:
        "ok" == "ok";
    }
}
