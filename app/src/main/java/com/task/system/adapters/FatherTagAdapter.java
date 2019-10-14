package com.task.system.adapters;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.task.system.utils.PerfectClickListener;
import com.yc.lib.api.ApiConfig;

public class FatherTagAdapter extends BaseQuickAdapter<CatergoryInfo, BaseViewHolder> {

    public FatherTagAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, CatergoryInfo item) {

        if (!TextUtils.isEmpty(item.title)) {
            ((TextView) helper.getView(R.id.tv_tag)).setText(item.title);
        }


        helper.getView(R.id.tv_tag).setOnClickListener(new PerfectClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putSerializable(Constans.PASS_OBJECT,item);
                ActivityUtils.startActivity(bundle, TaskListActivity.class);
            }
        });

        if (item._child!=null && item._child.size()>0){
            ((RecyclerView)helper.getView(R.id.recycle)).setLayoutManager(new GridLayoutManager(ApiConfig.context, 4));
//            ((RecyclerView)helper.getView(R.id.recycle)).addItemDecoration(new GridSpacingItemDecoration(4, 10, true));
            ((RecyclerView)helper.getView(R.id.recycle)).setAdapter(new TagAdapter(R.layout.adapter_tag,item._child));

        }
    }
}
