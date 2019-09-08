package com.task.system.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.blankj.utilcode.util.ActivityUtils;
import com.task.system.Constans;
import com.task.system.R;
import com.task.system.api.API;
import com.task.system.api.TaskInfo;
import com.task.system.api.TaskService;
import com.task.system.bean.AddressInfo;
import com.task.system.bean.AwardItem;
import com.task.system.utils.TUtils;
import com.yc.lib.api.ApiCallBack;
import com.yc.lib.api.ApiConfig;
import com.yc.lib.api.utils.SysUtils;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;

/**
 * Author;// dengyancheng
 * Date;// 2019-09-01 21;//42
 * Description;// 中奖详情
 * History;//
 */
public class AwardDetailActivity extends BaseActivity {

    @BindView(R.id.tv_award_name)
    TextView tvAwardName;
    @BindView(R.id.tv_award_time_tips)
    TextView tvAwardTimeTips;
    @BindView(R.id.tv_award_info_tips)
    TextView tvAwardInfoTips;
    @BindView(R.id.tv_award_info)
    TextView tvAwardInfo;
    @BindView(R.id.tv_award_status_tips)
    TextView tvAwardStatusTips;
    @BindView(R.id.tv_add_address)
    TextView tvAddAddress;
    @BindView(R.id.tv_address)
    TextView tvAddress;
    @BindView(R.id.tv_address_name)
    TextView tvAddressName;
    @BindView(R.id.tv_address_phone)
    TextView tvAddressPhone;
    @BindView(R.id.tv_award_exchange)
    TextView tvAwardExchange;
    @BindView(R.id.tv_award_time)
    TextView tvAwardTime;
    @BindView(R.id.tv_award_status)
    TextView tvAwardStatus;
    private String id;

    private AwardItem awardItem;

    private String addressId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_award_detail);
        ButterKnife.bind(this);

        setTitle("中奖信息");
        id = getIntent().getStringExtra(Constans.PASS_STRING);
        if (TextUtils.isEmpty(id)) {
            return;
        }
        getAwardDetail();


        //添加地址
        tvAddAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (awardItem != null) {
                    Bundle bundle = new Bundle();
                    bundle.putBoolean(Constans.PASS_STRING, true);
                    ActivityUtils.startActivityForResult(bundle, AwardDetailActivity.this, MyAddressActivity.class, 100);
                }
            }
        });


        //兑换
        tvAwardExchange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (awardItem != null) {
                    if (awardItem.can_set_address==1 ) {
                        SysUtils.showToast("请选择收货地址");
                    } else {
                        doExchage();
                    }
                }
            }
        });

    }

    //兑换
    private void doExchage() {
        showLoadingBar();

        HashMap<String, String> maps = new HashMap();

        maps.put("log_id", id);

        if (!TextUtils.isEmpty(addressId)){
            maps.put("address_id", addressId);
        }
        Call<TaskInfo> call = ApiConfig.getInstants().create(TaskService.class).setAwardAddress(TUtils.getParams(maps));

        API.getObject(call, AwardItem.class, new ApiCallBack<AwardItem>() {
            @Override
            public void onSuccess(int msgCode, String msg, AwardItem data) {
                dismissLoadingBar();
                SysUtils.showToast("已兑换");
                finish();

            }

            @Override
            public void onFaild(int msgCode, String msg) {
                dismissLoadingBar();
                SysUtils.showToast(""+msg);
            }
        });

    }

    private void getAwardDetail() {
        showLoadingBar();

        HashMap<String, String> maps = new HashMap();
        maps.put("log_id", id);

        Call<TaskInfo> call = ApiConfig.getInstants().create(TaskService.class).getAwardDetail(TUtils.getParams(maps));

        API.getObject(call, AwardItem.class, new ApiCallBack<AwardItem>() {
            @Override
            public void onSuccess(int msgCode, String msg, AwardItem data) {
                dismissLoadingBar();
                awardItem = data;
                initData(data);


            }

            @Override
            public void onFaild(int msgCode, String msg) {
                dismissLoadingBar();
            }
        });


    }

    private void initData(AwardItem data) {
        if (!TextUtils.isEmpty(data.title)) {
            tvAwardName.setText(data.title);
        }

        if (!TextUtils.isEmpty(data.create_time)) {
            tvAwardTime.setText(data.create_time);
        }

        if (!TextUtils.isEmpty(data.prize_name)) {
            tvAwardInfo.setText(data.prize_name);
        }

        if (!TextUtils.isEmpty(data.status_title)) {
            tvAwardStatus.setText(data.status_title);
        }
        if (data.status == 1) {
            tvAwardExchange.setVisibility(View.VISIBLE);
            if (data.can_set_address == 1) {
                tvAddAddress.setVisibility(View.VISIBLE);
            } else {
                if (data.address!=null && !TextUtils.isEmpty(data.address.address)) {
                        tvAddress.setText(""+data.address.address);
                        tvAddressPhone.setText(""+data.address.mobile);
                        tvAddressName.setText(""+data.address.name);
                }
            }
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            if (data.getSerializableExtra(Constans.PASS_OBJECT) != null) {
                AddressInfo addressInfo = (AddressInfo) data.getSerializableExtra(Constans.PASS_OBJECT);
                if (addressInfo != null && !TextUtils.isEmpty(addressInfo.getAddress())) {
                    tvAddress.setText(addressInfo.getAddress());
                    tvAddressPhone.setText("" + addressInfo.getContact_mobile());
                    tvAddressName.setText("" + addressInfo.getContact_name());
                    addressId = addressInfo.getId();
                }
            }
        }
    }
}
