package com.task.system.adapters;

import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.task.system.R;
import com.task.system.bean.TaskInfoItem;
import com.yc.lib.api.utils.ImageLoaderUtil;

public class HomeAdapter extends BaseQuickAdapter<TaskInfoItem, BaseViewHolder> {

    public HomeAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, TaskInfoItem item) {

        ImageLoaderUtil.loadNormal(item.thumbnail, (ImageView) helper.getView(R.id.iv_image), R.mipmap.load_err);

        if (!TextUtils.isEmpty(item.title)) {
            ((TextView) helper.getView(R.id.tv_title)).setText(item.title);
        }

        if (!TextUtils.isEmpty(item.sub_title)) {
            ((TextView) helper.getView(R.id.tv_info)).setText(item.sub_title);
        }


        if (!TextUtils.isEmpty(item.views)) {
            ((TextView) helper.getView(R.id.tv_num)).setText(item.views + "人浏览");
        }

        if (!TextUtils.isEmpty(item.market_score)) {
            ((TextView) helper.getView(R.id.tv_price)).setText(mContext.getString(R.string.money_unit) + item.market_score);
        }


    }
}
