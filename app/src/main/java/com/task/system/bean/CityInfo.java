package com.task.system.bean;

import java.io.Serializable;

public class CityInfo implements Serializable {


    public String region_id;//6,
    public String region_name;//北京市
    public String pinyin;
    public int open_status;//0  未开放  1 已开放

    public CityInfo(String region_name, String pinyin) {
        this.region_name = region_name;
        this.pinyin = pinyin;
    }

    public String getName() {
        return region_name;
    }

    public void setName(String name) {
        this.region_name = name;
    }

    public String getPinyin() {
        return pinyin;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }

    public String getRegion_id() {
        return region_id;
    }


}