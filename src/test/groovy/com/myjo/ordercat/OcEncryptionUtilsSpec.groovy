package com.myjo.ordercat

import com.myjo.ordercat.utils.OcEncryptionUtils
import com.myjo.ordercat.utils.OcStringUtils
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Created by lee5hx on 17/4/20.
 */
@Unroll
class OcEncryptionUtilsSpec extends Specification {

    private static final Logger Logger = LogManager.getLogger(OcEncryptionUtilsSpec.class);


    def setup() {
    }

    def println() {

    }

    def "base64"() {
        when:
            String d1 = OcEncryptionUtils.base64Encoder("123456",5)
            System.out.println(d1);
            String d2 = OcEncryptionUtils.base64Decoder("VmtaYVUxTnRWbkpPVlZaWFZrVnJPUT09",5)

            String d3 = OcEncryptionUtils.base64Encoder("wy789789",5)
            System.out.println(d3);

            String d4 = OcEncryptionUtils.base64Encoder("789789as",5)
            System.out.println(d4);

        String d5 = OcEncryptionUtils.base64Encoder("myjo123123_",5)
        System.out.println(d5);




        then:
            d1 == "VmtaYVUxTnRWbkpPVlZaWFZrVnJPUT09"
            d2 == "123456"


    }



}
