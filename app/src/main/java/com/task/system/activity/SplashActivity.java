package com.task.system.activity;

import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;

import com.blankj.utilcode.util.ActivityUtils;
import com.task.system.R;
import com.task.system.bean.UserInfo;
import com.task.system.utils.TUtils;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

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
