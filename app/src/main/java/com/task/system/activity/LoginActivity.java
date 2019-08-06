package com.task.system.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
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
import com.task.system.bean.SimpleBeanInfo;
import com.task.system.bean.UserInfo;
import com.task.system.bean.WxAccessToken;
import com.task.system.utils.TUtils;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.yc.lib.api.ApiCallBack;
import com.yc.lib.api.ApiConfig;
import com.yc.lib.api.utils.SysUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
    @BindView(R.id.btn_wechat)
    AppCompatImageView btnWechat;
    private boolean canLoginStatus;


    private IWXAPI api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        ImmersionBar.with(this).init();
        EventBus.getDefault().register(this);

        // acquire wxapi
        api = WXAPIFactory.createWXAPI(this, Constans.WX_SHARE_APP_ID,false);

        checkAccoutPsw();
        mSwipeBackHelper.setSwipeBackEnable(false);
        getCustom();

        if (!TextUtils.isEmpty(SPUtils.getInstance().getString(Constans.USER_ACOUNT))) {
            etAccont.setText(SPUtils.getInstance().getString(Constans.USER_ACOUNT));
        }

        if (!TextUtils.isEmpty(SPUtils.getInstance().getString(Constans.PASSWORD))) {
            etPassword.setText(SPUtils.getInstance().getString(Constans.PASSWORD));
        }

    }


    private void getCustom() {
        Call<TaskInfo> call = ApiConfig.getInstants().create(TaskService.class).getCustomeSerice(TUtils.getParams());

        API.getObject(call, SimpleBeanInfo.class, new ApiCallBack<SimpleBeanInfo>() {
            @Override
            public void onSuccess(int msgCode, String msg, SimpleBeanInfo data) {
                SPUtils.getInstance().put(Constans.KEFU, data.link);
            }

            @Override
            public void onFaild(int msgCode, String msg) {

            }
        });
    }

    @OnClick({R.id.tv_contact, R.id.tv_forget_password, R.id.btn_login, R.id.btn_wechat,R.id.tv_go_register})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_contact:
                TUtils.openKf();

                break;
            case R.id.tv_forget_password:
                ActivityUtils.startActivity(ForgetPasswordActivity.class);
                break;
            case R.id.btn_login:
                if (canLoginStatus) {
                    doLogin(etAccont.getEditableText().toString(), etPassword.getEditableText().toString());
                } else {
                    if (TextUtils.isEmpty(etAccont.getEditableText())) {
                        ToastUtils.showShort("请输入手机号/用户名");
                    }

                    if (TextUtils.isEmpty(etPassword.getEditableText().toString())) {
                        ToastUtils.showShort("请输入密码");
                    }
                }

                break;
            case R.id.btn_wechat:


                SendAuth.Req req = new SendAuth.Req();
                req.scope = "snsapi_userinfo";
                req.state = "wechat_sdk_demo_test";
                api.sendReq(req);
                break;
            case R.id.tv_go_register:
                ActivityUtils.startActivityForResult(LoginActivity.this, RegisterActivity.class, 200);
                break;
        }
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 200) {
            if (resultCode == RESULT_OK) {
                finish();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
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
                SPUtils.getInstance().put(Constans.USER_INFO, new Gson().toJson(data));
                SPUtils.getInstance().put(Constans.TOKEN, new Gson().toJson(data.tokens));
                SPUtils.getInstance().put(Constans.USER_ACOUNT, etAccont.getEditableText().toString());
                SPUtils.getInstance().put(Constans.PASSWORD, etPassword.getEditableText().toString());
//                if (data.user_type.equals(UserType.USER_TYPE_MEMBER.getType())){
                ActivityUtils.startActivity(MainActivity.class);
//                }else{
//                    ActivityUtils.startActivity(PercentCenterActivity.class);
//                }
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
        EventBus.getDefault().unregister(this);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onChange(SendAuth.Resp resp) {
        if (resp != null && !TextUtils.isEmpty(resp.code)) {

            HashMap<String,String> maps = new HashMap<>();
            maps.put("appid",Constans.WX_SHARE_APP_ID);
            maps.put("secret","");
            maps.put("code",resp.code);
            maps.put("grant_type","authorization_code");

            Call<WxAccessToken> call = ApiConfig.getInstants().create(TaskService.class).getAccessToken(maps);

            call.enqueue(new Callback<WxAccessToken>() {
                @Override
                public void onResponse(Call<WxAccessToken> call, Response<WxAccessToken> response) {
                    SysUtils.log(response.body().access_token);

                }

                @Override
                public void onFailure(Call<WxAccessToken> call, Throwable t) {
                    SysUtils.log(t.getMessage());
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        doubleClickExist();
    }

    private long mExitTime;

    /****
     * 连续两次点击退出
     */
    private boolean doubleClickExist() {
        if ((System.currentTimeMillis() - mExitTime) > 2000) {
            ToastUtils.showShort(R.string.double_click_exit);
            mExitTime = System.currentTimeMillis();
            return true;
        } else {
            Intent home = new Intent(Intent.ACTION_MAIN);
            home.addCategory(Intent.CATEGORY_HOME);
            startActivity(home);
//            AppManager.getAppManager().AppExit(this);
//            finish();
        }
        return false;
    }



}
