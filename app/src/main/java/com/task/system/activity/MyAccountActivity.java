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
import com.task.system.adapters.StaticContributeAdapter;
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

public class MyAccountActivity extends BaseActivity {

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
    @BindView(R.id.tv_uid)
    TextView tvUid;
    @BindView(R.id.tv_host_type)
    TextView tvHostType;
    @BindView(R.id.tv_nickname)
    TextView tvNickname;
    @BindView(R.id.tv_phone)
    TextView tvPhone;
    @BindView(R.id.tv_accout_intergray)
    TextView tvAccoutIntergray;
    @BindView(R.id.tv_history_money)
    TextView tvHistoryMoney;
    @BindView(R.id.tv_create_value)
    TextView tvCreateValue;
    @BindView(R.id.tv_finish_task)
    TextView tvFinishTask;
    @BindView(R.id.tv_remark)
    TextView tvRemark;
    @BindView(R.id.ll_static_info)
    LinearLayout llStaticInfo;
    @BindView(R.id.tv_aciton_setting)
    TextView tvAcitonSetting;
    @BindView(R.id.tv_below_nums)
    TextView tvBelowNums;
    @BindView(R.id.tv_agent_finish_task)
    TextView tvAgentFinishTask;
    @BindView(R.id.tv_agent_create_value)
    TextView tvAgentCreateValue;
    @BindView(R.id.ll_below_member_info)
    LinearLayout llBelowMemberInfo;
    @BindView(R.id.recycle)
    RecyclerView recycle;
    @BindView(R.id.tv_new_num)
    TextView tvNewNum;
    @BindView(R.id.tv_key_search)
    TextView tvKeySearch;
    @BindView(R.id.tv_below_seconde_nums)
    TextView tvBelowSecondeNums;
    @BindView(R.id.tv_second_new_num)
    TextView tvSecondNewNum;
    private String child_uid;//要查看的用户的uid
    public String search_key;//要搜索的用户uid
    private int page = 1;
    private TimePickerView start, end;
    private String start_date, end_date;
    private StaticContributeAdapter adapterStatic;
    private View viewHeader;
    private TextView tvUidHeader;
    private ScoreAccountUserInfo scoreAccountUserInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_acount);
        ButterKnife.bind(this);
        setTitle("我的团队");
        initPickView();
        if (!TextUtils.isEmpty(getIntent().getStringExtra(Constans.PASS_CHILD_UID))) {
            child_uid = getIntent().getStringExtra(Constans.PASS_CHILD_UID);
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
        adapterStatic = new StaticContributeAdapter(R.layout.adapter_score_static_record);
        adapterStatic.addHeaderView(viewHeader);
        recycle.setAdapter(adapterStatic);

        adapterStatic.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Bundle bundle = new Bundle();
                bundle.putString(Constans.PASS_CHILD_UID, adapterStatic.getData().get(position).uid);
                if (!TextUtils.isEmpty(adapterStatic.getData().get(position).uid)){
                    child_uid = adapterStatic.getData().get(position).uid;
                }else{
                    child_uid = adapterStatic.getData().get(position).id;
                }
                bundle.putString(Constans.PASS_CHILD_UID,child_uid);
                if (!TextUtils.isEmpty(start_date)) {
                    bundle.putString(Constans.PASS_START_TIME, start_date);
                }
                if (!TextUtils.isEmpty(end_date)) {
                    bundle.putString(Constans.PASS_END_TIME, end_date);
                }
                ActivityUtils.startActivity(bundle, MyAccountActivity.class);
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
        maps.put("page_size", "50");
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
                    llBelowMemberInfo.setVisibility(View.VISIBLE);
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
                    tvAcitonSetting.setVisibility(View.VISIBLE);
                    llBelowMemberInfo.setVisibility(View.VISIBLE);
                }
            }

            tvNickname.setText("昵称：" + data.user_info.username);
            tvPhone.setText("手机：" + data.user_info.mobile);
            tvAccoutIntergray.setText("账户积分：" + data.user_info.score);
            tvHistoryMoney.setText("历史积分：" + data.user_info.history_score);
            tvCreateValue.setText("创造价值：" + data.user_statistics.task_score);
            tvFinishTask.setText("完成任务：" + data.user_statistics.task_num);
            tvRemark.setText("" + data.user_info.remark);
            tvBelowNums.setText("" + data.user_statistics.member_num);
            tvNewNum.setText(String.format(getString(R.string.new_num), data.user_statistics.member_new_num));

            tvBelowSecondeNums.setText("" + data.user_statistics.second_member_num);
            tvSecondNewNum.setText(String.format(getString(R.string.new_num), data.user_statistics.second_member_new_num));
            tvAgentCreateValue.setText(String.format(getString(R.string.agent_create_value), data.user_statistics.task_score));
            tvAgentFinishTask.setText(String.format(getString(R.string.agent_finish_tast), data.user_statistics.task_num));


        } else {
            llStaticInfo.setVisibility(View.GONE);
        }

        if (data.list != null && data.list.size() > 0) {
            adapterStatic.setNewData(data.list);
            recycle.setVisibility(View.VISIBLE);
            tvUidHeader.setText(data.member_level+"");
        } else {
            recycle.setVisibility(View.GONE);
        }
    }


    private void initHeaderView() {
        viewHeader = LayoutInflater.from(mContext).inflate(R.layout.header_static_member_contribute, null, false);
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
                tvRemark.setText(marks);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
