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

//任务步骤介绍
public class FragmentStepInfo extends Fragment {

    private RichTextView tvOne,tvTwo,tvThree;

    private TaskInfoItem taskInfoItem;

    private TextView tvEndTime;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail_step,container,false);
        tvOne = view.findViewById(R.id.rich_step_one);
        tvTwo = view.findViewById(R.id.rich_step_two);
        tvThree = view.findViewById(R.id.rich_step_three);
        tvEndTime = view.findViewById(R.id.tv_count_time);
        taskInfoItem = (TaskInfoItem) getArguments().getSerializable(Constans.PASS_OBJECT);


        if (!TextUtils.isEmpty(taskInfoItem.end_time)){
            tvEndTime.setText(String.format(getString(R.string.end_time_tips),TUtils.getEndTimeTips(taskInfoItem.end_time)));
        }

        if (!TextUtils.isEmpty(taskInfoItem.step_1)){
            tvOne.setHtml(taskInfoItem.step_1);
        }

        if (!TextUtils.isEmpty(taskInfoItem.step_2)){
            tvTwo.setHtml(taskInfoItem.step_2);
        }

        if (!TextUtils.isEmpty(taskInfoItem.step_3)){
            tvThree.setHtml(taskInfoItem.step_3);
        }
        return view;
    }
}
