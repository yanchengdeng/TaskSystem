package com.task.system.activity;

import android.os.Bundle;

import com.task.system.Constans;
import com.task.system.R;
import com.task.system.api.API;
import com.task.system.api.TaskInfo;
import com.task.system.api.TaskService;
import com.task.system.bean.TaskInfoItem;
import com.task.system.bean.TaskInfoList;
import com.task.system.utils.TUtils;
import com.yc.lib.api.ApiCallBack;
import com.yc.lib.api.ApiConfig;

import java.util.HashMap;

import retrofit2.Call;

public class TaskDetailActivity extends BaseSimpleActivity {

    private TaskInfoItem taskInfoItem;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);

        taskInfoItem = (TaskInfoItem) getIntent().getSerializableExtra(Constans.PASS_OBJECT);

        getTaskDetail();
    }

    private void getTaskDetail() {
        HashMap<String, String> maps = new HashMap<>();
        maps.put("task_id", taskInfoItem.id);
        Call<TaskInfo> call = ApiConfig.getInstants().create(TaskService.class).getTaskDetail(TUtils.getParams(maps));

        API.getObject(call, TaskInfoList.class, new ApiCallBack<TaskInfoList>() {
            @Override
            public void onSuccess(int msgCode, String msg, TaskInfoList data) {
            }

            @Override
            public void onFaild(int msgCode, String msg) {
            }
        });
    }
}
