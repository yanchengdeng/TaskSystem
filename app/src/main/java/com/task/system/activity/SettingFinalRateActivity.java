package com.task.system.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.blankj.utilcode.util.ToastUtils;
import com.task.system.Constans;
import com.task.system.R;
import com.task.system.api.API;
import com.task.system.api.TaskInfoList;
import com.task.system.api.TaskService;
import com.task.system.utils.TUtils;
import com.yc.lib.api.ApiCallBackList;
import com.yc.lib.api.ApiConfig;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;

//设置返佣比例
public class SettingFinalRateActivity extends BaseActivity {

    @BindView(R.id.tv_uid)
    TextView tvUid;
    @BindView(R.id.tv_id)
    TextView tvId;
    @BindView(R.id.tv_rate)
    EditText tvRate;
    @BindView(R.id.tv_rate_tips)
    TextView tvRateTips;
    @BindView(R.id.btn_confirm)
    Button btnConfirm;

    private String chidUID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_final_rate);
        ButterKnife.bind(this);
        setTitle("返佣设置");
        chidUID = getIntent().getStringExtra(Constans.PASS_CHILD_UID);
        if (TextUtils.isEmpty(chidUID)) {
            tvId.setText(TUtils.getUserInfo().uid);
            chidUID = TUtils.getUserInfo().uid;
        } else {
            tvId.setText(chidUID);
        }
        tvRateTips.setText(String.format(getString(R.string.fanyong_rate_tips), "0"));

        tvRate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s.toString())) {
                    btnConfirm.setBackground(getResources().getDrawable(R.drawable.normal_submit_btn_gray));
                    tvRateTips.setText(String.format(getString(R.string.fanyong_rate_tips), "0"));
                } else {
                    btnConfirm.setBackground(getResources().getDrawable(R.drawable.normal_submit_btn_red));

                    tvRateTips.setText(String.format(getString(R.string.fanyong_rate_tips), s.toString()));

                }
            }
        });


        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(tvRate.getEditableText().toString())) {
                    ToastUtils.showShort("请输入返佣比例饿");
                    return;
                }

                doSetRate();
            }
        });
    }

    private void doSetRate() {
        showLoadingBar();
        HashMap<String,String> maps = new HashMap<>();
        maps.put("uid",chidUID);
        maps.put("fanli_ratio",tvRate.getEditableText().toString());
        Call<TaskInfoList> call = ApiConfig.getInstants().create(TaskService.class).setUseScale(TUtils.getParams(maps));

        API.getList(call, String.class, new ApiCallBackList<String>() {
            @Override
            public void onSuccess(int msgCode, String msg, List<String> data) {
                ToastUtils.showShort("" + msg);
                dismissLoadingBar();

            }

            @Override
            public void onFaild(int msgCode, String msg) {
                ToastUtils.showShort("" + msg);
                dismissLoadingBar();
            }
        });
    }
}
