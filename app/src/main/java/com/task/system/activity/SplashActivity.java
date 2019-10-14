package com.task.system.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.SPUtils;
import com.google.gson.Gson;
import com.task.system.Constans;
import com.task.system.R;
import com.task.system.bean.AdInfo;
import com.task.system.bean.UserInfo;
import com.task.system.utils.AppManager;
import com.task.system.utils.PerfectClickListener;
import com.task.system.utils.TUtils;
import com.yc.lib.api.utils.ImageLoaderUtil;

public class SplashActivity extends AppCompatActivity {

    private ImageView ivAd;

    private AdInfo adInfo;

    private TextView tvJump;

    private Runnable runnable;
    private Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ivAd = findViewById(R.id.iv_ad);
        tvJump = findViewById(R.id.tv_jump);
        handler = new Handler();
        AppManager.getAppManager().addActivity(this);

        if (!TextUtils.isEmpty(SPUtils.getInstance().getString(Constans.SAVE_SPLASH_AD))) {
            try {
                adInfo = new Gson().fromJson(SPUtils.getInstance().getString(Constans.SAVE_SPLASH_AD),AdInfo.class);
                if (adInfo!= null && !TextUtils.isEmpty(adInfo.cover)) {
                    ImageLoaderUtil.loadNormal(adInfo.cover, ivAd, R.mipmap.splash);
                }
            }catch (Exception e){
            }
        }

        ivAd.setOnClickListener(new PerfectClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                if (adInfo!=null){
                    TUtils.openBanner(adInfo);
                }
            }
        });

        tvJump.setOnClickListener(new PerfectClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                handler.removeCallbacks(runnable);
                jumpAction();
            }
        });

//        ImmersionBar.with(this).fullScreen(true).init();





        handler.postDelayed(runnable = () -> {
            jumpAction();
        },3000);

    }

    private void jumpAction(){
        UserInfo userInfo = TUtils.getUserInfo();
        if (userInfo!=null && !TextUtils.isEmpty(userInfo.user_type)){
//                    if (userInfo.user_type.equals(UserType.USER_TYPE_MEMBER.getType())){
            ActivityUtils.startActivity(MainActivity.class);
            finish();
//                    }else{
//                        ActivityUtils.startActivity(PercentCenterActivity.class);
//                        finish();
//                    }
        }else{
            ActivityUtils.startActivity(LoginActivity.class);
            finish();
        }
    }
}
