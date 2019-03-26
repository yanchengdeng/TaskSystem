package com.task.system.adapters;

import android.support.annotation.Nullable;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.task.system.R;
import com.task.system.bean.MessageItem;

import java.util.List;

public class MessageAdapter extends BaseQuickAdapter<MessageItem, BaseViewHolder> {
    private String message_type;
    //0  系统  1  个人
    public MessageAdapter(int layoutResId, @Nullable List<MessageItem> data, String message_type) {
        super(layoutResId, data);
        this.message_type = message_type;
    }

    @Override
    protected void convert(BaseViewHolder helper, MessageItem item) {

        if (message_type.equals("0")){
            ((ImageView) helper.getView(R.id.iv_image)).setImageResource(R.mipmap.icon_system_msg);
        }else{
            ((ImageView) helper.getView(R.id.iv_image)).setImageResource(R.mipmap.icon_person_msg);
        }

        ((TextView) helper.getView(R.id.tv_title)).setText("" + item.title);
        ((TextView) helper.getView(R.id.tv_date)).setText("" + item.create_time);
        if (item.is_read == 1) {
            ((TextView) helper.getView(R.id.tv_unread_tips)).setText(" ");
        } else {
            ((TextView) helper.getView(R.id.tv_unread_tips)).setText("【未读】");
        }
    }
}