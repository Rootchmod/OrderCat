package com.myjo.ordercat.domain;

/**
 * Created by lee5hx on 17/4/23.
 */
public class PickRateDelCondition {

    private Integer llPickRate;
    private Integer ulPickRate;
    private Integer repertory;

    public Integer getLlPickRate() {
        return llPickRate;
    }

    public void setLlPickRate(Integer llPickRate) {
        this.llPickRate = llPickRate;
    }

    public Integer getUlPickRate() {
        return ulPickRate;
    }

    public void setUlPickRate(Integer ulPickRate) {
        this.ulPickRate = ulPickRate;
    }

    public Integer getRepertory() {
        return repertory;
    }

    public void setRepertory(Integer repertory) {
        this.repertory = repertory;
    }
}
