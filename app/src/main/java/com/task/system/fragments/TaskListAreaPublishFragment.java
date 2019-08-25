package com.task.system.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.task.system.Constans;
import com.task.system.R;
import com.task.system.activity.BaseActivity;
import com.task.system.activity.LinkOrdersActivity;
import com.task.system.activity.OpenWebViewActivity;
import com.task.system.activity.PublishManageActivity;
import com.task.system.activity.TaskDetailActivity;
import com.task.system.adapters.AreaManagePublishAdapter;
import com.task.system.api.API;
import com.task.system.api.TaskInfoList;
import com.task.system.api.TaskService;
import com.task.system.bean.AreaManagePublish;
import com.task.system.utils.TUtils;
import com.yc.lib.api.ApiCallBackList;
import com.yc.lib.api.ApiConfig;
import com.yc.lib.api.utils.SysUtils;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.Unbinder;
import retrofit2.Call;

/**
 * Email: dengyc@dadaodata.com
 * FileName: TaskListAreaPublishFragment.java
 * Author: dengyancheng
 * Date: 2019-08-20 22:44
 * Description: 区域管理 发布任务
 * History:
 */
public class TaskListAreaPublishFragment extends BaseFragment {
    @BindView(R.id.recycle)
    RecyclerView recycle;
    @BindView(R.id.smartRefresh)
    SmartRefreshLayout smartRefresh;
    Unbinder unbinder;


    private String status;
    private int page = 1;
    private String sort;

    private AreaManagePublishAdapter taskOrderAdapter;


    @Override
    protected int getAbsLayoutId() {
        return R.layout.fragment_task_list;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {

        status = getArguments().getString(Constans.PASS_STRING);

        taskOrderAdapter = new AreaManagePublishAdapter(R.layout.adapter_operate_publish_item, status);
        recycle.setLayoutManager(new LinearLayoutManager(ApiConfig.context));
        recycle.setAdapter(taskOrderAdapter);

        taskOrderAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                page++;
                getOrderList();
            }
        }, recycle);

        smartRefresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                page = 1;
                getOrderList();
            }
        });


        taskOrderAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Bundle bundle = new Bundle();
                bundle.putString(Constans.PASS_NAME, "任务详情");
                bundle.putString(Constans.PASS_STRING, taskOrderAdapter.getData().get(position).getId());
                ActivityUtils.startActivity(bundle, TaskDetailActivity.class);
            }
        });


        taskOrderAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                //查看原因
//                TextView funLookReason = helper.getView(R.id.tv_look_for_reason);
                //取消任务
//                TextView funCancleTask = helper.getView(R.id.tv_cancle_task);
//                再次编辑
//                TextView funEditAgain = helper.getView(R.id.tv_edit_again);
//                订单数据
//                View  funOrderData = helper.getView(R.id.tv_order_data);


                switch (view.getId()) {
                    case R.id.tv_look_for_reason:
                        SysUtils.showToast("给一个原因！！！！xxxx");
                        break;
                    case R.id.tv_cancle_task:
                        cancleTask(position,taskOrderAdapter.getData().get(position));
                        break;
                    case R.id.tv_edit_again:
                        Bundle bundle = new Bundle();
                        bundle.putString(Constans.PASS_NAME, "编辑任务");
                        bundle.putString(Constans.PASS_STRING, taskOrderAdapter.getData().get(position).getEdit_url());
                        ActivityUtils.startActivity(bundle, OpenWebViewActivity.class);
                        break;
                    case R.id.tv_order_data:
                        Bundle orderdatas = new Bundle();
                        orderdatas.putSerializable(Constans.PASS_OBJECT, taskOrderAdapter.getData().get(position));
                        ActivityUtils.startActivity(orderdatas, LinkOrdersActivity.class);

                        break;
                }
            }
        });


    }

    //刷新数据
    public void setSortRefresh() {
        page = 1;
        getOrderList();
    }



    //弹出提示
    private void showDialogTips(String reason) {

        MaterialDialog.Builder builder = new MaterialDialog.Builder(ApiConfig.context)
//                .title("温馨提示")
                .content("" + reason)
                .positiveText("确定").positiveColor(getResources().getColor(R.color.color_blue)).onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                });

        MaterialDialog dialog = builder.build();
        dialog.show();
    }


    //放弃任务
    private void cancleTask(int position, AreaManagePublish orderInfo) {
        if (getActivity() != null) {
            ((BaseActivity) getActivity()).showLoadingBar("取消任务..");
        }
        HashMap<String, String> maps = new HashMap<>();
        maps.put("task_id", orderInfo.getId());
        //要变更的状态，中止任务和取消任务的值均为0
        maps.put("status", "0");
        Call<TaskInfoList> call = ApiConfig.getInstants().create(TaskService.class).operatorTaskStatus(TUtils.getParams(maps));

        API.getList(call, String.class, new ApiCallBackList<String>() {
            @Override
            public void onSuccess(int msgCode, String msg, List<String> data) {
                ToastUtils.showShort("" + msg);
                taskOrderAdapter.remove(position);

//                EventBus.getDefault().post(new RefreshUnreadCountEvent());
                if (taskOrderAdapter.getData().size() == 0) {
                    TUtils.dealNoReqestData(taskOrderAdapter, recycle, smartRefresh);
                }
                if (getActivity() != null) {
                    ((BaseActivity) getActivity()).dismissLoadingBar();
                }
            }

            @Override
            public void onFaild(int msgCode, String msg) {
                ToastUtils.showShort("" + msg);
                if (getActivity() != null) {
                    ((BaseActivity) getActivity()).dismissLoadingBar();
                }
            }
        });
    }




    @Override
    protected void initData(View view) {
        super.initData(view);

        getOrderList();
    }

    private void getOrderList() {
        if (ApiConfig.context != null) {
            ((BaseActivity) ApiConfig.context).showLoadingBar();
        }

        HashMap<String, String> maps = new HashMap<>();
        maps.put("page", String.valueOf(page));
        if (!TextUtils.isEmpty(status)){
            maps.put("tab",status);
        }
        if (!TextUtils.isEmpty(sort)) {
            maps.put("sort", sort);
        }

        maps.put("keywords", "");

        if (ApiConfig.context!=null) {
            if (!TextUtils.isEmpty (((PublishManageActivity)ApiConfig.context).etKey.getEditableText().toString())) {
                maps.put("keywords", ((PublishManageActivity)ApiConfig.context).etKey.getEditableText().toString());
            }
        }

        Call<TaskInfoList> call = ApiConfig.getInstants().create(TaskService.class).getOperaorTaskList(TUtils.getParams(maps));

        API.getList(call, AreaManagePublish.class, new ApiCallBackList<AreaManagePublish>() {
            @Override
            public void onSuccess(int msgCode, String msg, List<AreaManagePublish> data) {
                if (ApiConfig.context != null) {
                    ((BaseActivity) ApiConfig.context).dismissLoadingBar();
                }

                TUtils.dealReqestData(taskOrderAdapter, recycle, data, page, smartRefresh);
            }

            @Override
            public void onFaild(int msgCode, String msg) {
                if (ApiConfig.context != null) {
                    ((BaseActivity) ApiConfig.context).dismissLoadingBar();
                }

                TUtils.dealNoReqestData(taskOrderAdapter, recycle, smartRefresh);
            }
        });
    }
}
