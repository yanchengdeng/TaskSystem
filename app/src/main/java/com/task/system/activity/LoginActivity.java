package com.task.system.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.google.gson.Gson;
import com.gyf.barlibrary.ImmersionBar;
import com.task.system.Constans;
import com.task.system.R;
import com.task.system.api.API;
import com.task.system.api.TaskInfo;
import com.task.system.api.TaskService;
import com.task.system.bean.UserInfo;
import com.task.system.utils.TUtils;
import com.yc.lib.api.ApiCallBack;
import com.yc.lib.api.ApiConfig;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;

public class LoginActivity extends BaseSimpleActivity {


    @BindView(R.id.et_accont)
    EditText etAccont;
    @BindView(R.id.et_password)
    EditText etPassword;
    @BindView(R.id.tv_contact)
    TextView tvContact;
    @BindView(R.id.tv_forget_password)
    TextView tvForgetPassword;
    @BindView(R.id.btn_login)
    Button btnLogin;
    @BindView(R.id.tv_go_register)
    TextView tvGoRegister;
    @BindView(R.id.card_view)
    CardView cardView;
    private boolean canLoginStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        ImmersionBar.with(this).init();
        checkAccoutPsw();
        mSwipeBackHelper.setSwipeBackEnable(false);

        etAccont.setText("u10010001");
        etPassword.setText("11111111");


        if (TUtils.isLogin()) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }
    }


    @OnClick({R.id.tv_contact, R.id.tv_forget_password, R.id.btn_login, R.id.tv_go_register})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_contact:
                ToastUtils.showShort("联系人");
                break;
            case R.id.tv_forget_password:
                ActivityUtils.startActivity(ForgetPasswordActivity.class);
                break;
            case R.id.btn_login:
                if (canLoginStatus) {
                    doLogin(etAccont.getEditableText().toString(), etPassword.getEditableText().toString());
                } else {
                    if (TextUtils.isEmpty(etAccont.getEditableText())) {
                        etAccont.setError("请输入手机号/用户名");
                    }

                    if (TextUtils.isEmpty(etPassword.getEditableText().toString())) {
                        etPassword.setError("请输入密码");
                    }
                }

                break;
            case R.id.tv_go_register:
                ActivityUtils.startActivity( RegisterActivity.class);
                break;
        }
    }

    private void doLogin(String account, String password) {
        showLoadingBar();

        HashMap<String, String> hashMap = new HashMap();
        hashMap.put("username", account);
        hashMap.put("password", password);
        Call<TaskInfo> call = ApiConfig.getInstants().create(TaskService.class).doLogin(TUtils.getParams(hashMap));

        API.getObject(call, UserInfo.class, new ApiCallBack<UserInfo>() {
            @Override
            public void onSuccess(int msgCode, String msg, UserInfo data) {
                dismissLoadingBar();
                ToastUtils.showShort(msg);
                ActivityUtils.startActivity(MainActivity.class);
                SPUtils.getInstance().put(Constans.USER_INFO, new Gson().toJson(data));
                SPUtils.getInstance().put(Constans.TOKEN,new Gson().toJson(data.tokens));
                SPUtils.getInstance().put(Constans.USER_ACOUNT, etAccont.getEditableText().toString());
                SPUtils.getInstance().put(Constans.PASSWORD, etPassword.getEditableText().toString());
                finish();
            }

            @Override
            public void onFaild(int msgCode, String msg) {
                dismissLoadingBar();
                ToastUtils.showShort(msg);
            }
        });

    }

    private void checkAccoutPsw() {

        etAccont.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                checkLoginStatus();


            }
        });


        etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                checkLoginStatus();
            }
        });

    }

    private void checkLoginStatus() {

        if (!TextUtils.isEmpty(etAccont.getEditableText().toString()) &&
                !TextUtils.isEmpty(etPassword.getEditableText().toString())) {
            btnLogin.setBackground(getResources().getDrawable(R.drawable.normal_submit_btn_red_large));
            btnLogin.setTextColor(getResources().getColor(R.color.white));
            canLoginStatus = true;
        } else {
            btnLogin.setBackground(getResources().getDrawable(R.drawable.normal_submit_btn_gray_large));
            btnLogin.setTextColor(getResources().getColor(R.color.color_tittle));
            canLoginStatus = false;
        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        ImmersionBar.with(this).destroy();
    }
}