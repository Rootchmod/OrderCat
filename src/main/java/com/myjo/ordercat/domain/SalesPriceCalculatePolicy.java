package com.myjo.ordercat.domain;

/**
 * Created by lee5hx on 17/5/2.
 */
public class SalesPriceCalculatePolicy {

    //judge="salesCount<20",equation="proxyPrice/0.93+25",planType=""
    private String judge;
    private String equation;
    private String planType;

    public String getJudge() {
        return judge;
    }

    public void setJudge(String judge) {
        this.judge = judge;
    }

    public String getEquation() {
        return equation;
    }

    public void setEquation(String equation) {
        this.equation = equation;
    }

    public String getPlanType() {
        return planType;
    }

    public void setPlanType(String planType) {
        this.planType = planType;
    }
}
