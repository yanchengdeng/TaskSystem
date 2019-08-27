package com.task.system.bean;

import com.lzy.imagepicker.bean.ImageItem;

import java.util.List;

/**
 * Copyright (C), 2016-2019, 福建大道之行有限公司
 * FileName: UploadTaskImageItem
 * Author: dengyc
 * Date: 2019-08-27 23:16 上传图片信息
 * Description:
 * History:
 */

public class UploadTaskImageItem {

    public List<String> photoPaths;//网络图片地址  路径
    public List<ImageItem>  imageItems;//本地地址路径
    public String content;//输入内容
}
