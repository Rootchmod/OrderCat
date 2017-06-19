package com.myjo.ordercat.domain;

/**
 * Created by lee5hx on 17/6/15.
 */
public class TmSizeInfo {
    //7<>40<>1618062,7.5<>40.5<>1618060,8<>41<>1618063,8.5<>42<>1618058,9<>42.5<>1618059,9.5<>43<>1618057,10<>44<>1618065,10.5<>44.5<>1618061,11<>45<>1618064,13<>47.5<>1618066,12<>46<>1618067,
    private String size1;
    private String size2;
    private String tmSukId;

    public String getSize1() {
        return size1;
    }

    public void setSize1(String size1) {
        this.size1 = size1;
    }

    public String getSize2() {
        return size2;
    }

    public void setSize2(String size2) {
        this.size2 = size2;
    }

    public String getTmSukId() {
        return tmSukId;
    }

    public void setTmSukId(String tmSukId) {
        this.tmSukId = tmSukId;
    }
}
