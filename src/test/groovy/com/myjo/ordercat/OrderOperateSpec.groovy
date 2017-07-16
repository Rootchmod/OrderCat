package com.myjo.ordercat

import com.alibaba.fastjson.JSON
import com.myjo.ordercat.config.OrderCatConfig
import com.myjo.ordercat.exception.OCException
import com.myjo.ordercat.handle.ExecuteHandle
import com.myjo.ordercat.handle.OrderOperate
import com.myjo.ordercat.http.TaoBaoHttp
import com.myjo.ordercat.http.TianmaSportHttp
import com.myjo.ordercat.spm.OrdercatApplication
import com.myjo.ordercat.spm.OrdercatApplicationBuilder
import com.myjo.ordercat.spm.ordercat.ordercat.oc_tm_order_records.OcTmOrderRecordsManager
import com.myjo.ordercat.utils.OcDateTimeUtils
import com.myjo.ordercat.utils.OcEncryptionUtils
import org.apache.commons.io.FileUtils
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
class OrderOperateSpec extends Specification {

    private static final Logger Logger = LogManager.getLogger(OrderOperateSpec.class);

    private static OrderOperate orderOperate;
    private static ExecuteHandle eh;
    private static ExecuteHandle eh1;

    def setup() {

        OrdercatApplication app = new OrdercatApplicationBuilder()
                .withConnectionUrl(OrderCatConfig.getDBmsName(), OrderCatConfig.getDBConnectionUrl())
                .withUsername(OrderCatConfig.getDBUsername())
                .withPassword(OcEncryptionUtils.base64Decoder(OrderCatConfig.getDBPassword(), 5))
                .build();

        Map<String, String> map = new HashMap<>();

        TianmaSportHttp tianmaSportHttp = new TianmaSportHttp(map);
        TaoBaoHttp taoBaoHttp = new TaoBaoHttp();

        //脚本引擎
        ScriptEngineManager m = new ScriptEngineManager();
        ScriptEngine e = m.getEngineByName("nashorn");


        OcTmOrderRecordsManager ocTmOrderRecordsManager = app.getOrThrow(OcTmOrderRecordsManager.class);

        tianmaSportHttp.getVerifyCodeImage();

        String vcode = "1111";

        orderOperate = new OrderOperate(tianmaSportHttp, taoBaoHttp, e);
        orderOperate.setOcTmOrderRecordsManager(ocTmOrderRecordsManager)

        //Logger.error("test1", new Exception("11111"));
        tianmaSportHttp.login(vcode);

        tianmaSportHttp.main_html();


    }

    def "autoOrder"() {//36073288294561388,30704141463704213
        when:
        orderOperate.autoOrder(14078118090179836l, "c01");
        then:
        "ok" == "ok";
    }

//    /**
//     * Common
//     *
//     * var size_info = '7.5<>40.5<>353524,8.5<>42<>353522,9<>42.5<>353521,9.5<>43<>353520,10<>44<>353519,'.split(",");
//     *
//     */
//    def "cw-common.json"() {
//
//        setup:
//        def jsonObjectList = getJsonObjectList("common.json")
//        def tmSkuId = "353521"
//        def payAmount = new BigDecimal("500");
//        def whid =""
//        when:
//        whid = orderOperate.computeWarehouseId(jsonObjectList, tmSkuId, payAmount,LocalDateTime.now())
//        then:
//        "ok" == "ok"
//    }

//    def "cw-priority_order_wh_policy.json"() {
//
//        setup:
//        def jsonObjectList = getJsonObjectList("priority_order_wh_policy.json")
//        def tmSkuId = "353521"
//        def payAmount = new BigDecimal("500");
//        def whid =""
//        when:
//        whid = orderOperate.computeWarehouseId(jsonObjectList, tmSkuId, payAmount,LocalDateTime.now()).get()
//        then:
//        whid == "182"
//
//    }

//    Z1Z5(0),//配货时间：周一至周五|支持快递
//    Z1Z6(1),//配货时间：周一至周六|支持快递
//    Z1Z7(2);//配货时间：周一至周日|支持快递

//    def "cw-week_order_wh_policy.json-pr=0"() {
//
//        setup:
//        def jsonObjectList = getJsonObjectList("week_order_wh_policy-pr0.json")
//        def tmSkuId = "353521"
//        def payAmount = new BigDecimal("500");
//        def nowDate = OcDateTimeUtils.string2LocalDateTime("2017-07-07 04:00:00");
//        def nowDate1 = OcDateTimeUtils.string2LocalDateTime("2017-07-08 03:59:59");
//        def whid1;
//        def whid2;
//        when:
//        whid1 = orderOperate.computeWarehouseId(jsonObjectList, tmSkuId, payAmount,nowDate)
//        whid2 = orderOperate.computeWarehouseId(jsonObjectList, tmSkuId, payAmount,nowDate1)
//        then:
//        whid1.isPresent() == false
//        whid2.isPresent() == false
//
//    }

//    def "cw-week_order_wh_policy.json-pr=0,1,2"() {
//
//        setup:
//        def jsonObjectList = getJsonObjectList("week_order_wh_policy-pr012.json")
//        def tmSkuId = "353521"
//        def payAmount = new BigDecimal("500");
//        def nowDate = OcDateTimeUtils.string2LocalDateTime("2017-07-07 04:00:00");
//
//        def whid1;
//
//        when:
//        whid1 = orderOperate.computeWarehouseId(jsonObjectList, tmSkuId, payAmount,nowDate)
//        then:
//        whid1.isPresent() == true
//        whid1.get() == "222";
//
//
//    }


