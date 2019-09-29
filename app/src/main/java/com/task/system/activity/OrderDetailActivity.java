package com.task.system.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.task.system.Constans;
import com.task.system.R;
import com.task.system.api.API;
import com.task.system.api.TaskInfo;
import com.task.system.api.TaskInfoIgnoreBody;
import com.task.system.api.TaskService;
import com.task.system.bean.OrderDetalInfo;
import com.task.system.bean.OrderInfo;
import com.task.system.bean.SimpleBeanInfo;
import com.task.system.common.RichTextView;
import com.task.system.utils.PerfectClickListener;
import com.task.system.utils.TUtils;
import com.yc.lib.api.ApiCallBack;
import com.yc.lib.api.ApiConfig;
import com.yc.lib.api.utils.SysUtils;

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
    @BindView(R.id.tv_status_pass)
    TextView tvStatusPass;
    @BindView(R.id.tv_adjust_recharge)
    TextView tvAdjustRecharge;
    @BindView(R.id.tv_status_pass_not)
    TextView tvStatusPassNot;
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
                if (orderDetalInfo != null) {
                    Bundle orderdatas = new Bundle();
                    orderdatas.putString(Constans.PASS_STRING, orderDetalInfo.getTask_id());
                    ActivityUtils.startActivity(orderdatas, LinkOrdersActivity.class);
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
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
        llBottom.setVisibility(View.VISIBLE);
        if (!TextUtils.isEmpty(data.getOrder_score())) {
            tvMoney.setText(data.getOrder_score());
        }

        if (!TextUtils.isEmpty(data.getTask_id())) {
            tvTaskId.setText("任务ID:" + data.getTask_id());
        }

        if (!TextUtils.isEmpty(data.getTask_title())) {
            tvTaskName.setText(data.getTask_title());
        }


        tvOrderNum.setText("订单编号：" + data.getOrder_id());

        tvUserId.setText("用户id：" + data.getUid());

        tvOrderStatus.setText("订单状态：" + data.getStatus_title());

        richText.setHtml(data.getStep());


        tvTimeStart.setText("开始时间：" + data.getStart_time());
        tvTimeEnd.setText("结束时间：" + data.getEnd_time());
        tvTimeCreate.setText("创建时间：" + data.getCreate_time());

        if (data.getDispute_status() != 0) {
            tvLookArgue.setVisibility(View.VISIBLE);
        } else {
            tvLookArgue.setVisibility(View.GONE);
        }

        if (data.getStatus() == 2 || data.getStatus() == 3) {
            tvStatusPass.setVisibility(View.VISIBLE);
            tvStatusPassNot.setVisibility(View.VISIBLE);
            tvAdjustRecharge.setVisibility(View.VISIBLE);
        }else if (data.getStatus()==8){
            //显示调整理由
            tvStatusPassNot.setVisibility(View.VISIBLE);
            tvStatusPassNot.setText("调整理由");
            tvStatusPass.setVisibility(View.GONE);
            tvAdjustRecharge.setVisibility(View.GONE);
        }else {
            tvStatusPass.setVisibility(View.GONE);
            tvStatusPassNot.setVisibility(View.GONE);
            tvAdjustRecharge.setVisibility(View.GONE);
        }
    }

    /**
     * "status": 0,"title": "已中止" --显示编辑按钮
     * "status": "1","title": "展示中"--中止任务 订单数据
     * "status": "2","title": "草稿箱"--显示编辑按钮
     * "status": "3","title": "待审核"--显示 取消任务
     * "status": "4","title": "关联订单
     * "status": "5","title": "弹出理由  关联订单
     * "status": "6",已完结  关联订单
     * "status": "7","已超时  关联订单
     */


    //4-通过，5-不通过，8-修改价格
    @OnClick({R.id.tv_task_detail, R.id.tv_status_pass, R.id.tv_adjust_recharge, R.id.tv_status_pass_not, R.id.tv_look_argue})
    public void onViewClicked(View view) {
        OrderInfo item = new OrderInfo();
        item.title = orderDetalInfo.getTask_title();
        item.order_id = orderDetalInfo.getOrder_id();
        item.score = orderDetalInfo.getActual_score();
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constans.PASS_OBJECT, item);
        switch (view.getId()) {
            case R.id.tv_task_detail:
                if (orderDetalInfo != null) {
                    Bundle bundleDetail = new Bundle();
                    bundleDetail.putString(Constans.PASS_STRING, orderDetalInfo.getTask_id());
                    ActivityUtils.startActivity(bundleDetail, TaskDetailActivity.class);
                }
                break;
            case R.id.tv_status_pass:
                    doSubmitDispute();
//                bundle.putInt(Constans.PASS_STRING, 4);
//                ActivityUtils.startActivity(bundle, SetOrderStatusActivity.class);
                break;
            case R.id.tv_adjust_recharge:
                bundle.putInt(Constans.PASS_STRING, 8);
                ActivityUtils.startActivity(bundle, SetOrderStatusActivity.class);
                break;
            case R.id.tv_status_pass_not:
                if (orderDetalInfo.getStatus()==8){
                    ActivityUtils.startActivity(bundle,UpdateResonListActivity.class);
                }else {
                    bundle.putInt(Constans.PASS_STRING, 5);
                    ActivityUtils.startActivity(bundle, SetOrderStatusActivity.class);
                }
                break;
            case R.id.tv_look_argue:
                ActivityUtils.startActivity(bundle, DisputeListActivity.class);
                break;
        }
    }

    /**
     * 审核通过直接调接口
     */
    private void doSubmitDispute() {
        showLoadingBar();
        HashMap<String, String> hashMap = new HashMap();
        hashMap.put("uid", TUtils.getUserId());
        hashMap.put("order_id", orderId);
        hashMap.put("status","4");
        Call<TaskInfoIgnoreBody> call = ApiConfig.getInstants().create(TaskService.class).setOrderStatus(TUtils.getParams(hashMap));

        API.getObjectIgnoreBody(call, new ApiCallBack<SimpleBeanInfo>() {

            @Override
            public void onSuccess(int msgCode, String msg, SimpleBeanInfo data) {
                ToastUtils.showShort("提交成功");
                dismissLoadingBar();
                tvStatusPass.setVisibility(View.GONE);
                tvStatusPassNot.setVisibility(View.GONE);
                tvAdjustRecharge.setVisibility(View.GONE);

            }

            @Override
            public void onFaild(int msgCode, String msg) {
                dismissLoadingBar();
                SysUtils.showToast(msg+"");
            }
        });
    }
}
