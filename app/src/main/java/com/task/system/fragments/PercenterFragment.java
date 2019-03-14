package com.task.system.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.SPUtils;
import com.google.gson.Gson;
import com.task.system.Constans;
import com.task.system.R;
import com.task.system.activity.MyCollectedActivity;
import com.task.system.activity.MyInviteCodeActivity;
import com.task.system.activity.PersonSettingActivity;
import com.task.system.api.API;
import com.task.system.api.TaskInfo;
import com.task.system.api.TaskService;
import com.task.system.bean.UserInfo;
import com.task.system.utils.TUtils;
import com.yc.lib.api.ApiCallBack;
import com.yc.lib.api.ApiConfig;
import com.yc.lib.api.utils.ImageLoaderUtil;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import retrofit2.Call;

public class PercenterFragment extends Fragment {

    @BindView(R.id.iv_header)
    ImageView ivHeader;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_type)
    TextView tvType;
    @BindView(R.id.tv_id)
    TextView tvId;
    @BindView(R.id.ll_user_info_ui)
    LinearLayout llUserInfoUi;
    @BindView(R.id.tv_money)
    TextView tvMoney;
    @BindView(R.id.rl_my_money)
    RelativeLayout rlMyMoney;
    @BindView(R.id.tv_allwork_title)
    TextView tvAllworkTitle;
    @BindView(R.id.tv_collect)
    TextView tvCollect;
    @BindView(R.id.tv_my_accoutn)
    TextView tvMyAccoutn;
    @BindView(R.id.tv_invite_code)
    TextView tvInviteCode;
    Unbinder unbinder;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.percenter_fragment, container, false);
        unbinder = ButterKnife.bind(this, view);
        getUserDetail();

        return view;
    }


    private void getUserDetail(){
        HashMap<String, String> hashMap = new HashMap();
        hashMap.put("uid", TUtils.getUserId());
        Call<TaskInfo> call = ApiConfig.getInstants().create(TaskService.class).getUserInfo(TUtils.getParams(hashMap));

        API.getObject(call, UserInfo.class, new ApiCallBack<UserInfo>() {
            @Override
            public void onSuccess(int msgCode, String msg, UserInfo data) {
                SPUtils.getInstance().put(Constans.USER_INFO, new Gson().toJson(data));
                initData(data);
            }

            @Override
            public void onFaild(int msgCode, String msg) {
            }
        });
    }

    private void initData(UserInfo data) {
        if (!TextUtils.isEmpty(data.avatar)) {
            ImageLoaderUtil.loadCircle(data.avatar,ivHeader,R.mipmap.defalut_header);
        }

        if (!TextUtils.isEmpty(data.nickname)) {
            tvName.setText(data.nickname);
        }

        if (!TextUtils.isEmpty(data.user_type)) {
            tvType.setText(TUtils.getUserTypeName(data.user_type));
        }

        if (!TextUtils.isEmpty(data.uid)) {
            tvId.setText("ID:"+data.uid);
        }


        if (!TextUtils.isEmpty(data.score)) {
            tvMoney.setText(data.score);
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.ll_user_info_ui, R.id.rl_my_money, R.id.tv_allwork_title, R.id.tv_collect, R.id.tv_my_accoutn, R.id.tv_invite_code})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_user_info_ui:
                ActivityUtils.startActivity(PersonSettingActivity.class);
                break;
            case R.id.rl_my_money:
                break;
            case R.id.tv_allwork_title:
                break;
            case R.id.tv_collect:
                ActivityUtils.startActivity(MyCollectedActivity.class);
                break;
            case R.id.tv_my_accoutn:
                break;
            case R.id.tv_invite_code:
                ActivityUtils.startActivity(MyInviteCodeActivity.class);
                break;
        }
    }
}
