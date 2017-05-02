package com.myjo.ordercat

import com.myjo.ordercat.utils.OcBigDecimalUtils
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import spock.lang.Specification
import spock.lang.Unroll

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
        when:
        def ff = OcBigDecimalUtils.toSalesPrice(new BigDecimal("500"),true);
        def ff1 = OcBigDecimalUtils.toSalesPrice(new BigDecimal("495.02"),false);

        then:
        ff.toString() == "589";
        ff1.toString() == "559";

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
