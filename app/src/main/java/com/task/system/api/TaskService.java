package com.task.system.api;

import com.task.system.bean.WxAccessToken;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;

public interface TaskService {


    //检查版本更新
    @FormUrlEncoded
    @POST("client/checkVersion")
    Call<TaskInfo> checkVersion(@FieldMap HashMap<String, String> maps);

    //登陆接口
    @FormUrlEncoded
    @POST("public/login")
    Call<TaskInfo> doLogin(@FieldMap HashMap<String, String> maps);


    //退出登录
    @FormUrlEncoded
    @POST("public/logout")
    Call<TaskInfoList> doLoginOut(@FieldMap HashMap<String, String> maps);

    /**
     * 获取验证码
     * <p>
     * code_type
     * 1-注册时的手机验证码，不需要uid、token
     * 2-找回密码的时候的手机验证码，不需要uid、token
     * 3-修改密码的时候的手机验证码,需要uid、token
     * <p>
     * 4-新增银行账户信息，
     * 5-申请提现
     */

    @FormUrlEncoded
    @POST("public/getMobileCode")
    Call<TaskInfoList> getCode(@FieldMap HashMap<String, String> maps);

    /**
     * 注册
     * username
     * mobile
     * mobile_code
     * invite_code
     * password
     * re_password
     */
    @FormUrlEncoded
    @POST("public/register")
    Call<TaskInfoList> doRegister(@FieldMap HashMap<String, String> maps);

    //忘记密码

    /**
     * mobile
     * mobile_code
     * password
     * re_password
     */
    @FormUrlEncoded
    @POST("public/forgetpassword")
    Call<TaskInfoList> forgetPassword(@FieldMap HashMap<String, String> maps);


    //修改密码
    @FormUrlEncoded
    @POST("user/setPassword")
    Call<TaskInfoList> modifyPassword(@FieldMap HashMap<String, String> maps);


    //验证验证码
    @FormUrlEncoded
    @POST("public/checkMobileCode")
    Call<TaskInfoList> checkMobileCode(@FieldMap HashMap<String, String> maps);


    //修改手机号
    @FormUrlEncoded
    @POST("user/setMobile")
    Call<TaskInfoList> setMobile(@FieldMap HashMap<String, String> maps);


    //获取用户基本信息

    /**
     * uid
     * app_token
     */
    @FormUrlEncoded
    @POST("user/detail")
    Call<TaskInfo> getUserInfo(@FieldMap HashMap<String, String> maps);


    //修改用户名

    /**
     * uid
     * username
     * app_token
     */
    @FormUrlEncoded
    @POST("user/setUsername")
    Call<TaskInfoList> setUserName(@FieldMap HashMap<String, String> maps);


    /**
     * 修改用户头型
     * uid
     * avatar
     * app_token
     */
    @FormUrlEncoded
    @POST("user/setAvatar")
    Call<TaskInfoIgnoreBody> setUserAvatar(@FieldMap HashMap<String, String> maps);


    /**
     * 修改密码
     * uid
     * old_password
     * new_password
     * re_password
     */
    @FormUrlEncoded
    @POST("user/setPassword")
    Call<TaskInfo> setPassword(@FieldMap HashMap<String, String> maps);


    /**
     * 1=首页 2=启动页 3 新手
     *
     * @param map
     * @return 广告页面
     */
    @FormUrlEncoded
    @POST("public/getAdList")
    Call<TaskInfoList> getAdList(@FieldMap HashMap<String, String> map);

    //城市列表
    @FormUrlEncoded
    @POST("task/getCityList")
    Call<TaskInfoList> getCityList(@FieldMap HashMap<String, String> maps);


    //用户栏目
    @FormUrlEncoded
    @POST("user/category")
    Call<TaskInfoList> getUserCatergorylist(@FieldMap HashMap<String, String> maps);

    //任务栏目
    @FormUrlEncoded
    @POST("task/getCategoryList")
    Call<TaskInfoList> getCatergoryList(@FieldMap HashMap<String, String> maps);

