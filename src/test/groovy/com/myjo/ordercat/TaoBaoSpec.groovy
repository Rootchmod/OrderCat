package com.myjo.ordercat

import com.myjo.ordercat.domain.ItemsOnSale
import com.myjo.ordercat.domain.TaoBaoGoodInfo
import com.myjo.ordercat.http.TaoBaoHttp
import com.taobao.api.DefaultTaobaoClient
import com.taobao.api.TaobaoClient
import com.taobao.api.internal.tmc.Message
import com.taobao.api.internal.tmc.MessageHandler
import com.taobao.api.internal.tmc.MessageStatus
import com.taobao.api.internal.tmc.TmcClient
import com.taobao.api.request.TmcUserPermitRequest
import com.taobao.api.response.TmcUserPermitResponse
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



    def "updateTmallItemPriceUpdate"(){
        when:
        taoBaoHttp.updateTmallItemPriceUpdate();

        then:
        "ok" == "ok"
    }

    def "updateTmallItemQuantityUpdate"(){
        when:
        taoBaoHttp.updateTmallItemQuantityUpdate();

        then:
        "ok" == "ok"
    }


    def "test"(){
        when:
        taoBaoHttp.test();

        then:
        "ok" == "ok"
    }

    def "getFenxiaoOrdersByTcOrderId"(){
        when:
        taoBaoHttp.getFenxiaoOrdersByTcOrderId(21025551809015459l);

        then:
        "ok" == "ok"
    }

    def "getFenxiaoRefundBySubOrderId"(){
        when:
        taoBaoHttp.getFenxiaoRefundBySubOrderId(21852580950739l);

        then:
        "ok" == "ok"
    }

    def "TmcClient"(){
        when:

        taoBaoHttp.tmc_test()

        then:
        "ok" == "ok"
    }


    def "consumer_test"(){
        when:


//        def d = taoBaoHttp.getTaobaoTrade(37421251606578788l)
//        System.out.println(d.get().getType());
//        System.out.println(d.get().getTitle());
//        System.out.println(d.get().getIsDaixiao());
//
        def d2 = taoBaoHttp.getTaobaoTradeFullInfo(15052418593145554l)
        System.out.println(d2.get().getType());
        System.out.println(d2.get().getTitle());
        System.out.println(d2.get().getIsDaixiao());
        System.out.println(d2.get().getNum());



        def d1 = taoBaoHttp.getTaobaoTradeFullInfo(37432170161898885l)
        System.out.println(d1.get().getType());
        System.out.println(d1.get().getTitle());
        System.out.println(d1.get().getIsDaixiao());
        System.out.println(d1.get().getNum());
        then:
        "ok" == "ok"
    }



     // 消息环境地址：ws://mc.api.tbsandbox.com/



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
