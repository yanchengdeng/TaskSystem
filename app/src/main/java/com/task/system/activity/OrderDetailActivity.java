package com.task.system.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.ActivityUtils;
import com.task.system.Constans;
import com.task.system.R;
import com.task.system.api.API;
import com.task.system.api.TaskInfo;
import com.task.system.api.TaskService;
import com.task.system.bean.OrderDetalInfo;
import com.task.system.common.RichTextView;
import com.task.system.utils.PerfectClickListener;
import com.task.system.utils.TUtils;
import com.yc.lib.api.ApiCallBack;
import com.yc.lib.api.ApiConfig;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;

/**
 * Author: dengyancheng
 * Date: 2019-09-12 21:22
 * Description: 运营商订单详情
 * History:订单详情
 */
public class OrderDetailActivity extends BaseActivity {

    @BindView(R.id.tv_money)
    TextView tvMoney;
    @BindView(R.id.tv_task_id)
    TextView tvTaskId;
    @BindView(R.id.tv_task_detail)
    TextView tvTaskDetail;
    @BindView(R.id.tv_task_name)
    TextView tvTaskName;
    @BindView(R.id.tv_order_num)
    TextView tvOrderNum;
    @BindView(R.id.tv_user_id)
    TextView tvUserId;
    @BindView(R.id.tv_order_status)
    TextView tvOrderStatus;
    @BindView(R.id.rich_text)
    RichTextView richText;
    @BindView(R.id.tv_time_start)
    TextView tvTimeStart;
    @BindView(R.id.tv_time_end)
    TextView tvTimeEnd;
    @BindView(R.id.tv_time_create)
    TextView tvTimeCreate;
    @BindView(R.id.tv_status)
    TextView tvStatus;
    @BindView(R.id.tv_adjust_recharge)
    TextView tvAdjustRecharge;
    @BindView(R.id.tv_look_argue)
    TextView tvLookArgue;
    @BindView(R.id.ll_bottom)
    LinearLayout llBottom;


    private String orderId;

    private OrderDetalInfo orderDetalInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        ButterKnife.bind(this);
        orderId = getIntent().getStringExtra(Constans.PASS_STRING);


        setTitle("订单详情");
        tvRightFunction.setVisibility(View.VISIBLE);
        tvRightFunction.setText("关联订单>>");
        tvRightFunction.setOnClickListener(new PerfectClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                if (orderDetalInfo!=null) {
                    Bundle orderdatas = new Bundle();
                    orderdatas.putString(Constans.PASS_STRING, orderDetalInfo.getTask_id());
                    ActivityUtils.startActivity(orderdatas, LinkOrdersActivity.class);
                }
            }
        });

        getOrderDetail();


    }

    private void getOrderDetail() {
        showLoadingBar();
        HashMap<String, String> maps = new HashMap<>();
        if (!TextUtils.isEmpty(TUtils.getUserId())) {
            maps.put("uid", TUtils.getUserId());
        }
        maps.put("order_id", orderId);
        Call<TaskInfo> call = ApiConfig.getInstants().create(TaskService.class).getOrderDetail(TUtils.getParams(maps));

        API.getObject(call, OrderDetalInfo.class, new ApiCallBack<OrderDetalInfo>() {
            @Override
            public void onSuccess(int msgCode, String msg, OrderDetalInfo data) {
                dismissLoadingBar();
                orderDetalInfo = data;
                initData(data);

            }

            @Override
            public void onFaild(int msgCode, String msg) {
                dismissLoadingBar();

            }
        });
    }

    private void initData(OrderDetalInfo data) {
        if (!TextUtils.isEmpty(data.getActual_score())) {
            tvMoney.setText(data.getActual_score());
        }

        if (!TextUtils.isEmpty(data.getTask_id())) {
            tvTaskId.setText("任务ID:"+data.getTask_id());
        }

        if (!TextUtils.isEmpty(data.getTask_title())) {
            tvTaskName.setText(data.getTask_title());
        }


            tvOrderNum.setText("订单编号："+data.getOrder_id());

        tvUserId.setText("用户id："+data.getUid());

        tvOrderStatus.setText("订单状态："+ data.getStatus_title());

        richText.setHtml(data.getStep());


        tvTimeStart.setText("开始时间："+data.getStart_time());
        tvTimeEnd.setText("结束时间："+data.getEnd_time());
        tvTimeCreate.setText("创建时间："+data.getCreate_time());

        if (data.getDispute_status()!=0){
            tvLookArgue.setVisibility(View.VISIBLE);
        }



    }
/**
       "status": 0,"title": "已中止" --显示编辑按钮
         "status": "1","title": "展示中"--中止任务 订单数据
         "status": "2","title": "草稿箱"--显示编辑按钮
         "status": "3","title": "待审核"--显示 取消任务
         "status": "4","title": "关联订单
                 "status": "5","title": "弹出理由  关联订单
                 "status": "6",已完结  关联订单
         "status": "7","已超时  关联订单
                 */

    @OnClick({R.id.tv_task_detail,R.id.tv_status, R.id.tv_adjust_recharge, R.id.tv_look_argue})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_task_detail:
                if (orderDetalInfo!=null){
                    Bundle bundle = new Bundle();
                    bundle.putString(Constans.PASS_STRING, orderDetalInfo.getTask_id());
                    ActivityUtils.startActivity(bundle, TaskDetailActivity.class);
                }
                break;
            case R.id.tv_status:
                break;
            case R.id.tv_adjust_recharge:
                break;
            case R.id.tv_look_argue:
                break;
        }
    }
}
