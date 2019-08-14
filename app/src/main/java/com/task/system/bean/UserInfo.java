package com.task.system.bean;

import java.io.Serializable;

public class UserInfo implements Serializable {


    public String uid;//10001001,
    public String username;//10001001,
    public Object tokens;
    public String mobile;
    public String user_type;//用户类型 1-普通用户，2-代理，3-区域


    public String p_uid;//: "10010000",
    public String remark;//: "",
    public String avatar;//: "/Uploads/avatar/2019/5c80dfebbbe02.png",
    public String invite_code;//: "",
    public String score;//: "0.00"


    public String deposit_cash;//可提现金额，用户提现的时候显示，目前比例是1:1,以后可能会调整
    public String frozen_score;//冻结积分，提现申请成功后，会先冻结用户的积分
    public String history_score;//历史收益
    public String add_url;//发布任务地址
    public String is_new;//"1-是新用户，0不是新用户

}
