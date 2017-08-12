package com.myjo.ordercat

import com.myjo.ordercat.config.OrderCatConfig
import com.myjo.ordercat.domain.JobName
import com.myjo.ordercat.handle.*
import com.myjo.ordercat.http.TaoBaoHttp
import com.myjo.ordercat.http.TianmaSportHttp
import com.myjo.ordercat.spm.OrdercatApplication
import com.myjo.ordercat.spm.OrdercatApplicationBuilder
import com.myjo.ordercat.spm.ordercat.ordercat.oc_job_exec_info.OcJobExecInfoManager
import com.myjo.ordercat.spm.ordercat.ordercat.oc_refund_operate_record.OcRefundOperateRecordManager
import com.myjo.ordercat.spm.ordercat.ordercat.oc_sales_info.OcSalesInfoManager
import com.myjo.ordercat.spm.ordercat.ordercat.oc_sync_inventory_item_info.OcSyncInventoryItemInfoManager
import com.myjo.ordercat.spm.ordercat.ordercat.oc_warehouse_info.OcWarehouseInfoManager
import com.myjo.ordercat.utils.OcEncryptionUtils
import org.apache.commons.lang3.StringUtils
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.json.JSONObject
import spock.lang.Specification
import spock.lang.Unroll

import javax.script.ScriptEngine
import javax.script.ScriptEngineManager

/**
 * Created by lee5hx on 17/4/20.
 */
@Unroll
class RefundOperateSpec extends Specification {

    private static final Logger Logger = LogManager.getLogger(RefundOperateSpec.class);

    private static RefundOperate refundOperate;
    private static ExecuteHandle eh;

    def setup() {

        OrdercatApplication app = new OrdercatApplicationBuilder()
                .withConnectionUrl(OrderCatConfig.getDBmsName(), OrderCatConfig.getDBConnectionUrl())
                .withUsername(OrderCatConfig.getDBUsername())
                .withPassword(OcEncryptionUtils.base64Decoder(OrderCatConfig.getDBPassword(), 5))
                .build();

        //OcWarehouseInfoManager ocWarehouseInfoManager = app.getOrThrow(OcWarehouseInfoManager.class);
        OcJobExecInfoManager ocJobExecInfoManager = app.getOrThrow(OcJobExecInfoManager.class);
        //OcInventoryInfoManager ocInventoryInfoManager = app.getOrThrow(OcInventoryInfoManager.class);
        //OcSalesInfoManager ocSalesInfoManager = app.getOrThrow(OcSalesInfoManager.class);
        //OcSyncInventoryItemInfoManager ocSyncInventoryItemInfoManager = app.getOrThrow(OcSyncInventoryItemInfoManager.class);


        OcRefundOperateRecordManager ocRefundOperateRecordManager = app.getOrThrow(OcRefundOperateRecordManager.class);


        ScriptEngineManager m = new ScriptEngineManager();
        ScriptEngine e = m.getEngineByName("nashorn");

        Map<String,String> map = new HashMap<>();

        TianmaSportHttp tianmaSportHttp = new TianmaSportHttp(map);
        TaoBaoHttp taoBaoHttp = new TaoBaoHttp();
        refundOperate = new RefundOperate(tianmaSportHttp,taoBaoHttp,e);
        refundOperate.setOcRefundOperateRecordManager(ocRefundOperateRecordManager)




        //ExecuteHandle eh;
        eh = new RefundOperateHandle(refundOperate);
        eh.setJobName(JobName.AUTO_REFUND_JOB.getValue());
        eh.setOcJobExecInfoManager(ocJobExecInfoManager);

        tianmaSportHttp.getVerifyCodeImage();

        String vcode = "1111";

        //Logger.error("test1",new Exception("11111"));

        JSONObject jsonObject = tianmaSportHttp.login(vcode);

        tianmaSportHttp.main_html();



    }


    def "autoRefund"() {
        when:
        eh.exec()
        then:
        "ok" == "ok";
    }




}
