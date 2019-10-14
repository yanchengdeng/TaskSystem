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
import com.task.system.activity.ApplyDisputeOrReplyActivity;
import com.task.system.activity.DisputeListActivity;
import com.task.system.activity.DoTaskStepActivity;
import com.task.system.activity.MainActivity;
import com.task.system.activity.TaskDetailActivity;
import com.task.system.activity.UpdateResonListActivity;
import com.task.system.adapters.TaskOrderAdapter;
import com.task.system.api.API;
import com.task.system.api.TaskInfo;
import com.task.system.api.TaskInfoList;
import com.task.system.api.TaskService;
import com.task.system.bean.OrderInfo;
import com.task.system.bean.OrderList;
import com.task.system.bean.TaskInfoItem;
import com.task.system.event.RefreshUnreadCountEvent;
import com.task.system.utils.TUtils;
import com.yc.lib.api.ApiCallBack;
import com.yc.lib.api.ApiCallBackList;
import com.yc.lib.api.ApiConfig;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.Unbinder;
import retrofit2.Call;


public class TaskListFragment extends BaseFragment {
    @BindView(R.id.recycle)
    RecyclerView recycle;
    @BindView(R.id.smartRefresh)
    SmartRefreshLayout smartRefresh;
    Unbinder unbinder;


    public int status;
    private int page = 1;
    private String sort;

    private TaskOrderAdapter taskOrderAdapter;

    private boolean isStarted;


