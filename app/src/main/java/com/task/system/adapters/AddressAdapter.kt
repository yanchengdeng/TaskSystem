package com.task.system.adapters

import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.task.system.R
import com.task.system.bean.AddressInfo


/**
 * Copyright (C), 2016-2019, 福建大道之行有限公司
 * FileName: AddressAdapter
 * Author: dengyc
 * Date: 2019-08-14 23:49
 * Description:
 * History:
 */
class AddressAdapter(layoutResId: Int, data: List<AddressInfo>?) :
    BaseQuickAdapter<AddressInfo, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: AddressInfo) {

        helper?.let {
            helper.getView<TextView>(R.id.tv_name).text = item.contact_name
            helper.getView<TextView>(R.id.tv_address).text = item.address
            helper.getView<TextView>(R.id.tv_phone).text = item.contact_mobile

        }

    }
}
