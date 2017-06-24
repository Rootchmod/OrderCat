package com.myjo.ordercat.handle;

/**
 * Created by lee5hx on 17/4/30.
 */
public class SyncTaoBaoInventoryHandle extends ExecuteHandle {

    private SyncInventory syncInventory;


    public SyncTaoBaoInventoryHandle(SyncInventory syncInventory) {
        this.syncInventory = syncInventory;
    }

    @Override
    public void exec(Long execJobId) throws Exception {
       // setJobName("SyncTaoBaoInventoryJob");
        syncInventory.syncTaoBaoInventory(execJobId);
    }

    public SyncInventory getSyncInventory() {
        return syncInventory;
    }

    public void setSyncInventory(SyncInventory syncInventory) {
        this.syncInventory = syncInventory;
    }
}
