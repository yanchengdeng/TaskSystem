package com.task.system.api;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface TaskService {


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
     */

    @FormUrlEncoded
    @POST("public/getMobileCode")
    Call<TaskInfoList> getCode(@FieldMap HashMap<String, String> maps);

    //注册
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


    /**
     * 任务详情
     * task_id
     */
    @FormUrlEncoded
    @POST("task/taskDetail")
    Call<TaskInfo> getTaskDetail(@FieldMap HashMap<String, String> maps);


}
