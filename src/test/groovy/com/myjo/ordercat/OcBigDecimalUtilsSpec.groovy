package com.myjo.ordercat

import com.myjo.ordercat.utils.OcBigDecimalUtils
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import spock.lang.Specification
import spock.lang.Unroll

import javax.script.ScriptEngine
import javax.script.ScriptEngineManager

/**
 * Created by lee5hx on 17/4/20.
 */
@Unroll
class OcBigDecimalUtilsSpec extends Specification {

    private static final Logger Logger = LogManager.getLogger(OcBigDecimalUtilsSpec.class);



    def setup() {


    }

    def println() {

    }

    def "OcBigDecimalUtils.purchasePrice"(){

        setup:
        ScriptEngineManager m = new ScriptEngineManager();
        ScriptEngine e = m.getEngineByName("nashorn");

        when:
//        def ff = OcBigDecimalUtils.toSalesPrice(new BigDecimal("500"),true);
        def ff = OcBigDecimalUtils.toSalesPrice(e,new BigDecimal("495.02"),"proxyPrice/0.93+25");
        def ff1 = OcBigDecimalUtils.toSalesPrice(e,new BigDecimal("500"),"proxyPrice/0.9+25");

        then:
        ff.toString() == "559";
        ff1.toString() == "589";

    }



    def "OcBigDecimalUtils.salesLimitCountJudge"(){

        setup:
        ScriptEngineManager m = new ScriptEngineManager();
        ScriptEngine e = m.getEngineByName("nashorn");

        when:
        def ff = OcBigDecimalUtils.salesLimitCountJudge(e,21L,"salesCount>20 && salesCount<=50")
        def ff1 = OcBigDecimalUtils.salesLimitCountJudge(e,20L,"salesCount<=20")
        def ff2 = OcBigDecimalUtils.salesLimitCountJudge(e,21L,"salesCount<=20")
        def ff3 = OcBigDecimalUtils.salesLimitCountJudge(e,50L,"salesCount>50")
        def ff4 = OcBigDecimalUtils.salesLimitCountJudge(e,51L,"salesCount>50")

        then:
        ff == true;
        ff1 == true;
        ff2 == false;
        ff3 == false;
        ff4 == true;


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
