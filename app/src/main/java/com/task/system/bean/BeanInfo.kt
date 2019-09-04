package com.task.system.bean

import java.io.Serializable


/**
 * Copyright (C), 2016-2019, 福建大道之行有限公司
 * FileName: BeanInfo
 * Author: dengyc
 * Date: 2019-08-14 23:50
 * Description:
 * History:
 */

//收货地址
data class AddressInfo(
    val address: String,
    val contact_mobile: String,
    val contact_name: String,
    val create_time: String,
    val id: String,
    val status: String,
    val uid: String
) : Serializable


/**
 * 区域管理 发布
 */
data class AreaManagePublish(
    val actual_score: String,
    val create_time: String,
    val edit_step_url: String,
    val edit_url: String,
    val id: String,
    val status: String,
    val status_title: String,
    val sub_title: String,
    val title: String
):Serializable

/**
 * 区域管理订单
 */


data class AreaManageOrder(
    val cash_time: String,
    val dispute_status: String,
    val dispute_status_title: String,
    val end_time: String,
    val order_id: String,
    val order_score: String,
    val remark: String,
    val status: String,
    val status_title: String,
    val sub_title: String,
    val task_end_time: String,
    val task_id: String,
    val task_score: String,
    val title: String,
    val uid: String,
    val edit_url: String,//TODO   运营商订单管理  缺少  编辑url字段 :edit_url
    val update_time: String
):Serializable