package com.task.system.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.task.system.Constans;
import com.task.system.R;
import com.task.system.adapters.ScoreRecordAdapter;
import com.task.system.api.API;
import com.task.system.api.TaskInfo;
import com.task.system.api.TaskService;
import com.task.system.bean.OrderListInfo;
import com.task.system.utils.RecycleViewUtils;
import com.task.system.utils.TUtils;
import com.yc.lib.api.ApiCallBack;
import com.yc.lib.api.ApiConfig;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;

/**
 * Author: 邓言诚  Create at : 2019/3/25  00:26
 * Email: yanchengdeng@gmail.com
 * Describle: 我的收益记录
 */
public class MyScoreListActivity extends BaseActivity {


    @BindView(R.id.recycle)
    RecyclerView recycle;
    @BindView(R.id.smartRefresh)
    SmartRefreshLayout smartRefresh;
    @BindView(R.id.tv_start_time)
    TextView tvStartTime;
    @BindView(R.id.ll_start_time)
    LinearLayout llStartTime;
    @BindView(R.id.tv_end_time)
    TextView tvEndTime;
    @BindView(R.id.ll_end_time)
    LinearLayout llEndTime;
    @BindView(R.id.iv_search)
    ImageView ivSearch;
    private int page = 1;

    private TimePickerView start, end;
    private String  start_date, end_date;


    private ScoreRecordAdapter scoreRecordAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_score_list);
        ButterKnife.bind(this);
        setTitle("收益记录");
        scoreRecordAdapter = new ScoreRecordAdapter(R.layout.adapter_score_item);
        recycle.setLayoutManager(new LinearLayoutManager(this));
        recycle.addItemDecoration(RecycleViewUtils.getItemDecorationHorizontal());
        recycle.setAdapter(scoreRecordAdapter);
        scoreRecordAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Bundle bundle = new Bundle();
                bundle.putSerializable(Constans.PASS_OBJECT, scoreRecordAdapter.getItem(position));
                ActivityUtils.startActivity(bundle, ScoreDetailActivity.class);

            }
        });

        scoreRecordAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                page++;
                getScoreRecord();
            }
        },recycle);

        smartRefresh.setOnRefreshListener(refreshLayout -> {
        page=1;
        getScoreRecord();
        });

        initPickView();


        getScoreRecord();


    }

    //初始化日期选择器
    private void initPickView() {

        //时间选择器
        start = new TimePickerBuilder(mContext, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                start_date = TimeUtils.date2String(date, new SimpleDateFormat("yyyy-MM-dd"));
                tvStartTime.setText("" + start_date);
            }
        }).build();


        end = new TimePickerBuilder(mContext, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                end_date = TimeUtils.date2String(date, new SimpleDateFormat("yyyy-MM-dd"));
                tvEndTime.setText("" + end_date);
            }
        }).build();



        llStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start.show();
            }
        });

        llEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                end.show();
            }
        });


        ivSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(start_date)) {
                    ToastUtils.showShort("起始时间不能为空");
                    return;
                }

                if (TextUtils.isEmpty(end_date)) {
                    ToastUtils.showShort("结束时间不能为空");
                    return;
                }

                if (start_date.compareTo(end_date) > 0) {
                    ToastUtils.showShort("结束时间小于起始时间");
                    return;

                }

                page=1;
                getScoreRecord();

            }
        });
    }

    private void getScoreRecord() {
        if (page == 1) {
            showLoadingBar();
        }
        HashMap<String, String> maps = new HashMap<>();
        if (!TextUtils.isEmpty(start_date)) {
            maps.put("start_date", start_date);
        }
        if (!TextUtils.isEmpty(end_date)) {
            maps.put("end_date", end_date);
        }

        maps.put("page_size",Constans.PAGE_SIZE);
        maps.put("page", String.valueOf(page));
        Call<TaskInfo> call = ApiConfig.getInstants().create(TaskService.class).getStaticsList(TUtils.getParams(maps));

        API.getObject(call, OrderListInfo.class, new ApiCallBack<OrderListInfo>() {
            @Override
            public void onSuccess(int msgCode, String msg, OrderListInfo data) {
                dismissLoadingBar();
                TUtils.dealReqestData(scoreRecordAdapter, recycle, data.list, page, smartRefresh);

            }

            @Override
            public void onFaild(int msgCode, String msg) {
                dismissLoadingBar();
                TUtils.dealNoReqestData(scoreRecordAdapter, recycle, smartRefresh);

            }
        });
    }
}
