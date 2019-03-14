package com.task.system.enums;

public enum MobileCode {
    MOBILE_CODE_REGISTER("1"),
    MOBILE_CODE_FORGET("2"),
    MOBILE_CODE_RESET("3"),
    ;

    private String type;
    MobileCode(String s) {
        this.type = s;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
