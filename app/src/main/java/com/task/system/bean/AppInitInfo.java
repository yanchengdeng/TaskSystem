package com.task.system.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Copyright (C), 2016-2019, 福建大道之行有限公司
 * FileName: AppInitInfo
 * Author: dengyc
 * Date: 2019-08-08 00:59
 * Description:
 * History:
 */
public class AppInitInfo implements Serializable {

    public String remind_open_location ;//是否要求开启本地定位，0=否 1=是
    public List<AdInfo> ad;
    public List<DepositInfo> deposit;//奖赏
    public String new_message_sum;
    public List<CatergoryInfo> categorys;

    public String is_new;//是否是新用户
    public String avatar;




   public class DepositInfo implements Serializable{
        public String  nickname_txt;//: "lin***",
        public String   cash_txt;//": "40.00"
       public String avatar = "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1565209641952&di=ceb44008da52122f4a80f6fee148b2ee&imgtype=0&src=http%3A%2F%2Fh.hiphotos.baidu.com%2Fimage%2Fpic%2Fitem%2F0b46f21fbe096b63491b16ea06338744ebf8ac0e.jpg";
    }

}
