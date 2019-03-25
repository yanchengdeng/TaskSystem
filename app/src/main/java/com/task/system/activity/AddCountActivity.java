package com.task.system.activity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.blankj.utilcode.util.ToastUtils;
import com.task.system.R;
import com.task.system.api.API;
import com.task.system.api.TaskInfoList;
import com.task.system.api.TaskService;
import com.task.system.bean.UserInfo;
import com.task.system.enums.MobileCode;
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
 * Author: 邓言诚  Create at : 2019/3/23  01:55
 * Email: yanchengdeng@gmail.com
 * Describle: 添加账号
 */
public class AddCountActivity extends BaseActivity {

    @BindView(R.id.et_name)
    EditText etName;
    @BindView(R.id.tv_account_type)
    TextView tvAccountType;
    @BindView(R.id.et_for_withdraw)
    EditText etForWithdraw;
    @BindView(R.id.et_code)
    EditText etCode;
    @BindView(R.id.tv_get_code)
    TextView tvGetCode;
    @BindView(R.id.btn_withdraw)
    Button btnWithdraw;

    // 账户类型，1-支付宝账户，2-银行卡账户
    private String account_type;
    private CountDownTimer countDownTimer;
    private int selectedIndex = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_count);
        ButterKnife.bind(this);
        setTitle("绑定账号");
        countDownTimer = new CountDownTimer(60 * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                tvGetCode.setText(millisUntilFinished / 1000 + "s" + "重新获取");
                tvGetCode.setEnabled(false);
                tvGetCode.setTextColor(getResources().getColor(R.color.color_info));

            }

            @Override
            public void onFinish() {
                tvGetCode.setText(getString(R.string.get_sms_code));
                tvGetCode.setEnabled(true);
                tvGetCode.setTextColor(getResources().getColor(R.color.red));

            }
        };

        etName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                checkCanAdd();
            }
        });

        etCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                checkCanAdd();
            }
        });

        etCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                checkCanAdd();
            }
        });

    }

    private void checkCanAdd() {
        if (
                !TextUtils.isEmpty(etCode.getEditableText().toString())
                        && !TextUtils.isEmpty(etName.getEditableText().toString())
                        && !TextUtils.isEmpty(etForWithdraw.getEditableText().toString())
                        && !TextUtils.isEmpty(account_type)
        ) {
            btnWithdraw.setBackground(getResources().getDrawable(R.drawable.normal_submit_btn_red));
        } else {
            btnWithdraw.setBackground(getResources().getDrawable(R.drawable.normal_submit_btn_gray));
        }
    }

    @OnClick({R.id.tv_account_type, R.id.tv_get_code, R.id.btn_withdraw})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_account_type:
                showSelectCount();
                break;
            case R.id.tv_get_code:
                getCode();
                break;
            case R.id.btn_withdraw:
                if (TextUtils.isEmpty(etName.getEditableText().toString())) {
                    ToastUtils.showShort(R.string.input_your_name);
                } else {
                    if (TextUtils.isEmpty(account_type)) {
                        ToastUtils.showShort(R.string.please_choose_account_type);
                    } else {
                        if (TextUtils.isEmpty(etForWithdraw.getEditableText().toString())) {
                            ToastUtils.showShort(R.string.please_input_account);
                        } else if (TextUtils.isEmpty(etCode.getEditableText().toString())) {
                            ToastUtils.showShort(R.string.code_tips);
                        } else {
                            doAddCount(etName.getEditableText().toString(), etForWithdraw.getEditableText().toString(), etCode.getEditableText().toString());
                        }
                    }
                }
                break;
        }
    }

    /**
     * account
     * * account_name
     * * account_type
     * * mobile_code
     */
    private void doAddCount(String name, String account, String code) {

        showLoadingBar();
        HashMap<String, String> hashMap = new HashMap();
        hashMap.put("account", account);
        hashMap.put("account_name", name);
        hashMap.put("account_type", account_type);
        hashMap.put("mobile_code", code);
        Call<TaskInfoList> call = ApiConfig.getInstants().create(TaskService.class).addCard(TUtils.getParams(hashMap));

        API.getList(call, UserInfo.class, new ApiCallBackList<UserInfo>() {
            @Override
            public void onSuccess(int msgCode, String msg, List<UserInfo> data) {
                ToastUtils.showShort(msg);
                dismissLoadingBar();
                setResult(RESULT_OK);
                finish();
            }

            @Override
            public void onFaild(int msgCode, String msg) {
                dismissLoadingBar();
                ToastUtils.showShort(msg);
            }
        });

    }

    private void showSelectCount() {
        String[] items = {"支付宝", "银行卡"};
        new MaterialDialog.Builder(this)
                .title("请选择账户类型")// 标题
                .items(items)// 列表数据
                .itemsCallbackSingleChoice(selectedIndex, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                        // 如果使用 alwaysCallSingleChoiceCallback() 方法，在这边返回 false 将不允许新选择的单选按钮被选中。
                        account_type = which == 0 ? "1" : "2";
                        tvAccountType.setText("" + text);
                        selectedIndex = which;
                        checkCanAdd();
                        return true;
                    }
                })
                .positiveText("选择")
                .alwaysCallSingleChoiceCallback()
                .show();// 显示对话框
    }

    private void getCode() {
        HashMap<String, String> hashMap = new HashMap();
        hashMap.put("code_type", MobileCode.MOBILE_CODE_ADD_BANK_ACCOUNT.getType());
        hashMap.put("mobile", TUtils.getUserInfo().mobile);
        Call<TaskInfoList> call = ApiConfig.getInstants().create(TaskService.class).getCode(TUtils.getParams(hashMap));

        API.getList(call, UserInfo.class, new ApiCallBackList<UserInfo>() {
            @Override
            public void onSuccess(int msgCode, String msg, List<UserInfo> data) {
                ToastUtils.showShort(msg);
                countDownTimer.start();
            }

            @Override
            public void onFaild(int msgCode, String msg) {
                ToastUtils.showShort(msg);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }

    }
}
