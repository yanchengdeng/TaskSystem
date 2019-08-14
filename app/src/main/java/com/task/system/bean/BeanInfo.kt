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