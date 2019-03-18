package com.task.system.bean;

import java.io.Serializable;
import java.util.List;

public class CatergoryInfo implements Serializable {
    public String id;// "4",
    public int pid;// "0",
    public String title;// "案例展示",
    public String cover_url;// "",
    public String href;// "
    public boolean isSelected;
    public List<CatergoryInfo> _child;
}
