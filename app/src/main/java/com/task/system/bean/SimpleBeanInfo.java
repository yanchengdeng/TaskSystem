package com.task.system.bean;

import java.io.Serializable;

//简单数据格式实体信息
public class SimpleBeanInfo implements Serializable {
    public String link;//客服地址
    public int sum;
    public String uid;
    public String url;
    public String  thumbnail;//;// http;////task.fzgfxz.com/Uploads/2019/03/5c9dfcc19986d.jpg,
    public String title;//;// ddd,
    public String content;
    public String sub_title;//;//
    public String path;//图片路径

    public String link_url;
    public String link_type;//链接类型|1=url地址，2=任务ID，3=专题ID，4=抽奖ID

    //人气  图片id
    public String id;// 126,
//    public String  title;// 淘宝零元购,
    public String   market_score;// 16.00,
    public String   success_total;// 0,
    public String   storages;// 940


}
