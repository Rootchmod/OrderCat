package com.myjo.ordercat.domain;

/**
 * Created by lee5hx on 17/4/27.
 *
 //    WAIT_BUYER_PAY：等待买家付款
 //    WAIT_SELLER_SEND_GOODS：等待卖家发货
 //    SELLER_CONSIGNED_PART：卖家部分发货
 //    WAIT_BUYER_CONFIRM_GOODS：等待买家确认收货
 //    TRADE_BUYER_SIGNED：买家已签收（货到付款专用）
 //    TRADE_FINISHED：交易成功
 //    TRADE_CLOSED：交易关闭
 //    TRADE_CLOSED_BY_TAOBAO：交易被淘宝关闭
 //    TRADE_NO_CREATE_PAY：没有创建外部交易（支付宝交易）
 //    WAIT_PRE_AUTH_CONFIRM：余额宝0元购合约中
 //    PAY_PENDING：外卡支付付款确认中
 //    ALL_WAIT_PAY：所有买家未付款的交易（包含：WAIT_BUYER_PAY、TRADE_NO_CREATE_PAY）
 //    ALL_CLOSED：所有关闭的交易（包含：TRADE_CLOSED、TRADE_CLOSED_BY_TAOBAO）
 */
public enum JobStatus {
    SUCCESS("SUCCESS"),//SUCCESS
    FAILURE("FAILURE"),//FAILURE
    RUNNING("RUNNING");//RUNNING

    private String v;

    JobStatus(String v){
        this.v = v;
    }
}
