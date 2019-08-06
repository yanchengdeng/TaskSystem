package com.task.system.bean;

import java.io.Serializable;
import java.util.List;

public class CatergoryInfo implements Serializable {
    public String id;// 31,
    public String pid;// 29,
    public String title;// 实名认证,
    public String thumbnail;// ,
    public String cover_url;// /./Application/Home/View/Public/img/default/default.gif,
    public String href;//
    public boolean isSelected;
    public List<CatergoryInfo> _child;
}