    //智能排序 热门标签
    @FormUrlEncoded
    @POST("task/getSortTagList")
    Call<TaskInfo> getSortTagsList(@FieldMap HashMap<String, String> maps);


    /**
     * 任务列表
     * region_id
     * category_id
     * sort_id
     * tags_id
     * page
     * keywords
     */
    @FormUrlEncoded
    @POST("task/getTaskList")
    Call<TaskInfo> getTaskList(@FieldMap HashMap<String, String> maps);


    //分享
    @FormUrlEncoded
    @POST("taskOperate/share")
    Call<TaskInfo> getTaskShare(@FieldMap HashMap<String, String> maps);


    @FormUrlEncoded
    @POST("wheel/share")
    Call<TaskInfo>  getWheelShare(@FieldMap HashMap<String,String> maps);

    /**
     * 任务详情
     * task_id
     */
    @FormUrlEncoded
    @POST("task/taskDetail")
    Call<TaskInfo> getTaskDetail(@FieldMap HashMap<String, String> maps);


    //检查邀请码
    @FormUrlEncoded
    @POST("public/checkInviteCode")
    Call<TaskInfoList> checkInviteCode(@FieldMap HashMap<String, String> maps);


    //获取邀请码
    @FormUrlEncoded
    @POST("public/getInviteCode")
    Call<TaskInfo> getInviteCode(@FieldMap HashMap<String, String> maps);


    //添加收藏
    @FormUrlEncoded
    @POST("taskOperate/collect")
    Call<TaskInfoList> collectTask(@FieldMap HashMap<String, String> maps);


    //取消收藏
    @FormUrlEncoded
    @POST("taskOperate/cancelCollect")
    Call<TaskInfoList> cancleCollectTask(@FieldMap HashMap<String, String> maps);


    //收藏列表
    //page
    @FormUrlEncoded
    @POST("taskOperate/collectList")
    Call<TaskInfoList> getCollectList(@FieldMap HashMap<String, String> maps);

    //申请任务

    /**
     * task_id
     * uid
     * <p>
     * 任务不存在
     * 用户不存在
     * 任务已结束
     * 任务还未开始，暂不能申请
     * 任务已经结束，不能申请
     * 任务已经被抢完
     * 您已经申请了任务
     * 申请失败
     */
    @FormUrlEncoded
    @POST("taskOperate/apply")
    Call<TaskInfo> applyTaskOperate(@FieldMap HashMap<String, String> maps);

    /**
     * task_id
     * uid
     */
    //放弃任务
    @FormUrlEncoded
    @POST("order/giveUp")
    Call<TaskInfoList> giveUpTaskOperate(@FieldMap HashMap<String, String> maps);

    //客服地址
    @FormUrlEncoded
    @POST("public/getCustomerService")
    Call<TaskInfo> getCustomeSerice(@FieldMap HashMap<String, String> maps);


    /**
     * 添加银行卡
     * <p>
     * account
     * account_name
     * account_type
     * mobile_code
     * <p>
     * 账户，支付宝账号或者银行账户
     * 账户全名
     * 账户类型，1-支付宝账户，2-银行卡账户
     * 手机验证码
     */

    @FormUrlEncoded
    @POST("user/addCard")
    Call<TaskInfoList> addCard(@FieldMap HashMap<String, String> maps);


    /**
     * id
     * 解绑账号
     */
    @FormUrlEncoded
    @POST("user/delCard")
    Call<TaskInfoList> delCard(@FieldMap HashMap<String, String> maps);


    //获取银行卡账号
    @FormUrlEncoded
    @POST("user/getCard")
    Call<TaskInfo> getCards(@FieldMap HashMap<String, String> maps);


    /**
     * 提现
     * deposit_cash
     * card_id
     */

    @FormUrlEncoded
    @POST("user/deposit")
    Call<TaskInfoList> deposite(@FieldMap HashMap<String, String> maps);


