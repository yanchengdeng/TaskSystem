package com.task.system.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.task.system.Constans;
import com.task.system.R;
import com.task.system.adapters.ScoreDetailAdapter;
import com.task.system.api.API;
import com.task.system.api.TaskInfoList;
import com.task.system.api.TaskService;
import com.task.system.bean.OrderItem;
import com.task.system.bean.ScoreDetailInfo;
import com.task.system.utils.RecycleViewUtils;
import com.task.system.utils.TUtils;
import com.yc.lib.api.ApiCallBackList;
import com.yc.lib.api.ApiConfig;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;

/**
 * Author: 邓言诚  Create at : 2019/3/26  22:14
 * Email: yanchengdeng@gmail.com
 * Describle: 积分详情
 */
public class ScoreDetailActivity extends BaseActivity {

    @BindView(R.id.recycle)
    RecyclerView recycle;
    @BindView(R.id.smartRefresh)
    SmartRefreshLayout smartRefresh;
    private OrderItem orderItem;

    private ScoreDetailAdapter scoreDetailAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score_detail);
        ButterKnife.bind(this);
        setTitle("收益详情");
        orderItem = (OrderItem) getIntent().getSerializableExtra(Constans.PASS_OBJECT);
        scoreDetailAdapter = new ScoreDetailAdapter(R.layout.adapter_score_detail_item);
        recycle.setLayoutManager(new LinearLayoutManager(this));
        recycle.addItemDecoration(RecycleViewUtils.getItemDecorationHorizontal());
        recycle.setAdapter(scoreDetailAdapter);


        showLoadingBar();

        HashMap<String, String> maps = new HashMap<>();
        maps.put("log_id", orderItem.log_id);
        Call<TaskInfoList> call = ApiConfig.getInstants().create(TaskService.class).getStaticDetail(TUtils.getParams(maps));

        API.getList(call, ScoreDetailInfo.class, new ApiCallBackList<ScoreDetailInfo>() {
            @Override
            public void onSuccess(int msgCode, String msg, List<ScoreDetailInfo> data) {
                dismissLoadingBar();
                TUtils.dealReqestData(scoreDetailAdapter, recycle, data, 1, smartRefresh);

            }

            @Override
            public void onFaild(int msgCode, String msg) {
                dismissLoadingBar();
                TUtils.dealNoReqestData(scoreDetailAdapter, recycle, smartRefresh);
            }
        });

    }
}
