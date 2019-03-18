package com.task.system.adapters;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.task.system.R;
import com.task.system.bean.HomeMenu;

public class MenuAdapter extends BaseQuickAdapter<HomeMenu, BaseViewHolder> {

    public MenuAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, HomeMenu item) {


        if (!TextUtils.isEmpty(item.title)) {
            ((TextView) helper.getView(R.id.tv_title)).setText(item.title);
        }

        if (item.isSelected){
            helper.getView(R.id.tv_indictor).setVisibility(View.VISIBLE);
            helper.getView(R.id.ll_bg).setBackgroundColor(mContext.getResources().getColor(R.color.list_divider_color));
        }else{
            helper.getView(R.id.tv_indictor).setVisibility(View.INVISIBLE);
            helper.getView(R.id.ll_bg).setBackgroundColor(mContext.getResources().getColor(R.color.white));
        }

    }
}
