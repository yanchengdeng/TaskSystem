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

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.RegexUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.google.gson.Gson;
import com.task.system.Constans;
import com.task.system.R;
import com.task.system.api.API;
import com.task.system.api.TaskInfo;
import com.task.system.api.TaskService;
import com.task.system.bean.UserInfo;
import com.task.system.bean.WxAccessToken;
import com.task.system.utils.TUtils;
import com.yc.lib.api.ApiCallBack;
import com.yc.lib.api.ApiConfig;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;

/**
 * Email: dengyc@dadaodata.com
 * FileName: BindWxHaveAccountActivity.java
 * Author: dengyancheng
 * Date: 2019-08-08 23:31
 * Description: 绑定已注册账号
 * History:
 */
public class BindWxHaveAccountActivity extends BaseSimpleActivity {

    @BindView(R.id.iv_back)
    ImageView ivBack;
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

    private WxAccessToken wxAccessToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_wx_have_account);
        ButterKnife.bind(this);

        wxAccessToken = (WxAccessToken) getIntent().getSerializableExtra(Constans.PASS_OBJECT);

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

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(etAccont.getEditableText().toString())) {
                    ToastUtils.showShort(R.string.input_your_username);
                } else if (TextUtils.isEmpty(etPassword.getEditableText().toString())) {
                    ToastUtils.showShort("请输入密码");
                } else {
                    doBindWx();
                }

            }
        });
    }

    /**
     * username
     * mobile
     * mobile_code
     * password
     * re_password
     */

    private void doBindWx() {


        showLoadingBar();
        HashMap<String, String> hashMap = new HashMap();

        hashMap.put("oauth_type", "wx");
        hashMap.put("oauth_uid", wxAccessToken.openid);
        hashMap.put("oauth_token", wxAccessToken.access_token);
        hashMap.put("expire_time", String.valueOf(wxAccessToken.expires_in));
//        hashMap.put("mobile", etPhone.getEditableText().toString().trim());
        hashMap.put("username",etAccont.getEditableText().toString().trim());
        hashMap.put("password", etPassword.getEditableText().toString());
        hashMap.put("re_password", etPassword.getEditableText().toString());
        Call<TaskInfo> call = ApiConfig.getInstants().create(TaskService.class).bindByLogin(TUtils.getParams(hashMap));

        API.getObject(call, UserInfo.class, new ApiCallBack<UserInfo>() {
            @Override
            public void onSuccess(int msgCode, String msg, UserInfo data) {

                if (data == null) {
                    return;
                }
                SPUtils.getInstance().put(Constans.USER_INFO, new Gson().toJson(data));
                SPUtils.getInstance().put(Constans.TOKEN, new Gson().toJson(data.tokens));
                SPUtils.getInstance().put(Constans.USER_ACOUNT, data.username);
                dismissLoadingBar();
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

    private void checkCanRegister() {
        if (!TextUtils.isEmpty(
                etAccont.getEditableText().toString())
                && RegexUtils.isMobileSimple(etAccont.getEditableText().toString())
                && !TextUtils.isEmpty(etPassword.getEditableText().toString())
        ) {
            btnRegister.setBackground(getResources().getDrawable(R.drawable.normal_submit_btn_red_large));
            btnRegister.setTextColor(getResources().getColor(R.color.white));
        } else {
            btnRegister.setBackground(getResources().getDrawable(R.drawable.normal_submit_btn_gray_large));
            btnRegister.setTextColor(getResources().getColor(R.color.white));
        }
    }
}
