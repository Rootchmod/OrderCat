package com.myjo.ordercat.domain.constant;

/**
 * Created by lee5hx on 17/4/27.
 *
 * //     if(v==0){ return "配货时间：周一至周五|支持快递："+r.expressName;}
 * //		else if(v==1){ return "配货时间：周一至周六|支持快递："+r.expressName;}
 * //		else if(v==2){ return "配货时间：周一至周日|支持快递："+r.expressName;}
 */
public enum PickDate {
    Z1Z5(0),//配货时间：周一至周五|支持快递
    Z1Z6(1),//配货时间：周一至周六|支持快递
    Z1Z7(2);//配货时间：周一至周日|支持快递

    private int v;

    private final static PickDate[] PICK_DATES = PickDate.values();


    PickDate(int v){
        this.v = v;
    }

    public static PickDate valueOf(int value) {

        PickDate pickDate = null;
        for (PickDate pd : PICK_DATES) {
            if (pd.getValue() == value) {
                pickDate = pd;
                break;
            }
        }
        return pickDate;
    }

    public int getValue() {
        return v;
    }


}
