package com.task.system.api;

import java.io.Serializable;

/**
 * 忽略body 数据 ，只取code msg  ，防止后端数据格式有误导致数据解析失败
 */
public class TaskInfoIgnoreBody implements Serializable {

    private int code = 0;// 10001
    private String msg = "请求成功";// "错误的请求KEY",

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

}
