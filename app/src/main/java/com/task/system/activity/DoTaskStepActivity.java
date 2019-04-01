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
import com.task.system.bean.TaskInfoItem;
import com.task.system.common.RichTextView;
import com.task.system.utils.TUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

//任务第一步
public class DoTaskStepActivity extends BaseActivity {

    @BindView(R.id.tv_one)
    TextView tvOne;
    @BindView(R.id.tv_two)
    TextView tvTwo;
    @BindView(R.id.tv_three)
    TextView tvThree;
    @BindView(R.id.rich_step_one)
    RichTextView richStepOne;
    @BindView(R.id.rich_step_two)
    RichTextView richStepTwo;
    @BindView(R.id.rich_step_three)
    RichTextView richStepThree;
    @BindView(R.id.tv_custome)
    TextView tvCustome;
    @BindView(R.id.tv_next_step)
    TextView tvNextStep;
    private TaskInfoItem taskInfoItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_do_task_step);
        ButterKnife.bind(this);
        setTitle(getString(R.string.do_task_title));

        taskInfoItem = (TaskInfoItem) getIntent().getSerializableExtra(Constans.PASS_OBJECT);


        if (!TextUtils.isEmpty(taskInfoItem.step_1)){
            richStepOne.setHtml(taskInfoItem.step_1);
        }

        if (!TextUtils.isEmpty(taskInfoItem.step_2)){
            richStepTwo.setHtml(taskInfoItem.step_2);
        }

        if (!TextUtils.isEmpty(taskInfoItem.step_3)){
            richStepThree.setHtml(taskInfoItem.step_3);
        }


    }

    @OnClick({R.id.tv_custome, R.id.tv_next_step})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_custome:
                TUtils.openKf();
                break;
            case R.id.tv_next_step:
                ActivityUtils.startActivityForResult(getIntent().getExtras(),DoTaskStepActivity.this,DoTaskWorkStepTwoActivity.class,200);
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode==200){
            if (resultCode==RESULT_OK){
                setResult(RESULT_OK);
                finish();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
