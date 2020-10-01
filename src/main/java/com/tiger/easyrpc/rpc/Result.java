package com.tiger.easyrpc.rpc;

import java.io.Serializable;

public class Result implements Serializable {
    private Long mesId;
    private Object result;
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

    @Override
    public String toString() {
        return "Result{" +
                "mesId='" + mesId + '\'' +
                ", result=" + result +
                '}';
    }
}
