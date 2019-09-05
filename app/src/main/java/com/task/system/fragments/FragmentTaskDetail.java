package com.task.system.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.task.system.Constans;
import com.task.system.R;
import com.task.system.bean.TaskInfoItem;
import com.task.system.common.RichTextView;
import com.task.system.utils.TUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

//任务描述
public class FragmentTaskDetail extends Fragment {

    @BindView(R.id.tv_money)
    TextView tvMoney;
    @BindView(R.id.tv_end_time)
    TextView tvEndTime;
    @BindView(R.id.tv_wait_time)
    TextView tvWaitTime;
    @BindView(R.id.tv_easy_or)
    TextView tvEasyOr;
    @BindView(R.id.tv_deposit_score)
    TextView tvDepositScore;
    @BindView(R.id.tv_left_num)
    TextView tvLeftNum;
    @BindView(R.id.tv_task_id)
    TextView tvTaskId;
    @BindView(R.id.rich_text)
    RichTextView richText;
    Unbinder unbinder;
    private TaskInfoItem taskInfoItem;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail_info, container, false);

        taskInfoItem = (TaskInfoItem) getArguments().getSerializable(Constans.PASS_OBJECT);
        unbinder = ButterKnife.bind(this, view);
        initData();
        return view;
    }

    private void initData() {
        if (!TextUtils.isEmpty(taskInfoItem.market_score)) {
            tvMoney.setText(getString(R.string.money_unit) + taskInfoItem.market_score);
        }

        if (!TextUtils.isEmpty(taskInfoItem.audit_date)) {
            tvWaitTime.setText(String.format(getString(R.string.wait_time), taskInfoItem.audit_date));
        }

        if (!TextUtils.isEmpty(taskInfoItem.end_time)) {
            tvEndTime.setText(String.format(getString(R.string.end_time), taskInfoItem.end_time));
        }

        tvTaskId.setText(String.format(getString(R.string.task_id), taskInfoItem.id));

        tvEasyOr.setText(String.format(getString(R.string.easy_or), taskInfoItem.difficulty));

        tvDepositScore.setText(String.format(getString(R.string.deposit_score), String.valueOf(taskInfoItem.deposit_score)));

        if (TextUtils.isEmpty(taskInfoItem.storages)) {
            taskInfoItem.storages = "0";
        }
        tvLeftNum.setText(String.format(getString(R.string.left_num), taskInfoItem.storages));


        if (!TextUtils.isEmpty(taskInfoItem.description)) {
            richText.setHtml(taskInfoItem.description);
        }

        richText.setOnImageClickListener(new RichTextView.ImageClickListener() {
            @Override
            public void onImageClick(String imageUrl, String[] imageUrls, int position) {

                TUtils.openImageViews(imageUrls, position);

            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
