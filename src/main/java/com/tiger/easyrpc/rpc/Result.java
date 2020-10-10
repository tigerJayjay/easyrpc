package com.tiger.easyrpc.rpc;

import java.io.Serializable;

import static com.tiger.easyrpc.common.EasyrpcConstant.DATA_TYPE_INVOKE;

public class Result implements Serializable {
    private Integer type = DATA_TYPE_INVOKE;
    private Long mesId;
    private Object result;
    private Throwable exception;

    public Object getResult() {
        return result;
    }
    public void setResult(Object result) {
        this.result = result;
    }
    public Long getMesId() {
        return mesId;
    }
    public void setMesId(Long mesId) {
        this.mesId = mesId;
    }
    public Throwable getException() {
        return exception;
    }
    public void setException(Throwable exception) {
        this.exception = exception;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Result{" +
                "mesId='" + mesId + '\'' +
                ", result=" + result +
                '}';
    }
}
