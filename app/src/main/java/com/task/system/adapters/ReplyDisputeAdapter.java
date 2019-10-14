package com.task.system.adapters;

import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.ActivityUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.task.system.Constans;
import com.task.system.R;
import com.task.system.activity.ApplyDisputeOrReplyActivity;
import com.task.system.bean.DisputeItemInfo;
import com.task.system.bean.OrderInfo;
import com.task.system.enums.UserType;
import com.task.system.utils.TUtils;
import com.task.system.views.NineGridTestLayout;
import com.yc.lib.api.ApiConfig;

import java.util.ArrayList;


/**
 * Author: dengyancheng
 * Date: 2019-09-03 01:04
 * Description: 回复争议
 * History:
 */
public class ReplyDisputeAdapter extends BaseQuickAdapter<DisputeItemInfo, BaseViewHolder> {



    private OrderInfo orderInfo;

    public ReplyDisputeAdapter(int layoutResId, ArrayList arrayList, OrderInfo orderInfo) {
        super(layoutResId,arrayList);
        this.orderInfo = orderInfo;
    }

    @Override
    protected void convert(BaseViewHolder helper, DisputeItemInfo item) {

//        ImageLoaderUtil.loadCircle(item.avatar,helper.getView(R.id.iv_image),R.drawable.ic_default_image);

        Glide.with(ApiConfig.context)
                .load(item.avatar)
                .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                .into((ImageView) helper.getView(R.id.iv_image));


        ((TextView)  helper.getView(R.id.tv_uuid)).setText(""+item.uid);
        ((TextView)  helper.getView(R.id.tv_time)).setText(""+item.create_time);

        ((TextView)  helper.getView(R.id.tv_conten)).setText(""+item.content);




        if (TUtils.getUserInfo().user_type.equals(UserType.USER_TYPE_MEMBER.getType())) {
            helper.getView(R.id.tv_reply).setVisibility(View.GONE);
        }else{
            if (TUtils.getUserInfo().uid.equals(item.uid)){
                helper.getView(R.id.tv_reply).setVisibility(View.VISIBLE);
            }else{
                helper.getView(R.id.tv_reply).setVisibility(View.GONE);
            }
        }

        ((TextView) helper.getView(R.id.tv_reply)).setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);


        helper.getView(R.id.tv_reply).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putSerializable(Constans.PASS_OBJECT, orderInfo);
                ActivityUtils.startActivity(bundle, ApplyDisputeOrReplyActivity.class);
            }
        });


        if (item.images!=null && item.images.size()>0) {
            helper.getView(R.id.nineGrid).setVisibility(View.VISIBLE);
            ((NineGridTestLayout) helper.getView(R.id.nineGrid)).setUrlList(item.images);
        }else{
            helper.getView(R.id.nineGrid).setVisibility(View.GONE);
        }
    }
}
