package com.task.system.activity;

import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.blankj.utilcode.util.ActivityUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.task.system.Constans;
import com.task.system.R;
import com.task.system.adapters.LotteryMyAdapter;
import com.task.system.api.API;
import com.task.system.api.TaskInfo;
import com.task.system.api.TaskService;
import com.task.system.bean.LotteryList;
import com.task.system.utils.TUtils;
import com.yc.lib.api.ApiCallBack;
import com.yc.lib.api.ApiConfig;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;

/**
 * Author: dengyancheng
 * Date: 2019-09-01 15:56
 * Description: 我的活动
 * History:
 */
public class MyActivityActivity extends BaseActivity {

    @BindView(R.id.recycle)
    RecyclerView recycle;
    @BindView(R.id.smartRefresh)
    SmartRefreshLayout smartRefresh;
    private int page = 1;

    private LotteryMyAdapter homeAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recycle_refresh);
        ButterKnife.bind(this);
        setTitle("我的活动");
        getTaskList();
        homeAdapter = new LotteryMyAdapter(R.layout.adapter_lottery_my_item);
        recycle.setLayoutManager(new LinearLayoutManager(ApiConfig.context));
        recycle.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL) );
        recycle.setAdapter(homeAdapter);


        homeAdapter.setOnItemClickListener((adapter, view, position) -> {
            Bundle about = new Bundle();
            about.putString(Constans.PASS_NAME,homeAdapter.getData().get(position).title);
            about.putString(Constans.ARTICAL_TYPE,Constans.INTERGRAY_CODE);
            about.putString(Constans.PASS_STRING,homeAdapter.getData().get(position).id);
            ActivityUtils.startActivity(about, OpenWebViewActivity.class);
        });


        homeAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                page++;
                getTaskList();
            }
        },recycle);


        smartRefresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                page=1;
                getTaskList();
            }
        });


    }

    private void getTaskList() {
        if (page == 1) {
            showLoadingBar();
        }
        HashMap<String, String> maps = new HashMap<>();
        maps.put("page", String.valueOf(page));
        maps.put("page_size", Constans.PAGE_SIZE);
        Call<TaskInfo> call = ApiConfig.getInstants().create(TaskService.class).getWheelListByUid(TUtils.getParams(maps));

        API.getObject(call, LotteryList.class, new ApiCallBack<LotteryList>() {
            @Override
            public void onSuccess(int msgCode, String msg,LotteryList  data) {
                TUtils.dealReqestData(homeAdapter, recycle, data.list, page);
                dismissLoadingBar();
            }

            @Override
            public void onFaild(int msgCode, String msg) {
                TUtils.dealNoReqestData(homeAdapter, recycle);
                dismissLoadingBar();
            }
        });

    }
}
