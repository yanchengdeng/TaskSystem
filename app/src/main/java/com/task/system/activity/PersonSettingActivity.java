package com.task.system.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.task.system.R;
import com.task.system.api.API;
import com.task.system.api.TaskInfoList;
import com.task.system.api.TaskService;
import com.task.system.utils.TUtils;
import com.yc.lib.api.ApiCallBackList;
import com.yc.lib.api.ApiConfig;
import com.yc.lib.api.User;
import com.yc.lib.api.UserInfo;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;

public class PersonSettingActivity extends BaseActivity {

    @BindView(R.id.iv_header)
    ImageView ivHeader;
    @BindView(R.id.iv_header_arrow)
    ImageView ivHeaderArrow;
    @BindView(R.id.rl_header_ui)
    RelativeLayout rlHeaderUi;
    @BindView(R.id.iv_user_name_arrow)
    ImageView ivUserNameArrow;
    @BindView(R.id.rl_name_ui)
    RelativeLayout rlNameUi;
    @BindView(R.id.iv_phone_arrow)
    ImageView ivPhoneArrow;
    @BindView(R.id.rl_phone_ui)
    RelativeLayout rlPhoneUi;
    @BindView(R.id.iv_system_id_arrow)
    ImageView ivSystemIdArrow;
    @BindView(R.id.rl_sysyte_id_ui)
    RelativeLayout rlSysyteIdUi;
    @BindView(R.id.tv_modify_password_ui)
    TextView tvModifyPasswordUi;
    @BindView(R.id.tv_login_out)
    TextView tvLoginOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_setting);
        ButterKnife.bind(this);
        setTitle("个人设置");
    }

    @OnClick({R.id.rl_header_ui, R.id.rl_name_ui, R.id.rl_phone_ui, R.id.rl_sysyte_id_ui, R.id.tv_modify_password_ui, R.id.tv_login_out})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_header_ui:
                break;
            case R.id.rl_name_ui:
                break;
            case R.id.rl_phone_ui:
                break;
            case R.id.rl_sysyte_id_ui:
                break;
            case R.id.tv_modify_password_ui:
                break;
            case R.id.tv_login_out:
                showExitDialog();
                break;
        }
    }


    //退出登陆
    private void showExitDialog() {

        MaterialDialog.Builder builder = new MaterialDialog.Builder(ApiConfig.context)
                .title("温馨提示")
                .content("确定退出登录？")
                .positiveText("确定").positiveColor(getResources().getColor(R.color.color_blue)).onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                        doLoginOutAction();

                    }
                }).negativeText("取消").negativeColor(getResources().getColor(R.color.color_info)).onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                });

        MaterialDialog dialog = builder.build();
        dialog.show();
    }

    private void doLoginOutAction() {
        HashMap<String, String> hashMap = new HashMap();
        hashMap.put("uid", TUtils.getUserId());
        Call<TaskInfoList> call = ApiConfig.getInstants().create(TaskService.class).doLoginOut(TUtils.getParams(hashMap));

        API.getList(call, UserInfo.class, new ApiCallBackList<User>() {
            @Override
            public void onSuccess(int msgCode, String msg, List<User> data) {
                dismissLoadingBar();
                ToastUtils.showShort(msg);
                TUtils.clearUserInfo();
                ActivityUtils.startActivity(LoginActivity.class);
                finish();
            }

            @Override
            public void onFaild(int msgCode, String msg) {
                dismissLoadingBar();
//                ToastUtils.showShort(msg);
                dismissLoadingBar();
                ToastUtils.showShort(msg);
                TUtils.clearUserInfo();
                ActivityUtils.startActivity(LoginActivity.class);
                finish();
            }
        });
    }
}
