package com.tiger.easyrpc.rpc.api;

import java.io.Serializable;

public class Result implements Serializable {
    private Object result;


    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }
}
