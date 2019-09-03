package com.task.system.activity;

import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.blankj.utilcode.util.ActivityUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.task.system.Constans;
import com.task.system.R;
import com.task.system.adapters.ReplyDisputeAdapter;
import com.task.system.api.API;
import com.task.system.api.TaskInfoList;
import com.task.system.api.TaskService;
import com.task.system.bean.DisputeItemInfo;
import com.task.system.bean.OrderInfo;
import com.task.system.utils.TUtils;
import com.yc.lib.api.ApiCallBackList;
import com.yc.lib.api.ApiConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;

/**
 * Author: dengyancheng
 * Date: 2019-09-02 01:06
 * Description: 整合列表
 * History:
 */
public class DisputeListActivity extends BaseActivity {

    @BindView(R.id.recycle)
    RecyclerView recycle;
    @BindView(R.id.smartRefresh)
    SmartRefreshLayout smartRefresh;
    @BindView(R.id.tv_custome)
    TextView tvCustome;
    @BindView(R.id.tv_next_step)
    TextView tvNextStep;
    private int page = 1;

    private OrderInfo orderInfo;

    private ReplyDisputeAdapter replyDisputeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply_reply);
        ButterKnife.bind(this);
        orderInfo = (OrderInfo) getIntent().getSerializableExtra(Constans.PASS_OBJECT);

        replyDisputeAdapter = new ReplyDisputeAdapter(R.layout.adapter_dispute_item,new ArrayList(),orderInfo);
        recycle.setLayoutManager(new LinearLayoutManager(this));
        recycle.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        recycle.setAdapter(replyDisputeAdapter);


        replyDisputeAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                page++;
                getDisputeList();
            }
        }, recycle);

        smartRefresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                page = 1;
                getDisputeList();
            }
        });


        setTitle(""+orderInfo.title);

        getDisputeList();


    }


    private void getDisputeList() {
        HashMap<String,String> maps = new HashMap<>();
        maps.put("uid",TUtils.getUserId());
        maps.put("order_id", orderInfo.order_id);
        Call<TaskInfoList> call = ApiConfig.getInstants().create(TaskService.class).disputeList(TUtils.getParams(maps));
        API.getList(call, DisputeItemInfo.class, new ApiCallBackList<DisputeItemInfo>() {
            @Override
            public void onSuccess(int msgCode, String msg, List<DisputeItemInfo> datas) {

                TUtils.dealReqestData(replyDisputeAdapter, recycle, datas,page,smartRefresh);

            }

            @Override
            public void onFaild(int msgCode, String msg) {
                TUtils.dealNoReqestData(replyDisputeAdapter, recycle,smartRefresh,page);

            }
        });
    }


    @OnClick({R.id.tv_custome, R.id.tv_next_step})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_custome:
                TUtils.openKf();
                break;
            case R.id.tv_next_step:
                Bundle bundle = new Bundle();
                bundle.putSerializable(Constans.PASS_OBJECT, orderInfo);
                ActivityUtils.startActivity(bundle, ApplyDisputeOrReplyActivity.class);
                break;
        }
    }
}
