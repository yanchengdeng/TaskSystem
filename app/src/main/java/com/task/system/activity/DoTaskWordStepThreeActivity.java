package com.task.system.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.blankj.utilcode.util.ToastUtils;
import com.task.system.Constans;
import com.task.system.R;
import com.task.system.api.API;
import com.task.system.api.TaskInfoList;
import com.task.system.api.TaskService;
import com.task.system.bean.TaskInfoItem;
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
 * Author: 邓言诚  Create at : 2019/3/23  18:38
 * Email: yanchengdeng@gmail.com
 * Describle: 任务第三bu步
 */
public class DoTaskWordStepThreeActivity extends BaseActivity {

    @BindView(R.id.tv_one)
    TextView tvOne;
    @BindView(R.id.tv_two)
    TextView tvTwo;
    @BindView(R.id.tv_three)
    TextView tvThree;
    @BindView(R.id.et_content)
    EditText etContent;
    @BindView(R.id.tv_custome)
    TextView tvCustome;
    @BindView(R.id.tv_pre_styep)
    TextView tvPreStyep;
    @BindView(R.id.tv_next_step)
    TextView tvNextStep;

    private TaskInfoItem taskInfoItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_do_task_word_step_three);
        ButterKnife.bind(this);
        taskInfoItem = (TaskInfoItem) getIntent().getSerializableExtra(Constans.PASS_OBJECT);
        if (!TextUtils.isEmpty(taskInfoItem.title)){
            setTitle(taskInfoItem.title);
        }
        tvOne.setBackground(getResources().getDrawable(R.drawable.view_unread_gray_bg));
        tvTwo.setBackground(getResources().getDrawable(R.drawable.view_unread_gray_bg));
        tvThree.setBackground(getResources().getDrawable(R.drawable.view_unread_red_bg));
    }

    @OnClick({R.id.tv_custome, R.id.tv_pre_styep, R.id.tv_next_step})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_custome:
                TUtils.openKf();
                break;
            case R.id.tv_pre_styep:
                if (TextUtils.isEmpty(etContent.getEditableText().toString())) {
                    finish();
                }else{
                    showDialogTips();
                }
                break;
            case R.id.tv_next_step:
                doUploadTask();
                break;
        }
    }


    private void doUploadTask() {
        if (TextUtils.isEmpty(etContent.getEditableText().toString())) {
            ToastUtils.showShort(R.string.put_content);
            return;
        }
        showLoadingBar();

        HashMap<String, String> hashMap = new HashMap();
        hashMap.put("order_id", taskInfoItem.order_id);
        hashMap.put("content", etContent.getEditableText().toString());
        Call<TaskInfoList> call = ApiConfig.getInstants().create(TaskService.class).uploadContent(TUtils.getParams(hashMap));

        API.getList(call, String.class, new ApiCallBackList<String>() {

            @Override
            public void onSuccess(int msgCode, String msg, List<String> data) {
                ToastUtils.showShort("" + msg);
                dismissLoadingBar();
                setResult(RESULT_OK);
                finish();
            }

            @Override
            public void onFaild(int msgCode, String msg) {
                dismissLoadingBar();
                ToastUtils.showShort(""+msg);
            }
        });

    }

    private void showDialogTips() {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(this)
                .title("温馨提示")
                .content("退出本次编辑？")
                .positiveText("确定").positiveColor(getResources().getColor(R.color.color_blue)).onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                        finish();
                    }
                }).negativeText("取消").negativeColor(getResources().getColor(R.color.color_info)).onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                });

        MaterialDialog dialog = builder.build();
        dialog.show();
    }
}
