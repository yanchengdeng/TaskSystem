package com.task.system.bean;

import java.io.Serializable;

/**
 * Author;// dengyc
 * Date;// 2019-09-01 21;//36
 * Description;//
 * History;//
 */
public class AwardItem implements Serializable {

    public String id;// 15,
    public String prize_id;// 4,
    public String title;// 大转盘,
    public String prize_name;// 四等奖,
    public String prize_image;// 22,
    public String prize_score;// 5,
    public String prize_type;// 2,
    public int status;// 1,
    public String prize_type_title;// 实物,
    public String status_title;// 已兑奖
    public String create_time;//参与时间//
    public Address address;


    public int can_set_address;// can_set_address= 1的时候才需要设置地址


    public class Address implements Serializable{
        public String name;//";//"中wen",
        public String mobile;//;//"15259984565",
        public String address;///;//"福建省福州市 鼓楼区 古田路 政客的疯狂的风景"
    }


}
