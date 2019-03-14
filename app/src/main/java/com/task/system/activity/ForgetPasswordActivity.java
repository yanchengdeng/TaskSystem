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

public class ForgetPasswordActivity extends BaseSimpleActivity {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.et_phone)
    EditText etPhone;
    @BindView(R.id.et_code)
    EditText etCode;
    @BindView(R.id.tv_get_code)
    TextView tvGetCode;
    @BindView(R.id.et_password)
    EditText etPassword;
    @BindView(R.id.et_password_repeat)
    EditText etPasswordRepeat;
    @BindView(R.id.btn_confirm)
    Button btnConfirm;
    @BindView(R.id.card_view)
    CardView cardView;

    private CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
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


        etPasswordRepeat.addTextChangedListener(new TextWatcher() {
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
                && !TextUtils.isEmpty(etPasswordRepeat.getEditableText().toString())
                && !TextUtils.isEmpty(etPassword.getEditableText().toString())
        && etPasswordRepeat.getEditableText().toString().equals(etPassword.getEditableText().toString())) {
            btnConfirm.setBackground(getResources().getDrawable(R.drawable.normal_submit_btn_red_large));
            btnConfirm.setTextColor(getResources().getColor(R.color.white));
        } else {
            btnConfirm.setBackground(getResources().getDrawable(R.drawable.normal_submit_btn_red_large));
            btnConfirm.setTextColor(getResources().getColor(R.color.white));
        }
    }

    @OnClick({R.id.iv_back, R.id.tv_get_code, R.id.btn_confirm})
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
            case R.id.btn_confirm:
                doSubmit();
                break;
        }
    }

    private void getCode() {
        HashMap<String, String> hashMap = new HashMap();
        hashMap.put("code_type", MobileCode.MOBILE_CODE_FORGET.getType());
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


    private void doSubmit(){

        if (TextUtils.isEmpty(etPhone.getEditableText().toString())) {
            etPhone.setError(getString(R.string.phone_tips));
        } else if (RegexUtils.isMobileSimple(etPhone.getEditableText().toString())) {
            if (TextUtils.isEmpty(etCode.getEditableText().toString())) {
                etCode.setError(getString(R.string.code_tips));
            } else {
                if (!TextUtils.isEmpty(etPassword.getEditableText().toString())){
                    if (!TextUtils.isEmpty(etPasswordRepeat.getEditableText().toString())){
                        if (etPasswordRepeat.getEditableText().toString().equals(etPassword.getEditableText().toString())){
                            doFogetPassword(etPhone.getEditableText().toString(), etCode.getEditableText().toString(), etPassword.getEditableText().toString());
                        }else{
                            etPasswordRepeat.setError("两次密码不一致");
                            etPassword.setError("两次密码不一致");
                        }

                    }else{
                        etPasswordRepeat.setError(getString(R.string.get_password_tips));
                    }
                }else{
                    etPassword.setError(getString(R.string.get_password_tips));
                }
            }
        } else {
            etPhone.setError("手机号码错误");
        }

    }

    private void doFogetPassword(String phone, String code, String password) {

        showLoadingBar();
        HashMap<String, String> hashMap = new HashMap();
        hashMap.put("mobile_code", code);
        hashMap.put("mobile", phone);
        hashMap.put("password", password);
        hashMap.put("re_password", password);
        Call<TaskInfoList> call = ApiConfig.getInstants().create(TaskService.class).forgetPassword(TUtils.getParams(hashMap));

        API.getList(call, UserInfo.class, new ApiCallBackList<UserInfo>() {
            @Override
            public void onSuccess(int msgCode, String msg, List<UserInfo> data) {
                ToastUtils.showShort(msg);
                dismissLoadingBar();
                finish();
            }

            @Override
            public void onFaild(int msgCode, String msg) {
                ToastUtils.showShort(msg);
                dismissLoadingBar();
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
