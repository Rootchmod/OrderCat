package com.myjo.ordercat

import com.aol.micro.server.MicroserverApp
import com.aol.micro.server.module.Module
import com.myjo.ordercat.config.OrderCatConfig
import com.myjo.ordercat.context.OrderCatContext
import com.myjo.ordercat.domain.JobName
import com.myjo.ordercat.handle.AccountCheck
import com.myjo.ordercat.handle.ExecuteHandle
import com.myjo.ordercat.handle.FenXiaoAcHandle
import com.myjo.ordercat.handle.TianMaAcHandle
import com.myjo.ordercat.http.TaoBaoHttp
import com.myjo.ordercat.http.TianmaSportHttp
import com.myjo.ordercat.spm.OrdercatApplication
import com.myjo.ordercat.spm.OrdercatApplicationBuilder
import com.myjo.ordercat.spm.ordercat.ordercat.oc_fenxiao_check_result.OcFenxiaoCheckResultManager
import com.myjo.ordercat.spm.ordercat.ordercat.oc_inventory_info.OcInventoryInfoManager
import com.myjo.ordercat.spm.ordercat.ordercat.oc_job_exec_info.OcJobExecInfoManager
import com.myjo.ordercat.spm.ordercat.ordercat.oc_logistics_companies_info.OcLogisticsCompaniesInfoManager
import com.myjo.ordercat.spm.ordercat.ordercat.oc_sales_info.OcSalesInfoManager
import com.myjo.ordercat.spm.ordercat.ordercat.oc_sync_inventory_item_info.OcSyncInventoryItemInfoManager
import com.myjo.ordercat.spm.ordercat.ordercat.oc_tmsport_check_result.OcTmsportCheckResultManager
import com.myjo.ordercat.spm.ordercat.ordercat.oc_warehouse_info.OcWarehouseInfoManager
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import spock.lang.Shared
import spock.lang.Specification
import groovyx.net.http.RESTClient


import spock.lang.Unroll

/**
 * Created by lee5hx on 17/4/20.
 */
@Unroll
class RestApiSpec extends Specification {

    private static final Logger Logger = LogManager.getLogger(RestApiSpec.class);

    private static AccountCheck ac;
    private static ExecuteHandle eh;
    private static ExecuteHandle eh1;


    @Shared
    def client = new RESTClient("http://localhost:8080/order_cat_api/")



    def setup() {

        OrdercatApplication app = new OrdercatApplicationBuilder()
                .withConnectionUrl(OrderCatConfig.getDBmsName(), OrderCatConfig.getDBConnectionUrl())
                .withUsername(OrderCatConfig.getDBUsername())
                .withPassword(OrderCatConfig.getDBPassword())
                .build();



//
//        OcWarehouseInfoManager ocWarehouseInfoManager = app.getOrThrow(OcWarehouseInfoManager.class);
//        OcJobExecInfoManager ocJobExecInfoManager = app.getOrThrow(OcJobExecInfoManager.class);
//        OcInventoryInfoManager ocInventoryInfoManager = app.getOrThrow(OcInventoryInfoManager.class);
//        OcSalesInfoManager ocSalesInfoManager = app.getOrThrow(OcSalesInfoManager.class);
//        OcLogisticsCompaniesInfoManager ocLogisticsCompaniesInfoManager = app.getOrThrow(OcLogisticsCompaniesInfoManager.class);
//        OcSyncInventoryItemInfoManager ocSyncInventoryItemInfoManager = app.getOrThrow(OcSyncInventoryItemInfoManager.class);
        OcFenxiaoCheckResultManager ocFenxiaoCheckResultManager = app.getOrThrow(OcFenxiaoCheckResultManager.class);
        OcTmsportCheckResultManager ocTmsportCheckResultManager = app.getOrThrow(OcTmsportCheckResultManager.class);
//

        OrderCatContext.setOcTmsportCheckResultManager(ocTmsportCheckResultManager);
        OrderCatContext.setOcFenxiaoCheckResultManager(ocFenxiaoCheckResultManager);

//        Map<String,String> map = new HashMap<>();
//
//        TianmaSportHttp tianmaSportHttp = new TianmaSportHttp(map);
//        TaoBaoHttp taoBaoHttp = new TaoBaoHttp();
//        ac = new AccountCheck(tianmaSportHttp,taoBaoHttp);
//        ac.setOcSyncInventoryItemInfoManager(ocSyncInventoryItemInfoManager);
//        ac.setOcFenxiaoCheckResultManager(ocFenxiaoCheckResultManager);
//        ac.setOcTmsportCheckResultManager(ocTmsportCheckResultManager);
////        si.setOcInventoryInfoManager(ocInventoryInfoManager);
////        si.setOcWarehouseInfoManager(ocWarehouseInfoManager);
////        si.setOcJobExecInfoManager(ocJobExecInfoManager);
////        si.setOcSalesInfoManager(ocSalesInfoManager);
//
//        //ExecuteHandle eh;
//        eh = new FenXiaoAcHandle(ac)
//        eh.setJobName(JobName.FENXIAO_ACCOUNT_CHECK_JOB.getValue());
//        eh.setOcJobExecInfoManager(ocJobExecInfoManager);
//
//
//
//        eh1 = new TianMaAcHandle(ac)
//        eh1.setJobName(JobName.TIANMA_ACCOUNT_CHECK_JOB.getValue());
//        eh1.setOcJobExecInfoManager(ocJobExecInfoManager);
//
//
//
//
//        tianmaSportHttp.getVerifyCodeImage();
//
//        String vcode = "1111";
//
//        //Logger.error("test1",new Exception("11111"));
//
//        tianmaSportHttp.login(vcode);
//
//        tianmaSportHttp.main_html();


//        def server = new MicroserverApp( MicroserverApp.class,new Module() {
//            @Override
//            String getContext() {
//                return "order_cat_api";
//            }
//        });

        def server = new MicroserverApp( RestApiSpec.class,new Module() {
            @Override
            String getContext() {
                return "order_cat_api";
            }
        });

        server.run()

    }


    def "StatusResource-status-ping"() {
        when:
        def response = client.get(path : "status/ping")


        then:
        with(response) {

            status == 200
        }
    }


    def "AccountCheckResource-/account-check/tmsport/check/list"() {
        when:
        def response = client.get(path : "account-check/tmsport/check/list")

        then:
        with(response) {

            status == 200
        }
    }


    def "AccountCheckResource-/fenxiao/check/list"() {
        when:
        def response = client.get(path : "account-check/fenxiao/check/list")

        then:
        with(response) {
            status == 200
        }
    }



}
