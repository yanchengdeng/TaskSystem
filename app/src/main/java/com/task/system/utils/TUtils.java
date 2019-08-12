package com.task.system.utils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.DeviceUtils;
import com.blankj.utilcode.util.EncodeUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.task.system.Constans;
import com.task.system.activity.OpenWebViewActivity;
import com.task.system.bean.AreaBean;
import com.task.system.bean.TaskInfoList;
import com.task.system.bean.TaskType;
import com.task.system.bean.UserInfo;
import com.task.system.enums.UserType;
import com.task.system.views.photoview.ImageInfo;
import com.task.system.views.photoview.preview.ImagePreviewActivity;
import com.yc.lib.api.ApiConfig;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TUtils {

    private static List<AreaBean> cityInfos = new ArrayList<>();

    public static HashMap<String, String> getParams(HashMap<String, String> data) {
        data.put("version", AppUtils.getAppVersionName());
        data.put("app_imei", getImei());
        data.put("app_id", Constans.APP_ID);
        data.put("app_sign", getAppSign());
        if (!TextUtils.isEmpty(getUserId())) {
            data.put("uid", getUserId());
        }
        if (!TextUtils.isEmpty(getToken())) {
            data.put("app_token", getToken());
        }
        if (Constans.IS_DEBUG) {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("?");
            for (String key : data.keySet()) {
                stringBuffer.append(key).append("=").append(data.get(key)).append("&");
            }
            LogUtils.d("dyc", stringBuffer.toString());
        }
        return data;
    }


    public static HashMap<String, String> getParams() {
        HashMap<String, String> data = new HashMap<>();
        data.put("version", AppUtils.getAppVersionName());
        data.put("app_imei", getImei());
        data.put("app_id", Constans.APP_ID);
        data.put("app_sign", getAppSign());
        if (!TextUtils.isEmpty(getUserId())) {
            data.put("uid", getUserId());
        }
        if (!TextUtils.isEmpty(getToken())) {
            data.put("app_token", getToken());
        }
        if (Constans.IS_DEBUG) {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("?");
            for (String key : data.keySet()) {
                stringBuffer.append(key).append("=").append(data.get(key)).append("&");
            }
            LogUtils.d("dyc", stringBuffer.toString());
        }
        return data;
    }

    private static String getAppSign() {
        StringBuilder sb = new StringBuilder();
        sb.append(getImei()).append(Constans.APP_SERCET).append(Constans.APP_ID);
        return DataConvertUtils.MD5(sb.toString()).substring(0, 20);
    }


    public static String getImei() {
        return DeviceUtils.getAndroidID();
//        return SPUtils.getInstance().getString(Constans.IMEI, "354765086202488");
    }

    public static void setImei(String imei) {
        if (!TextUtils.isEmpty(imei)) {
            SPUtils.getInstance().put(Constans.IMEI, imei);
        } else {
            //TODO  有些设备imei 获取不到怎么办？
            SPUtils.getInstance().put(Constans.IMEI, "354765086202488");
        }
    }

    private static String getToken() {
        String json = SPUtils.getInstance().getString(Constans.TOKEN);
        if (!TextUtils.isEmpty(json)) {
            return new String(EncodeUtils.base64Encode(json));
        } else {
            return "";
        }
    }


    /***
     * 用户信息
     * @return
     */
    public static UserInfo getUserInfo() {
        String useinfo = SPUtils.getInstance().getString(Constans.USER_INFO);
        if (TextUtils.isEmpty(useinfo)) {
            return new UserInfo();
        } else {
            UserInfo userInfo = new Gson().fromJson(useinfo, UserInfo.class);
            return userInfo;
        }
    }


    /***
     * 用户信息
     * @return 130 6738 0836
     */
    public static String getHidePhone() {
        String phone = getUserInfo().mobile;
        if (!TextUtils.isEmpty(phone) && phone.length() == 11) {
            return phone.substring(0, 3) + "****" + phone.substring(7, 11);
        }
        return "异常账号";
    }


    /***
     * 用户信息
     * @return
     */
    public static void saveUserInfo(UserInfo userInfo) {
        String userinfo = new Gson().toJson(userInfo);
        SPUtils.getInstance().put(Constans.USER_INFO, userinfo);
    }

    /**
     * 登陆状态
     *
     * @return
     */
    public static boolean isLogin() {
        UserInfo userInfo = getUserInfo();
        if (userInfo != null && !TextUtils.isEmpty(userInfo.uid)) {
            return true;
        } else {
            return false;
        }
    }


    public static void clearUserInfo() {
        SPUtils.getInstance().put(Constans.USER_INFO, "");
    }


    //Uid
    public static String getUserId() {
        UserInfo userInfo = getUserInfo();
        if (userInfo != null) {
            return userInfo.uid;
        } else {
            return "";
        }
    }

    public static String getUserTypeName(String user_type) {
        if (user_type.equals(UserType.USER_TYPE_MEMBER.getType())) {
            return "会员";
        } else if (user_type.equals(UserType.USER_TYPE_AGENT.getType())) {
            return "二级代理";
        } else if (user_type.equals(UserType.USER_TYPE_AREA.getType())) {
            return "区域代理";
        }
        return "未知身份";
    }


    //处理列表无数据
    public static void dealNoReqestData(BaseQuickAdapter adapter, RecyclerView recycle, SmartRefreshLayout refreshLayout) {
        dealNoReqestData(adapter, recycle, refreshLayout, 1);
    }


    //处理列表无数据
    public static void dealNoReqestData(BaseQuickAdapter adapter, RecyclerView recycle, SmartRefreshLayout refreshLayout, int page) {
        if (adapter != null && recycle != null && refreshLayout != null) {
            if (page == 1) {
                adapter.getData().clear();
            }
            if (adapter.getData().size() > 0) {
                adapter.loadMoreComplete();
                adapter.loadMoreEnd();
            } else {
                adapter.setNewData(new ArrayList<TaskInfoList>());
                adapter.loadMoreComplete();
                adapter.loadMoreEnd();
                if (ApiConfig.context instanceof Activity) {
                    adapter.setEmptyView(RecycleViewUtils.getEmptyView((Activity) ApiConfig.context, recycle));
                }
            }
            refreshLayout.finishRefresh();
        }
    }

    //处理列表无数据
    public static void dealNoReqestData(BaseQuickAdapter adapter, RecyclerView recycle, int page) {
        if (adapter == null && recycle == null) {
            return;
        }

        if (page == 1) {
            adapter.getData().clear();
        }
        if (adapter.getData().size() > 0) {
            adapter.loadMoreComplete();
            adapter.loadMoreEnd();
        } else {
            adapter.setNewData(new ArrayList<TaskInfoList>());
            adapter.loadMoreComplete();
            adapter.loadMoreEnd();
            if (ApiConfig.context instanceof Activity) {
                adapter.setEmptyView(RecycleViewUtils.getEmptyView((Activity) ApiConfig.context, recycle));
            }
        }
    }


    //处理列表无数据
    public static void dealNoReqestData(BaseQuickAdapter adapter, RecyclerView recycle) {
        dealNoReqestData(adapter, recycle, 1);
    }

    //处理请求列表数据
    public static void dealReqestData(BaseQuickAdapter adapter, RecyclerView recycle, List list, int page, SmartRefreshLayout refreshLayout) {
        if (adapter != null && recycle != null) {
            if (page == 1) {
                adapter.getData().clear();
            }
            if (list != null && list.size() > 0) {
                adapter.addData(list);
                if (list.size() == Integer.parseInt(Constans.PAGE_SIZE)) {
                    adapter.loadMoreComplete();
                } else {
                    adapter.loadMoreComplete();
                    adapter.loadMoreEnd();
                }
            } else {
                TUtils.dealNoReqestData(adapter, recycle);
            }
            if (refreshLayout != null) {
                refreshLayout.finishRefresh();
            }
        }

    }

    public static void dealReqestData(BaseQuickAdapter adapter, RecyclerView recycle, List list, int page) {
        dealReqestData(adapter, recycle, list, page, null);
        if (adapter != null && recycle != null) {
            if (page == 1) {
                adapter.getData().clear();
            }
            if (list != null && list.size() > 0) {
                adapter.addData(list);
                if (list.size() == Integer.parseInt(Constans.PAGE_SIZE)) {
                    adapter.loadMoreComplete();
                } else {
                    adapter.loadMoreComplete();
                    adapter.loadMoreEnd();
                }
            } else {
                TUtils.dealNoReqestData(adapter, recycle);
            }
        }

    }

    public static List<AreaBean> getAllCitys() {
        String citys = SPUtils.getInstance().getString(Constans.PASS_ALL_CITYS);
        if (!TextUtils.isEmpty(citys)) {
            cityInfos = new Gson().fromJson(citys, new TypeToken<List<AreaBean>>() {
            }.getType());
            return cityInfos;
        }
        return null;
    }

    public static void setAllCitys(List<AreaBean> citys) {
        SPUtils.getInstance().put(Constans.PASS_ALL_CITYS, new Gson().toJson(citys));

    }

    //客服页面
    public static void openKf() {
        if (TextUtils.isEmpty(SPUtils.getInstance().getString(Constans.KEFU))) {
            ToastUtils.showShort("客服信息不存在");
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putString(Constans.PASS_NAME, "客服");
        bundle.putString(Constans.PASS_STRING, SPUtils.getInstance().getString(Constans.KEFU));
        ActivityUtils.startActivity(bundle, OpenWebViewActivity.class);
    }


    public static String getHideAccount(String account) {
        if (TextUtils.isEmpty(account)) {
            return "无账号";
        } else {

        }
        return account;
    }

    public static void setRecycleEmpty(BaseQuickAdapter collectedAdapter, RecyclerView recyclerView) {
        collectedAdapter.setNewData(new ArrayList<>());
        collectedAdapter.setEmptyView(RecycleViewUtils.getEmptyView((Activity) ApiConfig.context, recyclerView));
    }

    //格式：2019-04-26 17:29:12
    public static String getEndTimeTips(String end_time) {
        long endTime = TimeUtils.string2Millis(end_time);
        long differ = endTime - TimeUtils.getNowMills();
        return millis2FitTimeSpan(differ, 3);
    }


    public static String millis2FitTimeSpan(long millis, int precision) {
        return millis2FitTimeSpan(millis, precision, true);
    }


    public static String millis2FitTimeSpan(long millis, int precision, boolean isShowAllUnit) {
        if (precision <= 0) return null;
        precision = Math.min(precision, 4);
        String[] units = {"天", "小时", "分钟", " "};
        if (millis == 0) return 0 + units[precision - 1];
        StringBuilder sb = new StringBuilder();
        if (millis < 0) {
            sb.append("-");
            millis = -millis;
        }
        int[] unitLen = {60 * 60 * 1000 * 24, 60 * 60 * 1000, 60 * 1000, 1000};
        for (int i = 0; i < precision; i++) {
//            if (millis >= unitLen[i]) {
            long mode = millis / unitLen[i];
            millis -= mode * unitLen[i];
            String clockIndex = String.valueOf(mode);
            if (mode < 10) {
                if (i != 0) {
                    clockIndex = "0" + clockIndex;
                }
            }
            if (isShowAllUnit) {
                sb.append(clockIndex).append(units[i]);
            } else {
                if (i > 0) {
                    sb.append(clockIndex).append(units[i]);
                }
            }
//            }
        }
        return sb.toString();
    }


    /**
     * 1: "待工作",
     * 2: "待提交",
     * 3: "待审核",
     * 4: "已通过",
     * 5: "未通过"
     * 6 已作废
     */
    public static List<TaskType> getTaskType() {
        List<TaskType> taskTypeList = new ArrayList<>();
        taskTypeList.add(new TaskType(-1, "全部"));
        taskTypeList.add(new TaskType(1, "待工作"));//待工作 和待提交合并
//        taskTypeList.add(new TaskType(2,"待提交"));
        taskTypeList.add(new TaskType(3, "待审核"));
        taskTypeList.add(new TaskType(4, "已通过"));
        taskTypeList.add(new TaskType(5, "未通过"));//6 、7 未通过、已作废、已超时合并
        taskTypeList.add(new TaskType(6, "已作废"));


        return taskTypeList;

    }

    //获取字符串中的 数字
    public static String getIntegerInString(String tips) {
        if (TextUtils.isEmpty(tips)) {
            return "0";
        }
        String regEx = "[^0-9]";
        try {
            Pattern p = Pattern.compile(regEx);
            Matcher m = p.matcher(tips);

            return m.replaceAll("").trim();

        } catch (Exception e) {
            System.out.print("erro---");
        }
        return "0";

    }

    public static void openImageViews(String[] imageUrls, int position) {


        List<ImageInfo> imageInfo = new ArrayList<>();
        if (imageUrls == null || imageUrls.length == 0) {
            return;
        }

        for (String item : imageUrls) {
            ImageInfo info = new ImageInfo();
            info.bigImageUrl = item;
            info.thumbnailUrl = item;
            imageInfo.add(info);
        }


        Intent intent = new Intent(ApiConfig.context, ImagePreviewActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(ImagePreviewActivity.IMAGE_INFO, (Serializable) imageInfo);
        bundle.putInt(ImagePreviewActivity.CURRENT_ITEM, position);
        intent.putExtras(bundle);
        ApiConfig.context.startActivity(intent);
        ((Activity) ApiConfig.context).overridePendingTransition(0, 0);
    }


    public static boolean isMemberType() {
        return TUtils.getUserInfo() != null && TUtils.getUserInfo().user_type.equals(UserType.USER_TYPE_MEMBER.getType());
    }

    public static String getFromAssets(String fileName) {
        try {
            InputStreamReader inputReader = new InputStreamReader(ApiConfig.context.getResources().getAssets().open(fileName));
            BufferedReader bufReader = new BufferedReader(inputReader);
            String line = "";
            String Result = "";
            while ((line = bufReader.readLine()) != null)
                Result += line;
            return Result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
