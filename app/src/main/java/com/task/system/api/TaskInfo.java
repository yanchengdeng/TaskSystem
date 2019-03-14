package com.task.system.api;

import java.io.Serializable;

public class TaskInfo<T> implements Serializable {


    private int code = 0;// 10001
    private String msg = "请求成功";// "错误的请求KEY",
    private T body;// null,

    public int getStatus_code() {
        return code;
    }

    public void setStatus_code(int status_code) {
        this.code = status_code;
    }

    public String getMessage() {
        return msg;
    }

    public void setMessage(String message) {
        this.msg = message;
    }

    public T getData() {
        return body;
    }

    public void setData(T data) {
        this.body = data;
    }
}
