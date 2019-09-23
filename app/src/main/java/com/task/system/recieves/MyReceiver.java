package com.task.system.recieves;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.SPUtils;
import com.google.gson.Gson;
import com.task.system.Constans;
import com.task.system.activity.MessageListActivity;
import com.task.system.activity.OpenWebViewActivity;
import com.task.system.activity.TaskDetailActivity;
import com.task.system.bean.Extras;
import com.yc.lib.api.utils.SysUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.helper.Logger;

/**
 * Author: dengyc
 * Date: 2019-09-09 14:26
 * Description:
 * History:
 */
public class MyReceiver extends BroadcastReceiver {
    private static final String TAG = "JIGUANG-Example";

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            Bundle bundle = intent.getExtras();
            SysUtils.log("[MyReceiver] onReceive - " + intent.getAction() + ", extras: " + printBundle(bundle));

            if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
                String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
                SysUtils.log("[MyReceiver] 接收Registration Id : " + regId);
                if (!TextUtils.isEmpty(regId)) {
                    SPUtils.getInstance().put(Constans.JPUSH_REGIEST_ID, regId);
                }

                //send the Registration Id to your server...

            } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
                SysUtils.log("[MyReceiver] 接收到推送下来的自定义消息: " + bundle.getString(JPushInterface.EXTRA_MESSAGE));
//                processCustomMessage(context, bundle);

            } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
                SysUtils.log("[MyReceiver] 接收到推送下来的通知");
                int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
                SysUtils.log("[MyReceiver] 接收到推送下来的通知的ID: " + notifactionId);

            } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
                String message = bundle.getString(JPushInterface.EXTRA_EXTRA);

                SysUtils.log("[MyReceiver] 用户点击打开了通知" + message);

                /**
                 * 1-任务; 2-游戏; 3-消息
                 */

                if (TextUtils.isEmpty(message)) {
                    return;
                }
                Extras jpushExtraInfo = new Gson().fromJson(message, Extras.class);

                SysUtils.log("[MyReceiver] 用户点击打开了通知解析：" + jpushExtraInfo);

                if (jpushExtraInfo != null) {
                    Intent goIntent = null;
                    if (!AppUtils.isAppForeground("com.task.system")) {
                        AppUtils.launchApp("com.task.system");
                    }
                    if (jpushExtraInfo.getType().equals("1")) {
                        goIntent = new Intent(context, TaskDetailActivity.class);
                        Bundle task = new Bundle();
                        task.putString(Constans.PASS_STRING, String.valueOf(jpushExtraInfo.getId()));
                        goIntent.putExtras(task);
                    } else if (jpushExtraInfo.getType().equals("2")) {
                        goIntent = new Intent(context, OpenWebViewActivity.class);
                        Bundle game = new Bundle();
                        game.putString(Constans.PASS_NAME, bundle.getString(JPushInterface.EXTRA_ALERT));
                        game.putString(Constans.ARTICAL_TYPE, Constans.INTERGRAY_CODE);
                        game.putString(Constans.PASS_STRING, String.valueOf(jpushExtraInfo.getId()));
                        goIntent.putExtras(game);
                    } else if (jpushExtraInfo.getType().equals("3")) {
                        goIntent = new Intent(context, MessageListActivity.class);
                    }

                    if (goIntent != null) {
                        goIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        context.startActivity(goIntent);
                    }
                }


            } else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
                Logger.d(TAG, "[MyReceiver] 用户收到到RICH PUSH CALLBACK: " + bundle.getString(JPushInterface.EXTRA_EXTRA));
                //在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity， 打开一个网页等..

                String message = bundle.getString(JPushInterface.EXTRA_EXTRA);

                SysUtils.log("[MyReceiver------] 用户点击打开了通知" + message);

            } else if (JPushInterface.ACTION_NOTIFICATION_CLICK_ACTION == intent.getAction()) {
                Logger.d(TAG, "[MyReceiver] 用户收到到RICH ACTION_NOTIFICATION_CLICK_ACTION CALLBACK: " + bundle.getString(JPushInterface.EXTRA_EXTRA));
                //在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity， 打开一个网页等..

                String message = bundle.getString(JPushInterface.EXTRA_EXTRA);

                SysUtils.log("[MyReceiver------] 用户点击打开了通知" + message);
            } else if (JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
                boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
                Logger.w(TAG, "[MyReceiver]" + intent.getAction() + " connected state change to " + connected);
            } else {
                Logger.d(TAG, "[MyReceiver] Unhandled intent - " + intent.getAction());
            }
        } catch (Exception e) {

        }

    }

    // 打印所有的 intent extra 数据
    private static String printBundle(Bundle bundle) {
        StringBuilder sb = new StringBuilder();
        for (String key : bundle.keySet()) {
            if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
            } else if (key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key));
            } else if (key.equals(JPushInterface.EXTRA_EXTRA)) {
                if (TextUtils.isEmpty(bundle.getString(JPushInterface.EXTRA_EXTRA))) {
                    Logger.i(TAG, "This message has no Extra data");
                    continue;
                }

                try {
                    JSONObject json = new JSONObject(bundle.getString(JPushInterface.EXTRA_EXTRA));
                    Iterator<String> it = json.keys();

                    while (it.hasNext()) {
                        String myKey = it.next();
                        sb.append("\nkey:" + key + ", value: [" +
                                myKey + " - " + json.optString(myKey) + "]");
                    }
                } catch (JSONException e) {
                    Logger.e(TAG, "Get message extra JSON error!");
                }

            } else {
                sb.append("\nkey:" + key + ", value:" + bundle.get(key));
            }
        }
        return sb.toString();
    }

    //send msg to MainActivity
//    private void processCustomMessage(Context context, Bundle bundle) {
//        if (MainActivity.isForeground) {
//            String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
//            String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
//            Intent msgIntent = new Intent(MainActivity.MESSAGE_RECEIVED_ACTION);
//            msgIntent.putExtra(MainActivity.KEY_MESSAGE, message);
//            if (!ExampleUtil.isEmpty(extras)) {
//                try {
//                    JSONObject extraJson = new JSONObject(extras);
//                    if (extraJson.length() > 0) {
//                        msgIntent.putExtra(MainActivity.KEY_EXTRAS, extras);
//                    }
//                } catch (JSONException e) {
//
//                }
//
//            }
//            LocalBroadcastManager.getInstance(context).sendBroadcast(msgIntent);
//        }
//    }

}

