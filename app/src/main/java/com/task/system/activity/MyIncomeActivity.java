package com.task.system.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.SPUtils;
import com.google.gson.Gson;
import com.task.system.Constans;
import com.task.system.R;
import com.task.system.api.API;
import com.task.system.api.TaskInfo;
import com.task.system.api.TaskService;
import com.task.system.bean.UserInfo;
import com.task.system.utils.TUtils;
import com.yc.lib.api.ApiCallBack;
import com.yc.lib.api.ApiConfig;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;

/**
 * Author: 邓言诚  Create at : 2019/3/23  00:41
 * Email: yanchengdeng@gmail.com
 * Describle: 我的收益
 */
public class MyIncomeActivity extends BaseActivity {

    @BindView(R.id.tv_avable_monet)
    TextView tvAvableMonet;
    @BindView(R.id.tv_money_withdraw)
    TextView tvMoneyWithdraw;
    @BindView(R.id.tv_history_money)
    TextView tvHistoryMoney;
    @BindView(R.id.tv_apply_withdraw)
    TextView tvApplyWithdraw;
    @BindView(R.id.tv_income_records)
    TextView tvIncomeRecords;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_income);
        ButterKnife.bind(this);
        setTitle("我的收益");

    }


    @Override
    protected void onResume() {
        super.onResume();
        getUserDetail();
    }

    private void getUserDetail() {
        HashMap<String, String> hashMap = new HashMap();
        hashMap.put("uid", TUtils.getUserId());
        Call<TaskInfo> call = ApiConfig.getInstants().create(TaskService.class).getUserInfo(TUtils.getParams(hashMap));

        API.getObject(call, UserInfo.class, new ApiCallBack<UserInfo>() {
            @Override
            public void onSuccess(int msgCode, String msg, UserInfo data) {
                SPUtils.getInstance().put(Constans.USER_INFO, new Gson().toJson(data));
                if (!TextUtils.isEmpty(data.deposit_cash)) {
                    tvAvableMonet.setText(data.deposit_cash);
                }

                if (!TextUtils.isEmpty(data.frozen_score)) {
                    tvMoneyWithdraw.setText(data.frozen_score);
                }

                if (!TextUtils.isEmpty(data.history_score)) {
                    tvHistoryMoney.setText(data.history_score);
                }

            }

            @Override
            public void onFaild(int msgCode, String msg) {
            }
        });
    }

    @OnClick({R.id.tv_apply_withdraw, R.id.tv_income_records})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_apply_withdraw:
                ActivityUtils.startActivity(ApplyWithdrawActivity.class);
                break;
            case R.id.tv_income_records:
                ActivityUtils.startActivity(MyScoreListActivity.class);
                break;
        }
    }
}