    /**
     * task_id
     * uid
     * images
     * 上传多张图片
     */
    @FormUrlEncoded
    @POST("order/uploadImages")
    Call<TaskInfoList> uploadIamges(@FieldMap HashMap<String, String> maps);

    /**
     * order_id
     * uid
     * content
     * //上传信息 变更状态
     */
    @FormUrlEncoded
    @POST("order/uploadContent")
    Call<TaskInfoList> uploadContent(@FieldMap HashMap<String, String> maps);


    //短消息数量
    @FormUrlEncoded
    @POST("message/newMesssageSum")
    Call<TaskInfo> getMessageCount(@FieldMap HashMap<String, String> maps);


    //消息列表
    @FormUrlEncoded
    @POST("message/getlist")
    Call<TaskInfo> getMessageList(@FieldMap HashMap<String, String> maps);


    //消息详情
    @FormUrlEncoded
    @POST("message/detail")
    Call<TaskInfoList> getMessagDetail(@FieldMap HashMap<String, String> maps);


    //积分记录、
    // start_date
    //end_date
    @FormUrlEncoded
    @POST("statistics/getList")
    Call<TaskInfo> getStaticsList(@FieldMap HashMap<String, String> maps);


    //积分详情 log_id
    @FormUrlEncoded
    @POST("statistics/detail")
    Call<TaskInfoList> getStaticDetail(@FieldMap HashMap<String, String> maps);


    /**
     * child_uid
     * search_key
     * start_date
     * end_date
     * page
     */

    //我的账户
    @FormUrlEncoded
    @POST("statistics/getStatistics")
    Call<TaskInfo> getStatistics(@FieldMap HashMap<String, String> maps);


    //设置返佣比例饿
    //id
//    remark
//            fanli_ratio
    //user_type=3的用户才能修改
    @FormUrlEncoded
    @POST("leader/setUser")
    Call<TaskInfoList> setUseScale(@FieldMap HashMap<String, String> maps);


    /**
     * user_type=3的用户才能新增、修改会员
     *
     * @param maps operate
     *             add-新增会员，edit-修改会员@return
     */
    @FormUrlEncoded
    @POST("leader/getUserOption")
    Call<TaskInfo> getUserOptionEdit(@FieldMap HashMap<String, String> maps);

    @FormUrlEncoded
    @POST("leader/getUserOption")
    Call<TaskInfo> getUserOptionAdd(@FieldMap HashMap<String, String> maps);

    //新增用户
    @FormUrlEncoded
    @POST("leader/addLeader")
    Call<TaskInfo> addLeader(@FieldMap HashMap<String, String> maps);


    //代理获取邀请码
    @FormUrlEncoded
    @POST("user/getInviteCode")
    Call<TaskInfo> getInviteByAgent(@FieldMap HashMap<String, String> maps);

//    @FormUrlEncoded
//    @POST("leader/getInviteCode")
//    Call<TaskInfo> getInviteByAgent(@FieldMap HashMap<String,String> maps);


    //获取未读消息
    @FormUrlEncoded
    @POST("order/getOrderSum")
    Call<TaskInfo> getUnreadInfo(@FieldMap HashMap<String, String> maps);

    //  Call<com.task.system.api.TaskInfoList> call = ApiConfig.getInstants().create(TaskService.class).getCityList(TUtils.getParams());


    //获取今日签到情况
    @FormUrlEncoded
    @POST("user/sign")
    Call<TaskInfoIgnoreBody> userSign(@FieldMap HashMap<String, String> maps);

    //签到记录
    @FormUrlEncoded
    @POST("user/signList")
    Call<TaskInfo> userSignList(@FieldMap HashMap<String, String> maps);


    @GET("https://api.weixin.qq.com/sns/oauth2/access_token")
    Call<WxAccessToken> getAccessToken(@QueryMap HashMap<String, String> maps);

    //第三方登录
    @FormUrlEncoded
    @POST("public/loginByThird")
    Call<TaskInfo> loginByThird(@FieldMap HashMap<String, String> maps);


