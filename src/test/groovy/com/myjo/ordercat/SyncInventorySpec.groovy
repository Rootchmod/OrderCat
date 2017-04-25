package com.myjo.ordercat

import com.myjo.ordercat.config.OrderCatConfig
import com.myjo.ordercat.handle.SyncInventory
import com.myjo.ordercat.http.TianmaSportHttp
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

    def setup() {

        map = new HashMap<>();

        tianmaSportHttp = new TianmaSportHttp(map);
        tianmaSportHttp.getVerifyCodeImage();

        String v = "1111";

        tianmaSportHttp.login(v);
        tianmaSportHttp.main_html()

        si = new SyncInventory(tianmaSportHttp)
    }



    def "syncWarehouseInfo"(){
        when:
        si.syncWarehouseInfo();
        then:
        "ok" == "ok";
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
