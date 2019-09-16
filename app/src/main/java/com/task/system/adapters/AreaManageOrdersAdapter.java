package com.task.system.adapters;

import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.task.system.R;
import com.task.system.bean.AreaManageOrder;

//订单
public class AreaManageOrdersAdapter extends BaseQuickAdapter<AreaManageOrder, BaseViewHolder> {

    private String status;

    public AreaManageOrdersAdapter(int layoutResId) {
        super(layoutResId);
    }

    public AreaManageOrdersAdapter(int layoutResId, String status) {
        super(layoutResId);
        this.status = status;
    }

    @Override
    protected void convert(BaseViewHolder helper, AreaManageOrder item) {



        if (!TextUtils.isEmpty(item.getTitle())) {
            ((TextView)helper.getView(R.id.tv_title)).setText(item.getTitle());
        }

        if (!TextUtils.isEmpty(item.getSub_title())) {
            ((TextView)helper.getView(R.id.tv_info)).setText(Html.fromHtml(item.getSub_title()));
        }

        if (!TextUtils.isEmpty(item.getTask_score())) {
            ((TextView)helper.getView(R.id.tv_price)).setText(mContext.getString(R.string.money_unit)+item.getTask_score());
        }

        if (!TextUtils.isEmpty(item.getUid())) {
            ((TextView)helper.getView(R.id.tv_user_id)).setText("用户ID:"+item.getUid());
            helper.getView(R.id.tv_user_id).setVisibility(View.VISIBLE);
        }

        if (!TextUtils.isEmpty(item.getOrder_id())) {
            ((TextView)helper.getView(R.id.tv_order_id)).setText("订单ID:"+item.getOrder_id());
        }



        ((TextView) helper.getView(R.id.tv_status)).setText("状态："+item.getStatus_title());




        helper.addOnClickListener(R.id.tv_cancle_task);
        helper.addOnClickListener(R.id.tv_look_for_reason);
        helper.addOnClickListener(R.id.tv_edit_again);
        helper.addOnClickListener(R.id.tv_order_data);



        //时间
        TextView tvTime = helper.getView(R.id.tv_time);
        //第二条虚线
        View dashLineTwo = helper.getView(R.id.dash_line_two);



        //查看原因
        TextView funLookReason = helper.getView(R.id.tv_look_for_reason);

        //订单数据
        View  funOrderData = helper.getView(R.id.tv_order_data);
        /**
         "status": 0,"title": "已中止" --显示编辑按钮
         "status": "1","title": "展示中"--中止任务 订单数据
         "status": "2","title": "草稿箱"--显示编辑按钮
         "status": "3","title": "待审核"--显示 取消任务
         "status": "4","title": "关联订单
         "status": "5","title": "弹出理由  关联订单
         "status": "6",已完结  关联订单
         "status": "7","已超时  关联订单
         */

        switch (item.getStatus()){
            case "0":
                ((TextView)helper.getView(R.id.tv_time)).setText("结束时间："+item.getTask_end_time());
                funOrderData.setVisibility(View.GONE);
                funLookReason.setVisibility(View.GONE);
                break;
            case "1":
                ((TextView)helper.getView(R.id.tv_time)).setText("结束时间："+item.getTask_end_time());
                funOrderData.setVisibility(View.VISIBLE);
                funLookReason.setVisibility(View.GONE);
                break;
            case "2":
                ((TextView)helper.getView(R.id.tv_time)).setText("创建时间："+item.getUpdate_time());
                funOrderData.setVisibility(View.GONE);
                funLookReason.setVisibility(View.GONE);
                break;
            case "3":
                ((TextView)helper.getView(R.id.tv_time)).setText("结束时间："+item.getTask_end_time());
                funOrderData.setVisibility(View.GONE);
                funLookReason.setVisibility(View.GONE);
                break;
            case "4":
                ((TextView)helper.getView(R.id.tv_time)).setText("审核时间："+item.getUpdate_time());
                funOrderData.setVisibility(View.GONE);
                funLookReason.setVisibility(View.VISIBLE);
                break;
            case "5":
                ((TextView)helper.getView(R.id.tv_time)).setText("审核时间："+item.getUpdate_time());
                funOrderData.setVisibility(View.VISIBLE);
                funLookReason.setVisibility(View.GONE);
                break;
            case "6":
                ((TextView)helper.getView(R.id.tv_time)).setText("审核时间："+item.getUpdate_time());
                funOrderData.setVisibility(View.GONE);
                funLookReason.setVisibility(View.GONE);
                break;
            case "7":
                ((TextView)helper.getView(R.id.tv_time)).setText("到期时间："+item.getUpdate_time());
                funOrderData.setVisibility(View.GONE);
                funLookReason.setVisibility(View.GONE);
                break;
        }


    }
}
