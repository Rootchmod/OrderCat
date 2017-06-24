package com.myjo.ordercat

import com.myjo.ordercat.domain.Brand
import com.myjo.ordercat.domain.Sex
import com.myjo.ordercat.utils.OcSizeUtils
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Created by lee5hx on 17/4/20.
 */
@Unroll
class OcSizeUtilsSpec extends Specification {

    private static final Logger Logger = LogManager.getLogger(OcSizeUtilsSpec.class);


    def setup() {
    }

    def println() {

    }

    def "OcSizeUtilsSpec.getShoeSize1BySize2"() {
        when:
        def f = OcSizeUtils.getShoeSize1BySize2(Brand.NIKE, Sex.MALE, "9");
        def f1 = OcSizeUtils.getShoeSize1BySize2(Brand.NIKE, Sex.FEMALE, "6");
        def f2 = OcSizeUtils.getShoeSize1BySize2(Brand.NIKE, Sex.FEMALE, "5");



        then:
        f == "42.5"
        f1 == "36.5"
        f2 == "35.5"


    }


}
