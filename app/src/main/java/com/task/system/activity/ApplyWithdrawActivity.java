package com.task.system.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
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
import com.task.system.api.TaskInfoList;
import com.task.system.api.TaskService;
import com.task.system.bean.AcountList;
import com.task.system.utils.TUtils;
import com.yc.lib.api.ApiCallBackList;
import com.yc.lib.api.ApiConfig;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;

/**
 * Author: 邓言诚  Create at : 2019/3/23  01:07
 * Email: yanchengdeng@gmail.com
 * Describle: 申请提现
 */
public class ApplyWithdrawActivity extends BaseActivity {

    @BindView(R.id.tv_money_left)
    TextView tvMoneyLeft;
    @BindView(R.id.et_for_withdraw)
    EditText etForWithdraw;
    @BindView(R.id.tv_acount)
    TextView tvAcount;
    @BindView(R.id.btn_withdraw)
    Button btnWithdraw;

    private String cardId ;//账号id

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply_withdraw);
        ButterKnife.bind(this);
        setTitle("申请提现");
        if (!TextUtils.isEmpty(TUtils.getUserInfo().deposit_cash)) {
            tvMoneyLeft.setText(TUtils.getUserInfo().deposit_cash + "元");
        }


        etForWithdraw.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(cardId) && !TextUtils.isEmpty(s.toString())) {
                    btnWithdraw.setBackground(getResources().getDrawable(R.drawable.normal_submit_btn_red));
                } else {
                    btnWithdraw.setBackground(getResources().getDrawable(R.drawable.normal_submit_btn_gray));
                }

            }
        });
    }

    @OnClick({R.id.tv_acount, R.id.btn_withdraw})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_acount:
                ActivityUtils.startActivityForResult(ApplyWithdrawActivity.this, BankListActivity.class, 200);
                break;
            case R.id.btn_withdraw:
                if (TextUtils.isEmpty(etForWithdraw.getEditableText().toString())) {
                    ToastUtils.showShort("请输入提申请提现金额");
                } else if (TextUtils.isEmpty(cardId)) {
                    ToastUtils.showShort("亲选择提现账户");
                } else {
                    doWithdraw();
                }
                break;
        }
    }

    private void doWithdraw() {
        showLoadingBar("提交中...");
        HashMap<String, String> hashMap = new HashMap();
        hashMap.put("deposit_cash", etForWithdraw.getEditableText().toString());
        hashMap.put("card_id",cardId);
        Call<TaskInfoList> call = ApiConfig.getInstants().create(TaskService.class).deposite(TUtils.getParams(hashMap));

        API.getList(call, String.class, new ApiCallBackList<String>() {

            @Override
            public void onSuccess(int msgCode, String msg, List<String> data) {
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
        if (requestCode == 200) {
            if (resultCode == RESULT_OK) {
                if (data != null && data.getSerializableExtra(Constans.PASS_OBJECT)!=null) {
                    AcountList.Accouninfo    accouninfo = (AcountList.Accouninfo) data.getSerializableExtra(Constans.PASS_OBJECT);
                    cardId = accouninfo.id;
                    tvAcount.setText(""+accouninfo.account);
                    if (accouninfo!=null && !TextUtils.isEmpty(etForWithdraw.getEditableText().toString())) {
                        btnWithdraw.setBackground(getResources().getDrawable(R.drawable.normal_submit_btn_red));
                    } else {
                        btnWithdraw.setBackground(getResources().getDrawable(R.drawable.normal_submit_btn_gray));
                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
