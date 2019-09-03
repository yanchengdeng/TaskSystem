package com.task.system.adapters;

import android.text.TextUtils;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.task.system.R;
import com.task.system.bean.AwardItem;

//奖品
public class AwardItemAdapter extends BaseQuickAdapter<AwardItem, BaseViewHolder> {

    private int status;

    public AwardItemAdapter(int layoutResId) {
        super(layoutResId);
    }

    public AwardItemAdapter(int layoutResId, int status) {
        super(layoutResId);
        this.status = status;
    }

    @Override
    protected void convert(BaseViewHolder helper, AwardItem item) {



        if (!TextUtils.isEmpty(item.prize_name)) {
            ((TextView)helper.getView(R.id.tv_award_leavel)).setText(item.prize_name);
        }

        if (!TextUtils.isEmpty(item.title)) {
            ((TextView)helper.getView(R.id.tv_title)).setText(item.title);
        }

        if (!TextUtils.isEmpty(item.create_time)) {
            ((TextView)helper.getView(R.id.tv_date)).setText(item.create_time);
        }

    }
}
