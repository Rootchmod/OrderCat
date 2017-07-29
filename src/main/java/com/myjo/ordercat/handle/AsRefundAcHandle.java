package com.myjo.ordercat.handle;

/**
 * Created by lee5hx on 17/4/30.
 */
public class AsRefundAcHandle extends ExecuteHandle {

    private AccountCheck accountCheck;

    public AsRefundAcHandle(AccountCheck accountCheck) {
        this.accountCheck = accountCheck;
    }

    @Override
    public void exec(Long execJobId) throws Exception {
        //setJobName("SyncWarehouseJob");
        accountCheck.afterSalesRefundCheck(execJobId);
    }


    public AccountCheck getAccountCheck() {
        return accountCheck;
    }

    public void setAccountCheck(AccountCheck accountCheck) {
        this.accountCheck = accountCheck;
    }
}
