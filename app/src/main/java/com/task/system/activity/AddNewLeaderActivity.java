package com.task.system.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.task.system.Constans;
import com.task.system.R;
import com.task.system.api.API;
import com.task.system.api.TaskInfo;
import com.task.system.api.TaskService;
import com.task.system.bean.AddLeaderInfo;
import com.task.system.bean.CityInfo;
import com.task.system.bean.SimpleBeanInfo;
import com.task.system.utils.PerfectClickListener;
import com.task.system.utils.TUtils;
import com.yc.lib.api.ApiCallBack;
import com.yc.lib.api.ApiConfig;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;

/**
 * Author: 邓言诚  Create at : 2019/3/27  02:20
 * Email: yanchengdeng@gmail.com
 * Describle: 新增代理
 */
public class AddNewLeaderActivity extends BaseActivity {

    @BindView(R.id.et_id)
    EditText etId;
    @BindView(R.id.et_nickname)
    EditText etNickname;
    @BindView(R.id.et_mark)
    EditText etMark;
    @BindView(R.id.et_phone)
    EditText etPhone;
    @BindView(R.id.et_password)
    EditText etPassword;
    @BindView(R.id.tv_select_region)
    TextView tvSelectRegion;
    @BindView(R.id.btn_confirm)
    Button btnConfirm;
    private CityInfo cityInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_leader);
        ButterKnife.bind(this);
        setTitle(getString(R.string.add_new_memner));

        addLisen();


        btnConfirm.setOnClickListener(new PerfectClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {

                addLeader();
            }
        });


        getLeaderInfo();
    }

    private void getLeaderInfo() {
        showLoadingBar();

        HashMap<String, String> maps = new HashMap<>();
        maps.put("operate", "add");
        Call<TaskInfo> call = ApiConfig.getInstants().create(TaskService.class).getUserOptionAdd(TUtils.getParams(maps));
        API.getObject(call, AddLeaderInfo.class, new ApiCallBack<AddLeaderInfo>() {
            @Override
            public void onSuccess(int msgCode, String msg, AddLeaderInfo data) {
                dismissLoadingBar();
                if (data.id != null) {
                    if (!TextUtils.isEmpty(data.id.value)) {
                        etId.setText(data.id.value);
                    }
                }

                if (data.username != null) {
                    if (!TextUtils.isEmpty(data.username.value)) {
                        etNickname.setText(data.username.value);
                    }
                }
            }

            @Override
            public void onFaild(int msgCode, String msg) {
                dismissLoadingBar();

            }
        });

    }

    private void addLisen() {
        etId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                doCheckId();
            }
        });
        etNickname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                doCheckId();
            }
        });

        etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                doCheckId();
            }
        });

        etPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                doCheckId();
            }
        });

        tvSelectRegion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityUtils.startActivityForResult(AddNewLeaderActivity.this, CityListActivity.class, 100);
            }
        });


    }


    private void doCheckId() {
        if (!TextUtils.isEmpty(etId.getEditableText().toString())
                && etId.getEditableText().toString().length() == 8
                && !TextUtils.isEmpty(etNickname.getEditableText().toString())
                && !TextUtils.isEmpty(etPhone.getEditableText().toString())
                && !TextUtils.isEmpty(etPassword.getEditableText().toString()
        )
                && !TextUtils.isEmpty(tvSelectRegion.getText().toString())) {
            btnConfirm.setBackground(getResources().getDrawable(R.drawable.normal_submit_btn_red));
        } else {
            btnConfirm.setBackground(getResources().getDrawable(R.drawable.normal_submit_btn_gray));
        }
    }

    /**
     * Id
     * 必须代理ID
     * mobile 手机号码
     * username 用户名
     * password 必须密码
     * remark 可选备注
     * fanli_ratio 必须
     */
    private void addLeader() {


        if (TextUtils.isEmpty(etId.getEditableText().toString())){
            ToastUtils.showShort("请输入id");
            return;
        }


        if (TextUtils.isEmpty(etPhone.getEditableText().toString())){
            ToastUtils.showShort("请输入手机号");
            return;
        }

        if (TextUtils.isEmpty(etPassword.getEditableText().toString())){
            ToastUtils.showShort("请输入密码");
            return;
        }

        if (cityInfo == null || TextUtils.isEmpty(cityInfo.region_id)){
            ToastUtils.showShort("请选择城市");
            return;
        }


        HashMap<String, String> map = new HashMap<>();
        map.put("id", etId.getEditableText().toString());
        map.put("mobile", etPhone.getEditableText().toString());
        map.put("username", etNickname.getEditableText().toString());
        map.put("password", etPassword.getEditableText().toString());
        if (!TextUtils.isEmpty(etMark.getEditableText().toString())) {
            map.put("remark", etMark.getEditableText().toString());
        }

        if (cityInfo != null && !TextUtils.isEmpty(cityInfo.region_id)) {
            map.put("region_id", cityInfo.region_id);
        }
        showLoadingBar();

        Call<TaskInfo> call = ApiConfig.getInstants().create(TaskService.class).addLeader(TUtils.getParams(map));
        API.getObject(call, SimpleBeanInfo.class, new ApiCallBack<SimpleBeanInfo>() {
            @Override
            public void onSuccess(int msgCode, String msg, SimpleBeanInfo data) {
                ToastUtils.showShort("" + msg);
                dismissLoadingBar();
                finish();

            }

            @Override
            public void onFaild(int msgCode, String msg) {
                ToastUtils.showShort("" + msg);
                dismissLoadingBar();

            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 100) {
            if (resultCode == RESULT_OK) {
                if (data != null && data.getExtras() != null && data.getExtras().getSerializable(Constans.PASS_OBJECT) != null) {
                    cityInfo = (CityInfo) data.getExtras().getSerializable(Constans.PASS_OBJECT);
                    if (!TextUtils.isEmpty(cityInfo.region_name)) {
                        tvSelectRegion.setText(cityInfo.region_name);
                        doCheckId();
                    }
                }
                super.onActivityResult(requestCode, resultCode, data);
            }
        }
    }
}
