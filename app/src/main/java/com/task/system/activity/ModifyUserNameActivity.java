package com.task.system.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.blankj.utilcode.util.ToastUtils;
import com.task.system.R;
import com.task.system.api.API;
import com.task.system.api.TaskInfoList;
import com.task.system.api.TaskService;
import com.task.system.bean.UserInfo;
import com.task.system.event.UpdateUserInfoEvent;
import com.task.system.utils.TUtils;
import com.yc.lib.api.ApiCallBackList;
import com.yc.lib.api.ApiConfig;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;

//修改用户名
public class ModifyUserNameActivity extends BaseActivity {

    @BindView(R.id.et_username)
    EditText etUsername;
    @BindView(R.id.iv_close)
    ImageView ivClose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_user_name);
        ButterKnife.bind(this);
        setTitle("修改用户名");

        etUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s.toString())){
                    ivClose.setVisibility(View.GONE);
                }else{
                    ivClose.setVisibility(View.VISIBLE);
                }

            }
        });

        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etUsername.setHint("输入用户名");
                etUsername.setText("");
            }
        });

        tvRightFunction.setVisibility(View.VISIBLE);
        tvRightFunction.setText("确定");
        tvRightFunction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(etUsername.getEditableText().toString())) {
                    ToastUtils.showShort("输入用户名");
                }else{
                    doModifyUserName();
                }
            }
        });
    }

    private void doModifyUserName() {
        showLoadingBar("提交中...");
        HashMap<String, String> hashMap = new HashMap();
        hashMap.put("uid", TUtils.getUserId());
        hashMap.put("username", etUsername.getEditableText().toString());
        Call<TaskInfoList> call = ApiConfig.getInstants().create(TaskService.class).setUserName(TUtils.getParams(hashMap));

        API.getList(call, String.class, new ApiCallBackList<String>() {

            @Override
            public void onSuccess(int msgCode, String msg, List<String> data) {
                ToastUtils.showShort("" + msg);
                UserInfo userInfo = TUtils.getUserInfo();
                userInfo.username = etUsername.getEditableText().toString();
                TUtils.saveUserInfo(userInfo);
                dismissLoadingBar();
                EventBus.getDefault().post(new UpdateUserInfoEvent());
            }

            @Override
            public void onFaild(int msgCode, String msg) {
                ToastUtils.showShort("" + msg);
                dismissLoadingBar();
            }
        });
    }
}
