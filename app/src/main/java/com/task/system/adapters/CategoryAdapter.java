package com.task.system.adapters;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.task.system.R;
import com.task.system.bean.CatergoryInfo;


public class CategoryAdapter extends BaseQuickAdapter<CatergoryInfo, BaseViewHolder> {


    private boolean isWorkBg = true;
    public CategoryAdapter(int layoutResId) {
        super(layoutResId);
    }

    public CategoryAdapter(int layoutResId,boolean isWorkBg) {
        super(layoutResId);
        this.isWorkBg = isWorkBg;
    }

    @Override
    protected void convert(BaseViewHolder helper, CatergoryInfo item) {


        if (!TextUtils.isEmpty(item.title)) {
            ((TextView) helper.getView(R.id.tv_title)).setText(item.title);
        }


//        if (item.pid>0){
//            helper.getView(R.id.tv_indictor).setVisibility(View.INVISIBLE);
//        }else {
//            helper.getView(R.id.tv_indictor).setVisibility(View.VISIBLE);
            if (item.isSelected) {
                helper.getView(R.id.ll_bg).setBackgroundColor(mContext.getResources().getColor(R.color.white));
                helper.getView(R.id.tv_indictor).setVisibility(isWorkBg?View.VISIBLE:View.INVISIBLE);
                ((TextView) ((TextView) helper.getView(R.id.tv_title))).setTextColor(mContext.getResources().getColor(R.color.red));
            } else {
                if (isWorkBg) {
                    helper.getView(R.id.ll_bg).setBackgroundColor(mContext.getResources().getColor(R.color.list_divider_color));
                    helper.getView(R.id.tv_indictor).setVisibility(View.INVISIBLE);
                }else{
                    helper.getView(R.id.ll_bg).setBackgroundColor(mContext.getResources().getColor(R.color.white));
                    helper.getView(R.id.tv_indictor).setVisibility(View.INVISIBLE);
                }
                ((TextView) ((TextView) helper.getView(R.id.tv_title))).setTextColor(mContext.getResources().getColor(R.color.color_tittle));
            }
        }

//    }
}