    @Override
    protected int getAbsLayoutId() {
        return R.layout.fragment_task_list;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        status = getArguments().getInt(Constans.PASS_STRING, -1);
        EventBus.getDefault().register(this);

        isStarted = true;
        taskOrderAdapter = new TaskOrderAdapter(R.layout.adapter_task_order_item, status);
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
                bundle.putString(Constans.PASS_STRING, taskOrderAdapter.getData().get(position).task_id);
                ActivityUtils.startActivity(bundle, TaskDetailActivity.class);
            }
        });


        /** * 1——待工作
         * 2——待提交 // 待审核--客服审核
         * 3——待审核 // 待审核--客户审核
         * 4——已通过
         * 5——未通过
         * 6——已作废
         * 7——已超时
         * 8  修改价格  查看理由*/
        taskOrderAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                int status = taskOrderAdapter.getData().get(position).status;
                OrderInfo item = taskOrderAdapter.getData().get(position);
                //待工作
                if (status == 1 || status == 2) {
                    if (view.getId() == R.id.tv_cancle_task) {
                        giveUpTask(position, taskOrderAdapter.getData().get(position));
                    } else if (view.getId() == R.id.tv_going_work_task) {
                        getTaskDetail(taskOrderAdapter.getData().get(position).task_id);
                    }
                }

                //未通过  4  5   6  7   可添加一个 立即申请的功能按钮
                if (status > 3) {
                    if (view.getId() == R.id.tv_look_for_reason) {
                        if (TextUtils.isEmpty(taskOrderAdapter.getData().get(position).remark)) {
                            taskOrderAdapter.getData().get(position).remark = "请联系客服";
                        }
                        if (status==8){
//                            OrderInfo item = new OrderInfo();
//                            item.title = orderDetalInfo.getTask_title();
//                            item.order_id = orderDetalInfo.getOrder_id();
//                            item.score = orderDetalInfo.getActual_score();
                            Bundle bundle = new Bundle();
                            bundle.putSerializable(Constans.PASS_OBJECT, item);
                            ActivityUtils.startActivity(bundle, UpdateResonListActivity.class);
                        }else {
                            showDialogTips(taskOrderAdapter.getData().get(position).remark);
                        }
                    } else if (view.getId() == R.id.tv_else_function) {
                        //立即申请
                        applyTask(position, taskOrderAdapter.getItem(position).task_id);
                    } else if (view.getId() == R.id.tv_apply_dispute) {
                        if (status == 5) {
                            /**
                             * 当dispute_status=0时：显示提出争议按钮-跳转到提出争议页面
                             * 【order/dispute】，通过此接口可以提交争议
                             *
                             *
                             * 当dispute_status=1 、2、 3时：显示查看争议-跳转到争议列表
                             * 争议内容：这个使用接口【争议内容列表 order/disputeList】
                             */
                            if (item.dispute_status == 0) {
                                Bundle bundle = new Bundle();
                                bundle.putSerializable(Constans.PASS_OBJECT, item);
                                ActivityUtils.startActivity(bundle, ApplyDisputeOrReplyActivity.class);
                            } else if (item.dispute_status == 1 || item.dispute_status == 2 || item.dispute_status == 3) {
                                Bundle bundle = new Bundle();
                                bundle.putSerializable(Constans.PASS_OBJECT, item);
                                ActivityUtils.startActivity(bundle, DisputeListActivity.class);
                            }
                        }
                    }
                }
            }
        });

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Object event) {
        if (event instanceof RefreshUnreadCountEvent) {
                page=1;
                getOrderList();
        }
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
                    ((MainActivity) getActivity()).dismissLoadingBar();
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
    private void giveUpTask(int position, OrderInfo orderInfo) {
        if (getActivity() != null) {
            ((MainActivity) getActivity()).showLoadingBar("取消中..");
        }
        HashMap<String, String> maps = new HashMap<>();
        maps.put("task_id", orderInfo.task_id);
        maps.put("order_id", orderInfo.order_id);
        Call<TaskInfoList> call = ApiConfig.getInstants().create(TaskService.class).giveUpTaskOperate(TUtils.getParams(maps));

        API.getList(call, String.class, new ApiCallBackList<String>() {
            @Override
            public void onSuccess(int msgCode, String msg, List<String> data) {
                ToastUtils.showShort("" + msg);
                taskOrderAdapter.remove(position);
                EventBus.getDefault().post(new RefreshUnreadCountEvent());
                if (taskOrderAdapter.getData().size() == 0) {
                    TUtils.dealNoReqestData(taskOrderAdapter, recycle, smartRefresh);
                }
                if (getActivity() != null) {
                    ((MainActivity) getActivity()).dismissLoadingBar();
                }
            }

            @Override
            public void onFaild(int msgCode, String msg) {
                ToastUtils.showShort("" + msg);
                if (getActivity() != null) {
                    ((MainActivity) getActivity()).dismissLoadingBar();
                }
            }
        });
    }


    private void getTaskDetail(String task_id) {
        if (getActivity() != null) {
            ((MainActivity) getActivity()).showLoadingBar("取消中..");
        }
        HashMap<String, String> maps = new HashMap<>();
        maps.put("task_id", task_id);
        Call<TaskInfo> call = ApiConfig.getInstants().create(TaskService.class).getTaskDetail(TUtils.getParams(maps));

        API.getObject(call, TaskInfoItem.class, new ApiCallBack<TaskInfoItem>() {
            @Override
            public void onSuccess(int msgCode, String msg, TaskInfoItem data) {
                ToastUtils.showShort("" + msg);
                if (getActivity() != null) {
                    ((MainActivity) getActivity()).dismissLoadingBar();
                }

                Bundle bundle = new Bundle();
                bundle.putSerializable(Constans.PASS_OBJECT, data);
                ActivityUtils.startActivity(bundle, DoTaskStepActivity.class);
            }

            @Override
            public void onFaild(int msgCode, String msg) {
                ToastUtils.showShort("" + msg);
                if (getActivity() != null) {
                    ((MainActivity) getActivity()).dismissLoadingBar();
                }
            }
        });
    }

    @Override
    protected void initData(View view) {
        super.initData(view);

        getOrderList();
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    private void getOrderList() {
        if (getActivity() != null) {
            ((MainActivity) getActivity()).showLoadingBar();
        }

        HashMap<String, String> maps = new HashMap<>();
        maps.put("page", String.valueOf(page));
        if (status > 0) {
            maps.put("status", String.valueOf(status));
        }

        if (!TextUtils.isEmpty(sort)) {
            maps.put("sort", sort);
        }

        Call<TaskInfo> call = ApiConfig.getInstants().create(TaskService.class).getOrderList(TUtils.getParams(maps));

        API.getObject(call, OrderList.class, new ApiCallBack<OrderList>() {
            @Override
            public void onSuccess(int msgCode, String msg, OrderList data) {
                if (getActivity() != null) {
                    ((MainActivity) getActivity()).dismissLoadingBar();
                }

                TUtils.dealReqestData(taskOrderAdapter, recycle, data.list, page, smartRefresh);
            }

            @Override
            public void onFaild(int msgCode, String msg) {
                if (getActivity() != null) {
                    ((MainActivity) getActivity()).dismissLoadingBar();
                }

                TUtils.dealNoReqestData(taskOrderAdapter, recycle, smartRefresh);
            }
        });
    }
}
