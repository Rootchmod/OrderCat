package com.myjo.ordercat.domain;

import java.util.Optional;

/**
 * Created by lee5hx on 17/5/26.
 */
public class LogisticsCompany {

    private String code;
    private String name;

    public Optional<String> getCode() {
        return Optional.ofNullable(code);
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Optional<String> getName() {
        return Optional.ofNullable(name);
    }

    public void setName(String name) {
        this.name = name;
    }

}
