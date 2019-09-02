package com.task.system.activity;

import android.os.Bundle;

import com.task.system.Constans;
import com.task.system.R;
import com.task.system.bean.OrderInfo;

/**
 * Author: dengyancheng
 * Date: 2019-09-02 01:03
 * Description: 提交订单 或回复订单
 * History:
 */
public class ApplyDisputeOrReplyActivity extends BaseActivity {

    private OrderInfo orderInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply_dispute_or_reply);
        setTitle("争议订单");

        orderInfo = (OrderInfo) getIntent().getSerializableExtra(Constans.PASS_OBJECT);
    }
}
