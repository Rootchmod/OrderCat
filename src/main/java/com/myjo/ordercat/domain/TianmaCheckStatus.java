package com.myjo.ordercat.domain;

public enum TianmaCheckStatus {
    OUTER_SKU_MATCHING_ERROR("OUTER_SKU_MATCHING_ERROR"),//外部SKU匹配失败
    OUTER_SKU_MAIJU_ERROR("OUTER_SKU_MAIJU_ERROR"), //外部供应商编码包含-麦巨
    REFUND_DOES_NOT_MATCH("REFUND_DOES_NOT_MATCH"), //退款信息不匹配（淘宝已退款，天马未退款）
    OTHER_ERROR("OTHER_ERROR"), //其他错误
    NOT_FOUND_TAOBAO_ORDER("NOT_FOUND_TAOBAO_ORDER");//没有找到淘宝订单

    private String v;

    TianmaCheckStatus(String v){
        this.v = v;
    }

    public String getValue() {
        return v;
    }

}
