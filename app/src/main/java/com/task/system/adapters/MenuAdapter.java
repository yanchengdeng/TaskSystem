package com.task.system.adapters;

import android.text.TextUtils;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.task.system.R;
import com.task.system.bean.HomeMenu;

public class MenuAdapter extends BaseQuickAdapter<HomeMenu, BaseViewHolder> {

    public MenuAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, HomeMenu item) {


        if (!TextUtils.isEmpty(item.title)) {
            ((TextView) helper.getView(R.id.tv_title)).setText(item.title);
        }

    }
}
