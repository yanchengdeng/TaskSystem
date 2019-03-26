package com.task.system.adapters;

import android.text.TextUtils;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.task.system.R;
import com.task.system.bean.ScoreDetailInfo;

public class ScoreDetailAdapter extends BaseQuickAdapter<ScoreDetailInfo, BaseViewHolder> {
    //0  系统  1  个人
    public ScoreDetailAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, ScoreDetailInfo item) {

        if (!TextUtils.isEmpty(item.title)) {
            ((TextView)helper.getView(R.id.tv_title)).setText(item.title);
        }

        if (!TextUtils.isEmpty(item.info)) {
            ((TextView)helper.getView(R.id.tv_info)).setText(item.info);
        }


    }
}