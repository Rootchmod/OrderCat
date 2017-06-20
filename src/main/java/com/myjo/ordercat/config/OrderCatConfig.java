package com.myjo.ordercat.config;

import com.myjo.ordercat.domain.FocusWHInfoReplace;
import com.myjo.ordercat.domain.InventoryQueryCondition;
import com.myjo.ordercat.domain.PickRateDelCondition;
import com.myjo.ordercat.domain.SalesPriceCalculatePolicy;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigBeanFactory;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigObject;


import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by lee5hx on 17/4/22.
 */
public class OrderCatConfig {

    private static final String TIANMA_SPORT = "tianma-sport.%s";
    private static final String ORDER_CAT = "order-cat.%s";
    private static final String DATA_GATHERING = "data-gathering.%s";
    private static final String SYNC_INVENTORY = "sync-inventory.%s";
    private static final String DATABASE = "database.%s";
    private static final String TAOBAO_API = "taobao-api.%s";
    private static final String SCHEDULER_CRON = "scheduler-cron.%s";
    private static final String ACCOUNT_CHECK = "account-check.%s";
    private static final String AUTO_SEND_GOODS = "auto-send-goods.%s";
    private static final String ORDER_OPERATE = "order-operate.%s";





    private static Config config;


    static {
        config = ConfigFactory.load("oc.conf");
//        config.checkValid(ConfigFactory.defaultReference(),
//                "tianma-sport",
//                "order-cat",
//                "data-gathering",
//                "sync-inventory",
//                "database",
//                "taobao-api");
    }

    // we have a constructor allowing the app to provide a custom Config
    private OrderCatConfig() {

    }

    public static void init(String cs) throws Exception{
        config = ConfigFactory.parseFile(new File(cs));
//        config.checkValid(ConfigFactory.defaultReference(),
//                "tianma-sport",
//                "order-cat",
//                "data-gathering",
//                "sync-inventory",
//                "database",
//                "taobao-api");
        //config = ConfigFactory.parseFile(new File(config));
    }


    public static Integer getAsdOrderDateIntervalDay(){
        return config.getInt(String.format(AUTO_SEND_GOODS, "order_date_interval_day"));
    }


    public static Integer getFenxiaoOrderDateIntervalDay(){
        return config.getInt(String.format(ACCOUNT_CHECK, "fenxiao_order_date_interval_day"));
    }


    public static Integer getTianmaOrderDateIntervalDay(){
        return config.getInt(String.format(ACCOUNT_CHECK, "tianma_order_date_interval_day"));
    }

    public static Long getTianmaPaytimeDifferDay(){
        return config.getLong(String.format(ACCOUNT_CHECK, "tianma_paytime_differ_day"));
    }






    public static List<String> getFeixiaoNoCheckNumIidList(){
        return config.getStringList(String.format(ACCOUNT_CHECK, "fenxiao_no_check_numIid_list"));
    }

    //syncWarehouseJob_trigger_cron
    public static String getSyncWarehouseJobTriggerCron(){
        return config.getString(String.format(SCHEDULER_CRON, "syncWarehouseJob_trigger_cron"));
    }

    //syncTaoBaoInventoryJob_trigger_cron
    public static String getSyncTaoBaoInventoryJobTriggerCron(){
        return config.getString(String.format(SCHEDULER_CRON, "syncTaoBaoInventoryJob_trigger_cron"));
    }


    public static String getSyncSalesInfoJobTriggerCron(){
        return config.getString(String.format(SCHEDULER_CRON, "syncSalesInfoJob_trigger_cron"));
    }


    public static String getFenxiaoAccountCheckJobTriggerCron(){
        return config.getString(String.format(SCHEDULER_CRON, "fenxiaoAccountCheckJob_trigger_cron"));
    }
    public static String getTianmaAccountCheckJobTriggerCron(){
        return config.getString(String.format(SCHEDULER_CRON, "tianmaAccountCheckJob_trigger_cron"));
    }


    public static String getAutoSendGoodsJobTriggerCron(){
        return config.getString(String.format(SCHEDULER_CRON, "autoSendGoodsJob_trigger_cron"));
    }


    public static String getGuessMailNoKeepJobTriggerCron(){
        return config.getString(String.format(SCHEDULER_CRON, "guessMailNoKeepJob_trigger_cron"));
    }











