package com.myjo.ordercat

import com.myjo.ordercat.utils.OcStringUtils
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Created by lee5hx on 17/4/20.
 */
@Unroll
class OcStringUtilsSpec extends Specification {

    private static final Logger Logger = LogManager.getLogger(OcStringUtilsSpec.class);


    def setup() {
    }

    def println() {

    }

    def "OcStringUtils.getGoodsNoByOuterId"() {
        when:
        String dd = OcStringUtils.getGoodsNoByOuterId("805942-600-36.5")
        String dd1 = OcStringUtils.getGoodsNoByOuterId("810506-011-XXXL")
        then:
        dd == "805942-600"
        dd1 == "810506-011"

    }


}
