package com.myjo.ordercat.domain;

public enum TianmaCheckStatus {
    ORDER_STATUS_DOES_NOT_MATCH("ORDER_STATUS_DOES_NOT_MATCH"), //订单状态不匹配
    ORDER_TIME_DOES_NOT_MATCH("ORDER_TIME_DOES_NOT_MATCH"), //订单时间不匹配
    ARTICLE_NUMBER_IS_NOT_CONSISTENT("ARTICLE_NUMBER_IS_NOT_CONSISTENT"),//货号不一致
    ORDER_QUANTITY_IS_NOT_CONSISTENT("ORDER_QUANTITY_IS_NOT_CONSISTENT"),//订单数量不一致
    NUM_QUANTITY_IS_NOT_CONSISTENT("NUM_QUANTITY_IS_NOT_CONSISTENT"), //订单中商品数量不一致
    ILLEGAL_OUTER_ORDER_ID("ILLEGAL_OUTER_ORDER_ID"), //天马外部订单编码非法（不是淘宝订单ID）
    NOT_FOUND_TAOBAO_ORDER("NOT_FOUND_TAOBAO_ORDER"),//没有找到淘宝订单
    DZ_SUCCESS("DZ_SUCCESS");//对账成功
    private String v;

    TianmaCheckStatus(String v) {
        this.v = v;
    }

    public String getValue() {
        return v;
    }

}
