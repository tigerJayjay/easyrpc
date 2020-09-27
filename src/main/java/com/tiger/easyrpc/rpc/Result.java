package com.tiger.easyrpc.rpc;

import java.io.Serializable;

public class Result implements Serializable {
    private String mesId;
    private Object result;
    public Object getResult() {
        return result;
    }
    public void setResult(Object result) {
        this.result = result;
    }
    public String getMesId() {
        return mesId;
    }
    public void setMesId(String mesId) {
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
