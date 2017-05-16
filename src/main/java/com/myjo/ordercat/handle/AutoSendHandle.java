package com.myjo.ordercat.handle;

/**
 * Created by lee5hx on 17/4/30.
 */
public class AutoSendHandle extends ExecuteHandle {

    private SendGoods sendGoods;

    public AutoSendHandle(SendGoods sendGoods) {
        this.sendGoods = sendGoods;
    }

    @Override
    public void exec(Long execJobId) throws Exception {
        //setJobName("SyncWarehouseJob");
        sendGoods.autoSend(execJobId);
    }

    public SendGoods getSendGoods() {
        return sendGoods;
    }

    public void setSendGoods(SendGoods sendGoods) {
        this.sendGoods = sendGoods;
    }
}
