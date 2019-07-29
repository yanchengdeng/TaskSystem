package com.task.system.adapters;

import androidx.cardview.widget.CardView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.task.system.R;
import com.task.system.bean.AcountList;
import com.task.system.utils.TUtils;


public class BankAdapter extends BaseQuickAdapter<AcountList.Accouninfo, BaseViewHolder> {

    private int[] colors = {R.color.random_1,R.color.random_2,R.color.random_3,R.color.random_4,R.color.random_5,
            R.color.random_6,R.color.random_7,R.color.random_8,R.color.random_9,R.color.random_1,R.color.random_2,R.color.random_3,R.color.random_4,R.color.random_5,
            R.color.random_6,R.color.random_7,R.color.random_8,R.color.random_9,R.color.random_1,R.color.random_2,R.color.random_3,R.color.random_4,R.color.random_5,
            R.color.random_6,R.color.random_7,R.color.random_8,R.color.random_9,R.color.random_1,R.color.random_2,R.color.random_3,R.color.random_4,R.color.random_5,
            R.color.random_6,R.color.random_7,R.color.random_8,R.color.random_9,R.color.random_1,R.color.random_2,R.color.random_3,R.color.random_4,R.color.random_5,
            R.color.random_6,R.color.random_7,R.color.random_8,R.color.random_9};

    public BankAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, AcountList.Accouninfo item) {

        ((CardView) helper.getView(R.id.card)).setCardBackgroundColor(colors[helper.getLayoutPosition()]);

        if (!TextUtils.isEmpty(item.account)) {
            ((TextView) helper.getView(R.id.tv_acount_num)).setText(TUtils.getHideAccount(item.account));
        }

        if (!TextUtils.isEmpty(item.account_name)) {
            ((TextView) helper.getView(R.id.tv_account)).setText(item.account_name);
        }
        if (!TextUtils.isEmpty(item.account_type)) {
            ((TextView) helper.getView(R.id.tv_acount_type)).setText(item.account_type.equals("1")?"支付宝":"银行卡");
        }

        if (item.isSelected){
            helper.getView(R.id.iv_selected).setVisibility(View.VISIBLE);
        }else{
            helper.getView(R.id.iv_selected).setVisibility(View.GONE);
        }
    }
}