    public static String getSalesPriceEndReplace(){
        return config.getString(String.format(SYNC_INVENTORY, "sales_price_end_replace"));
    }


    public static Map<Integer,FocusWHInfoReplace> getFocusWarehouseInfoReplaceMap(){
        Map<Integer,FocusWHInfoReplace> rtMap = new HashMap<>();
        List<? extends ConfigObject> list = config.getObjectList(String.format(SYNC_INVENTORY, "focus_warehouse_info_replaces"));
        FocusWHInfoReplace fwir;
        for(ConfigObject cb :list){
            fwir = ConfigBeanFactory.create(cb.toConfig(),FocusWHInfoReplace.class);
            rtMap.put(fwir.getWarehouseId(),fwir);
        }
        return rtMap;
    }



//    public static SalesPriceCalculatePolicy getSalesPriceGtCalculate(){
//        return ConfigBeanFactory.create(config.getObject(String.format(SYNC_INVENTORY, "sales_price_gt_calculate")).toConfig(),SalesPriceCalculatePolicy.class);
//    }
//    public static SalesPriceCalculatePolicy getSalesPriceLtCalculate(){
//        return ConfigBeanFactory.create(config.getObject(String.format(SYNC_INVENTORY, "sales_price_lt_calculate")).toConfig(),SalesPriceCalculatePolicy.class);
//    }


    public static List<SalesPriceCalculatePolicy> getSalesPriceCalculatePolicys(){
        List<SalesPriceCalculatePolicy> rtList = new ArrayList<>();
        List<? extends ConfigObject> list = config.getObjectList(String.format(SYNC_INVENTORY, "sales_price_calculate_policy"));
        SalesPriceCalculatePolicy spcp;
        for(ConfigObject cb :list){
            spcp = ConfigBeanFactory.create(cb.toConfig(),SalesPriceCalculatePolicy.class);
            rtList.add(spcp);
        }
        return rtList;
    }



    public static String getTaobaoApiUrl(){

        //System.out.println(config.getString(String.format(TAOBAO_API, "url")));
        return config.getString(String.format(TAOBAO_API, "url"));
    }
    public static String getTaobaoApiAppKey(){
        //System.out.println(config.getString(String.format(TAOBAO_API, "app_key")));

        return config.getString(String.format(TAOBAO_API, "app_key"));
    }
    public static String getTaobaoApiAppSecret(){
        //System.out.println(config.getString(String.format(TAOBAO_API, "app_secret")));
        return config.getString(String.format(TAOBAO_API, "app_secret"));
    }
    public static String getTaobaoApiSessionKey(){
        //System.out.println(config.getString(String.format(TAOBAO_API, "session_key")));
        return config.getString(String.format(TAOBAO_API, "session_key"));
    }




    public static String getDBmsName(){
        return config.getString(String.format(DATABASE, "dbmsName"));
    }

    public static String getDBConnectionUrl(){
        return config.getString(String.format(DATABASE, "connectionUrl"));
    }
    public static String getDBUsername(){
        return config.getString(String.format(DATABASE, "username"));
    }
    public static String getDBPassword(){
        return config.getString(String.format(DATABASE, "password"));
    }


    //平均价格+1%
    public static Integer getAvgPriceAboveRate(){
        return Integer.valueOf(config.getString(String.format(SYNC_INVENTORY, "avg_price_above_rate")));
    }
    //销量大于等于20,或小于20
    public static Integer getProductSalesLimitCount(){
        return Integer.valueOf(config.getString(String.format(SYNC_INVENTORY, "product_sales_limit_count")));
    }
    //SKU基础线百分比
    public static Integer getSkuMultiplyRate(){
        return Integer.valueOf(config.getString(String.format(SYNC_INVENTORY, "sku_multiply_rate")));
    }


    public static Integer getFailurePickRateCount(){
        return Integer.valueOf(config.getString(String.format(SYNC_INVENTORY, "failure_pick_rate_count")));
    }

    public static Integer getPickRateLessThanDelLimit(){
        return Integer.valueOf(config.getString(String.format(SYNC_INVENTORY, "pick_rate_less_than_del_limit")));
    }

