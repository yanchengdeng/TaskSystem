package com.task.system.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.SPUtils;
import com.google.gson.Gson;
import com.task.system.Constans;
import com.task.system.R;
import com.task.system.bean.AdInfo;
import com.task.system.bean.UserInfo;
import com.task.system.utils.PerfectClickListener;
import com.task.system.utils.TUtils;
import com.yc.lib.api.utils.ImageLoaderUtil;

public class SplashActivity extends AppCompatActivity {

    private ImageView ivAd;

    private AdInfo adInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ivAd = findViewById(R.id.iv_ad);

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

//        ImmersionBar.with(this).fullScreen(true).init();


        UserInfo userInfo = TUtils.getUserInfo();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
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
        },3000);

    }
}
