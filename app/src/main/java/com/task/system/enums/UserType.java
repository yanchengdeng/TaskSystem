package com.task.system.enums;

public enum UserType {
    // 1  会员 2  代理  3  区域
    USER_TYPE_MEMBER("1"), USER_TYPE_AGENT("2"), USER_TYPE_AREA("3");

    private String type;

    UserType(String s) {
        this.type = s;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