    public static List<PickRateDelCondition> getPickRateDelConditions(){
        List<PickRateDelCondition> rtList = new ArrayList<>();
        List<? extends ConfigObject> list = config.getObjectList(String.format(SYNC_INVENTORY, "pick_rate_less_than_del_conditions"));
        PickRateDelCondition prdc;
        for(ConfigObject cb :list){
            prdc = ConfigBeanFactory.create(cb.toConfig(),PickRateDelCondition.class);
            rtList.add(prdc);
        }
        return rtList;
    }


    public static String getInventoryGroupWhfile(){
        return config.getString(String.format(DATA_GATHERING, "wh_file"));
    }

    public static String getInventoryGroupIwhfile(){
        return config.getString(String.format(DATA_GATHERING, "iwh_file"));
    }


    public static List<InventoryQueryCondition> getInventoryQueryConditions(){
        List<InventoryQueryCondition> rtList = new ArrayList<>();
        List<? extends ConfigObject> list = config.getObjectList(String.format(DATA_GATHERING, "inventory_query_conditions"));
        InventoryQueryCondition iqc;
        for(ConfigObject cb :list){
            iqc = ConfigBeanFactory.create(cb.toConfig(),InventoryQueryCondition.class);
            rtList.add(iqc);
        }
        return rtList;
    }

    public static String getOrderCatOutPutPath(){
        return config.getString(String.format(ORDER_CAT, "output_path"));
    }


    public static String getOrderCatTempPath(){
        return config.getString(String.format(ORDER_CAT, "temp_path"));
    }



    public static boolean isProduction(){
        return config.getBoolean(String.format(ORDER_CAT, "is_production"));
    }


    public static String getTianmaSportUserName() {

        //return "麦巨对接测试";
        return config.getString(String.format(TIANMA_SPORT, "username"));
    }

    public static String getTianmaSportPassWord() {

        //return "123456";
        return config.getString(String.format(TIANMA_SPORT, "password"));
    }

    public static String getTianmaSportVcImageFileName() {
        //return "vcode.jpg";
        return config.getString(String.format(TIANMA_SPORT, "verify_code_image_file_name"));
    }

    public static String getTianmaSportVcHttpUrl() {
        return config.getString(String.format(TIANMA_SPORT, "verify_code_http_url"));
        //return "http://www.tianmasport.com/ms/ImageServlet?time=%d";
    }

    public static String getTianmaSportLoginHttpUrl() {
        //return "http://www.tianmasport.com/ms/beLogin.do";
        return config.getString(String.format(TIANMA_SPORT, "login_http_url"));
    }


    public static String getTianmaSportIDGHttpUrl() {
        return config.getString(String.format(TIANMA_SPORT, "inventory_down_group_http_url"));
        //return "http://www.tianmasport.com/ms/Inventory/downGroup.do";
    }

    public static String getTianmaSportDownLoadHttpUrl() {
        return config.getString(String.format(TIANMA_SPORT, "data_download_http_url"));
        //return "http://www.tianmasport.com/ms/Inventory/downGroup.do";
    }

    public static String getTianmaGetAreaHttpUrl() {
        return config.getString(String.format(TIANMA_SPORT, "get_area_http_url"));
        //http://www.tianmasport.com/ms/order/getArea.do?pid=0
    }

    public static String getSearchByArticlenoHttpUrl() {
        return config.getString(String.format(TIANMA_SPORT, "search_by_articleno_http_url"));
    }

    public static String getTradeOrdersDataListHttpUrl() {
        return config.getString(String.format(TIANMA_SPORT, "trade_orders_data_list_http_url"));
    }

    public static String getTradeOrderAddRemarkHttpUrl() {
        return config.getString(String.format(TIANMA_SPORT, "trade_order_add_remark_http_url"));
    }







    //发顺丰的快递价格上限
    //sf_price_gate = "25"
    public static String getOrderOperateSfPriceGate() {
        return config.getString(String.format(ORDER_OPERATE, "sf_price_gate"));
    }

    //天马支付密码
    //tm_pay_pwd = "VmtjMWQySnJOVlpPVkZwaFpXeGFZVll3VlhkUFVUMDk="
    public static String getOrderOperateTmPayPwd() {
        return config.getString(String.format(ORDER_OPERATE, "tm_pay_pwd"));
    }









}
