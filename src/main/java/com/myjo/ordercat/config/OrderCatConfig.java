package com.myjo.ordercat.config;

import com.myjo.ordercat.domain.InventoryQueryCondition;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigBeanFactory;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigObject;



import java.util.ArrayList;
import java.util.List;


/**
 * Created by lee5hx on 17/4/22.
 */
public class OrderCatConfig {

    private static final String TIANMA_SPORT = "tianma-sport.%s";
    private static final String ORDER_CAT = "order-cat.%s";
    private static final String DATA_GATHERING = "data-gathering.%s";
    private static Config config;

    static {
        config = ConfigFactory.load("oc.conf");
    }

    // we have a constructor allowing the app to provide a custom Config
    private OrderCatConfig() {

    }
//    public OrderCatConfig() {
//        this(ConfigFactory.load("oc.conf"));
//
//        // config.checkValid(ConfigFactory.load("oc.conf"), "tianmasport");
//
//
//        //Logger.debug(config.origin().resource());
//    }



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

    public static String getSearchByArticlenoHttpUrl() {
        return config.getString(String.format(TIANMA_SPORT, "search_by_articleno_http_url"));
    }







}
