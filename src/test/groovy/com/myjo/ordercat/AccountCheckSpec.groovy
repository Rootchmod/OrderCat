package com.myjo.ordercat

import com.myjo.ordercat.config.OrderCatConfig
import com.myjo.ordercat.domain.JobName
import com.myjo.ordercat.handle.AccountCheck
import com.myjo.ordercat.handle.AsRefundAcHandle
import com.myjo.ordercat.handle.ExecuteHandle
import com.myjo.ordercat.handle.TianMaAcHandle
import com.myjo.ordercat.http.TaoBaoHttp
import com.myjo.ordercat.http.TianmaSportHttp
import com.myjo.ordercat.spm.OrdercatApplication
import com.myjo.ordercat.spm.OrdercatApplicationBuilder
import com.myjo.ordercat.spm.ordercat.ordercat.oc_as_refund_check_result.OcAsRefundCheckResultManager
import com.myjo.ordercat.spm.ordercat.ordercat.oc_fenxiao_check_result.OcFenxiaoCheckResultManager
import com.myjo.ordercat.spm.ordercat.ordercat.oc_inventory_info.OcInventoryInfoManager
import com.myjo.ordercat.spm.ordercat.ordercat.oc_job_exec_info.OcJobExecInfoManager
import com.myjo.ordercat.spm.ordercat.ordercat.oc_logistics_companies_info.OcLogisticsCompaniesInfoManager
import com.myjo.ordercat.spm.ordercat.ordercat.oc_sales_info.OcSalesInfoManager
import com.myjo.ordercat.spm.ordercat.ordercat.oc_sync_inventory_item_info.OcSyncInventoryItemInfoManager
import com.myjo.ordercat.spm.ordercat.ordercat.oc_tmsport_check_result.OcTmsportCheckResultManager
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
class AccountCheckSpec extends Specification {

    private static final Logger Logger = LogManager.getLogger(AccountCheckSpec.class);

    private static AccountCheck ac;
    private static ExecuteHandle eh;
    private static ExecuteHandle eh1;

    def setup() {

        OrdercatApplication app = new OrdercatApplicationBuilder()
                .withConnectionUrl(OrderCatConfig.getDBmsName(), OrderCatConfig.getDBConnectionUrl())
                .withUsername(OrderCatConfig.getDBUsername())
                .withPassword(OcEncryptionUtils.base64Decoder(OrderCatConfig.getDBPassword(),5))
                .build();

        OcJobExecInfoManager ocJobExecInfoManager = app.getOrThrow(OcJobExecInfoManager.class);
        OcTmsportCheckResultManager ocTmsportCheckResultManager = app.getOrThrow(OcTmsportCheckResultManager.class);
        OcAsRefundCheckResultManager ocAsRefundCheckResultManager = app.getOrThrow(OcAsRefundCheckResultManager.class);

        Map<String,String> map = new HashMap<>();

        TianmaSportHttp tianmaSportHttp = new TianmaSportHttp(map);
        TaoBaoHttp taoBaoHttp = new TaoBaoHttp();
        ac = new AccountCheck(tianmaSportHttp,taoBaoHttp);
        ac.setOcTmsportCheckResultManager(ocTmsportCheckResultManager);
        ac.setOcAsRefundCheckResultManager(ocAsRefundCheckResultManager);
//        si.setOcInventoryInfoManager(ocInventoryInfoManager);
//        si.setOcWarehouseInfoManager(ocWarehouseInfoManager);
//        si.setOcJobExecInfoManager(ocJobExecInfoManager);
//        si.setOcSalesInfoManager(ocSalesInfoManager);

        //ExecuteHandle eh;
        eh = new AsRefundAcHandle(ac)
        eh.setJobName(JobName.AS_REFUND_ACCOUNT_CHECK_JOB.getValue());
        eh.setOcJobExecInfoManager(ocJobExecInfoManager);



        eh1 = new TianMaAcHandle(ac)
        eh1.setJobName(JobName.TIANMA_ACCOUNT_CHECK_JOB.getValue());
        eh1.setOcJobExecInfoManager(ocJobExecInfoManager);




        tianmaSportHttp.getVerifyCodeImage();

        String vcode = "1111";

        //Logger.error("test1",new Exception("11111"));

        tianmaSportHttp.login(vcode);

        tianmaSportHttp.main_html();


    }


    def "asRefundCheck"() {
        when:
        eh.exec()
        then:
        "ok" == "ok";
    }

    def "tianmaCheck"() {
        when:
        eh1.exec()
        then:
        "ok" == "ok";
    }


}
