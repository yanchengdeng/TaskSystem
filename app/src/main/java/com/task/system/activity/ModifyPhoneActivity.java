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
import com.blankj.utilcode.util.ToastUtils;
import com.gyf.barlibrary.ImmersionBar;
import com.task.system.Constans;
import com.task.system.R;
import com.task.system.api.API;
import com.task.system.api.TaskInfoList;
import com.task.system.api.TaskService;
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
 * Author: 邓言诚  Create at : 2019/3/23  00:02
 * Email: yanchengdeng@gmail.com
 * Describle: 修改手机号
 */
public class ModifyPhoneActivity extends BaseSimpleActivity {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.et_phone)
    EditText etPhone;
    @BindView(R.id.et_code)
    EditText etCode;
    @BindView(R.id.tv_get_code)
    TextView tvGetCode;
    @BindView(R.id.btn_register)
    Button btnRegister;
    @BindView(R.id.card_view)
    CardView cardView;
    @BindView(R.id.tv_phone_tips)
    TextView tvPhoneTips;

    private CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_phone);
        ButterKnife.bind(this);
        ImmersionBar.with(this).init();
        tvPhoneTips.setText("当前手机号");
        btnRegister.setText("下一步");
        etPhone.setEnabled(false);
        etPhone.setText(TUtils.getHidePhone());
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


    }


    private void checkCanRegister() {
        if (!TextUtils.isEmpty(etCode.getEditableText().toString())
        ) {
            btnRegister.setBackground(getResources().getDrawable(R.drawable.normal_submit_btn_red_large));
            btnRegister.setTextColor(getResources().getColor(R.color.white));
        } else {
            btnRegister.setBackground(getResources().getDrawable(R.drawable.normal_submit_btn_gray_large));
            btnRegister.setTextColor(getResources().getColor(R.color.white));
        }
    }

    @OnClick({R.id.iv_back, R.id.tv_get_code, R.id.btn_register})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_get_code:
//                if (TextUtils.isEmpty(etPhone.getEditableText().toString())) {
//                    ToastUtils.showShort(getString(R.string.old_phone_tips));
//                } else if (RegexUtils.isMobileSimple(etPhone.getEditableText().toString())) {
                getCode();
//                } else {
//                    ToastUtils.showShort("手机号码错误");
//                }

                break;
            case R.id.btn_register:

                doCheckCode();
                break;
        }
    }

    private void doCheckCode() {
//        if (TextUtils.isEmpty(etPhone.getEditableText().toString())) {
//            ToastUtils.showShort(R.string.new_phone_tips);
//            return;
//        }
//        if (!RegexUtils.isMobileSimple(etPhone.getEditableText().toString())) {
//            ToastUtils.showShort("手机号码错误");
//            return;
//        }

        if (TextUtils.isEmpty(etCode.getEditableText().toString())) {
            ToastUtils.showShort(R.string.code_tips);
            return;
        }
        showLoadingBar();
        HashMap<String, String> hashMap = new HashMap();

        hashMap.put("code_type", MobileCode.MOBILE_CODE_RESET.getType());
        hashMap.put("mobile", TUtils.getUserInfo().mobile);
        hashMap.put("mobile_code", etCode.getEditableText().toString());
        Call<TaskInfoList> call = ApiConfig.getInstants().create(TaskService.class).checkMobileCode(TUtils.getParams(hashMap));
        API.getList(call, UserInfo.class, new ApiCallBackList<UserInfo>() {
            @Override
            public void onSuccess(int msgCode, String msg, List<UserInfo> data) {
                ToastUtils.showShort(msg);
                dismissLoadingBar();
                Bundle bundle = new Bundle();
                bundle.putString(Constans.PASS_STRING, etCode.getEditableText().toString());
                ActivityUtils.startActivityForResult(bundle, ModifyPhoneActivity.this, ModifyPhoneStepTwoActivity.class, 100);
            }

            @Override
            public void onFaild(int msgCode, String msg) {
                ToastUtils.showShort(msg);
                dismissLoadingBar();
            }
        });
    }

    private void getCode() {


        HashMap<String, String> hashMap = new HashMap();
        hashMap.put("code_type", MobileCode.MOBILE_CODE_RESET.getType());
        hashMap.put("mobile", TUtils.getUserInfo().mobile);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 100) {
            if (resultCode == RESULT_OK) {
                finish();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
