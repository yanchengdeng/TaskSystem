package com.task.system.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Copyright (C), 2016-2019, 福建大道之行有限公司
 * FileName;// LotteryItem
 * Author;// dengyc
 * Date;// 2019-08-11 21;//20
 * Description;//积分抽奖
 * History;//
 */

public class LotteryList implements Serializable{

    public List<LotteryItem> list;


    public class LotteryItem implements Serializable {


        public String id;// 1,
        public String title;// 大转盘,
        public String content;//     大转盘内容,
        public String thumbnail;// 22,
        public String bg_image;// ,
        public String share_description;// 大转盘分享,
        public String daily_number;// 5,
        public String score;// 2,
        public String thumbnail_url;// http;////joyce.task.com/Uploads/2019/03/5c9dfcc19986d.jpg,
        public String bg_image_url;// http;////joyce.task.com/Public/default.png

        public String play_url;//游戏地址
    }
    
    
}


