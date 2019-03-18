package com.task.system.utils;

import android.app.Activity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.task.system.R;
import com.yc.lib.api.ApiConfig;


/**
*
* Author: 邓言诚  Create at : 2018/7/6  11:49
* Email: yanchengdeng@gmail.com
* Describle: RecycleView 工具
*/
public class RecycleViewUtils {


    //1个dp 横线
    public static DividerItemDecoration getItemDecorationHorizontal() {
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(ApiConfig.context, DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(ApiConfig.context.getResources().getDrawable(R.drawable.list_line_diver));
        return dividerItemDecoration;
    }





    //RecycleView  无数据提示
    public static View getEmptyView(Activity mContext, RecyclerView recycle) {
        if (mContext==null){
            return  recycle.getChildAt(-1);
        }
        return mContext.getLayoutInflater().inflate(R.layout.layout_empty_view, (ViewGroup) recycle.getParent(), false);
    }

    public static View getEmptyView(Activity mContext, RecyclerView recycle, String tips) {
        if (mContext==null){
            return  recycle.getChildAt(-1);
        }
        View view = mContext.getLayoutInflater().inflate(R.layout.layout_empty_view, (ViewGroup) recycle.getParent(), false);
        TextView tvTips = view.findViewById(R.id.tv_error_tips);
        tvTips.setText(tips);
        return view;
    }
}