    def "保本价450选择仓库111.json"() {

        setup:
        def jsonObjectList = getJsonObjectList("保本价450_选择仓库111.json")
        def tmSkuId = "353519"
        def payAmount = new BigDecimal("508");
        def nowDate = OcDateTimeUtils.string2LocalDateTime("2017-07-10 09:00:00");

        def whid1;

        when:
        whid1 = orderOperate.computeWarehouseId(jsonObjectList, tmSkuId, payAmount, nowDate, "B")
        then:
        //whid1.isPresent() == true
        whid1.get().getWarehouseId() == "111";
        whid1.get().getInventoryCount() == "25";
    }


    def "保本价400_A策略选择仓库222.json"() {

        setup:
        def jsonObjectList = getJsonObjectList("保本价400_A策略选择仓库222.json")
        def tmSkuId = "353519"
        def payAmount = new BigDecimal("465");
        def nowDate = OcDateTimeUtils.
                string2LocalDateTime("2017-07-10 09:00:00");

        def whid1;

        when:
        whid1 = orderOperate.computeWarehouseId(jsonObjectList, tmSkuId, payAmount, nowDate, "A")
        then:
        //whid1.isPresent() == true
        whid1.get().getWarehouseId() == "222";
        whid1.get().getInventoryCount() == "30";
    }

    def "保本价400_B策略选择仓库333.json"() {

        setup:
        def jsonObjectList = getJsonObjectList("保本价400_B策略选择仓库333.json")
        def tmSkuId = "353519"
        def payAmount = new BigDecimal("465");
        def nowDate = OcDateTimeUtils.string2LocalDateTime("2017-07-10 09:00:00");

        def whid1;

        when:
        whid1 = orderOperate.computeWarehouseId(jsonObjectList, tmSkuId, payAmount, nowDate, "B")
        then:
        //whid1.isPresent() == true
        whid1.get().getWarehouseId() == "333";
        whid1.get().getInventoryCount() == "1";
    }


    def "保本价326_选择仓库444_保本价过滤.json"() {

        setup:
        def jsonObjectList = getJsonObjectList("保本价326_选择仓库444_保本价过滤.json")
        def tmSkuId = "353519"
        def payAmount = new BigDecimal("375");
        def nowDate = OcDateTimeUtils.string2LocalDateTime("2017-07-10 09:00:00");

        def whid1;

        when:
        whid1 = orderOperate.computeWarehouseId(jsonObjectList, tmSkuId, payAmount, nowDate, "B")
        then:
        //whid1.isPresent() == true
        whid1.get().getWarehouseId() == "444";
        whid1.get().getInventoryCount() == "4532";
    }


    def "cw-not_order_wh_policy.json"() {

        setup:
        def jsonObjectList = getJsonObjectList("not_order_wh_policy.json")
        def tmSkuId = "353521"
        def payAmount = new BigDecimal("500");
        def nowDate = OcDateTimeUtils.string2LocalDateTime("2017-07-10 09:00:00");

        def whid = ""
        when:
        whid = orderOperate.computeWarehouseId(jsonObjectList, tmSkuId, payAmount, nowDate, "B")
        then:
        def e = thrown(OCException)
        e.getMessage() == "[成都虚拟仓]-需要劲浪下单"

    }


    def "priority_order_wh_policy.json"() {

        setup:
        def jsonObjectList = getJsonObjectList("priority_order_wh_policy.json")
        def tmSkuId = "353521"
        def payAmount = new BigDecimal("500");
        def nowDate = OcDateTimeUtils.string2LocalDateTime("2017-07-10 09:00:00");

        def whid = ""
        when:
        whid = orderOperate.computeWarehouseId(jsonObjectList, tmSkuId, payAmount, nowDate, "B")
        then:
        whid.get().getWarehouseId() == "182";
        whid.get().getInventoryCount() == "8";

    }


    def getJsonObjectList(fileName) {

        def list = new ArrayList<com.alibaba.fastjson.JSONObject>();
        def jsonstr = FileUtils.readFileToString(new File("/Users/lee5hx/src/myjo/OrderCat/src/test/resources/com/myjo/ordercat/" + fileName), "UTF-8")
        def object = JSON.parseObject(jsonstr)
        def jsonObject
        def array = object.getJSONArray("rows")

        for (int i = 0; i < array.size(); i++) {
            jsonObject = array.getJSONObject(i)

            list.add(jsonObject)

        }
        return list;

    }


}
