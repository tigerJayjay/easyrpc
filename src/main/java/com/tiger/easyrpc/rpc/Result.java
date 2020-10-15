package com.tiger.easyrpc.rpc;

import java.io.Serializable;

import static com.tiger.easyrpc.common.EasyrpcConstant.DATA_TYPE_INVOKE;

/**
 * 远程服务相应信息
 */
public class Result implements Serializable {
    //相应类型
    private Integer type = DATA_TYPE_INVOKE;
    //消息id，与请求id一致
    private Long mesId;
    //方法返回结果
    private Object result;
    //保存远程调用异常信息
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
