package com.task.system.activity;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.gyf.barlibrary.ImmersionBar;
import com.task.system.Constans;
import com.task.system.R;
import com.task.system.adapters.AreaManageAdapter;
import com.task.system.api.API;
import com.task.system.api.TaskInfoList;
import com.task.system.api.TaskService;
import com.task.system.bean.AreaManageIitem;
import com.task.system.utils.TUtils;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;
import com.yc.lib.api.ApiCallBackList;
import com.yc.lib.api.ApiConfig;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;

/**
 * Email: dengyc@dadaodata.com
 * FileName: MyAreaManageActivity.java
 * Author: dengyancheng
 * Date: 2019-08-11 22:18
 * Description: 我的区域管理
 * History:
 */
public class MyAreaManageActivity extends BaseSimpleActivity {


    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_right_function)
    TextView tvRightFunction;
    @BindView(R.id.rl_tittle_ui)
    RelativeLayout rlTittleUi;
    @BindView(R.id.iv_header)
    ImageView ivHeader;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_type)
    TextView tvType;
    @BindView(R.id.btn_invite_code)
    Button btnInviteCode;
    @BindView(R.id.ll_user_info_ui)
    LinearLayout llUserInfoUi;
    @BindView(R.id.btnPublish)
    Button btnPublish;
    @BindView(R.id.recycle_publish)
    RecyclerView recyclePublish;
    @BindView(R.id.recycle_orders)
    RecyclerView recycleOrders;
    private int REQUEST_CODE_SELECT = 402;


    private AreaManageAdapter disputeAdapter,publishAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_area_manage);
        ButterKnife.bind(this);

        ImmersionBar.with(mContext).statusBarDarkFont(false,0.2f).statusBarColor(R.color.blue_ball).init();


        disputeAdapter =  new AreaManageAdapter(R.layout.adapter_area_manage,new ArrayList());
        publishAdapter= new AreaManageAdapter(R.layout.adapter_area_manage, new ArrayList());

        //订单争议
        recycleOrders.setLayoutManager(new GridLayoutManager(this,3));
//        recycleOrders.setLayoutManager(new LinearLayoutManager(this));
        recycleOrders.setNestedScrollingEnabled(false);
        recycleOrders.setAdapter( disputeAdapter);


        //发布
        recyclePublish.setLayoutManager(new GridLayoutManager(this,3));
        recyclePublish.setAdapter( publishAdapter);
        recyclePublish.setNestedScrollingEnabled(false);



        getRecycleList();


    }


    private void getRecycleList(){


        //争议
        Call<TaskInfoList> callDispute = ApiConfig.getInstants().create(TaskService.class).getDisputeStatus(TUtils.getParams());
        API.getList(callDispute, AreaManageIitem.class, new ApiCallBackList<AreaManageIitem>() {
            @Override
            public void onSuccess(int msgCode, String msg, List<AreaManageIitem> data) {
                TUtils.dealReqestData(disputeAdapter, recycleOrders, data);

            }

            @Override
            public void onFaild(int msgCode, String msg) {
                TUtils.dealNoReqestData(disputeAdapter, recycleOrders);
            }
        });


        //发布
        Call<TaskInfoList> callPublish = ApiConfig.getInstants().create(TaskService.class).getOrderStatus(TUtils.getParams());
        API.getList(callPublish, AreaManageIitem.class, new ApiCallBackList<AreaManageIitem>() {
            @Override
            public void onSuccess(int msgCode, String msg, List<AreaManageIitem> data) {
                TUtils.dealReqestData(publishAdapter, recyclePublish, data);

            }

            @Override
            public void onFaild(int msgCode, String msg) {
                TUtils.dealNoReqestData(publishAdapter, recyclePublish);
            }
        });


    }

    @OnClick({R.id.iv_back, R.id.tv_right_function, R.id.btn_invite_code, R.id.btnPublish})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_right_function:
                break;
            case R.id.btn_invite_code:
                ActivityUtils.startActivity(MyInviteCodeActivity.class);
                break;
            case R.id.btnPublish:
                AndPermission.with(MyAreaManageActivity.this)
                        .runtime()
                        .permission(Permission.Group.CAMERA)
                        .onGranted(permissions -> {
                            Bundle bundle = new Bundle();
                            bundle.putString(Constans.PASS_NAME, "发布任务");
                            bundle.putString(Constans.PASS_STRING, TUtils.getUserInfo().add_url);
                            ActivityUtils.startActivity(bundle, OpenWebViewActivity.class);
                        })
                        .onDenied(permissions -> {
                            ToastUtils.showShort("请打开相机权限");
                        })
                        .start();
                break;
        }
    }
}
