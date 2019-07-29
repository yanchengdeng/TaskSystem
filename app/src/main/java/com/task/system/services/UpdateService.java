package com.task.system.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.FileProvider;
import android.widget.Toast;

import com.blankj.utilcode.util.AppUtils;
import com.task.system.R;
import com.task.system.utils.SdcardUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;

import java.io.File;

import okhttp3.Call;

/**
 * Created by wj on 2017/5/9.
 */

public class UpdateService extends Service {

    private static final CharSequence NOTIFICATION_CHANNEL_NAME = "lz_notice";
    private String apkURL;

    private NotificationManager notificationManager;

    private NotificationCompat.Builder builder;

    private int mProceess = -1;

    private SdcardUtils sdcardUtils;

    private static final String PACKAGE_NAME = AppUtils.getAppPackageName();

    private static final String APP_NAME = AppUtils.getAppName();
    public static final String NOTIFICATION_CHANNEL_ID = "4655";


    @Override
    public void onDestroy() {
        super.onDestroy();
        OkHttpUtils.getInstance().cancelTag(this);
    }

    @Override
    public void onCreate() {
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_LOW);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(false);
//            notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            notificationManager.createNotificationChannel(notificationChannel);
        }
        sdcardUtils = new SdcardUtils();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            notifyUser("下载失败", 0);
            stopSelf();//取消
        } else {
            apkURL = intent.getStringExtra("apkUrl");
            initNotify();
            initFileDir();
            notifyUser("下载开始", 0);
            startDownload();//启动下载
        }
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 创建文件夹
     */
    private void initFileDir() {
        if (SdcardUtils.existSdcard()) {
            if (!sdcardUtils.isFileExist(PACKAGE_NAME)) {//不存在就重新创建文件
                sdcardUtils.creatSDDir(PACKAGE_NAME);
            }
        } else {
            Toast.makeText(this, "sd卡不存在！", Toast.LENGTH_SHORT).show();
        }
    }

    private void initNotify() {

        builder = new NotificationCompat.Builder(this,NOTIFICATION_CHANNEL_ID);
        builder.setContentTitle(APP_NAME);
        builder.setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_round));
        builder.setAutoCancel(true);
//        builder.setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
        builder.setWhen(System.currentTimeMillis());
        builder.setContentIntent(PendingIntent.getActivity(this, 0, new Intent(),
                PendingIntent.FLAG_UPDATE_CURRENT));
        builder.setDefaults(NotificationCompat.FLAG_ONLY_ALERT_ONCE);
        Notification notification = builder.build();

//这通知的其他属性，比如：声音和振动

//        notification.defaults |= Notification.DEFAULT_SOUND;
//        notification.defaults |= Notification.DEFAULT_VIBRATE;
    }

    private void startDownload() {
        OkHttpUtils//
                .get()//
                .tag(this)
                .url(apkURL)//
                .build()//
                .execute(new FileCallBack(sdcardUtils.getSDPATH() + "/" + PACKAGE_NAME, APP_NAME)//
                {
                    int fProgress;

                    @Override
                    public void inProgress(float progress, long total, int id) {//下载进度
                        super.inProgress(progress, total, id);
                        fProgress = (int) (100 * progress);
                        if (fProgress != mProceess) {//避免刷新太快，会卡死
                            mProceess = fProgress;
                            notifyUser("正在下载", fProgress);
                        }
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {//下载失败
                        notifyUser("下载失败", 0);
                        stopSelf();
                    }

                    @Override
                    public void onResponse(File response, int id) {//下载完成
                        notifyUser("下载完成", 100);
                        stopSelf();

                        getContentIntent();
                        if (notificationManager != null) {
                            notificationManager.cancelAll();
                        }

                    }
                });
    }

    private void notifyUser(String result, int process) {
        if (builder == null) {
            return;
        }
        if (process > 0 && process < 100) {
            builder.setProgress(100, process, false);
        } else {
            builder.setProgress(0, 0, false);
        }
        if (process >= 100) {
//            builder.setContentIntent(getContentIntent());
            getContentIntent();
        }

        builder.setContentInfo(process + "%");
        builder.setContentText(result);
        notificationManager.notify(0, builder.build());
    }

    private void getContentIntent() {
         File apkFile = new File(sdcardUtils.getSDPATH() + "/" + PACKAGE_NAME + "/" + APP_NAME);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        // 由于没有在Activity环境下启动Activity,设置下面的标签
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 24) { //判读版本是否在7.0以上
            //参数1 上下文, 参数2 Provider主机地址 和配置文件中保持一致   参数3  共享的文件
            Uri apkUri =
                    FileProvider.getUriForFile(UpdateService.this, PACKAGE_NAME + ".fileprovider", apkFile);
            //添加这一句表示对目标应用临时授权该Uri所代表的文件
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(apkFile),
                    "application/vnd.android.package-archive");
        }
        startActivity(intent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
