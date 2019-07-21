package com.task.system.enums;

/**
*
* Author: 邓言诚  Create at : 2019-07-21  14:24
* Email: yanchengdeng@gmail.com
* Describle:
 * v2.0.1  取消代理类型
 * 个人中心入口 新增会员和 修改会员只有  类型3 才可以
*/
public enum UserType {
    // 1  会员 2  代理  3  区域
    USER_TYPE_MEMBER("1"),
    USER_TYPE_AGENT("2"),
    USER_TYPE_AREA("3");

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
