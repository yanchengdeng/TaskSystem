package com.task.system.utils;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.DeviceUtils;
import com.blankj.utilcode.util.EncodeUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SPUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.task.system.Constans;
import com.task.system.bean.CityInfo;
import com.task.system.bean.TaskInfoList;
import com.task.system.bean.UserInfo;
import com.task.system.enums.UserType;
import com.yc.lib.api.ApiConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TUtils {

    private static List<CityInfo> cityInfos = new ArrayList<>();

    public static HashMap<String, String> getParams(HashMap<String, String> data) {
        data.put("version", AppUtils.getAppVersionName());
        data.put("app_imei", getImei());
        data.put("app_id", Constans.APP_ID);
        data.put("app_sign", getAppSign());
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
            return "正式会员";
        } else if (user_type.equals(UserType.USER_TYPE_AGENT.getType())) {
            return "代理";
        } else if (user_type.equals(UserType.USER_TYPE_AGENT.getType())) {
            return "区域";
        }
        return "正式会员";
    }

    //处理列表无数据
    public static void dealNoReqestData(BaseQuickAdapter adapter, RecyclerView recycle, SmartRefreshLayout refreshLayout) {
        if (adapter != null && recycle != null && refreshLayout != null) {
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
    public static void dealNoReqestData(BaseQuickAdapter adapter, RecyclerView recycle) {
        if (adapter == null && recycle == null) {
            return;
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

    //处理请求列表数据
    public static void dealReqestData(BaseQuickAdapter adapter, RecyclerView recycle, List list, int page, SmartRefreshLayout refreshLayout) {
        if (adapter != null && recycle != null && refreshLayout != null) {


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
            refreshLayout.finishRefresh();
        }

    }

    public static List<CityInfo> getAllCitys() {
        String citys = SPUtils.getInstance().getString(Constans.PASS_ALL_CITYS);
        if (!TextUtils.isEmpty(citys)) {
            cityInfos = new Gson().fromJson(citys, new TypeToken<List<CityInfo>>() {
            }.getType());
        }
        return cityInfos;
    }

    public static void setAllCitys(List<CityInfo> citys) {
        SPUtils.getInstance().put(Constans.PASS_ALL_CITYS, new Gson().toJson(citys));

    }
}
