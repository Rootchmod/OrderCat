package com.myjo.ordercat.handle;

/**
 * Created by lee5hx on 17/4/30.
 */
public class RefundOperateHandle extends ExecuteHandle {

    private RefundOperate refundOperate;


    public RefundOperateHandle(RefundOperate refundOperate) {
        this.refundOperate = refundOperate;
    }

    @Override
    public void exec(Long execJobId) throws Exception {
       // setJobName("SyncTaoBaoInventoryJob");
        refundOperate.autoRefund(execJobId);
    }

    public RefundOperate getRefundOperate() {
        return refundOperate;
    }

    public void setRefundOperate(RefundOperate refundOperate) {
        this.refundOperate = refundOperate;
    }
}
