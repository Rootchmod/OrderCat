package com.myjo.ordercat.domain;

/**
 * Created by lee5hx on 17/4/26.
 */
public class Size {

    private Sex sex;
    private String US;
    private String UK;
    private String Europe;
    private String CM;


    public Size(Sex sex, String US, String UK, String europe, String CM) {
        this.sex = sex;
        this.US = US;
        this.UK = UK;
        Europe = europe;
        this.CM = CM;
    }

    public Sex getSex() {
        return sex;
    }

    public void setSex(Sex sex) {
        this.sex = sex;
    }

    public String getUS() {
        return US;
    }

    public void setUS(String US) {
        this.US = US;
    }

    public String getUK() {
        return UK;
    }

    public void setUK(String UK) {
        this.UK = UK;
    }

    public String getEurope() {
        return Europe;
    }

    public void setEurope(String europe) {
        Europe = europe;
    }

    public String getCM() {
        return CM;
    }

    public void setCM(String CM) {
        this.CM = CM;
    }
}
