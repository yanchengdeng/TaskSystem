package com.task.system.fragments;

import android.content.Intent;
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
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.task.system.Constans;
import com.task.system.R;
import com.task.system.activity.AddNewLeaderActivity;
import com.task.system.activity.MainActivity;
import com.task.system.activity.MyAccountActivity;
import com.task.system.activity.MyAreaManageActivity;
import com.task.system.activity.MyCollectedActivity;
import com.task.system.activity.MyIncomeActivity;
import com.task.system.activity.MyInviteCodeActivity;
import com.task.system.activity.MyTeamActivity;
import com.task.system.activity.OpenWebViewActivity;
import com.task.system.activity.PersonSettingActivity;
import com.task.system.api.API;
import com.task.system.api.TaskInfo;
import com.task.system.api.TaskService;
import com.task.system.bean.UserInfo;
import com.task.system.enums.UserType;
import com.task.system.event.UpdateUserInfoEvent;
import com.task.system.utils.TUtils;
import com.yc.lib.api.ApiCallBack;
import com.yc.lib.api.ApiConfig;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

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
    @BindView(R.id.ll_allwork_title)
    LinearLayout llAllWork;
    @BindView(R.id.tv_collect)
    TextView tvCollect;
    @BindView(R.id.tv_my_accoutn)
    TextView tvMyAccoutn;
    @BindView(R.id.tv_my_team)
    TextView tvMyTeam;
    @BindView(R.id.tv_invite_code)
    TextView tvInviteCode;
    Unbinder unbinder;
    @BindView(R.id.smartRefresh)
    SmartRefreshLayout smartRefresh;
    @BindView(R.id.tv_add_lead)
    TextView tvAddLead;
    @BindView(R.id.tv_about_us)
    TextView tvAboutUs;
    @BindView(R.id.tv_my_area_manage)
    TextView tvMyAreaManage;
    @BindView(R.id.tv_help_center)
    TextView tvHelpCenter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_percenter, container, false);
        unbinder = ButterKnife.bind(this, view);
        EventBus.getDefault().register(this);
        smartRefresh.autoRefresh();
        smartRefresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                getUserDetail();
            }
        });

        UserInfo userInfo = TUtils.getUserInfo();
        if (userInfo != null && !TextUtils.isEmpty(userInfo.user_type)) {
            //会员
            if (userInfo.user_type.equals(UserType.USER_TYPE_MEMBER.getType())) {
                tvCollect.setVisibility(View.VISIBLE);
                tvMyTeam.setVisibility(View.VISIBLE);
                tvInviteCode.setVisibility(View.VISIBLE);
                tvMyAreaManage.setVisibility(View.GONE);
            } else if (userInfo.user_type.equals(UserType.USER_TYPE_AREA.getType())) {
                //区域管理
                tvMyAreaManage.setVisibility(View.VISIBLE);
                tvAddLead.setVisibility(View.VISIBLE);
                tvInviteCode.setVisibility(View.VISIBLE);
            } else if (userInfo.user_type.equals(UserType.USER_TYPE_AGENT.getType())) {
                tvInviteCode.setVisibility(View.VISIBLE);
                tvMyTeam.setVisibility(View.GONE);

            }


            //TODO  我的团队隐藏掉
            tvMyTeam.setVisibility(View.GONE);
        }
        return view;
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Object event) {
        if (event instanceof UpdateUserInfoEvent) {
            smartRefresh.autoRefresh();
        }
    }

    private void getUserDetail() {
        HashMap<String, String> hashMap = new HashMap();
        hashMap.put("uid", TUtils.getUserId());
        Call<TaskInfo> call = ApiConfig.getInstants().create(TaskService.class).getUserInfo(TUtils.getParams(hashMap));

        API.getObject(call, UserInfo.class, new ApiCallBack<UserInfo>() {
            @Override
            public void onSuccess(int msgCode, String msg, UserInfo data) {
                SPUtils.getInstance().put(Constans.USER_INFO, new Gson().toJson(data));
                initData(data);
                smartRefresh.finishRefresh();
            }

            @Override
            public void onFaild(int msgCode, String msg) {
                if (smartRefresh != null) {
                    smartRefresh.finishRefresh();
                }
            }
        });
    }

    private void initData(UserInfo data) {
        if (!TextUtils.isEmpty(data.avatar)) {
            Glide.with(ApiConfig.context).load(data.avatar).apply(RequestOptions.circleCropTransform().placeholder(R.mipmap.defalut_header).error(R.mipmap.defalut_header)).into(ivHeader);
        }

        if (!TextUtils.isEmpty(data.username)) {
            tvName.setText(data.username);
        }

        if (!TextUtils.isEmpty(data.user_type)) {
            tvType.setText(TUtils.getUserTypeName(data.user_type));
        }

        if (!TextUtils.isEmpty(data.uid)) {
            tvId.setText("ID:" + data.uid);
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

    @OnClick({R.id.ll_user_info_ui, R.id.rl_my_money, R.id.ll_allwork_title, R.id.tv_add_lead, R.id.tv_collect, R.id.tv_my_team, R.id.tv_my_accoutn, R.id.tv_invite_code, R.id.tv_help_center,R.id.tv_about_us, R.id.tv_my_area_manage})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_user_info_ui:
                PercenterFragment.this.startActivityForResult(new Intent(ApiConfig.context, PersonSettingActivity.class), 20);
//                ActivityUtils.startActivity(PersonSettingActivity.class);
                break;
            case R.id.rl_my_money:
                ActivityUtils.startActivity(MyIncomeActivity.class);
                break;
            case R.id.ll_allwork_title:
                if (getActivity() != null) {
                    ((MainActivity) ((MainActivity) getActivity())).viewPager.setCurrentItem(1);
                }
                break;
            case R.id.tv_collect:
                ActivityUtils.startActivity(MyCollectedActivity.class);
                break;
            case R.id.tv_my_team:
                ActivityUtils.startActivity(MyTeamActivity.class);
                break;
            case R.id.tv_my_accoutn:
                ActivityUtils.startActivity(MyAccountActivity.class);
                break;
            case R.id.tv_add_lead:
                ActivityUtils.startActivity(AddNewLeaderActivity.class);
                break;
            case R.id.tv_invite_code:
                ActivityUtils.startActivity(MyInviteCodeActivity.class);
                break;
            case R.id.tv_about_us:
                Bundle about = new Bundle();
                about.putString(Constans.PASS_NAME,"关于我们");
                about.putString(Constans.ARTICAL_TYPE,Constans.ABOUT_US);
                ActivityUtils.startActivity(about, OpenWebViewActivity.class);
                break;
            case R.id.tv_help_center:
                Bundle help = new Bundle();
                help.putString(Constans.PASS_NAME,"帮助中心");
                help.putString(Constans.ARTICAL_TYPE,Constans.HELP_CENTER);
                ActivityUtils.startActivity(help, OpenWebViewActivity.class);
                break;
            case R.id.tv_my_area_manage:
                ActivityUtils.startActivity(MyAreaManageActivity.class);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 20) {
            if (resultCode == -1) {
                getUserDetail();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
