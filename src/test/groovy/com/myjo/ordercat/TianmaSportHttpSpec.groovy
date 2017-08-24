package com.myjo.ordercat

import com.myjo.ordercat.domain.LogisticsCompany
import com.myjo.ordercat.domain.TmArea
import com.myjo.ordercat.http.TianmaSportHttp
import com.taobao.api.domain.PurchaseOrder
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

    def "ajaxGuessMailNoRequest"(){

        when:
        //tianmaSportHttp = new TianmaSportHttp(map);
        //String v = IOUtils.toString(System.in)
        for(int i=0;i<100000;i++){
            Optional<LogisticsCompany> logisticsCompany = tianmaSportHttp.ajaxGuessMailNoRequest("885214803258033378","23134990467245578");
            Optional<LogisticsCompany> logisticsCompany1 = tianmaSportHttp.ajaxGuessMailNoRequest("3921971273918","23134990467245578");
            Thread.sleep(10000)
        }

        //def f = tianmaSportHttp.getSearchByArticleno("tianmaSportHttp")
        //System.out.println(postExample.login());
        then:
        logisticsCompany.get().code.get() == "YTO"
        logisticsCompany1.get().code.get() == "YUNDA"

    }


   // 新疆维吾尔自治区 吐鲁番市 高昌区
    private String getPidInAreas(List<TmArea> list, String name) {
        String pid = null;
        for (TmArea t : list) {
            if (t.getName().equals(name)) {
                pid = String.valueOf(t.getId());
                break;
            }
        }
        return pid;
    }

    def "getArea-test"(){

        when:
//        //tianmaSportHttp = new TianmaSportHttp(map);
//        //String v = IOUtils.toString(System.in)
//        for(int i=0;i<100000;i++){
//            Optional<LogisticsCompany> logisticsCompany = tianmaSportHttp.ajaxGuessMailNoRequest("885214803258033378","23134990467245578");
//            Optional<LogisticsCompany> logisticsCompany1 = tianmaSportHttp.ajaxGuessMailNoRequest("3921971273918","23134990467245578");
//            Thread.sleep(10000)
//        }
//
//        //def f = tianmaSportHttp.getSearchByArticleno("tianmaSportHttp")
//        //System.out.println(postExample.login());

        List<TmArea> list = tianmaSportHttp.getArea("0");
        String province_id = getPidInAreas(list, "新疆维吾尔自治区");
        list = tianmaSportHttp.getArea(province_id);
        String city_id = getPidInAreas(list, "吐鲁番市");
        list = tianmaSportHttp.getArea(city_id);
        String area_id = getPidInAreas(list, "高昌区");
        System.out.println(area_id)

        then:
        logisticsCompany.get().code.get() == "YTO"
        logisticsCompany1.get().code.get() == "YUNDA"

    }


    def "orderCancel"(){

        when:
//        //tianmaSportHttp = new TianmaSportHttp(map);
//        //String v = IOUtils.toString(System.in)
//        for(int i=0;i<100000;i++){
//            Optional<LogisticsCompany> logisticsCompany = tianmaSportHttp.ajaxGuessMailNoRequest("885214803258033378","23134990467245578");
//            Optional<LogisticsCompany> logisticsCompany1 = tianmaSportHttp.ajaxGuessMailNoRequest("3921971273918","23134990467245578");
//            Thread.sleep(10000)
//        }
//
//        //def f = tianmaSportHttp.getSearchByArticleno("tianmaSportHttp")
//        //System.out.println(postExample.login());

         def rt = tianmaSportHttp.orderCancel("23947062");
         System.out.println(rt.getResult().get())

        then:
        "ok" == "ok"

    }

    def "backExpressNo"(){

        when:
//        //tianmaSportHttp = new TianmaSportHttp(map);
//        //String v = IOUtils.toString(System.in)
//        for(int i=0;i<100000;i++){
//            Optional<LogisticsCompany> logisticsCompany = tianmaSportHttp.ajaxGuessMailNoRequest("885214803258033378","23134990467245578");
//            Optional<LogisticsCompany> logisticsCompany1 = tianmaSportHttp.ajaxGuessMailNoRequest("3921971273918","23134990467245578");
//            Thread.sleep(10000)
//        }
//
//        //def f = tianmaSportHttp.getSearchByArticleno("tianmaSportHttp")
//        //System.out.println(postExample.login());

        def rt = tianmaSportHttp.backExpressNo("2390201412312312","88880000001111","oc-顺丰速递1");
        System.out.println(rt.getResult().get())

        then:
        "ok" == "ok"

    }

    def "soldProblem"(){

        when:
        //    id:23783596
//    problemType:无理由退货
//    proxyId:147424
//    wareHouseName:天马总仓1仓
//    marketPrice:599
//    discount:5.8
//    problemContent:OC-售后
//    delivery:顺丰标快
//    mapPath:

        def rt = tianmaSportHttp.soldProblem("23783596","599","天马总仓1仓","5.8","顺丰标快")
        System.out.println(rt.getResult().get())

        then:
        "ok" == "ok"

    }


    def "getTianmaOrder"(){
        when:
        //    id:23783596
//    problemType:无理由退货
//    proxyId:147424
//    wareHouseName:天马总仓1仓
//    marketPrice:599
//    discount:5.8
//    problemContent:OC-售后
//    delivery:顺丰标快
//    mapPath:

        def rt = tianmaSportHttp.getTianmaOrder(23899969)
        System.out.println(rt)

        then:
        "ok" == "ok"

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
