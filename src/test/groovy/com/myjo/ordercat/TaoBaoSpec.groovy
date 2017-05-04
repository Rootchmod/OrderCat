package com.myjo.ordercat

import com.myjo.ordercat.domain.ItemsOnSale
import com.myjo.ordercat.domain.TaoBaoGoodInfo
import com.myjo.ordercat.http.TaoBaoHttp
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Created by lee5hx on 17/4/20.
 */
@Unroll
class TaoBaoSpec extends Specification {

    private static final Logger Logger = LogManager.getLogger(TaoBaoSpec.class);

    private static TaoBaoHttp taoBaoHttp;



    def setupSpec() {
        taoBaoHttp = new TaoBaoHttp()
    }



    def "getTaobaoItemsOnSale and TaoBaoItemSkus"(){
        when:
        List<ItemsOnSale> list =  taoBaoHttp.getTaobaoItemsOnSale();
        List<TaoBaoGoodInfo> list1 =  taoBaoHttp.getTaoBaoItemSkus(list);

        then:
        list.size() == list1.size();
    }



    def "itemcatsGetRequest"(){
        when:
        taoBaoHttp.itemcatsGetRequest();

        then:
        "ok" == "ok"
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
