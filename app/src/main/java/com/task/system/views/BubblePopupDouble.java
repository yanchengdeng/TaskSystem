package com.task.system.views;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.task.system.R;
import com.yc.lib.api.ApiConfig;

import razerdp.basepopup.BasePopupWindow;

//联动列表

public class BubblePopupDouble extends BasePopupWindow {
    public View doubleView;
    private Context context;

    public BubblePopupDouble(Context context) {
        super(context);
        this.context  = context;
        doubleView= findViewById(R.id.ll_double);
        setAlignBackground(true);
    }

    @Override
    public View onCreateContentView() {
        return createPopupById(R.layout.layout_double_recycle);
    }

    public View getContentView() {
        return doubleView;
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