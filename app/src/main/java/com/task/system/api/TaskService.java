package com.task.system.api;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface TaskService {


    //检查版本更新
    @FormUrlEncoded
    @POST("client/checkVersion")
    Call<TaskInfo> checkVersion(@FieldMap HashMap<String,String> maps);

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
     *
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
    Call<TaskInfoList>  checkMobileCode(@FieldMap HashMap<String,String> maps);


    //修改手机号
    @FormUrlEncoded
    @POST("user/setMobile")
    Call<TaskInfoList>  setMobile(@FieldMap HashMap<String,String> maps);


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
    Call<TaskInfoList> setUserAvatar(@FieldMap HashMap<String, String> maps);




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



    @FormUrlEncoded
    @POST("public/getAdList")
    Call<TaskInfoList> getAdList(@FieldMap HashMap<String,String> map);

    //城市列表
    @FormUrlEncoded
    @POST("task/getCityList")
    Call<TaskInfoList> getCityList(@FieldMap HashMap<String, String> maps);


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
    Call<TaskInfo> getTaskShare(@FieldMap HashMap<String,String> maps);

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
    Call<TaskInfoList> checkInviteCode(@FieldMap HashMap<String,String> maps );


    //获取邀请码
    @FormUrlEncoded
    @POST("public/getInviteCode")
    Call<TaskInfo> getInviteCode(@FieldMap HashMap<String,String> maps);



    //添加收藏
    @FormUrlEncoded
    @POST("taskOperate/collect")
    Call<TaskInfoList> collectTask(@FieldMap HashMap<String,String> maps);


    //取消收藏
    @FormUrlEncoded
    @POST("taskOperate/cancelCollect")
    Call<TaskInfoList> cancleCollectTask(@FieldMap HashMap<String,String> maps);


    //收藏列表
    //page
    @FormUrlEncoded
    @POST("taskOperate/collectList")
    Call<TaskInfoList> getCollectList(@FieldMap HashMap<String,String> maps);

    //申请任务

    /**
     * task_id
     * uid
     *
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
    Call<TaskInfo>  applyTaskOperate(@FieldMap HashMap<String,String> maps);

    /**
     * task_id
     * uid
     */
    //放弃任务
    @FormUrlEncoded
    @POST("order/giveUp")
    Call<TaskInfoList>  giveUpTaskOperate(@FieldMap HashMap<String,String> maps);

    //客服地址
    @FormUrlEncoded
    @POST("public/getCustomerService")
    Call<TaskInfo> getCustomeSerice(@FieldMap HashMap<String,String> maps);


    /**添加银行卡
     *
     * account
     * account_name
     * account_type
     * mobile_code
     *
     * 账户，支付宝账号或者银行账户
     * 账户全名
     * 账户类型，1-支付宝账户，2-银行卡账户
     * 手机验证码
     */

    @FormUrlEncoded
    @POST("user/addCard")
    Call<TaskInfoList>  addCard(@FieldMap HashMap<String,String> maps);


    /**
     * id
     *   解绑账号
     */
    @FormUrlEncoded
    @POST("user/delCard")
    Call<TaskInfoList>  delCard(@FieldMap HashMap<String,String> maps);


    //获取银行卡账号
    @FormUrlEncoded
    @POST("user/getCard")
    Call<TaskInfo> getCards(@FieldMap HashMap<String,String> maps);


    /**
     * 提现
     * deposit_cash
     * card_id
     */

    @FormUrlEncoded
    @POST("user/deposit")
    Call<TaskInfoList> deposite(@FieldMap HashMap<String,String> maps);


    /**
     * task_id
     * uid
     * images
     上传多张图片
     */
    @FormUrlEncoded
    @POST("order/uploadImages")
    Call<TaskInfoList> uploadIamges(@FieldMap HashMap<String,String> maps);

    /**
     * order_id
     * uid
     * content
     //上传信息 变更状态
     */
    @FormUrlEncoded
    @POST("order/uploadContent")
    Call<TaskInfoList> uploadContent(@FieldMap HashMap<String,String> maps);


    /** 订单列表
     * status
     * page
     * keywords
     * sort
     */
    @FormUrlEncoded
    @POST("order/getList")
    Call<TaskInfo> getOrderList(@FieldMap HashMap<String,String> maps);


    //短消息数量
    @FormUrlEncoded
    @POST("message/newMesssageSum")
    Call<TaskInfo> getMessageCount(@FieldMap HashMap<String,String> maps);



    //消息列表
    @FormUrlEncoded
    @POST("message/getlist")
    Call<TaskInfo> getMessageList(@FieldMap HashMap<String,String> maps);



    //消息详情
    @FormUrlEncoded
    @POST("message/detail")
    Call<TaskInfoList> getMessagDetail(@FieldMap HashMap<String,String> maps);


    //积分记录、
    // start_date
    //end_date
    @FormUrlEncoded
    @POST("statistics/getList")
    Call<TaskInfo> getStaticsList(@FieldMap HashMap<String,String> maps);


    //积分详情 log_id
    @FormUrlEncoded
    @POST("statistics/detail")
    Call<TaskInfoList> getStaticDetail(@FieldMap HashMap<String,String> maps);


    /**
     *
     child_uid
     search_key
     start_date
     end_date
     page

     */

    //我的账户
    @FormUrlEncoded
    @POST("statistics/getStatistics")
    Call<TaskInfo> getStatistics(@FieldMap HashMap<String,String> maps);


    //设置返佣比例饿
    //id
//    remark
//            fanli_ratio
    //user_type=3的用户才能修改
    @FormUrlEncoded
    @POST("leader/setUser")
    Call<TaskInfoList>  setUseScale(@FieldMap HashMap<String,String> maps);


    /**
     * user_type=3的用户才能新增、修改会员
     * @param maps
     *
     *operate
     * add-新增会员，edit-修改会员@return
     */
    @FormUrlEncoded
    @POST("leader/getUserOption")
    Call<TaskInfo> getUserOptionEdit(@FieldMap HashMap<String,String> maps);

    @FormUrlEncoded
    @POST("leader/getUserOption")
    Call<TaskInfo> getUserOptionAdd(@FieldMap HashMap<String,String> maps);

    //新增用户
    @FormUrlEncoded
    @POST("leader/addLeader")
    Call<TaskInfo> addLeader(@FieldMap HashMap<String,String> maps);


    //代理获取邀请码
    @FormUrlEncoded
    @POST("leader/getInviteCode")
    Call<TaskInfo> getInviteByAgent(@FieldMap HashMap<String,String> maps);


    //获取未读消息
    @FormUrlEncoded
    @POST("order/getOrderSum")
    Call<TaskInfo> getUnreadInfo(@FieldMap HashMap<String,String> maps);

    //  Call<com.task.system.api.TaskInfoList> call = ApiConfig.getInstants().create(TaskService.class).getCityList(TUtils.getParams());




}
