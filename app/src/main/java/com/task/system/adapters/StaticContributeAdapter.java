package com.task.system.adapters;

import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.task.system.R;
import com.task.system.bean.ScoreAccountUserInfo;

//账单明细
public class StaticContributeAdapter extends BaseQuickAdapter<ScoreAccountUserInfo, BaseViewHolder> {

    public StaticContributeAdapter(int layoutResId) {
        super(layoutResId);
    }

    /**
     * contribute_daili_score
     * 贡献返利
     * contribute_bonus_score
     * 贡献分红
     * child_total
     * 下级数量
     * credit_line_score
     * 下级信用额度
     */
    @Override
    protected void convert(BaseViewHolder helper, final ScoreAccountUserInfo item) {
        ((TextView) helper.getView(R.id.tv_one)).setText(""+item.uid);
        ((TextView) helper.getView(R.id.tv_two)).setText(""+item.task_num);
        ((TextView) helper.getView(R.id.tv_three)).setText(""+item.task_score);
        ((TextView) helper.getView(R.id.tv_four)).setText(""+item.score);
        ((TextView) helper.getView(R.id.tv_five)).setText(""+item.task_repulse_sum);


    }

}
