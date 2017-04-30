package com.myjo.ordercat

import com.myjo.ordercat.config.OrderCatConfig
import com.myjo.ordercat.handle.SyncInventory
import com.myjo.ordercat.http.TaoBaoHttp
import com.myjo.ordercat.http.TianmaSportHttp
import com.myjo.ordercat.spm.OrdercatApplication
import com.myjo.ordercat.spm.OrdercatApplicationBuilder
import com.myjo.ordercat.spm.ordercat.ordercat.oc_inventory_info.OcInventoryInfoManager
import com.myjo.ordercat.spm.ordercat.ordercat.oc_job_exec_info.OcJobExecInfoManager
import com.myjo.ordercat.spm.ordercat.ordercat.oc_warehouse_info.OcWarehouseInfoManager
import org.apache.commons.lang3.StringUtils
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Created by lee5hx on 17/4/20.
 */
@Unroll
class SyncInventorySpec extends Specification {

    private static final Logger Logger = LogManager.getLogger(SyncInventorySpec.class);

    private static SyncInventory si;


    def setupSpec() {

        OrdercatApplication app = new OrdercatApplicationBuilder()
                .withConnectionUrl(OrderCatConfig.getDBmsName(), OrderCatConfig.getDBConnectionUrl())
                .withUsername(OrderCatConfig.getDBUsername())
                .withPassword(OrderCatConfig.getDBPassword())
                .build();

        OcWarehouseInfoManager ocWarehouseInfoManager = app.getOrThrow(OcWarehouseInfoManager.class);
        OcJobExecInfoManager ocJobExecInfoManager = app.getOrThrow(OcJobExecInfoManager.class);
        OcInventoryInfoManager ocInventoryInfoManager = app.getOrThrow(OcInventoryInfoManager.class);

        Map<String,String> map = new HashMap<>();

        TianmaSportHttp tianmaSportHttp = new TianmaSportHttp(map);
        TaoBaoHttp taoBaoHttp = new TaoBaoHttp();
        si = new SyncInventory(tianmaSportHttp,taoBaoHttp);
        si.setOcInventoryInfoManager(ocInventoryInfoManager);
        si.setOcWarehouseInfoManager(ocWarehouseInfoManager);
        si.setOcJobExecInfoManager(ocJobExecInfoManager);

    }


    def "syncWarehouseInfo"() {
        when:
        si.syncWarehouseInfo();
        then:
        "ok" == "ok";
    }


    def "syncTaoBaoInventory"() {
        when:
        si.syncTaoBaoInventory();
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
