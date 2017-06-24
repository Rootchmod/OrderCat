package com.myjo.ordercat.handle;

/**
 * Created by lee5hx on 17/4/30.
 */
public class TianMaAcHandle extends ExecuteHandle {

    private AccountCheck accountCheck;

    public TianMaAcHandle(AccountCheck accountCheck) {
        this.accountCheck = accountCheck;
    }

    @Override
    public void exec(Long execJobId) throws Exception {
        //setJobName("SyncWarehouseJob");
        accountCheck.tianmaCheck(execJobId);
    }


    public AccountCheck getAccountCheck() {
        return accountCheck;
    }

    public void setAccountCheck(AccountCheck accountCheck) {
        this.accountCheck = accountCheck;
    }
}
