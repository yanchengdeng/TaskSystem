package com.task.system.adapters;

import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.task.system.R;
import com.task.system.bean.HomeMenu;

public class TagAdapter extends BaseQuickAdapter<HomeMenu, BaseViewHolder> {

    public TagAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, HomeMenu item) {


        if (!TextUtils.isEmpty(item.title)) {
            ((TextView) helper.getView(R.id.tv_tag)).setText(item.title);
        }

        if (item.isSelected) {
            ((TextView) helper.getView(R.id.tv_tag)).setTextColor(mContext.getResources().getColor(R.color.white));
            ((CardView)helper.getView(R.id.cv_tag)).setCardBackgroundColor(mContext.getResources().getColor(R.color.red));
        } else {
            ((CardView)helper.getView(R.id.cv_tag)).setCardBackgroundColor(mContext.getResources().getColor(R.color.white));
            ((TextView) helper.getView(R.id.tv_tag)).setTextColor(mContext.getResources().getColor(R.color.color_tittle));
        }

    }
}
