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
import com.task.system.activity.DoTaskStepActivity;
import com.task.system.activity.LinkOrdersActivity;
import com.task.system.activity.OpenWebViewActivity;
import com.task.system.activity.OrderDetailActivity;
import com.task.system.activity.OrderManageActivity;
import com.task.system.adapters.AreaManageOrdersAdapter;
import com.task.system.api.API;
import com.task.system.api.TaskInfo;
import com.task.system.api.TaskInfoList;
import com.task.system.api.TaskService;
import com.task.system.bean.AreaManageOrder;
import com.task.system.bean.OperateOrderList;
import com.task.system.bean.OrderInfo;
import com.task.system.bean.TaskInfoItem;
import com.task.system.event.RefreshUnreadCountEvent;
import com.task.system.utils.TUtils;
import com.yc.lib.api.ApiCallBack;
import com.yc.lib.api.ApiCallBackList;
import com.yc.lib.api.ApiConfig;
import com.yc.lib.api.utils.SysUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.Unbinder;
import retrofit2.Call;

/**
 * Author: dengyancheng
 * Date: 2019-08-22 23:20
 * Description: 订单列表
 * History:
 */
public class TaskListAreaOrdersFragment extends BaseFragment {
    @BindView(R.id.recycle)
    RecyclerView recycle;
    @BindView(R.id.smartRefresh)
    SmartRefreshLayout smartRefresh;
    Unbinder unbinder;


    private String status;
    private int page = 1;
    private String sort;

    private AreaManageOrdersAdapter taskOrderAdapter;


    @Override
    protected int getAbsLayoutId() {
        return R.layout.fragment_task_list;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {

        status = getArguments().getString(Constans.PASS_STRING);

        taskOrderAdapter = new AreaManageOrdersAdapter(R.layout.adapter_operate_publish_item, status);
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
                bundle.putString(Constans.PASS_STRING, taskOrderAdapter.getData().get(position).getOrder_id());
                ActivityUtils.startActivity(bundle, OrderDetailActivity.class);
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
                        Bundle about = new Bundle();
                        about.putString(Constans.PASS_NAME,"审核理由");
                        about.putString(Constans.ARTICAL_TYPE,Constans.ORDER_ROOLBACK_REASON);
                        about.putString(Constans.PASS_STRING,taskOrderAdapter.getData().get(position).getOrder_id());
                        ActivityUtils.startActivity(about, OpenWebViewActivity.class);
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
                        orderdatas.putString(Constans.PASS_STRING, taskOrderAdapter.getData().get(position).getTask_id());
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

    //申请任务
    private void applyTask(int position, String taskId) {
        HashMap<String, String> maps = new HashMap<>();
        maps.put("task_id", taskId);
        maps.put("uid", TUtils.getUserId());
        Call<TaskInfo> call = ApiConfig.getInstants().create(TaskService.class).applyTaskOperate(TUtils.getParams(maps));

        API.getObject(call, OrderInfo.class, new ApiCallBack<OrderInfo>() {
            @Override
            public void onSuccess(int msgCode, String msg, OrderInfo data) {
                ToastUtils.showShort("" + msg);
                EventBus.getDefault().post(new RefreshUnreadCountEvent());
                taskOrderAdapter.remove(position);
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
            }
        });
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
    private void cancleTask(int position, AreaManageOrder orderInfo) {
        if (getActivity() != null) {
            ((BaseActivity) getActivity()).showLoadingBar("取消任务..");
        }
        HashMap<String, String> maps = new HashMap<>();
        maps.put("task_id", orderInfo.getTask_id());
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


    private void getTaskDetail(String task_id) {
        if (getActivity() != null) {
            ((BaseActivity) getActivity()).showLoadingBar("取消中..");
        }
        HashMap<String, String> maps = new HashMap<>();
        maps.put("task_id", task_id);
        Call<TaskInfo> call = ApiConfig.getInstants().create(TaskService.class).getTaskDetail(TUtils.getParams(maps));

        API.getObject(call, TaskInfoItem.class, new ApiCallBack<TaskInfoItem>() {
            @Override
            public void onSuccess(int msgCode, String msg, TaskInfoItem data) {
                ToastUtils.showShort("" + msg);
                if (getActivity() != null) {
                    ((BaseActivity) getActivity()).dismissLoadingBar();
                }

                Bundle bundle = new Bundle();
                bundle.putSerializable(Constans.PASS_OBJECT, data);
                ActivityUtils.startActivity(bundle, DoTaskStepActivity.class);
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
        SysUtils.log(getActivity()+"----"+getContext()+ApiConfig.context);
        if (ApiConfig.context!= null) {
            ((OrderManageActivity)ApiConfig.context).showLoadingBar();
        }

        HashMap<String, String> maps = new HashMap<>();
        maps.put("page", String.valueOf(page));
        if (!TextUtils.isEmpty(status)){
            maps.put("tab",String.valueOf((int)Double.parseDouble(status)));
        }else{
            maps.put("tab","");
        }
        if (!TextUtils.isEmpty(sort)) {
            maps.put("sort", sort);
        }

        maps.put("keywords", "");

        if (ApiConfig.context!= null) {
            if (!TextUtils.isEmpty (((OrderManageActivity)ApiConfig.context).etKey.getEditableText().toString())) {
                maps.put("keywords", ((OrderManageActivity)ApiConfig.context).etKey.getEditableText().toString());
            }
        }

        Call<TaskInfo> call = ApiConfig.getInstants().create(TaskService.class).getOperatOrderList(TUtils.getParams(maps));

        API.getObject(call, OperateOrderList.class, new ApiCallBack<OperateOrderList>() {
            @Override
            public void onSuccess(int msgCode, String msg, OperateOrderList data) {
                if (ApiConfig.context!= null) {
                    ((OrderManageActivity)ApiConfig.context).dismissLoadingBar();
                }

                TUtils.dealReqestData(taskOrderAdapter, recycle, data.list, page, smartRefresh);
            }

            @Override
            public void onFaild(int msgCode, String msg) {
                if (ApiConfig.context != null) {
                    ((OrderManageActivity)ApiConfig.context).dismissLoadingBar();
                }

                TUtils.dealNoReqestData(taskOrderAdapter, recycle, smartRefresh);
            }
        });
    }
}
