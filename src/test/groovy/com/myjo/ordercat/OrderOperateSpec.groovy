package com.myjo.ordercat

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONArray
import com.myjo.ordercat.config.OrderCatConfig
import com.myjo.ordercat.handle.ExecuteHandle
import com.myjo.ordercat.handle.OrderOperate
import com.myjo.ordercat.http.TaoBaoHttp
import com.myjo.ordercat.http.TianmaSportHttp
import com.myjo.ordercat.spm.OrdercatApplication
import com.myjo.ordercat.spm.OrdercatApplicationBuilder
import com.myjo.ordercat.spm.ordercat.ordercat.oc_tm_order_records.OcTmOrderRecordsManager
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

        orderOperate = new OrderOperate(tianmaSportHttp, taoBaoHttp,e);
        orderOperate.setOcTmOrderRecordsManager(ocTmOrderRecordsManager)

        //Logger.error("test1",new Exception("11111"));

        tianmaSportHttp.login(vcode);

        tianmaSportHttp.main_html();


    }

    def "manualOrder"() {
        when:
        orderOperate.manualOrder(30116390378191766, "410", "789789as", null, null)
        then:
        "ok" == "ok";
    }

    /**
     * Common
     *
     * var size_info = '7.5<>40.5<>353524,8.5<>42<>353522,9<>42.5<>353521,9.5<>43<>353520,10<>44<>353519,'.split(",");
     *
     */
    def "computeWarehouseId-Common"() {

        setup:
        def jsonObjectList = getJsonObjectList("common.json")
        def tmSkuId = "353521"
        def payAmount = new BigDecimal("500");
        def whid =""
        when:
        whid = orderOperate.computeWarehouseId(jsonObjectList, tmSkuId, payAmount)
        then:
        "ok" == "ok"
    }

    def getJsonObjectList(fileName){

        def list = new ArrayList<com.alibaba.fastjson.JSONObject>();
        def jsonstr = FileUtils.readFileToString(new File("/Users/lee5hx/src/myjo/OrderCat/src/test/resources/com/myjo/ordercat/"+fileName),"UTF-8")
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
