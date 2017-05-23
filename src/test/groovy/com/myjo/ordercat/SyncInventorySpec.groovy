package com.myjo.ordercat

import com.myjo.ordercat.config.OrderCatConfig
import com.myjo.ordercat.domain.JobName
import com.myjo.ordercat.handle.ExecuteHandle
import com.myjo.ordercat.handle.SyncInventory
import com.myjo.ordercat.handle.SyncSalesInfoHandle
import com.myjo.ordercat.handle.SyncTaoBaoInventoryHandle
import com.myjo.ordercat.handle.SyncWarehouseHandle
import com.myjo.ordercat.http.TaoBaoHttp
import com.myjo.ordercat.http.TianmaSportHttp
import com.myjo.ordercat.spm.OrdercatApplication
import com.myjo.ordercat.spm.OrdercatApplicationBuilder
import com.myjo.ordercat.spm.ordercat.ordercat.oc_inventory_info.OcInventoryInfoManager
import com.myjo.ordercat.spm.ordercat.ordercat.oc_job_exec_info.OcJobExecInfoManager
import com.myjo.ordercat.spm.ordercat.ordercat.oc_sales_info.OcSalesInfoManager
import com.myjo.ordercat.spm.ordercat.ordercat.oc_sync_inventory_item_info.OcSyncInventoryItemInfoManager
import com.myjo.ordercat.spm.ordercat.ordercat.oc_warehouse_info.OcWarehouseInfoManager
import org.apache.commons.lang3.StringUtils
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.json.JSONObject
import spock.lang.Specification
import spock.lang.Unroll

import javax.script.ScriptEngine
import javax.script.ScriptEngineManager

/**
 * Created by lee5hx on 17/4/20.
 */
@Unroll
class SyncInventorySpec extends Specification {

    private static final Logger Logger = LogManager.getLogger(SyncInventorySpec.class);

    private static SyncInventory si;
    private static ExecuteHandle eh;
    private static ExecuteHandle eh1;
    private static ExecuteHandle eh2;

    def setup() {

        OrdercatApplication app = new OrdercatApplicationBuilder()
                .withConnectionUrl(OrderCatConfig.getDBmsName(), OrderCatConfig.getDBConnectionUrl())
                .withUsername(OrderCatConfig.getDBUsername())
                .withPassword(OrderCatConfig.getDBPassword())
                .build();

        OcWarehouseInfoManager ocWarehouseInfoManager = app.getOrThrow(OcWarehouseInfoManager.class);
        OcJobExecInfoManager ocJobExecInfoManager = app.getOrThrow(OcJobExecInfoManager.class);
        //OcInventoryInfoManager ocInventoryInfoManager = app.getOrThrow(OcInventoryInfoManager.class);
        OcSalesInfoManager ocSalesInfoManager = app.getOrThrow(OcSalesInfoManager.class);
        OcSyncInventoryItemInfoManager ocSyncInventoryItemInfoManager = app.getOrThrow(OcSyncInventoryItemInfoManager.class);



        ScriptEngineManager m = new ScriptEngineManager();
        ScriptEngine e = m.getEngineByName("nashorn");

        Map<String,String> map = new HashMap<>();

        TianmaSportHttp tianmaSportHttp = new TianmaSportHttp(map);
        TaoBaoHttp taoBaoHttp = new TaoBaoHttp();
        si = new SyncInventory(tianmaSportHttp,taoBaoHttp,e);
        si.setOcSyncInventoryItemInfoManager(ocSyncInventoryItemInfoManager);
        si.setOcWarehouseInfoManager(ocWarehouseInfoManager);
        si.setOcJobExecInfoManager(ocJobExecInfoManager);
        si.setOcSalesInfoManager(ocSalesInfoManager);




        //ExecuteHandle eh;
        eh = new SyncWarehouseHandle(si);
        eh.setJobName(JobName.SYNC_WAREHOUSE_JOB.getValue());
        eh.setOcJobExecInfoManager(ocJobExecInfoManager);



        //ExecuteHandle eh1;
        eh1 = new SyncTaoBaoInventoryHandle(si);
        eh1.setJobName(JobName.SYNC_TAOBAO_INVENTORY_JOB.getValue());
        eh1.setOcJobExecInfoManager(ocJobExecInfoManager);


        //ExecuteHandle eh2;
        eh2 = new SyncSalesInfoHandle(si);
        eh2.setJobName(JobName.SYNC_SALES_INFO_JOB.getValue());
        eh2.setOcJobExecInfoManager(ocJobExecInfoManager);

        tianmaSportHttp.getVerifyCodeImage();

        String vcode = "1111";

        Logger.error("test1",new Exception("11111"));

        JSONObject jsonObject = tianmaSportHttp.login(vcode);

        tianmaSportHttp.main_html();



    }


    def "syncWarehouseInfo"() {
        when:
        eh.exec()
        then:
        "ok" == "ok";
    }


    def "syncTaoBaoInventory"() {
        when:
        eh1.exec()
        then:
        "ok" == "ok";
    }

    def "syncSalesInfo"() {
        when:
        eh2.exec()
        then:
        "ok" == "ok";
    }


    def "StringUtils.substringAfterLast()"() {
        when:
        String dd = StringUtils.substringBeforeLast("805942-600-36.5", "-");
        String dd1 = StringUtils.substringBeforeLast("配货率：78%<br/>发货时效:18小时", "%");
        String dd2 = StringUtils.substringAfterLast("配货率：78%<br/>发货时效:18小时", "发货时效:");
        String dd3 = StringUtils.substringBeforeLast("配货率：100%<br/>发货时效:18小时", "%");

        // StringUtils.

        //StringUtils.substring()

        dd1 = dd1.replaceAll("配货率：", "");
        dd2 = dd2.substring(0, 2).replaceAll("小时", "");
        dd3 = dd3.replaceAll("配货率：", "");






        System.out.println(dd.toString())
        System.out.println(dd1.toString())
        System.out.println(dd2.toString())


        then:
        dd == "805942-600";
        dd1 == "78";
        dd2 == "18";
        dd3 == "100";
    }

//    def "config"() {
//        when:
//        Logger.info("getTianmaSportUserName:" + orderCatContext.getTianmaSportUserName())
//        Logger.info("getTianmaSportPassWord:" + orderCatContext.getTianmaSportPassWord())
//        Logger.info("getTianmaSportVcHttpUrl:" + orderCatContext.getTianmaSportVcHttpUrl())
//        Logger.info("getTianmaSportVcImageFileName:" + orderCatContext.getTianmaSportVcImageFileName())
//        Logger.info("getTianmaSportLoginHttpUrl:" + orderCatContext.getTianmaSportLoginHttpUrl())
//        Logger.info("getTianmaSportIDGHttpUrl:" + orderCatContext.getTianmaSportIDGHttpUrl())
//        //System.out.println(postExample.login());
//        then:
//        "ok" == "ok"
//    }


}
