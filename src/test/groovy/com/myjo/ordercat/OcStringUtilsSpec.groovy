package com.myjo.ordercat

import com.myjo.ordercat.config.OrderCatConfig
import com.myjo.ordercat.utils.OcStringUtils
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
class OcStringUtilsSpec extends Specification {

    private static final Logger Logger = LogManager.getLogger(OcStringUtilsSpec.class);


    def setup() {

    }

    def println() {

    }

    def "OcStringUtils.getGoodsNoByOuterId"() {
        when:
        Random random = new Random();
        System.out.println(random.nextLong())
        String dd = OcStringUtils.getGoodsNoByOuterId("805942-600-36.5")
        String dd1 = OcStringUtils.getGoodsNoByOuterId("810506-011-XXXL")
        then:
        dd == "805942-600"
        dd1 == "810506-011"

    }


    def "OcStringUtils.judgeFilterOuterId"() {
        when:
//脚本引擎
        ScriptEngineManager m = new ScriptEngineManager();
        ScriptEngine e = m.getEngineByName("nashorn");
        e.eval(new FileReader(OrderCatConfig.getOrderCatScriptFilePath()));

        def t1 = OcStringUtils.judgeFilterOuterId(e,"805942-600-36.5麦巨");
        def t2 = OcStringUtils.judgeFilterOuterId(e,"805942-600-36.5");
        then:
        t1 == true
        t2 == false

    }





    def "OcStringUtils.isNumeric"() {
        when:

        def dd = OcStringUtils.isNumeric("818098-601")
        def dd1 = OcStringUtils.isNumeric("818098")
        def f = OcStringUtils.isNumeric("443.2")
        def f1 = OcStringUtils.isNumeric("12C")
        def f2 = OcStringUtils.isNumeric("-44.2")


        then:
        dd == false
        dd1 == true
        f == true
        f1 == false
        f2 == true
    }



}
