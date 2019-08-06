package com.task.system.adapters;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.blankj.utilcode.util.ActivityUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.task.system.Constans;
import com.task.system.R;
import com.task.system.activity.TaskListActivity;
import com.task.system.bean.CatergoryInfo;
import com.yc.lib.api.utils.ImageLoaderUtil;

import java.util.List;

public class TagAdapter extends BaseQuickAdapter<CatergoryInfo, BaseViewHolder> {

    public TagAdapter(int layoutResId) {
        super(layoutResId);
    }

    public TagAdapter(int layoutResId, List<CatergoryInfo> datas) {
        super(layoutResId,datas);
    }

    @Override
    protected void convert(BaseViewHolder helper, CatergoryInfo item) {

        ImageLoaderUtil.loadNormal(item.cover_url,helper.getView(R.id.iv_icon),R.mipmap.icon_tag_default);

        if (!TextUtils.isEmpty(item.title)) {
            ((TextView) helper.getView(R.id.tv_tag)).setText(item.title);
        }

        helper.getView(R.id.cv_tag).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putSerializable(Constans.PASS_OBJECT,item);
                ActivityUtils.startActivity(bundle, TaskListActivity.class);
            }
        });
    }
}
