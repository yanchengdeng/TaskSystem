package com.task.system.activity;

import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

import com.task.system.Constans;
import com.task.system.R;
import com.task.system.api.API;
import com.task.system.api.TaskInfoList;
import com.task.system.api.TaskService;
import com.task.system.bean.MessageItem;
import com.task.system.common.RichTextView;
import com.task.system.utils.TUtils;
import com.yc.lib.api.ApiCallBackList;
import com.yc.lib.api.ApiConfig;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;

public class MessageDetailActivity extends BaseActivity {

    @BindView(R.id.tv_info_title)
    TextView tvInfoTitle;
    @BindView(R.id.tv_date)
    TextView tvDate;
    @BindView(R.id.tv_content)
    RichTextView tvContent;
    private MessageItem messageItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_detail);
        ButterKnife.bind(this);
        setTitle("消息详情");
        messageItem = (MessageItem) getIntent().getExtras().getSerializable(Constans.PASS_OBJECT);

        tvInfoTitle.setText("" + messageItem.title);
        tvDate.setText("" + messageItem.create_time);
        tvContent.setHtml("" + Html.fromHtml(messageItem.content));


        HashMap<String,String> maps = new HashMap<>();
        maps.put("id",messageItem.id);
        Call<TaskInfoList> call = ApiConfig.getInstants().create(TaskService.class).getMessagDetail(TUtils.getParams(maps));

        API.getList(call, String.class, new ApiCallBackList<String>() {
            @Override
            public void onSuccess(int msgCode, String msg, List<String> data) {

            }

            @Override
            public void onFaild(int msgCode, String msg) {

            }
        });
    }
}
