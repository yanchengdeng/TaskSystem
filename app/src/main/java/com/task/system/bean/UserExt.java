package com.task.system.bean;

import java.io.Serializable;

/**
 * Copyright (C), 2016-2019, 福建大道之行有限公司
 * FileName;// UserExt
 * Author;// dengyc
 * Date;// 2019-08-15 23;//14
 * Description;//
 * History;//
 */
public class UserExt implements Serializable {

    public String idcard_bind;// 0,
    public String business_bind;// "1",
    public String wx_bind;// "1",
    public IdCardInfo idcard_info;

    public BussinessInfo business_info;



   public class IdCardInfo implements Serializable{
        public String   idcard_front;// "http;////dev.xhdcmgood.com/Public/default.png",
        public String   idcard_back;// "http;////dev.xhdcmgood.com/Public/default.png",
        public String  idcard_hand;// "http;////dev.xhdcmgood.com/Public/default.png",
        public String  idcard_name;// "刚的",
        public String  idcard;// "340322198802023256",
        public String  status;// "0"
    }

  public   class BussinessInfo implements Serializable{
        public String   business_image;// "http;////dev.xhdcmgood.com/Public/default.png",
        public String  business_open_permit;// "http;////dev.xhdcmgood.com/Public/default.png",
        public String   business_hand;// "http;////dev.xhdcmgood.com/Public/default.png",
        public String  business_name;// "福建丁丁网络科技有限公司",
        public String  business_address;// "福建省福州市鼓楼区六一路",
        public String  business_bank_account;// "6225001904384333",
        public String   business_open_bank;// "福建省福州市鼓楼支行",
        public String   business_contact;// "林某人",
        public String  business_contact_mobile;// "15259267856",
        public String  status;// "1"
    }

}
