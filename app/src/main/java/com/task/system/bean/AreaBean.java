package com.task.system.bean;

import java.util.List;

/**
 * Copyright (C), 2016-2019, 福建大道之行有限公司
 * FileName: AreaBean
 * Author: dengyc
 * Date: 2019-08-10 22:09
 * Description:
 * History:
 */
public class AreaBean {

    private String id;// "110000",
    private String pid;// "0",
    private String region_name;// "北京",
    private String pinyin;// "Beijing";,
    private List<AreaBean> _child;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getRegion_name() {
        return region_name;
    }

    public void setRegion_name(String region_name) {
        this.region_name = region_name;
    }

    public String getPinyin() {
        return pinyin;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }

    public List<AreaBean> get_child() {
        return _child;
    }

    public void set_child(List<AreaBean> _child) {
        this._child = _child;
    }
}
