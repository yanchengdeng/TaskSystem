package com.task.system.adapters;

import android.text.Html;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.task.system.R;
import com.task.system.bean.LotteryItem;
import com.yc.lib.api.utils.ImageLoaderUtil;

//积分
public class LotteryAdapter extends BaseQuickAdapter<LotteryItem, BaseViewHolder> {

    public LotteryAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, LotteryItem item) {

        ImageLoaderUtil.loadNormal(item.thumbnail_url, (ImageView) helper.getView(R.id.iv_image), R.mipmap.load_err);

        if (!TextUtils.isEmpty(item.title)) {
            ((TextView) helper.getView(R.id.tv_title)).setText(item.title);
        }

        if (!TextUtils.isEmpty(item.content)) {
            ((TextView) helper.getView(R.id.tv_info)).setText(Html.fromHtml(item.content));
        }

        if (!TextUtils.isEmpty(item.score)) {
            ((TextView) helper.getView(R.id.tv_price)).setText(item.score + "");
        }


    }
}
