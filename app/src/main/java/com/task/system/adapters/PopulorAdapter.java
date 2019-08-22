package com.task.system.adapters;

import android.support.annotation.Nullable;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.task.system.R;
import com.task.system.bean.SimpleBeanInfo;
import com.yc.lib.api.utils.ImageLoaderUtil;

import java.util.List;

/**
 * Copyright (C), 2016-2019, 福建大道之行有限公司
 * FileName: PopulorAdapter
 * Author: dengyc
 * Date: 2019-08-17 11:17
 * Description:
 * History:
 */
public class PopulorAdapter extends BaseQuickAdapter<SimpleBeanInfo, BaseViewHolder> {

    public PopulorAdapter(int layoutResId, @Nullable List<SimpleBeanInfo> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, SimpleBeanInfo item) {

        ImageLoaderUtil.loadCircle(item.thumbnail,helper.getView(R.id.iv_header),R.mipmap.defalut_header);

        ((TextView)helper.getView(R.id.tv_tittle)).setText(item.title);

        ((TextView)helper.getView(R.id.tv_money)).setText("+"+item.market_score);

        ((TextView)helper.getView(R.id.tv_content)).setText(item.success_total+"人已找到赏金,剩"+item.storages+"个名额");


    }
}
