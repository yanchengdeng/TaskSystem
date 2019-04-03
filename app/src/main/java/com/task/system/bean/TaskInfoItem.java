package com.task.system.bean;

import java.io.Serializable;

//任务列表数据
public class TaskInfoItem implements Serializable {
    public String id;// "1",
    public String title;// "信用卡注册",
    public String sub_title;// "注册平安信用卡",
    public String thumbnail;// "/./Application/Home/View/Public/img/default/default.gif",
    public String market_score;// "60.00",
    public String start_time;// "2019-02-21 17;//29;//07",
    public String end_time;// "2019-04-26 17;//29;//12",
    public String views;// "84",
    public String collects;// "2",
    public String storages;// "85",
    public String difficulty;// "0",
    public String audit_date;// "7",
    public String description;// "<span style="font-family;//Verdana;font-size;//13px;white-space;//normal;background-color;//#FFFFFF;">写一篇推广刺绣工艺技术的文章，目的是使手工刺绣这项文化活动被衲入素质教育中，与绘画、钢琴的地位一样。以培训养耐性，稳定情绪，提高审美能力，传扬中华文化，多了一技傍身，可表演可自娱，提升生活情趣，调节身心使浮燥的身心回归宁静，可以送给朋友独一无二的礼物等等从多个角度讲述刺绣对于个人素质提升好处。写一份小于2000字的宣传稿</span>",
    public String step_1;// "<span style="font-family;//Verdana;font-size;//13px;white-space;//normal;background-color;//#FFFFFF;"><span style="color;//#333333;font-family;//"font-size;//14px;font-weight;//700;white-space;//normal;background-color;//#FFFFFF;">步骤一描述：请查看</span>写一篇推广刺绣工艺技术的文章，目的是使手工刺绣这项文化活动被衲入素质教育中，与绘画、钢琴的地位一样。以培训养耐性，稳定情绪，提高审美能力，传扬中华文化，多了一技傍身，可表演可自娱，提升生活情趣，调节身心使浮燥的身心回归宁静，可以送给朋友独一无二的礼物等等从多个角度讲述刺绣对于个人素质提升好处。写一份小于2000字的宣传稿<img src="http;////task.mayimayi.cn/Uploads/2019/03/3.jpg" alt="" /></span>",
    public String step_2;// "<img src="http;////task.mayimayi.cn/Uploads/2019/03/1.jpg" alt="" />",
    public String step_3;// "<img src="http;////task.mayimayi.cn/Uploads/2019/03/2.jpg" alt="" />"
    public int is_collect;//为收藏
    public String order_id;
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
     *4 5  6  7  按钮灰色
     */
    public int order_status;
    public String order_status_title;
    public int is_apply;//1  可以申请 0 不可以
}
