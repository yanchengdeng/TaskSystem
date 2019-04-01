package com.task.system.adapters;

import android.text.TextUtils;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.task.system.R;
import com.task.system.bean.OrderItem;
import com.task.system.utils.TUtils;


public class ScoreRecordAdapter extends BaseQuickAdapter<OrderItem, BaseViewHolder> {


    public ScoreRecordAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, OrderItem item) {


        if (!TextUtils.isEmpty(item.create_time)) {
            ((TextView) helper.getView(R.id.tv_date)).setText(TUtils.getHideAccount(item.create_time));
        }

        if (!TextUtils.isEmpty(item.remark)) {
            ((TextView) helper.getView(R.id.tv_title)).setText(TUtils.getHideAccount(item.remark));
        }


        if (!TextUtils.isEmpty(item.score)) {
            ((TextView) helper.getView(R.id.tv_score)).setText(TUtils.getHideAccount(item.score));
        }

        if (TextUtils.isEmpty(item.color)) {
            if (item.color.equals("red")) {
                ((TextView) helper.getView(R.id.tv_score)).setTextColor(mContext.getResources().getColor(R.color.red));
            } else {
                ((TextView) helper.getView(R.id.tv_score)).setTextColor(mContext.getResources().getColor(R.color.green));
            }
        } else {
            ((TextView) helper.getView(R.id.tv_score)).setTextColor(mContext.getResources().getColor(R.color.green ));
        }
    }
}
