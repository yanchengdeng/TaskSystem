package com.task.system.views;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.task.system.R;
import com.yc.lib.api.ApiConfig;

import razerdp.basepopup.BasePopupWindow;

//单个列表

public class BubblePopupSingle extends BasePopupWindow {
    public RecyclerView recyclerView;
    private Context context;

    public BubblePopupSingle(Context context) {
        super(context);
        this.context  = context;
        recyclerView = findViewById(R.id.recycle);
    }

    @Override
    public View onCreateContentView() {
        return createPopupById(R.layout.recycle);
    }

    public RecyclerView getContentView() {
        return recyclerView;
    }

    @Override
    protected Animation onCreateShowAnimation() {

        return AnimationUtils.loadAnimation(ApiConfig.context, R.anim.slid_in_top);
//        return getShowAnimation();
    }

    @Override
    protected Animation onCreateDismissAnimation() {
        return AnimationUtils.loadAnimation(ApiConfig.context, R.anim.slid_out_top);
//        return getDismissAnimation();
    }



}