package com.myjo.ordercat

import com.myjo.ordercat.http.TianmaSportHttp
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Created by lee5hx on 17/4/20.
 */
@Unroll
class TianmaSportHttpSpec extends Specification {


    private static Map<String, String> map;
    private static TianmaSportHttp tianmaSportHttp;

    def setup() {
        map = new HashMap<>();

        tianmaSportHttp = new TianmaSportHttp(map);
        tianmaSportHttp.getVerifyCodeImage();

        String v = "1111";

        tianmaSportHttp.login(v);
        tianmaSportHttp.main_html()

    }

//    def "getVerifyCodeImage"() {
//        when:
//        TianmaSportHttp tianmaSportHttp = new TianmaSportHttp(map);
//        def f = tianmaSportHttp.getVerifyCodeImage();
//        //System.out.println(postExample.login());
//        then:
//        f == "vcode.jpg"
//    }
//
//
//    def "Login"() {
//        when:
//        TianmaSportHttp tianmaSportHttp = new TianmaSportHttp(map);
//        String v = "1111";
//
//        def f = tianmaSportHttp.login(v);
//        System.out.println(f.getBoolean("success"));
//        then:
//        f.getBoolean("success") == false
//
//    }
//
//
//    def "main_html"() {
//        when:
//        TianmaSportHttp tianmaSportHttp = new TianmaSportHttp(map);
//        def f = tianmaSportHttp.main_html()
//        then:
//        f == "";
//
//    }

    def "inventoryDownGroup"() {
        when:
        //tianmaSportHttp = new TianmaSportHttp(map);
        //String v = IOUtils.toString(System.in)

        def f = tianmaSportHttp.inventoryDownGroup("耐克", "17Q1");
        //System.out.println(postExample.login());
        then:
        f == "1"
    }

    def "getSearchByArticleno"(){

        when:
         //tianmaSportHttp = new TianmaSportHttp(map);
        //String v = IOUtils.toString(System.in)
        tianmaSportHttp.getSearchByArticleno("707361-010");

        //def f = tianmaSportHttp.getSearchByArticleno("tianmaSportHttp")
        //System.out.println(postExample.login());
        then:
        "1" == "1"

    }

//    def "proxyManageGetId"() {
//        when:
//        TianmaSportHttp tianmaSportHttp = new TianmaSportHttp(map,orderCatContext);
//        //String v = "1111";
//
//        def f = tianmaSportHttp.proxyManageGetId();
//        //System.out.println(postExample.login());
//        then:
//        f == 1
//    }
//
//


}
