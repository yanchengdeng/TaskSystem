package com.task.system.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.RegexUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.gyf.barlibrary.ImmersionBar;
import com.task.system.Constans;
import com.task.system.R;
import com.task.system.api.API;
import com.task.system.api.TaskInfoList;
import com.task.system.api.TaskService;
import com.task.system.bean.RegisterParams;
import com.task.system.bean.UserInfo;
import com.task.system.enums.MobileCode;
import com.task.system.utils.TUtils;
import com.yc.lib.api.ApiCallBackList;
import com.yc.lib.api.ApiConfig;

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
    private CountDownTimer countDownTimer;
    private boolean isCodeOk, isInviteCodeOk;

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

                tvGetCode.setText(millisUntilFinished / 1000 + "s" + "重新获取");
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


    }


    private void checkCanRegister() {
        if (!TextUtils.isEmpty(
                etPhone.getEditableText().toString())
                && RegexUtils.isMobileSimple(etPhone.getEditableText().toString())
                && !TextUtils.isEmpty(etCode.getEditableText().toString())
                && !TextUtils.isEmpty(etInvideCode.getEditableText().toString())
        ) {
            btnRegister.setBackground(getResources().getDrawable(R.drawable.normal_submit_btn_red_large));
            btnRegister.setTextColor(getResources().getColor(R.color.white));
        } else {
            btnRegister.setBackground(getResources().getDrawable(R.drawable.normal_submit_btn_gray_large));
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
                    ToastUtils.showShort(getString(R.string.phone_tips));
                } else if (RegexUtils.isMobileSimple(etPhone.getEditableText().toString())) {
                    getCode();
                } else {
                    ToastUtils.showShort("手机号码错误");
                }

                break;
            case R.id.tv_get_inviter_code:
                Bundle bundle = new Bundle();
                bundle.putBoolean(Constans.PASS_STRING, true);//ture表示来自注册  要调用获取邀请码接口 否则 去代理邀请码接口
                ActivityUtils.startActivity(bundle, MyInviteCodeActivity.class);
                break;
            case R.id.btn_register:
                if (TextUtils.isEmpty(etInvideCode.getEditableText().toString())) {
                    ToastUtils.showShort("请输入邀请码");
                    return;
                }
                doCheckCode();
                break;
        }
    }


    private void doCheckCode() {

        if (TextUtils.isEmpty(etPhone.getEditableText().toString())) {
            ToastUtils.showShort(getString(R.string.phone_tips));
            return;
        } else if (RegexUtils.isMobileSimple(etPhone.getEditableText().toString())) {
            if (TextUtils.isEmpty(etCode.getEditableText().toString())) {
                ToastUtils.showShort(getString(R.string.code_tips));
                return;
            } else {
                if (!TextUtils.isEmpty(etInvideCode.getEditableText().toString())) {


                } else {
                    ToastUtils.showShort(getString(R.string.invide_code_tips));
                    return;
                }
            }
        } else {
            ToastUtils.showShort("手机号码错误");
            return;
        }


        showLoadingBar();
        HashMap<String, String> hashMap = new HashMap();

        hashMap.put("code_type", MobileCode.MOBILE_CODE_REGISTER.getType());
        hashMap.put("mobile", etPhone.getEditableText().toString());
        hashMap.put("mobile_code", etCode.getEditableText().toString());
        Call<TaskInfoList> call = ApiConfig.getInstants().create(TaskService.class).checkMobileCode(TUtils.getParams(hashMap));
        API.getList(call, UserInfo.class, new ApiCallBackList<UserInfo>() {
            @Override
            public void onSuccess(int msgCode, String msg, List<UserInfo> data) {
                checkInviteCode();
            }

            @Override
            public void onFaild(int msgCode, String msg) {
                ToastUtils.showShort(msg);
                dismissLoadingBar();
            }
        });
    }


    //校验邀请码
    private void checkInviteCode() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("invite_code", etInvideCode.getEditableText().toString());
        Call<TaskInfoList> call = ApiConfig.getInstants().create(TaskService.class).checkInviteCode(TUtils.getParams(hashMap));

        API.getList(call, String.class, new ApiCallBackList<String>() {
            @Override
            public void onSuccess(int msgCode, String msg, List<String> data) {
                checkParams(etPhone.getEditableText().toString(), etCode.getEditableText().toString(), etInvideCode.getEditableText().toString());

            }

            @Override
            public void onFaild(int msgCode, String msg) {
                ToastUtils.showShort(msg);
                dismissLoadingBar();
            }
        });


    }

    private void checkParams(String phone, String smsCode, String inviteCode) {
        if (TextUtils.isEmpty(phone)) {
            ToastUtils.showShort(getString(R.string.phone_tips));
        } else if (RegexUtils.isMobileSimple(phone)) {
            if (TextUtils.isEmpty(smsCode)) {
                ToastUtils.showShort(getString(R.string.code_tips));
            } else {
                if (!TextUtils.isEmpty(etInvideCode.getEditableText().toString())) {

                    RegisterParams registerParams = new RegisterParams();
                    registerParams.invite_code = inviteCode;
                    registerParams.mobile = phone;
                    registerParams.mobile_code = smsCode;
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(Constans.PASS_OBJECT, registerParams);
                    ActivityUtils.startActivityForResult(bundle, RegisterActivity.this, RegisterStepTwoActivity.class, 300);
                } else {
                    ToastUtils.showShort(getString(R.string.invide_code_tips));
                }
            }
        } else {
            ToastUtils.showShort("手机号码错误");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 300) {
            if (resultCode == RESULT_OK) {
                setResult(RESULT_OK);
                finish();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    private void getCode() {


        HashMap<String, String> hashMap = new HashMap();
        hashMap.put("code_type", MobileCode.MOBILE_CODE_REGISTER.getType());
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
