package com.task.system.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
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
import com.task.system.Constans;
import com.task.system.R;
import com.task.system.adapters.StaticAreaBusinessAdapter;
import com.task.system.api.API;
import com.task.system.api.TaskInfo;
import com.task.system.api.TaskService;
import com.task.system.bean.ScoreAccountUserInfo;
import com.task.system.bean.ScoreAcount;
import com.task.system.enums.UserType;
import com.task.system.utils.PerfectClickListener;
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
 * Author: dengyancheng
 * Date: 2019-08-24 16:21
 * Description: 运营数据
 * History:
 */
public class BusinessDataActivity extends BaseActivity {


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
    @BindView(R.id.et_input)
    EditText etInput;
    @BindView(R.id.tv_key_search)
    TextView tvKeySearch;
    @BindView(R.id.tv_uid)
    TextView tvUid;
    @BindView(R.id.tv_host_type)
    TextView tvHostType;
    @BindView(R.id.tv_nickname)
    TextView tvNickname;
    @BindView(R.id.tv_phone)
    TextView tvPhone;
    @BindView(R.id.tv_publish_balance)
    TextView tvPublishBalance;
    @BindView(R.id.tv_history_publish_balance)
    TextView tvHistoryPublishBalance;
    @BindView(R.id.tv_publish_count)
    TextView tvPublishCount;
    @BindView(R.id.tv_finish_task)
    TextView tvFinishTask;
    @BindView(R.id.tv_member_count)
    TextView tvMemberCount;
    @BindView(R.id.tv_accept_nums)
    TextView tvAcceptNums;
    @BindView(R.id.tv_submit_orders)
    TextView tvSubmitOrders;
    @BindView(R.id.tv_finish_orders)
    TextView tvFinishOrders;
    @BindView(R.id.tv_rollback_orders)
    TextView tvRollbackOrders;
    @BindView(R.id.tv_zhengyi_orders)
    TextView tvZhengyiOrders;
    @BindView(R.id.tv_finish_money)
    TextView tvFinishMoney;
    @BindView(R.id.tv_finish_orders_scale)
    TextView tvFinishOrdersScale;
    @BindView(R.id.tv_pass_scale)
    TextView tvPassScale;
    @BindView(R.id.tv_zhengyi_scale)
    TextView tvZhengyiScale;
    @BindView(R.id.ll_static_info)
    LinearLayout llStaticInfo;
    @BindView(R.id.tv_aciton_setting)
    TextView tvAcitonSetting;
    @BindView(R.id.tv_below_nums)
    TextView tvBelowNums;
    @BindView(R.id.tv_new_num)
    TextView tvNewNum;
    @BindView(R.id.ll_below_member_info)
    LinearLayout llBelowMemberInfo;
    @BindView(R.id.recycle)
    RecyclerView recycle;
    @BindView(R.id.hide_view1)
    LinearLayout hideView1;
    @BindView(R.id.hide_view3)
    LinearLayout hideView3;
    @BindView(R.id.hide_view5)
    LinearLayout hideView5;
    @BindView(R.id.hide_view7)
    LinearLayout hideView7;
    @BindView(R.id.tv_member_account_intergray)
    TextView tvMemberAccountIntergray;
    @BindView(R.id.tv_member_address)
    TextView tvMemberAddress;
    @BindView(R.id.ll_for_member)
    LinearLayout llForMember;
    @BindView(R.id.hide_view8)
    LinearLayout hideView8;
    @BindView(R.id.hide_view10)
    LinearLayout hideView10;
    @BindView(R.id.tv_remark)
    TextView tvRemark;
    @BindView(R.id.hide_view11)
    LinearLayout hideView11;
    @BindView(R.id.hide_view2)
    TextView hideView2;
    @BindView(R.id.hide_view4)
    TextView hideView4;
    @BindView(R.id.hide_view6)
    TextView hideView6;
    @BindView(R.id.hide_view9)
    TextView hideView9;
    private String child_uid;//要查看的用户的uid
    public String search_key;//要搜索的用户uid
    private int page = 1;
    private TimePickerView start, end;
    private String start_date, end_date;
    private StaticAreaBusinessAdapter adapterStatic;
    private View viewHeader;
    private TextView tvUidHeader;
    private ScoreAccountUserInfo scoreAccountUserInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_data);
        ButterKnife.bind(this);
        setTitle("运营数据");
        initPickView();
        if (!TextUtils.isEmpty(getIntent().getStringExtra(Constans.PASS_CHILD_UID))) {
            child_uid = getIntent().getStringExtra(Constans.PASS_CHILD_UID);

            //会员
            hideView1.setVisibility(View.GONE);
            hideView2.setVisibility(View.GONE);
            hideView3.setVisibility(View.GONE);
            hideView4.setVisibility(View.GONE);
            hideView5.setVisibility(View.GONE);
            hideView6.setVisibility(View.GONE);
            hideView7.setVisibility(View.GONE);
            hideView8.setVisibility(View.GONE);
            hideView9.setVisibility(View.GONE);
            hideView10.setVisibility(View.GONE);
        } else {
            //区域管理
            hideView11.setVisibility(View.GONE);
            llForMember.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(getIntent().getStringExtra(Constans.PASS_START_TIME))) {
            start_date = getIntent().getStringExtra(Constans.PASS_START_TIME);
            tvStartTime.setText(start_date);
        }

        if (!TextUtils.isEmpty(getIntent().getStringExtra(Constans.PASS_END_TIME))) {
            end_date = getIntent().getStringExtra(Constans.PASS_END_TIME);
            tvEndTime.setText(end_date);
        }

        tvKeySearch.setOnClickListener(new PerfectClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                if (TextUtils.isEmpty(etInput.getEditableText().toString())) {
                    ToastUtils.showShort("请输入用户ID/手机号码");
                    return;
                }
                page = 1;
                getMyAcount();
            }
        });

        tvAcitonSetting.setOnClickListener(new PerfectClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
//                if (scoreAccountUserInfo != null && scoreAccountUserInfo.user_type.equals(UserType.USER_TYPE_AGENT.getType())) {
                if (scoreAccountUserInfo != null) {
                    Bundle bundle = new Bundle();
                    bundle.putString(Constans.PASS_CHILD_UID, child_uid);
                    ActivityUtils.startActivityForResult(bundle, mContext, SettingFinalRateActivity.class, 200);
                }
            }
        });

        initHeaderView();
        recycle.setLayoutManager(new LinearLayoutManager(this));
        recycle.addItemDecoration(RecycleViewUtils.getItemDecorationHorizontal());
        adapterStatic = new StaticAreaBusinessAdapter(R.layout.adapter_area_business_statics_record);
        adapterStatic.addHeaderView(viewHeader);
        recycle.setAdapter(adapterStatic);

        adapterStatic.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Bundle bundle = new Bundle();
                bundle.putString(Constans.PASS_CHILD_UID, adapterStatic.getData().get(position).uid);
                child_uid = adapterStatic.getData().get(position).uid;
                if (!TextUtils.isEmpty(start_date)) {
                    bundle.putString(Constans.PASS_START_TIME, start_date);
                }
                if (!TextUtils.isEmpty(end_date)) {
                    bundle.putString(Constans.PASS_END_TIME, end_date);
                }
                ActivityUtils.startActivity(bundle, BusinessDataActivity.class);
            }
        });

        getMyAcount();
    }

    private void getMyAcount() {
        showLoadingBar();
        HashMap<String, String> maps = new HashMap<>();
        maps.put("page", String.valueOf(page));
        if (!TextUtils.isEmpty(start_date)) {
            maps.put("start_date", start_date);
        }
        if (!TextUtils.isEmpty(end_date)) {
            maps.put("end_date", end_date);
        }
        if (!TextUtils.isEmpty(child_uid)) {
            maps.put("child_uid", child_uid);
        }
        if (!TextUtils.isEmpty(etInput.getEditableText().toString())) {
            maps.put("search_key", etInput.getEditableText().toString());
        }
        maps.put("page_size", String.valueOf(Integer.MAX_VALUE));
        Call<TaskInfo> call = ApiConfig.getInstants().create(TaskService.class).getStatistics(TUtils.getParams(maps));
        API.getObject(call, ScoreAcount.class, new ApiCallBack<ScoreAcount>() {
            @Override
            public void onSuccess(int msgCode, String msg, ScoreAcount data) {
                dismissLoadingBar();
                initData(data);

            }

            @Override
            public void onFaild(int msgCode, String msg) {
                dismissLoadingBar();

            }
        });

    }

    private void initData(ScoreAcount data) {

        if (data.user_info != null) {
            scoreAccountUserInfo = data.user_info;
            llStaticInfo.setVisibility(View.VISIBLE);
            if (!TextUtils.isEmpty(data.user_info.uid)) {
                tvUid.setText(data.user_info.uid);
                child_uid = data.user_info.uid;
            }
            if (!TextUtils.isEmpty(data.user_info.user_type)) {
                tvHostType.setText(TUtils.getUserTypeName(data.user_info.user_type));
                //只有代理 才会显示设置积分返利


                if (data.user_info.user_type.equals(UserType.USER_TYPE_MEMBER.getType())) {
                    //会员
                    tvAcitonSetting.setVisibility(View.GONE);
                    llBelowMemberInfo.setVisibility(View.GONE);
                }
                if (data.user_info.user_type.equals(UserType.USER_TYPE_AGENT.getType())) {

                    if (TUtils.getUserInfo().user_type.equals(UserType.USER_TYPE_AREA.getType())) {
                        //代理
                        tvAcitonSetting.setVisibility(View.VISIBLE);
                    } else {
                        tvAcitonSetting.setVisibility(View.GONE);
                    }
                    llBelowMemberInfo.setVisibility(View.VISIBLE);
                }
                if (data.user_info.user_type.equals(UserType.USER_TYPE_AREA.getType())) {
                    //区域
                    tvAcitonSetting.setVisibility(View.GONE);
                    llBelowMemberInfo.setVisibility(View.VISIBLE);
                }
            }

            tvNickname.setText("昵称：" + data.user_info.username);
            tvPhone.setText("手机：" + data.user_info.mobile);

            tvMemberAccountIntergray.setText("账户积分："+data.user_info.score);

            tvPublishBalance.setText("发布账户余额：" + data.user_info.score);
            tvHistoryPublishBalance.setText("历史发布总金额：" + data.user_info.history_score);
            tvPublishCount.setText("发布任务：" + data.user_statistics.publish_task_num);
            tvFinishTask.setText("完成任务：" + data.user_statistics.complete_task_num);
            tvMemberCount.setText("会员总数：" + data.user_statistics.member_num);
            tvAcceptNums.setText("接受订单：" + data.user_statistics.order_accept);
            tvSubmitOrders.setText("提交订单：" + data.user_statistics.order_submit);
            tvFinishOrders.setText("完成订单：" + data.user_statistics.order_adopt);
            tvRollbackOrders.setText("打回订单：" + data.user_statistics.order_refuse);
            tvZhengyiOrders.setText("争议订单：" + data.user_statistics.order_dispute);
            tvFinishMoney.setText("完成金额：" + data.user_statistics.actual_score);
            tvFinishOrdersScale.setText("成单比例：" + data.user_statistics.order_complete_ratio);
            tvPassScale.setText("通过比例：" + data.user_statistics.order_adopt_ratio);
            tvZhengyiScale.setText("争议比例：" + data.user_statistics.order_dispute_ratio);
            if (!TextUtils.isEmpty(data.user_info.remark)) {
                tvRemark.setText(data.user_info.remark);
            }

            if (!TextUtils.isEmpty(data.user_info.region_name)) {
                tvMemberAddress.setText("所在地："+data.user_info.region_name);
            }


            tvBelowNums.setText("" + data.user_statistics.member_num);
            tvNewNum.setText(String.format(getString(R.string.new_num), data.user_statistics.member_new_num));
        } else {
            llStaticInfo.setVisibility(View.GONE);
        }

        if (data.list != null && data.list.size() > 0) {
            adapterStatic.setNewData(data.list);
            recycle.setVisibility(View.VISIBLE);
            String leve = "会员";
            if (data.level == 1) {
                leve = "下级";
            } else if (data.level == 2) {
                leve = "二级";
            }
            tvUidHeader.setText(leve);
        } else {
            recycle.setVisibility(View.GONE);
        }
    }


    private void initHeaderView() {
        viewHeader = LayoutInflater.from(mContext).inflate(R.layout.header_static_area_business, null, false);
        tvUidHeader = viewHeader.findViewById(R.id.tv_below_class_tittle);


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

                page = 1;
                getMyAcount();

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 200 && resultCode == RESULT_OK) {
            String marks = data.getStringExtra(Constans.PASS_STRING);
            if (!TextUtils.isEmpty(marks)) {
//                tvRemark.setText(marks);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
