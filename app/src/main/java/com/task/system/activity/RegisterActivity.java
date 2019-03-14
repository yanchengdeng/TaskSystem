package com.task.system.activity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.RegexUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.gyf.barlibrary.ImmersionBar;
import com.task.system.R;
import com.task.system.api.API;
import com.task.system.api.TaskInfoList;
import com.task.system.api.TaskService;
import com.task.system.enums.MobileCode;
import com.task.system.utils.TUtils;
import com.yc.lib.api.ApiCallBackList;
import com.yc.lib.api.ApiConfig;
import com.yc.lib.api.UserInfo;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;

/**
 * Author: 邓言诚  Create at : 2019/3/4  22:03
 * Email: yanchengdeng@gmail.com
 * Describle: register UI
 */
public class RegisterActivity extends BaseSimpleActivity {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.et_phone)
    EditText etPhone;
    @BindView(R.id.et_code)
    EditText etCode;
    @BindView(R.id.tv_get_code)
    TextView tvGetCode;
    @BindView(R.id.et_invide_code)
    EditText etInvideCode;
    @BindView(R.id.tv_get_inviter_code)
    TextView tvGetInviterCode;
    @BindView(R.id.btn_register)
    Button btnRegister;
    @BindView(R.id.card_view)
    CardView cardView;
    @BindView(R.id.et_password)
    EditText etPassword;
    private CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        ImmersionBar.with(this).init();
        checkPhoneCode();

        countDownTimer = new CountDownTimer(60 * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                tvGetCode.setText("还剩" + millisUntilFinished / 1000 + "s");
                tvGetCode.setEnabled(false);
                tvGetCode.setTextColor(getResources().getColor(R.color.color_info));

            }

            @Override
            public void onFinish() {
                tvGetCode.setText(getString(R.string.get_sms_code));
                tvGetCode.setEnabled(true);
                tvGetCode.setTextColor(getResources().getColor(R.color.red));

            }
        };
    }

    private void checkPhoneCode() {
        etPhone.addTextChangedListener(new TextWatcher() {
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

        etCode.addTextChangedListener(new TextWatcher() {
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


        etInvideCode.addTextChangedListener(new TextWatcher() {
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
    }


    private void checkCanRegister() {
        if (!TextUtils.isEmpty(
                etPhone.getEditableText().toString())
                && RegexUtils.isMobileSimple(etPhone.getEditableText().toString())
                && !TextUtils.isEmpty(etCode.getEditableText().toString())
                && !TextUtils.isEmpty(etInvideCode.getEditableText().toString())
                && !TextUtils.isEmpty(etPassword.getEditableText().toString())) {
            btnRegister.setBackground(getResources().getDrawable(R.drawable.normal_submit_btn_red_large));
            btnRegister.setTextColor(getResources().getColor(R.color.white));
        } else {
            btnRegister.setBackground(getResources().getDrawable(R.drawable.normal_submit_btn_red_large));
            btnRegister.setTextColor(getResources().getColor(R.color.white));
        }
    }

    @OnClick({R.id.iv_back, R.id.tv_get_code, R.id.tv_get_inviter_code, R.id.btn_register})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_get_code:
                if (TextUtils.isEmpty(etPhone.getEditableText().toString())) {
                    etPhone.setError(getString(R.string.phone_tips));
                } else if (RegexUtils.isMobileSimple(etPhone.getEditableText().toString())) {
                    getCode();
                } else {
                    etPhone.setError("手机号码错误");
                }

                break;
            case R.id.tv_get_inviter_code:



                break;
            case R.id.btn_register:
                checkParams(etPhone.getEditableText().toString(), etCode.getEditableText().toString(),etPassword.getEditableText().toString(), etInvideCode.getEditableText().toString());
                break;
        }
    }

    private void checkParams(String phone, String smsCode,String password, String inviteCode) {
        if (TextUtils.isEmpty(phone)) {
            etPhone.setError(getString(R.string.phone_tips));
        } else if (RegexUtils.isMobileSimple(phone)) {
            if (TextUtils.isEmpty(smsCode)) {
                etCode.setError(getString(R.string.code_tips));
            } else {
                if (!TextUtils.isEmpty(password)){
                    if (!TextUtils.isEmpty(etInvideCode.getEditableText().toString())){
                        doRegiste(phone, smsCode, password,inviteCode);
                    }else{
                       etInvideCode.setError(getString(R.string.invide_code_tips));
                    }
                }else{
                    etPassword.setError(getString(R.string.get_password_tips));
                }
            }
        } else {
            etPhone.setError("手机号码错误");
        }
    }


    //注册接口
    private void doRegiste(String phone, String smsCode, String password,String inviteCode) {


        HashMap<String, String> hashMap = new HashMap();
        hashMap.put("mobile_code", smsCode);
        hashMap.put("mobile", phone);
        hashMap.put("invite_code", inviteCode);
        hashMap.put("password", password);
        Call<TaskInfoList> call = ApiConfig.getInstants().create(TaskService.class).doRegister(TUtils.getParams(hashMap));

        API.getList(call, UserInfo.class, new ApiCallBackList<UserInfo>() {
            @Override
            public void onSuccess(int msgCode, String msg, List<UserInfo> data) {
                ToastUtils.showShort(msg);
                finish();
            }

            @Override
            public void onFaild(int msgCode, String msg) {
                ToastUtils.showShort(msg);
            }
        });
    }

    private void getCode() {


        HashMap<String, String> hashMap = new HashMap();
        hashMap.put("code_type",  MobileCode.MOBILE_CODE_REGISTER.getType());
        hashMap.put("mobile", etPhone.getEditableText().toString());
        Call<TaskInfoList> call = ApiConfig.getInstants().create(TaskService.class).getCode(TUtils.getParams(hashMap));

        API.getList(call, UserInfo.class, new ApiCallBackList<UserInfo>() {
            @Override
            public void onSuccess(int msgCode, String msg, List<UserInfo> data) {
                ToastUtils.showShort(msg);
                countDownTimer.start();
            }

            @Override
            public void onFaild(int msgCode, String msg) {
                ToastUtils.showShort(msg);
            }
        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }

        ImmersionBar.with(this).destroy();
    }
}
