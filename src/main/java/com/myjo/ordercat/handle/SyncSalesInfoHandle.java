package com.myjo.ordercat.handle;

/**
 * Created by lee5hx on 17/4/30.
 */
public class SyncSalesInfoHandle extends ExecuteHandle {

    private SyncInventory syncInventory;

    public SyncSalesInfoHandle(SyncInventory syncInventory) {
        this.syncInventory = syncInventory;
    }

    @Override
    public void exec(Long execJobId) throws Exception {
        //setJobName("SyncWarehouseJob");
        syncInventory.syncSalesInfo(execJobId);
    }

    public SyncInventory getSyncInventory() {
        return syncInventory;
    }

    public void setSyncInventory(SyncInventory syncInventory) {
        this.syncInventory = syncInventory;
    }
}
