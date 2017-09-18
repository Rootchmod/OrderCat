package com.myjo.ordercat.handle;

/**
 * Created by lee5hx on 17/4/30.
 */
public class RepairOrderHandle extends ExecuteHandle {

    private OrderOperate orderOperate;

    public RepairOrderHandle(OrderOperate orderOperate) {
        this.orderOperate = orderOperate;
    }

    @Override
    public void exec(Long execJobId) throws Exception {
        //setJobName("SyncWarehouseJob");
        orderOperate.batchRepairOrder(execJobId);
    }

    public OrderOperate getOrderOperate() {
        return orderOperate;
    }

    public void setOrderOperate(OrderOperate orderOperate) {
        this.orderOperate = orderOperate;
    }
}