    //账号绑定微信
    @FormUrlEncoded
    @POST("user/bind")
    Call<TaskInfo> AccountbindWx(@FieldMap HashMap<String, String> maps);

    //绑定账号
    @FormUrlEncoded
    @POST("public/bindByLogin")
    Call<TaskInfo> bindByLogin(@FieldMap HashMap<String, String> maps);

    //启动app 首页
    @GET("public/appinit")
    Call<TaskInfo> appInit(@QueryMap HashMap<String, String> maps);


    //游戏列表
    @FormUrlEncoded
    @POST("wheel/getList")
    Call<TaskInfo> getWheelList(@FieldMap HashMap<String, String> maps);

    //上传身份证信息
    @FormUrlEncoded
    @POST("user/addIdCard")
    Call<TaskInfoIgnoreBody> addIdCards(@FieldMap HashMap<String, String> maps);


    //修改省份证

    @FormUrlEncoded
    @POST("user/setIdCard")
    Call<TaskInfoIgnoreBody> setIdCards(@FieldMap HashMap<String, String> maps);

    //单张图片上传

    /**
     * 图片类型：task=任务，order=订单，avatar=头像，certificate=证件
     */
    @FormUrlEncoded
    @POST("user/uploadImage")
    Call<TaskInfo> uploadImage(@FieldMap HashMap<String, String> maps);


    //获取地址
    @FormUrlEncoded
    @POST("user/getAddressList")
    Call<TaskInfoList> getAddressList(@FieldMap HashMap<String, String> maps);

    //修改地址
    @FormUrlEncoded
    @POST("user/setAddress")
    Call<TaskInfoIgnoreBody> setAddress(@FieldMap HashMap<String, String> maps);

    //新增地址
    @FormUrlEncoded
    @POST("user/addAddress")
    Call<TaskInfoIgnoreBody> addAddress(@FieldMap HashMap<String, String> maps);


    //删除地址
    @FormUrlEncoded
    @POST("user/delAddress")
    Call<TaskInfoIgnoreBody> delAddress(@FieldMap HashMap<String, String> maps);


    //用户身份 微信绑定数据
    @GET("user/detailext")
    Call<TaskInfo> getUserDetailExt(@QueryMap HashMap<String, String> maps);


    /**
     * aid
     * 6-联系客服
     * 7-隐私声明
     * 8-社会公约
     * 9-服务条款
     * 10-用户注册协议
     * 11-关于我们
     * 12-帮助中心
     */
    //文章
    @FormUrlEncoded
    @POST("public/articleDetail")
    Call<TaskInfo> getArticalDetail(@FieldMap HashMap<String, String> maps);


    /**
     * 人气排行
     */
    @FormUrlEncoded
    @POST("task/getPopularityTaskList")
    Call<TaskInfoList> getPopulatityTaskLst(@FieldMap HashMap<String, String> maps);


    /*****会员使用**/

     /**
     * 订单 标签
     */
    @FormUrlEncoded
    @POST("order/getStatus")
    Call<TaskInfoList> getOrderStatus(@FieldMap HashMap<String, String> maps);


    /**
     * 订单列表
     * status
     * page
     * keywords
     * sort
     */
    @FormUrlEncoded
    @POST("order/getList")
    Call<TaskInfo> getOrderList(@FieldMap HashMap<String, String> maps);

    /**
     * 争议 标签
     */
    @FormUrlEncoded
    @POST("order/disputeStatus")
    Call<TaskInfoList> getDisputeStatus(@FieldMap HashMap<String, String> maps);


    /*****区域使用**/

    /**
     * 任务列表标签
     */
    @FormUrlEncoded
    @POST("operator/getTaskTabs")
    Call<TaskInfoList> getOperatorTaskTags(@FieldMap HashMap<String, String> maps);

    /**
     * 任务列表
     */
    @FormUrlEncoded
    @POST("operator/getTaskList")
    Call<TaskInfoList>  getOperaorTaskList(@FieldMap HashMap<String,String> maps);



