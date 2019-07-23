package com.task.system.activity;

import android.content.Intent;
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
import com.task.system.api.TaskInfo;
import com.task.system.api.TaskInfoList;
import com.task.system.api.TaskService;
import com.task.system.bean.ScaleMark;
import com.task.system.utils.TUtils;
import com.yc.lib.api.ApiCallBack;
import com.yc.lib.api.ApiCallBackList;
import com.yc.lib.api.ApiConfig;
import com.yc.lib.api.utils.SysUtils;

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
    @BindView(R.id.et_mark)
    EditText etMark;
    @BindView(R.id.tv_max_rate)
    TextView tvMaxRate;

    private String chidUID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_final_rate);
        ButterKnife.bind(this);
        setTitle("备注设置");
        chidUID = getIntent().getStringExtra(Constans.PASS_CHILD_UID);

        tvId.setText("" + chidUID);
        tvRateTips.setText(String.format(getString(R.string.fanyong_rate_tips), "0"));

        etMark.addTextChangedListener(new TextWatcher() {
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
                } else {
                    btnConfirm.setBackground(getResources().getDrawable(R.drawable.normal_submit_btn_red));
                }
            }
        });


        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(etMark.getEditableText().toString())) {
                    ToastUtils.showShort("请输入备注信息");
                    return;
                }

                doSetRate();
            }
        });


        getRate();
    }

    //获取比例
    private void getRate() {
        showLoadingBar();
        HashMap<String, String> maps = new HashMap<>();
        maps.put("child_uid", chidUID);
        maps.put("operate", "edit");
        Call<TaskInfo> call = ApiConfig.getInstants().create(TaskService.class).getUserOptionEdit(TUtils.getParams(maps));

        API.getObject(call, ScaleMark.class, new ApiCallBack<ScaleMark>() {
            @Override
            public void onSuccess(int msgCode, String msg, ScaleMark data) {
                dismissLoadingBar();
                if (data!=null && data.remark!=null && !TextUtils.isEmpty(data.remark.value)){
                    etMark.setText(data.remark.value);
                }

            }

            @Override
            public void onFaild(int msgCode, String msg) {
                dismissLoadingBar();
            }
        });
    }


    private void doSetRate() {
        showLoadingBar();
        HashMap<String, String> maps = new HashMap<>();
        maps.put("id", chidUID);
        if (!TextUtils.isEmpty(etMark.getEditableText().toString())) {
            maps.put("remark", etMark.getEditableText().toString());
        }
        maps.put("fanli_ratio", tvRate.getEditableText().toString());
        Call<TaskInfoList> call = ApiConfig.getInstants().create(TaskService.class).setUseScale(TUtils.getParams(maps));

        API.getList(call, String.class, new ApiCallBackList<String>() {
            @Override
            public void onSuccess(int msgCode, String msg, List<String> data) {
                SysUtils.showToast("" + msg);
                dismissLoadingBar();
                Intent intent = new Intent();
                intent.putExtra(Constans.PASS_STRING,etMark.getEditableText().toString());
                setResult(RESULT_OK,intent);
                finish();

            }

            @Override
            public void onFaild(int msgCode, String msg) {
                SysUtils.showToast("" + msg);
                dismissLoadingBar();
            }
        });
    }
}
