package com.task.system.adapters;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.task.system.R;
import com.task.system.bean.AreaManageIitem;

import java.util.ArrayList;


public class AreaManageAdapter extends BaseQuickAdapter<AreaManageIitem, BaseViewHolder> {



    public AreaManageAdapter(int layoutResId, ArrayList arrayList) {
        super(layoutResId,arrayList);
    }

    @Override
    protected void convert(BaseViewHolder helper, AreaManageIitem item) {

        ((TextView) helper.getView(R.id.tv_status)).setText(item.title);

        if (!TextUtils.isEmpty(item.count)){
            ((TextView) helper.getView(R.id.tv_message_num)).setText(item.count);
            helper.getView(R.id.tv_message_num).setVisibility(View.VISIBLE);
        }else{
            helper.getView(R.id.tv_message_num).setVisibility(View.INVISIBLE);
        }
        Glide.with(mContext).load(item.image).apply(RequestOptions.errorOf(R.mipmap.wait_check)).into((ImageView) helper.getView(R.id.iv_icon));
    }
}
