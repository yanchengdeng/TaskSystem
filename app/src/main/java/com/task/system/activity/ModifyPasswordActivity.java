package com.task.system.activity;

import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.blankj.utilcode.util.ToastUtils;
import com.gyf.barlibrary.ImmersionBar;
import com.task.system.R;
import com.task.system.api.API;
import com.task.system.api.TaskInfoList;
import com.task.system.api.TaskService;
import com.task.system.bean.UserInfo;
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
 * Author: 邓言诚  Create at : 2019/3/22  23:23
 * Email: yanchengdeng@gmail.com
 * Describle: 更改密码
 */
public class ModifyPasswordActivity extends BaseSimpleActivity {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.old_password)
    EditText oldPassword;
    @BindView(R.id.et_password_new)
    EditText etPasswordNew;
    @BindView(R.id.et_password_new_repeat)
    EditText etPasswordNewRepeat;
    @BindView(R.id.btn_confirm)
    Button btnConfirm;
    @BindView(R.id.card_view)
    CardView cardView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_password);
        ButterKnife.bind(this);
        ImmersionBar.with(this).init();
        checkPhoneCode();
    }


    private void checkPhoneCode() {


        oldPassword.addTextChangedListener(new TextWatcher() {
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


        etPasswordNew.addTextChangedListener(new TextWatcher() {
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


        etPasswordNewRepeat.addTextChangedListener(new TextWatcher() {
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
        if (!TextUtils.isEmpty(oldPassword.getEditableText().toString())
                && !TextUtils.isEmpty(etPasswordNew.getEditableText().toString())
                && !TextUtils.isEmpty(etPasswordNewRepeat.getEditableText().toString())
                && etPasswordNew.getEditableText().toString().equals(etPasswordNewRepeat.getEditableText().toString())) {
            btnConfirm.setBackground(getResources().getDrawable(R.drawable.normal_submit_btn_red_large));
            btnConfirm.setTextColor(getResources().getColor(R.color.white));
        } else {
            btnConfirm.setBackground(getResources().getDrawable(R.drawable.normal_submit_btn_gray_large));
            btnConfirm.setTextColor(getResources().getColor(R.color.white));
        }
    }

    @OnClick({R.id.iv_back, R.id.btn_confirm})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.btn_confirm:
                doSubmit();
                break;
        }
    }

    private void doSubmit() {
        if (!TextUtils.isEmpty(oldPassword.getEditableText().toString())) {
            if (!TextUtils.isEmpty(etPasswordNew.getEditableText().toString())) {
                if (!TextUtils.isEmpty(etPasswordNewRepeat.getEditableText().toString())) {
                    if (etPasswordNewRepeat.getEditableText().toString().equals(etPasswordNew.getEditableText().toString())) {
                        doModifyPassword(oldPassword.getEditableText().toString(), etPasswordNew.getEditableText().toString());
                    } else {
                        ToastUtils.showShort("两次密码不一致");
                    }
                } else {
                    ToastUtils.showShort(R.string.get_new_password_tips_repeat);
                }
            } else {
                ToastUtils.showShort(R.string.get_new_password_tips);
            }
        } else {
            ToastUtils.showShort(R.string.old_password_tips);
        }
    }


    /**
     * @param oldPwd
     * @param newPwd
     */
    private void doModifyPassword(String oldPwd, String newPwd) {
        showLoadingBar();
        HashMap<String, String> hashMap = new HashMap();

        hashMap.put("old_password", oldPwd);
        hashMap.put("new_password", newPwd);
        hashMap.put("re_password", newPwd);
        Call<TaskInfoList> call = ApiConfig.getInstants().create(TaskService.class).modifyPassword(TUtils.getParams(hashMap));

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
}
