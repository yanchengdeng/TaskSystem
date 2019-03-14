package com.task.system.views;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.task.system.R;


/**
*
* Author: 邓言诚  Create at : 2018/7/3  16:48
* Email: yanchengdeng@gmail.com
* Describle: 加载对话框
*/
public class LoadDialog extends Dialog {
    private Context mContext;

    private ImageView imageView;

    private TextView textView;

    private String loadingText;

    private boolean cancelable;

    private AnimationDrawable animation;

    public LoadDialog(@NonNull Context context) {
        this(context, null);
    }

    public LoadDialog(@NonNull Context context, String loadingText) {
        this(context, loadingText, false);
    }

    public LoadDialog(@NonNull Context context, String loadingText, boolean cancelable) {
        super(context, R.style.LoadingDialog);
        this.cancelable = cancelable;
        this.mContext = context;
        this.loadingText = loadingText;
        init();
    }

    private void init() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_loading, null);
        imageView = view.findViewById(R.id.iv_loading);
        textView = view.findViewById(R.id.tv_loading);
        imageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.loading_ui_loading));

//        RotateAnimation rotateAnimation = new RotateAnimation(0f, 360f,
//                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
//        rotateAnimation.setDuration(1200);
//        rotateAnimation.setRepeatCount(-1);
//        rotateAnimation.setFillAfter(false);
//        imageView.setAnimation(rotateAnimation);

        animation = (AnimationDrawable) imageView.getDrawable();
        if (!animation.isRunning()) {
            animation.start();
        }

        if (loadingText != null && !"".equals(loadingText)) {
            textView.setVisibility(View.VISIBLE);
            textView.setText(loadingText);
        } else {
            textView.setVisibility(View.GONE);
        }

        setCancelable(cancelable);
        setContentView(view);
    }

    public void stopAnimal() {
        if (animation!=null && animation.isRunning()){
            animation.stop();
        }
    }
}