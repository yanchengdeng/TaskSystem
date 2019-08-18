package com.task.system.adapters;

import android.widget.TextView;

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


    }
}
