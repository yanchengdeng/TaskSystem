package com.task.system.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.task.system.Constans;
import com.task.system.R;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;

/**
 * Email: dengyc@dadaodata.com
 * FileName: MyAreaManageActivity.java
 * Author: dengyancheng
 * Date: 2019-08-11 22:18
 * Description: 我的区域管理
 * History:
 */
public class MyAreaManageActivity extends AppCompatActivity {


    private int REQUEST_CODE_SELECT = 402;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_area_manage);


        findViewById(R.id.btnPublish).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AndPermission.with(MyAreaManageActivity.this)
                        .runtime()
                        .permission(Permission.Group.CAMERA)
                        .onGranted(permissions -> {
                            Bundle bundle = new Bundle();
                            bundle.putString(Constans.PASS_NAME, "发布任务");
                            String url = "http://dev.xhdcmgood.com/index.php/task/task/add.html?sk=224fO%2BujZfR9PRGpDsRenoz%2FW4u2LeaHcRsyMXf4BjCkr%2Bb0OeueGqBYj4ctoZvf";
                            bundle.putString(Constans.PASS_STRING,url );
                            ActivityUtils.startActivity(bundle, OpenWebViewActivity.class);
                        })
                        .onDenied(permissions -> {
                            ToastUtils.showShort("请打开相机权限");
                        })
                        .start();

            }
        });
    }
}
