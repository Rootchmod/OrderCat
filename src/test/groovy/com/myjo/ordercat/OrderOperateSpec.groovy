package com.myjo.ordercat

import com.myjo.ordercat.config.OrderCatConfig
import com.myjo.ordercat.domain.JobName
import com.myjo.ordercat.handle.AutoSendHandle
import com.myjo.ordercat.handle.ExecuteHandle
import com.myjo.ordercat.handle.OrderOperate
import com.myjo.ordercat.handle.SendGoods
import com.myjo.ordercat.handle.SyncLcHandle
import com.myjo.ordercat.http.TaoBaoHttp
import com.myjo.ordercat.http.TianmaSportHttp
import com.myjo.ordercat.spm.OrdercatApplication
import com.myjo.ordercat.spm.OrdercatApplicationBuilder
import com.myjo.ordercat.spm.ordercat.ordercat.oc_inventory_info.OcInventoryInfoManager
import com.myjo.ordercat.spm.ordercat.ordercat.oc_job_exec_info.OcJobExecInfoManager
import com.myjo.ordercat.spm.ordercat.ordercat.oc_logistics_companies_info.OcLogisticsCompaniesInfoManager
import com.myjo.ordercat.spm.ordercat.ordercat.oc_sales_info.OcSalesInfoManager
import com.myjo.ordercat.spm.ordercat.ordercat.oc_tm_order_records.OcTmOrderRecordsManager
import com.myjo.ordercat.spm.ordercat.ordercat.oc_warehouse_info.OcWarehouseInfoManager
import com.myjo.ordercat.utils.OcEncryptionUtils
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import spock.lang.Specification
import spock.lang.Unroll

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
                .withPassword(OcEncryptionUtils.base64Decoder(OrderCatConfig.getDBPassword(),5))
                .build();

        Map<String,String> map = new HashMap<>();

        TianmaSportHttp tianmaSportHttp = new TianmaSportHttp(map);
        TaoBaoHttp taoBaoHttp = new TaoBaoHttp();


        OcTmOrderRecordsManager ocTmOrderRecordsManager = app.getOrThrow(OcTmOrderRecordsManager.class);



        tianmaSportHttp.getVerifyCodeImage();

        String vcode = "1111";

        orderOperate = new OrderOperate(tianmaSportHttp,taoBaoHttp);
        orderOperate.setOcTmOrderRecordsManager(ocTmOrderRecordsManager)

        //Logger.error("test1",new Exception("11111"));

        tianmaSportHttp.login(vcode);

        tianmaSportHttp.main_html();


    }

    def "manualOrder"() {
        when:

        //订单号: 25988863027166718
        orderOperate.manualOrder(30116390378191766,"410","789789as",null,null)
        then:
        "ok" == "ok";
    }


}
