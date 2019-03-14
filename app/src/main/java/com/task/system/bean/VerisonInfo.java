package com.task.system.bean;

import java.io.Serializable;

public class VerisonInfo implements Serializable {

    public String version;//V1.0,
    public String base_url;//http;////88.mayimayi.cn;//8888/api.php/V1/,
    public String app_url;//http;////88.mayimayi.cn;//8888/download/bigdata-V1.0.apk,
    public String update_content;//更新内容：,
    public String update_level= "0";//0  //0不更新   1普通更新  2强制更新
}
