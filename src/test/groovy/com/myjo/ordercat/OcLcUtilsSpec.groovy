package com.myjo.ordercat

import com.myjo.ordercat.utils.OcLcUtils
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Created by lee5hx on 17/4/20.
 */
@Unroll
class OcLcUtilsSpec extends Specification {

    private static final Logger Logger = LogManager.getLogger(OcLcUtilsSpec.class);


    def "OcLcUtils.getPickRate"() {
        when:
        String dd = OcLcUtils.getPickRate("配货率：87%<br/>发货时效:11小时").toPlainString()
        String dd1 = OcLcUtils.getPickRate("配货率：0%<br/>发货时效:0小时").toPlainString()
        then:
        dd == "87"
        dd1 == "0"

    }



}
