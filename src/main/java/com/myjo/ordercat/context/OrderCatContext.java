package com.myjo.ordercat.context;

import com.myjo.ordercat.handle.OrderOperate;
import com.myjo.ordercat.spm.ordercat.ordercat.oc_fenxiao_check_result.OcFenxiaoCheckResultManager;
import com.myjo.ordercat.spm.ordercat.ordercat.oc_tmsport_check_result.OcTmsportCheckResultManager;

/**
 * Created by lee5hx on 17/6/14.
 */
public class OrderCatContext {

    private static OcTmsportCheckResultManager ocTmsportCheckResultManager;
    private static OcFenxiaoCheckResultManager ocFenxiaoCheckResultManager;
    private static OrderOperate orderOperate;

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
}
