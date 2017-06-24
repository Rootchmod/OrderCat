package com.myjo.ordercat.domain;

import java.util.Optional;

/**
 * Created by lee5hx on 17/5/27.
 */
public class ReturnResult<T> {
    private boolean success;
    private String errorCode;
    private String errorMessages;
    private Optional<T> result;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessages() {
        return errorMessages;
    }

    public void setErrorMessages(String errorMessages) {
        this.errorMessages = errorMessages;
    }

    public Optional<T> getResult() {
        return result;
    }
    public void setResult(T result) {
        this.result = Optional.ofNullable(result);
    }
}
