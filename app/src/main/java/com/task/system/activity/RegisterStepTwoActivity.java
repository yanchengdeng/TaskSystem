package com.task.system.activity;

import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.google.gson.Gson;
import com.task.system.Constans;
import com.task.system.R;
import com.task.system.api.API;
import com.task.system.api.TaskInfo;
import com.task.system.api.TaskService;
import com.task.system.bean.RegisterParams;
import com.task.system.bean.UserInfo;
import com.task.system.utils.TUtils;
import com.yc.lib.api.ApiCallBack;
import com.yc.lib.api.ApiConfig;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;

/**
 * Author: 邓言诚  Create at : 2019/3/17  00:37
 * Email: yanchengdeng@gmail.com
 * Describle: 注册第二步
 */
public class RegisterStepTwoActivity extends BaseSimpleActivity {

    @BindView(R.id.et_accont)
    EditText etAccont;
    @BindView(R.id.et_password)
    EditText etPassword;
    @BindView(R.id.et_password_confirm)
    EditText etPasswordConfirm;
    @BindView(R.id.btn_register)
    Button btnRegister;
    @BindView(R.id.card_view)
    CardView cardView;

    private RegisterParams registerParams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_step_two);
        ButterKnife.bind(this);
        checkParasmFill();

        registerParams = (RegisterParams) getIntent().getSerializableExtra(Constans.PASS_OBJECT);


        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (TextUtils.isEmpty(etAccont.getEditableText().toString())) {
//                    ToastUtils.showShort("请输入用户名");
//                } else
                    if (TextUtils.isEmpty(etPassword.getEditableText().toString())) {
                    ToastUtils.showShort("请输入密码");
                } else if (TextUtils.isEmpty(etPasswordConfirm.getEditableText().toString())) {
                    ToastUtils.showShort("请输入确认密码");
                } else if (etPassword.getEditableText().toString().equals(etPasswordConfirm.getEditableText().toString())) {
                    doRegister();
                } else {
                    ToastUtils.showShort("密码不一致");
                }
            }
        });


    }

    //注册接口
    private void doRegister() {
/**
 * username
 * mobile
 * mobile_code
 * invite_code
 * password
 * re_password
 */

        showLoadingBar();

        HashMap<String, String> hashMap = new HashMap();
//        hashMap.put("username", etAccont.getEditableText().toString());
        hashMap.put("mobile_code", registerParams.mobile_code);
        hashMap.put("mobile", registerParams.mobile);
        hashMap.put("invite_code", registerParams.invite_code);
        hashMap.put("password", etPassword.getEditableText().toString());
        hashMap.put("re_password", etPasswordConfirm.getEditableText().toString());
        Call<TaskInfo> call = ApiConfig.getInstants().create(TaskService.class).doRegister(TUtils.getParams(hashMap));

        API.getObject(call, UserInfo.class, new ApiCallBack<UserInfo>() {
            @Override
            public void onSuccess(int msgCode, String msg, UserInfo data) {
                dismissLoadingBar();
                ToastUtils.showShort(msg);
//                JPushInterface.onResume(RegisterStepTwoActivity.this);
                SPUtils.getInstance().put(Constans.USER_INFO, new Gson().toJson(data));
                SPUtils.getInstance().put(Constans.TOKEN, new Gson().toJson(data.tokens));
                SPUtils.getInstance().put(Constans.USER_ACOUNT, etAccont.getEditableText().toString());
                SPUtils.getInstance().put(Constans.PASSWORD, etPassword.getEditableText().toString());
                ActivityUtils.startActivity(MainActivity.class);
                setResult(RESULT_OK);
                finish();
            }

            @Override
            public void onFaild(int msgCode, String msg) {
                dismissLoadingBar();
                ToastUtils.showShort(msg);
            }
        });
    }


    private void checkParasmFill() {
        etAccont.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                checkCanRegister();
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

                checkCanRegister();
            }
        });


        etPasswordConfirm.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                checkCanRegister();
            }
        });


    }


    private void checkCanRegister() {
        if (
//                !TextUtils.isEmpty(
//                etAccont.getEditableText().toString())
                !TextUtils.isEmpty(etPassword.getEditableText().toString())
                && !TextUtils.isEmpty(etPasswordConfirm.getEditableText().toString())
        ) {
            btnRegister.setBackground(getResources().getDrawable(R.drawable.normal_submit_btn_red_large));
            btnRegister.setTextColor(getResources().getColor(R.color.white));
        } else {
            btnRegister.setBackground(getResources().getDrawable(R.drawable.normal_submit_btn_gray_large));
            btnRegister.setTextColor(getResources().getColor(R.color.white));
        }
    }
}
