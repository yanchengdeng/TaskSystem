package com.task.system.activity;

import android.os.Bundle;

import com.task.system.R;
import com.task.system.api.API;
import com.task.system.api.TaskInfo;
import com.task.system.api.TaskService;
import com.task.system.bean.SimpleBeanInfo;
import com.task.system.utils.TUtils;
import com.yc.lib.api.ApiCallBack;
import com.yc.lib.api.ApiConfig;

import retrofit2.Call;

/**
*
* Author: 邓言诚  Create at : 2019/3/25  00:26
* Email: yanchengdeng@gmail.com
* Describle: 我的收益记录
*/
public class MyScoreListActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_score_list);
        setTitle("收益记录");


        getScoreRecord();
    }

    private void getScoreRecord() {
        Call<TaskInfo> call = ApiConfig.getInstants().create(TaskService.class).getStaticsList(TUtils.getParams());

        API.getObject(call, SimpleBeanInfo.class, new ApiCallBack<SimpleBeanInfo>() {
            @Override
            public void onSuccess(int msgCode, String msg, SimpleBeanInfo data) {

            }

            @Override
            public void onFaild(int msgCode, String msg) {

            }
        });


    }
}