    /**
     * 订单列表
     * status
     * page
     * keywords
     * sort
     */
    @FormUrlEncoded
    @POST("operator/getOrderList")
    Call<TaskInfo> getOperatOrderList(@FieldMap HashMap<String, String> maps);

    /**
     * 争议 标签
     */
    @FormUrlEncoded
    @POST("operator/getOrderTabs")
    Call<TaskInfoList> getOperatorOrderTabs(@FieldMap HashMap<String, String> maps);


    //任务列表标签
    @FormUrlEncoded
    @POST("operator/getTaskSorts")
    Call<TaskInfoList> getOperateTaskSorts(@FieldMap HashMap<String, String> maps);


    //根据转盘id 获取游戏playurl
    @FormUrlEncoded
    @POST("wheel/getUrl")
    Call<TaskInfo> getPlayUl(@FieldMap HashMap<String,String> maps);


    //发布者 取消任务接口
    @FormUrlEncoded
    @POST("operator/setTaskStatus")
    Call<TaskInfoList> operatorTaskStatus(@FieldMap HashMap<String,String> mapas);


    //获取订单审核理由
    @FormUrlEncoded
    @POST("order/getAuditReason")
    Call<TaskInfo> getAuditReason(@FieldMap HashMap<String,String> maps);



    //获取任务审核理由
    @FormUrlEncoded
    @POST("operator/getTaskAuditReason")
    Call<TaskInfo> getTaskAuditReason(@FieldMap HashMap<String,String> maps);



    //我参与过的的活动
    @FormUrlEncoded
    @POST("wheel/getListByUid")
    Call<TaskInfo> getWheelListByUid(@FieldMap HashMap<String,String> mapas);

    //我的中奖列表
    @FormUrlEncoded
    @POST("wheel/getPrizeListByUid")
    Call<TaskInfo> getPrizeListByUid(@FieldMap HashMap<String,String> maps);

    //中奖详情
    @FormUrlEncoded
    @POST("wheel/getDetailById")
    Call<TaskInfo> getAwardDetail(@FieldMap HashMap<String,String> maps);

    //兑换奖品
    @FormUrlEncoded
    @POST("wheel/setAddress")
    Call<TaskInfo> setAwardAddress(@FieldMap HashMap<String,String> maps);

    //提交争议
    @FormUrlEncoded
    @POST("order/dispute")
    Call<TaskInfo>  disputeOrder(@FieldMap HashMap<String,String> maps);

    //查看争议列表
    @FormUrlEncoded
    @POST("order/disputeList")
    Call<TaskInfoList>  disputeList(@FieldMap HashMap<String,String> maps);

    //待审核数量
    @FormUrlEncoded
    @POST("operator/getTaskCount")
    Call<TaskInfo> getOperateTaskCount(@FieldMap HashMap<String,String> maps);


    //上传企业认证
    @FormUrlEncoded
    @POST("user/addBusiness")
    Call<TaskInfoIgnoreBody>  userAddBuisness(@FieldMap HashMap<String,String> maps);

    @FormUrlEncoded
    @POST("user/setBusiness")
    Call<TaskInfoIgnoreBody>  userSetBusiness(@FieldMap HashMap<String,String> maps);


    //绑定机关推送register_id
    @FormUrlEncoded
    @POST("user/bindPushId")
    Call<TaskInfoIgnoreBody>  userBindPushId(@FieldMap HashMap<String,String> maps);

    //发布者订单详情
    @FormUrlEncoded
    @POST("order/detail")
    Call<TaskInfo>   getOrderDetail(@FieldMap HashMap<String,String > mpas);

    //修改订单状态及价格
//    4-通过，5-不通过，8-修改价格
    @FormUrlEncoded
    @POST("operator/setOrderStatus")
    Call<TaskInfoIgnoreBody>  setOrderStatus(@FieldMap HashMap<String,String> maps);

 }
