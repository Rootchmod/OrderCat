package com.myjo.ordercat.context;

import com.myjo.ordercat.handle.OrderOperate;
import com.myjo.ordercat.spm.ordercat.ordercat.oc_fenxiao_check_result.OcFenxiaoCheckResultManager;
import com.myjo.ordercat.spm.ordercat.ordercat.oc_job_exec_info.OcJobExecInfo;
import com.myjo.ordercat.spm.ordercat.ordercat.oc_job_exec_info.OcJobExecInfoManager;
import com.myjo.ordercat.spm.ordercat.ordercat.oc_tm_order_records.OcTmOrderRecords;
import com.myjo.ordercat.spm.ordercat.ordercat.oc_tm_order_records.OcTmOrderRecordsManager;
import com.myjo.ordercat.spm.ordercat.ordercat.oc_tmsport_check_result.OcTmsportCheckResultManager;
import com.myjo.ordercat.spm.ordercat.ordercat.oc_warehouse_info.OcWarehouseInfo;
import com.myjo.ordercat.spm.ordercat.ordercat.oc_warehouse_info.OcWarehouseInfoManager;

/**
 * Created by lee5hx on 17/6/14.
 */
public class OrderCatContext {

    private static OcTmsportCheckResultManager ocTmsportCheckResultManager;
    private static OcFenxiaoCheckResultManager ocFenxiaoCheckResultManager;
    private static OcTmOrderRecordsManager ocTmOrderRecordsManager;
    private static OrderOperate orderOperate;
    private static OcWarehouseInfoManager ocWarehouseInfoManager;
    private static OcJobExecInfoManager ocJobExecInfoManager;

    public static OcTmsportCheckResultManager getOcTmsportCheckResultManager() {
        return ocTmsportCheckResultManager;
    }

    public static void setOcTmsportCheckResultManager(OcTmsportCheckResultManager ocTmsportCheckResultManager) {
        OrderCatContext.ocTmsportCheckResultManager = ocTmsportCheckResultManager;
    }

    public static OcFenxiaoCheckResultManager getOcFenxiaoCheckResultManager() {
        return ocFenxiaoCheckResultManager;
    }

    public static void setOcFenxiaoCheckResultManager(OcFenxiaoCheckResultManager ocFenxiaoCheckResultManager) {
        OrderCatContext.ocFenxiaoCheckResultManager = ocFenxiaoCheckResultManager;
    }

    public static OrderOperate getOrderOperate() {
        return orderOperate;
    }

    public static void setOrderOperate(OrderOperate orderOperate) {
        OrderCatContext.orderOperate = orderOperate;
    }

    public static OcTmOrderRecordsManager getOcTmOrderRecordsManager() {
        return ocTmOrderRecordsManager;
    }

    public static void setOcTmOrderRecordsManager(OcTmOrderRecordsManager ocTmOrderRecordsManager) {
        OrderCatContext.ocTmOrderRecordsManager = ocTmOrderRecordsManager;
    }

    public static OcWarehouseInfoManager getOcWarehouseInfoManager() {
        return ocWarehouseInfoManager;
    }

    public static void setOcWarehouseInfoManager(OcWarehouseInfoManager ocWarehouseInfoManager) {
        OrderCatContext.ocWarehouseInfoManager = ocWarehouseInfoManager;
    }

    public static OcJobExecInfoManager getOcJobExecInfoManager() {
        return ocJobExecInfoManager;
    }

    public static void setOcJobExecInfoManager(OcJobExecInfoManager ocJobExecInfoManager) {
        OrderCatContext.ocJobExecInfoManager = ocJobExecInfoManager;
    }
}
