package com.task.system.adapters;

import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.task.system.R;
import com.task.system.bean.TaskIndicator;

import java.util.ArrayList;


public class IndicatorAdapter extends BaseQuickAdapter<TaskIndicator, BaseViewHolder> {


    private LinearLayout.LayoutParams paramsline;

    private ArrayList datas;

    public IndicatorAdapter(int layoutResId, ArrayList arrayList) {
        super(layoutResId, arrayList);
        this.datas = arrayList;

        paramsline = new LinearLayout.LayoutParams((ScreenUtils.getScreenWidth() / (arrayList.size() + 1)), ConvertUtils.dp2px(1));
        paramsline.gravity = Gravity.CENTER_VERTICAL;
    }

    @Override
    protected void convert(BaseViewHolder helper, TaskIndicator item) {

        ((TextView) helper.getView(R.id.tv_num)).setText(String.valueOf(item.num));

        helper.getView(R.id.line).setLayoutParams(paramsline);
        if (item.isShowLineLeft) {
            helper.getView(R.id.line).setVisibility(View.VISIBLE);
        } else {
            helper.getView(R.id.line).setVisibility(View.GONE);
        }

        if (item.isSelect) {
            helper.getView(R.id.tv_num).setBackground(mContext.getResources().getDrawable(R.drawable.view_unread_red_bg));
        } else {
            helper.getView(R.id.tv_num).setBackground(mContext.getResources().getDrawable(R.drawable.view_unread_gray_bg));
        }


    }
}
