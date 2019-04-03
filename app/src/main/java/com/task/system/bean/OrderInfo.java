package com.task.system.bean;

import java.io.Serializable;

/**
 * 0——待申请
 * 1——待工作
 * 2——待提交 // 待审核--客服审核
 * 3——待审核 // 待审核--客户审核
 * 4——已通过
 * 5——未通过
 * 6——已作废
 * 7——已超时
 *
 * 5  6  7  按钮灰色
 */


/**
 *  [1] => string(9) "待工作"
 *       [2] => string(9) "待提交"
 *       [3] => string(9) "待审核"
 *       [4] => string(9) "已通过"
 *       [5] => string(9) "未通过"
 */
public class OrderInfo implements Serializable {
    public String order_id;
    public String score;//: "60",
    public int status;//: "1",
    public String title;//: "信用卡注册",
    public String description;//: "<span style="font-family:Verdana;font-size:13px;white-space:normal;background-color:#FFFFFF;">写一篇推广刺绣工艺技术的文章，目的是使手工刺绣这项文化活动被衲入素质教育中，与绘画、钢琴的地位一样。以培训养耐性，稳定情绪，提高审美能力，传扬中华文化，多了一技傍身，可表演可自娱，提升生活情趣，调节身心使浮燥的身心回归宁静，可以送给朋友独一无二的礼物等等从多个角度讲述刺绣对于个人素质提升好处。写一份小于2000字的宣传稿</span>",
    public String task_id;//:
    public String  end_time ="2019-04-26 17:29:12";//TODO 假设时间
    public String  cash_time ="2019-04-26 17:29:12";//TODO 假设时间
    public String remark ="xx客户取消";//TODO 假设失败理由
    public String sub_title;//简介
    public int is_apply;//1可以申请，0不能申请
}
