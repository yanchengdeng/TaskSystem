package com.task.system.adapters;

import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.task.system.R;
import com.task.system.bean.OrderInfo;
import com.task.system.utils.TUtils;

//订单
public class TaskOrderAdapter extends BaseQuickAdapter<OrderInfo, BaseViewHolder> {

    private int status;

    public TaskOrderAdapter(int layoutResId) {
        super(layoutResId);
    }

    public TaskOrderAdapter(int layoutResId, int status) {
        super(layoutResId);
        this.status = status;
    }

    @Override
    protected void convert(BaseViewHolder helper, OrderInfo item) {



        if (!TextUtils.isEmpty(item.title)) {
            ((TextView)helper.getView(R.id.tv_title)).setText(item.title);
        }

        if (!TextUtils.isEmpty(item.sub_title)) {
            ((TextView)helper.getView(R.id.tv_info)).setText(Html.fromHtml(item.sub_title));
        }

        if (!TextUtils.isEmpty(item.order_score)) {
            ((TextView)helper.getView(R.id.tv_price)).setText(mContext.getString(R.string.money_unit)+item.order_score);
        }

        if (!TextUtils.isEmpty(item.order_id)) {
            ((TextView)helper.getView(R.id.tv_order_id)).setText("ID:"+item.order_id);
        }



        helper.addOnClickListener(R.id.tv_cancle_task);
        helper.addOnClickListener(R.id.tv_look_for_reason);
        helper.addOnClickListener(R.id.tv_going_work_task);
        helper.addOnClickListener(R.id.tv_else_function);
        helper.addOnClickListener(R.id.tv_apply_dispute);//提起争议



        //时间
        TextView tvTime = helper.getView(R.id.tv_time);
        //第二条虚线
        View dashLineTwo = helper.getView(R.id.dash_line_two);
        //未通过理由
        TextView tvReason = helper.getView(R.id.tv_destory_reason);
        //查看原因
        TextView tvLookReason = helper.getView(R.id.tv_look_for_reason);
        //取消任务
        TextView tvCancleTask = helper.getView(R.id.tv_cancle_task);
        //继续工作
        TextView tvGoingWord = helper.getView(R.id.tv_going_work_task);
        //其他任务提示
        TextView tvElseFunction = helper.getView(R.id.tv_else_function);

        //提起争议
        TextView tvApplyDispute = helper.getView(R.id.tv_apply_dispute);
       /** * 1——待工作
                * 2——待提交 // 待审核--客服审核
                * 3——待审核 // 待审核--客户审核
                * 4——已通过
                * 5——未通过  只有5 才有提出争议状态
                * 6——已作废
                * 7——已超时*/

//       //TODO测试完成后 需要隐藏代码
//       if (status==1){
//           if (helper.getAdapterPosition()%2==0){
//               item.status = 1;
//           }else{
//               item.status=2;
//           }
//       }else if (status==5) {
//           if (helper.getAdapterPosition()%3==0) {
//               item.status = 5;
//           }else if (helper.getAdapterPosition()%3==1){
//               item.status = 6;
//           }else{
//               item.status = 7;
//           }
//       }else {
//           item.status = status;
//       }

        /**
         * cash_time
         * 4、5、6、7才有这个时间
         * PHP-张晓燕  14:43:32
         * end_time是拿来做倒计时的
         */

        switch (item.status){
            case 1:
            case 2:
                if (!TextUtils.isEmpty(item.end_time)) {
                    tvTime.setText(String.format(mContext.getString(R.string.end_time_tips), TUtils.getEndTimeTips(item.end_time)));
                }
                tvTime.setVisibility(View.VISIBLE);
                dashLineTwo.setVisibility(View.VISIBLE);
                tvReason.setVisibility(View.GONE);
                tvLookReason.setVisibility(View.GONE);
                tvCancleTask.setVisibility(View.VISIBLE);
                tvGoingWord.setVisibility(View.VISIBLE);
                tvElseFunction.setVisibility(View.GONE);
                tvApplyDispute.setVisibility(View.GONE);
                break;
            case 3:
//                待审核
                tvTime.setVisibility(View.GONE);
                dashLineTwo.setVisibility(View.GONE);
                tvReason.setVisibility(View.GONE);
                tvLookReason.setVisibility(View.GONE);
                tvCancleTask.setVisibility(View.GONE);
                tvGoingWord.setVisibility(View.GONE);
                tvElseFunction.setVisibility(View.VISIBLE);
                tvElseFunction.setBackground(mContext.getResources().getDrawable(R.drawable.normal_submit_btn_red_trans));
                tvElseFunction.setText("待审核");
                tvElseFunction.setTextColor(mContext.getResources().getColor(R.color.red));
                tvApplyDispute.setVisibility(View.GONE);
                break;
            //4——已通过
            case 4:
                if (!TextUtils.isEmpty(item.cash_time)) {
                    tvTime.setText(String.format(mContext.getString(R.string.pass_time_tips), TUtils.getEndTimeTips(item.cash_time)));
                }
                tvTime.setVisibility(View.VISIBLE);
                dashLineTwo.setVisibility(View.VISIBLE);
                tvReason.setVisibility(View.GONE);
                tvLookReason.setVisibility(View.GONE);
                tvCancleTask.setVisibility(View.GONE);
                tvGoingWord.setVisibility(View.GONE);
                tvElseFunction.setBackground(mContext.getResources().getDrawable(R.drawable.normal_submit_btn_red));
                tvElseFunction.setText("重新申请");
                tvElseFunction.setTextColor(mContext.getResources().getColor(R.color.white));
                if (item.is_apply==1){
                    tvElseFunction.setVisibility(View.VISIBLE);
                }else{
                    tvElseFunction.setVisibility(View.GONE);
                }
                tvApplyDispute.setVisibility(View.GONE);
                break;
            //5——未通过
            case 5:
                if (!TextUtils.isEmpty(item.cash_time)) {
                    tvTime.setText(String.format(mContext.getString(R.string.not_pass_time_tips), item.cash_time));
                }
                tvTime.setVisibility(View.VISIBLE);
                dashLineTwo.setVisibility(View.VISIBLE);
                tvReason.setVisibility(View.GONE);
//                tvLookReason.setVisibility(View.GONE);
                tvCancleTask.setVisibility(View.GONE);
                tvGoingWord.setVisibility(View.GONE);
                tvElseFunction.setBackground(mContext.getResources().getDrawable(R.drawable.normal_submit_btn_red));
                tvElseFunction.setText("重新申请");
                tvElseFunction.setTextColor(mContext.getResources().getColor(R.color.white));
                if (item.is_apply==1){
                    tvElseFunction.setVisibility(View.VISIBLE);
                }else{
                    tvElseFunction.setVisibility(View.GONE);
                }
                tvApplyDispute.setVisibility(View.VISIBLE);
                if (item.dispute_status==0){
                    tvApplyDispute.setText("提出争议");
                    tvApplyDispute.setTextColor(mContext.getResources().getColor(R.color.color_info));
                    tvApplyDispute.setBackground(mContext.getResources().getDrawable(R.drawable.normal_submit_btn_gray_deep_trans));
                }else if (item.dispute_status==1 || item.dispute_status==2 || item.dispute_status==3){
                    tvApplyDispute.setText("查看争议");
                    tvApplyDispute.setTextColor(mContext.getResources().getColor(R.color.color_info));
                    tvApplyDispute.setBackground(mContext.getResources().getDrawable(R.drawable.normal_submit_btn_gray_deep_trans));
                }else{
                    if (!TextUtils.isEmpty(item.dispute_status_title)) {
                        tvApplyDispute.setText(item.dispute_status_title);
                        tvApplyDispute.setBackground(null);
                        tvApplyDispute.setTextColor(mContext.getResources().getColor(R.color.red));
                    }
                }

                break;
            // 6——已作废
            case 6:
                if (!TextUtils.isEmpty(item.cash_time)) {
                    tvTime.setText(String.format(mContext.getString(R.string.destory_time_tips), item.cash_time));
                }
                tvTime.setVisibility(View.VISIBLE);
                dashLineTwo.setVisibility(View.VISIBLE);
                tvTime.setVisibility(View.VISIBLE);
                dashLineTwo.setVisibility(View.VISIBLE);
                tvReason.setVisibility(View.VISIBLE);
                tvReason.setText("已放弃");
                tvLookReason.setVisibility(View.GONE);
                tvCancleTask.setVisibility(View.GONE);
                tvGoingWord.setVisibility(View.GONE);
                tvElseFunction.setBackground(mContext.getResources().getDrawable(R.drawable.normal_submit_btn_red));
                tvElseFunction.setText("重新申请");
                tvElseFunction.setTextColor(mContext.getResources().getColor(R.color.white));
                if (item.is_apply==1){
                    tvElseFunction.setVisibility(View.VISIBLE);
                }else{
                    tvElseFunction.setVisibility(View.GONE);
                }
                tvApplyDispute.setVisibility(View.GONE);

                break;
//                7——已超时
            case 7:
                if (!TextUtils.isEmpty(item.cash_time)) {
                    tvTime.setText(String.format(mContext.getString(R.string.destory_time_tips), item.cash_time));
                }
                tvTime.setVisibility(View.VISIBLE);
                dashLineTwo.setVisibility(View.VISIBLE);
                tvReason.setVisibility(View.VISIBLE);
                tvReason.setText("已超时");
                tvLookReason.setVisibility(View.GONE);
                tvCancleTask.setVisibility(View.GONE);
                tvGoingWord.setVisibility(View.GONE);
                tvElseFunction.setBackground(mContext.getResources().getDrawable(R.drawable.normal_submit_btn_red));
                tvElseFunction.setText("重新申请");
                tvElseFunction.setTextColor(mContext.getResources().getColor(R.color.white));
                if (item.is_apply==1){
                    tvElseFunction.setVisibility(View.VISIBLE);
                }else{
                    tvElseFunction.setVisibility(View.GONE);
                }
                tvApplyDispute.setVisibility(View.GONE);
                break;

        }
    }
}
