package com.task.system.enums;

/**
 * 1-注册时的手机验证码，不需要uid、token
 * 2-找回密码的时候的手机验证码，不需要uid、token
 * 3-修改手机号码时候的手机验证码,需要uid、token
 * 4-新增银行账户信息，
 * 5-申请提现
 */
public enum MobileCode {
    MOBILE_CODE_REGISTER("1"),
    MOBILE_CODE_FORGET("2"),
    MOBILE_CODE_RESET("3"),
    MOBILE_CODE_ADD_BANK_ACCOUNT("4"),
    MOBILE_CODE_WITHDRAW("5"),
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
