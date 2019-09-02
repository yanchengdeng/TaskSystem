package com.task.system.fragments;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.blankj.utilcode.util.ActivityUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.task.system.Constans;
import com.task.system.R;
import com.task.system.activity.AwardDetailActivity;
import com.task.system.activity.BaseActivity;
import com.task.system.adapters.AwardItemAdapter;
import com.task.system.api.API;
import com.task.system.api.TaskInfo;
import com.task.system.api.TaskService;
import com.task.system.bean.OrderList;
import com.task.system.utils.TUtils;
import com.yc.lib.api.ApiCallBack;
import com.yc.lib.api.ApiConfig;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.Unbinder;
import retrofit2.Call;

/**
 * Author: dengyancheng
 * Date: 2019-09-01 21:27
 * Description: 中奖记录
 * History:
 */
public class AwardListFragment extends BaseFragment {
    @BindView(R.id.recycle)
    RecyclerView recycle;
    @BindView(R.id.smartRefresh)
    SmartRefreshLayout smartRefresh;
    Unbinder unbinder;


    private int status;
    private int page = 1;

    private AwardItemAdapter taskOrderAdapter;


    @Override
    protected int getAbsLayoutId() {
        return R.layout.fragment_task_list;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {

        status = getArguments().getInt(Constans.PASS_STRING, -1);

        taskOrderAdapter = new AwardItemAdapter(R.layout.adapter_award_item, status);
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
                bundle.putString(Constans.PASS_STRING, taskOrderAdapter.getData().get(position).id);
                ActivityUtils.startActivity(bundle, AwardDetailActivity.class);
            }
        });
    }


    @Override
    protected void initData(View view) {
        super.initData(view);

        getOrderList();
    }

    private void getOrderList() {
        if (getActivity() != null) {
            ((BaseActivity) getActivity()).showLoadingBar();
        }

        HashMap<String, String> maps = new HashMap<>();
        maps.put("page", String.valueOf(page));
        if (status>0) {
            maps.put("status", String.valueOf(status));
        }



        Call<TaskInfo> call = ApiConfig.getInstants().create(TaskService.class).getPrizeListByUid(TUtils.getParams(maps));

        API.getObject(call, OrderList.class, new ApiCallBack<OrderList>() {
            @Override
            public void onSuccess(int msgCode, String msg, OrderList data) {
                if (getActivity() != null) {
                    ((BaseActivity)getActivity()).dismissLoadingBar();
                }

                TUtils.dealReqestData(taskOrderAdapter, recycle, data.list, page, smartRefresh);
            }

            @Override
            public void onFaild(int msgCode, String msg) {
                if (getActivity() != null) {
                    ((BaseActivity) getActivity()).dismissLoadingBar();
                }

                TUtils.dealNoReqestData(taskOrderAdapter, recycle, smartRefresh);
            }
        });
    }
}
