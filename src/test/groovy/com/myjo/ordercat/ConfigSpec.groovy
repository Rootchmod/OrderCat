package com.myjo.ordercat

import com.myjo.ordercat.config.OrderCatConfig
import com.myjo.ordercat.http.TianmaSportHttp
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Created by lee5hx on 17/4/20.
 */
@Unroll
class ConfigSpec extends Specification {

    private static final Logger Logger = LogManager.getLogger(TianmaSportHttp.class);


    OrderCatConfig orderCatContext;

    def setup() {
        orderCatContext = new OrderCatConfig();
    }

    def println() {

    }

    def "data-gathering.%s"(){
        when:
        def list = orderCatContext.getInventoryQueryConditions()
        def inventoryGroupFileName = orderCatContext.getInventoryGroupFileName();
        then:
        list.size() == 3
        list.get(0).brandName == "耐克"
        list.get(0).quarter == "17Q1"
        inventoryGroupFileName == "inventory_group.cvs"
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
