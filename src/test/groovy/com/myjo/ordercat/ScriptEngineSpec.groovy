package com.myjo.ordercat

import com.myjo.ordercat.utils.OcEncryptionUtils
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import spock.lang.Specification
import spock.lang.Unroll

import javax.script.Invocable
import javax.script.ScriptEngine
import javax.script.ScriptEngineManager

/**
 * Created by lee5hx on 17/4/20.
 */
@Unroll
class ScriptEngineSpec extends Specification {

    private static final Logger Logger = LogManager.getLogger(ScriptEngineSpec.class);


    def setup() {
    }

    def println() {

    }

    def "ScriptEngine-Test"() {
        when:
            ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
            engine.eval(new FileReader("/Users/lee5hx/src/myjo/OrderCat/src/test/groovy/com/myjo/ordercat/oc_function.js"));

            Invocable invocable = (Invocable) engine;

            Object result = invocable.invokeFunction("judgeFilterOuterId", "1111-1111-111麦巨");
            System.out.println(result);
            System.out.println(result.getClass());

        then:
            "ok"=="ok"


    }



}
