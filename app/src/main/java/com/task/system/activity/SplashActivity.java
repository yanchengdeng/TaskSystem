package com.task.system.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.ImageView;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.SPUtils;
import com.task.system.Constans;
import com.task.system.R;
import com.task.system.bean.UserInfo;
import com.task.system.utils.TUtils;
import com.yc.lib.api.utils.ImageLoaderUtil;

public class SplashActivity extends AppCompatActivity {

    private ImageView ivAd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ivAd = findViewById(R.id.iv_ad);

        if (!TextUtils.isEmpty(SPUtils.getInstance().getString(Constans.SAVE_SPLASH_AD))) {
            ImageLoaderUtil.loadNormal(SPUtils.getInstance().getString(Constans.SAVE_SPLASH_AD),ivAd,R.mipmap.splash);
        }

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
        },1000);

    }
}
