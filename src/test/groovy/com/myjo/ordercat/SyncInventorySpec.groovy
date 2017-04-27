package com.myjo.ordercat

import com.myjo.ordercat.config.OrderCatConfig
import com.myjo.ordercat.handle.SyncInventory
import com.myjo.ordercat.http.TaoBaoHttp
import com.myjo.ordercat.http.TianmaSportHttp
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

    private static final Logger Logger = LogManager.getLogger(TianmaSportHttp.class);

    private static SyncInventory si;

    private static Map<String, String> map;
    private static TianmaSportHttp tianmaSportHttp;
    private static TaoBaoHttp taoBaoHttp;

    def setup() {

        map = new HashMap<>();

        tianmaSportHttp = new TianmaSportHttp(map);
        tianmaSportHttp.getVerifyCodeImage();

        taoBaoHttp = new TaoBaoHttp();

        String v = "1111";

//        tianmaSportHttp.login(v);
//        tianmaSportHttp.main_html()

        si = new SyncInventory(tianmaSportHttp,taoBaoHttp)
    }



    def "syncWarehouseInfo"(){
        when:
        si.syncWarehouseInfo();
        then:
        "ok" == "ok";
    }


    def "syncTaoBaoInventory"(){
        when:
        si.syncTaoBaoInventory();
        then:
        "ok" == "ok";
    }



    def "StringUtils.substringAfterLast()"(){
        when:
        String dd = StringUtils.substringBeforeLast("805942-600-36.5","-");
        String dd1 = StringUtils.substringBeforeLast("配货率：78%<br/>发货时效:18小时","%");
        String dd2 = StringUtils.substringAfterLast("配货率：78%<br/>发货时效:18小时","发货时效:");
        String dd3 = StringUtils.substringBeforeLast("配货率：100%<br/>发货时效:18小时","%");


        // StringUtils.

        //StringUtils.substring()

        dd1 = dd1.replaceAll("配货率：","");
        dd2 = dd2.substring(0,2).replaceAll("小时","");
        dd3 = dd3.replaceAll("配货率：","");






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
