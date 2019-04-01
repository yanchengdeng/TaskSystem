package com.task.system.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.blankj.utilcode.util.ToastUtils;
import com.task.system.R;
import com.task.system.api.API;
import com.task.system.api.TaskInfo;
import com.task.system.api.TaskService;
import com.task.system.bean.InviteCode;
import com.task.system.bean.UserInfo;
import com.task.system.utils.TUtils;
import com.yc.lib.api.ApiCallBack;
import com.yc.lib.api.ApiConfig;

import retrofit2.Call;

//我的邀请码
public class MyInviteCodeActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_invite_code);
        setTitle("我的邀请码");

        UserInfo userInfo = TUtils.getUserInfo();
        findViewById(R.id.btn_copy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (TextUtils.isEmpty(userInfo.invite_code)){
                    return;
                }
                ClipboardManager clipboardManager = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
                clipboardManager.setPrimaryClip(ClipData.newPlainText(null, ((TextView)findViewById(R.id.tv_invite_code)).getText()));
                ToastUtils.showShort("复制成功");
            }
        });
        getLeaderInviteCode();
    }

    //获取邀请码  只有type=2  代理才有
    private void getLeaderInviteCode() {
        showLoadingBar();
        Call<TaskInfo> call = ApiConfig.getInstants().create(TaskService.class).getInviteByAgent(TUtils.getParams());

        API.getObject(call, InviteCode.class, new ApiCallBack<InviteCode>() {
            @Override
            public void onSuccess(int msgCode, String msg, InviteCode data) {
                dismissLoadingBar();
                ((TextView)findViewById(R.id.tv_invite_code)).setText(""+data.invite_code);
            }

            @Override
            public void onFaild(int msgCode, String msg) {
                dismissLoadingBar();
                ToastUtils.showShort(msg);
            }
        });
    }
}
