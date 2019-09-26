package com.task.system.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.FutureTarget;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;
import com.task.system.Constans;
import com.task.system.R;
import com.task.system.api.API;
import com.task.system.api.TaskInfo;
import com.task.system.api.TaskService;
import com.task.system.bean.SimpleBeanInfo;
import com.task.system.utils.PerfectClickListener;
import com.task.system.utils.TUtils;
import com.task.system.utils.ThreadManager;
import com.task.system.utils.Util;
import com.tencent.connect.share.QQShare;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.yc.lib.api.ApiCallBack;
import com.yc.lib.api.ApiConfig;
import com.yc.lib.api.utils.SysUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import retrofit2.Call;

import static android.os.Environment.DIRECTORY_DCIM;


//打开网页
public class OpenWebViewActivity extends BaseActivity {

    private String name;
    private String url;
    private WebView webView;

    public final static int FILECHOOSER_RESULTCODE = 1;
    public final static int FILECHOOSER_RESULTCODE_FOR_ANDROID_5 = 2;

    private String webType;
    // IWXAPI 是第三方app和微信通信的openApi接口
    private IWXAPI api;
    private String shareurl, title, subInfo, shareIcon;
    private Tencent mTencent;
    private DialogPlus shareDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_web_view);
        webView = getView(R.id.webview);
        name = getIntent().getStringExtra(Constans.PASS_NAME);
        url = getIntent().getStringExtra(Constans.PASS_STRING);
        webType = getIntent().getStringExtra(Constans.ARTICAL_TYPE);
        LogUtils.w("dyc",url);
        setTitle("" + name);
        initwebview();
        regToWx();


        ivRightFunction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(shareurl)) {
                    shareDialog();
                } else {
                    getShareInfo();
                }

            }
        });




        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle("温馨提示").setMessage(message).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        result.cancel();
                        result.confirm();
                    }
                });
                builder.setCancelable(false);
//                builder.setIcon(R.mipmap.);
                AlertDialog dialog = builder.create();
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
                return true;
            }

            //扩展浏览器上传文件
            //3.0++版本
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
                openFileChooserImpl(uploadMsg);
            }

            //3.0--版本
            public void openFileChooser(ValueCallback<Uri> uploadMsg) {
                openFileChooserImpl(uploadMsg);
            }

            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
                openFileChooserImpl(uploadMsg);
            }

            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                onenFileChooseImpleForAndroid(filePathCallback);
                return true;
            }
        });

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });


        if (TextUtils.isEmpty(webType)) {
            webView.loadUrl(url);
        }else{
            if (webType.equals(Constans.INTERGRAY_CODE)){
                ivRightFunction.setVisibility(View.VISIBLE);
                ivRightFunction.setImageResource(R.mipmap.iv_share);
                getIntergrayGame();
                getShareInfo();
            }else if(webType.equals(Constans.MY_INVITER_CODE)){
                ivRightFunction.setVisibility(View.VISIBLE);
                ivRightFunction.setImageResource(R.mipmap.iv_share);
                getUserShare();
                getUserShareInfo();
            }else if (webType.equals(Constans.ORDER_ROOLBACK_REASON)){
                getOrderReaseon();
            }else if(webType.equals(Constans.TASK_ROOLBACK_REASON)){
                getTaskReaseon();
            }else {
                getWebType();
            }
        }


        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               onBackPressed();
            }
        });


        webView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final WebView.HitTestResult hitTestResult = webView.getHitTestResult();
                if (hitTestResult.getType() == WebView.HitTestResult.IMAGE_TYPE ||
                        hitTestResult.getType() == WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE) {
                    // 弹出保存图片的对话框

                    new AlertDialog.Builder(OpenWebViewActivity.this).setItems(new String[]{"保存图片"}, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String pic = hitTestResult.getExtra();//获取图片
                            //保存图片到相册
                            new Thread(() -> saveImage(pic)).start();

                        }
                    }).show();
                    return true;

                }
                return false;
            }
        });

    }

    public void saveImage(String data) {



        FutureTarget<Bitmap> bitmap = Glide.with(this)
                .asBitmap()
                .load(data)
                .submit();
        try{
            if (bitmap != null) {
                save2Album(bitmap.get(), new SimpleDateFormat("SXS_yyyyMMddHHmmss", Locale.getDefault()).format(new Date()) + ".jpg");
            } else {
                SysUtils.showToast("保存失败");
            }
        }catch (Exception e){
            SysUtils.showToast("保存失败");
            e.printStackTrace();
        }
    }

    public Bitmap webData2bitmap(String data) {
        byte[] imageBytes = Base64.decode(data.split(",")[1], Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
    }

    private void save2Album(Bitmap bitmap, String fileName) {
        File file = new File(Environment.getExternalStoragePublicDirectory(DIRECTORY_DCIM), fileName);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            runOnUiThread(() -> {
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
                SysUtils.showToast("保存成功");
            });
        } catch (Exception e) {
            SysUtils.showToast("保存失败");
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (Exception ignored) {
            }
        }
    }


    private void getShareInfo() {
        showLoadingBar();
        HashMap<String, String> maps = new HashMap<>();
        maps.put("uid", TUtils.getUserId());
        maps.put("wheel_id", url);//游戏类型  url 是id
        Call<TaskInfo> call = ApiConfig.getInstants().create(TaskService.class).getWheelShare(TUtils.getParams(maps));

        API.getObject(call, SimpleBeanInfo.class, new ApiCallBack<SimpleBeanInfo>() {
            @Override
            public void onSuccess(int msgCode, String msg, SimpleBeanInfo data) {
                dismissLoadingBar();
                if (!TextUtils.isEmpty(data.url)) {
                    shareurl = data.url;
                    title = data.title;
                    subInfo = data.sub_title;
                    shareIcon = data.thumbnail;
                }
            }

            @Override
            public void onFaild(int msgCode, String msg) {
                dismissLoadingBar();
            }
        });
    }


    private void getUserShareInfo() {
        showLoadingBar();
        HashMap<String, String> maps = new HashMap<>();
        maps.put("uid", TUtils.getUserId());
        Call<TaskInfo> call = ApiConfig.getInstants().create(TaskService.class).getUserShare(TUtils.getParams(maps));

        API.getObject(call, SimpleBeanInfo.class, new ApiCallBack<SimpleBeanInfo>() {
            @Override
            public void onSuccess(int msgCode, String msg, SimpleBeanInfo data) {
                dismissLoadingBar();
                if (!TextUtils.isEmpty(data.url)) {
                    shareurl = data.url;
                    title = data.title;
                    subInfo = data.sub_title;
                    shareIcon = data.thumbnail;
                }
            }

            @Override
            public void onFaild(int msgCode, String msg) {
                dismissLoadingBar();
            }
        });
    }



    private void regToWx() {
        // 通过WXAPIFactory工厂，获取IWXAPI的实例
        api = WXAPIFactory.createWXAPI(this, Constans.WX_SHARE_APP_ID, true);

        // 将应用的appId注册到微信
        api.registerApp(Constans.WX_SHARE_APP_ID);
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()){
            webView.goBack();
        }else{
            OpenWebViewActivity.super.onBackPressed();
        }
    }

    private void getIntergrayGame() {
        HashMap<String, String> hashMap = new HashMap();
        hashMap.put("wheel_id", url);
        Call<TaskInfo> call = ApiConfig.getInstants().create(TaskService.class).getPlayUl(TUtils.getParams(hashMap));

        API.getObject(call, SimpleBeanInfo.class, new ApiCallBack<SimpleBeanInfo>() {
            @Override
            public void onSuccess(int msgCode, String msg, SimpleBeanInfo data) {
                webView.loadUrl(data.url);
            }

            @Override
            public void onFaild(int msgCode, String msg) {


            }
        });
    }

    //获取分享地址
    private void getUserShare() {
        HashMap<String, String> hashMap = new HashMap();
        Call<TaskInfo> call = ApiConfig.getInstants().create(TaskService.class).getUserShareUrl(TUtils.getParams(hashMap));

        API.getObject(call, SimpleBeanInfo.class, new ApiCallBack<SimpleBeanInfo>() {
            @Override
            public void onSuccess(int msgCode, String msg, SimpleBeanInfo data) {
                webView.loadUrl(data.url);
            }

            @Override
            public void onFaild(int msgCode, String msg) {


            }
        });
    }

    private void getWebType() {
        HashMap<String, String> hashMap = new HashMap();
        hashMap.put("aid", webType);
        Call<TaskInfo> call = ApiConfig.getInstants().create(TaskService.class).getArticalDetail(TUtils.getParams(hashMap));

        API.getObject(call, SimpleBeanInfo.class, new ApiCallBack<SimpleBeanInfo>() {
            @Override
            public void onSuccess(int msgCode, String msg, SimpleBeanInfo data) {
                setTitle(data.title);
                if (!TextUtils.isEmpty(data.content)) {
                    ((TextView)  findViewById(R.id.tv_content)).setText(Html.fromHtml(data.content));
//                    webView.loadDataWithBaseURL("about:blank",data.content, "text/html",  "utf-8", null);
                }
            }

            @Override
            public void onFaild(int msgCode, String msg) {


            }
        });
    }


    //订单审核理由
    private void getOrderReaseon() {
        HashMap<String, String> hashMap = new HashMap();
        hashMap.put("order_id", webType);
        Call<TaskInfo> call = ApiConfig.getInstants().create(TaskService.class).getAuditReason(TUtils.getParams(hashMap));

        API.getObject(call, SimpleBeanInfo.class, new ApiCallBack<SimpleBeanInfo>() {
            @Override
            public void onSuccess(int msgCode, String msg, SimpleBeanInfo data) {
                if (!TextUtils.isEmpty(data.content)) {
                    ((TextView)  findViewById(R.id.tv_content)).setText(data.content);
                }else{
                    ((TextView) findViewById(R.id.tv_content)).setText(getString(R.string.no_reason_tips));
                }
            }

            @Override
            public void onFaild(int msgCode, String msg) {
                ((TextView) findViewById(R.id.tv_content)).setText(getString(R.string.no_reason_tips));

            }
        });
    }

    //任务审核理由
    private void getTaskReaseon() {
        HashMap<String, String> hashMap = new HashMap();
        hashMap.put("task_id", webType);
        Call<TaskInfo> call = ApiConfig.getInstants().create(TaskService.class).getTaskAuditReason(TUtils.getParams(hashMap));

        API.getObject(call, SimpleBeanInfo.class, new ApiCallBack<SimpleBeanInfo>() {
            @Override
            public void onSuccess(int msgCode, String msg, SimpleBeanInfo data) {
                if (!TextUtils.isEmpty(data.content)) {
                    ((TextView)  findViewById(R.id.tv_content)).setText(data.content);
                }else{
                    ((TextView) findViewById(R.id.tv_content)).setText(getString(R.string.no_reason_tips));
                }
            }

            @Override
            public void onFaild(int msgCode, String msg) {
                ((TextView) findViewById(R.id.tv_content)).setText(getString(R.string.no_reason_tips));

            }
        });
    }




    public ValueCallback<Uri> mUploadMessage;
    private void openFileChooserImpl(ValueCallback<Uri> uploadMsg) {
        mUploadMessage = uploadMsg;
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("image/*");
        startActivityForResult(Intent.createChooser(i, "File Chooser"), FILECHOOSER_RESULTCODE);
    }

    public ValueCallback<Uri[]> mUploadMessageForAndroid5;
    private void onenFileChooseImpleForAndroid(ValueCallback<Uri[]> filePathCallback) {
        mUploadMessageForAndroid5 = filePathCallback;
        Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
        contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
        contentSelectionIntent.setType("image/*");

        Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
        chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
        chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser");

        startActivityForResult(chooserIntent, FILECHOOSER_RESULTCODE_FOR_ANDROID_5);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,Intent intent) {
        if (requestCode == FILECHOOSER_RESULTCODE) {
            if (null == mUploadMessage)
                return;
            Uri result = intent == null || resultCode != RESULT_OK ? null: intent.getData();
            mUploadMessage.onReceiveValue(result);
            mUploadMessage = null;

        } else if (requestCode == FILECHOOSER_RESULTCODE_FOR_ANDROID_5){
            if (null == mUploadMessageForAndroid5)
                return;
            Uri result = (intent == null || resultCode != RESULT_OK) ? null: intent.getData();
            if (result != null) {
                mUploadMessageForAndroid5.onReceiveValue(new Uri[]{result});
            } else {
                mUploadMessageForAndroid5.onReceiveValue(new Uri[]{});
            }
            mUploadMessageForAndroid5 = null;
        }
    }

    private void initwebview() {
        WebSettings webSettings = webView.getSettings();
        // 让WebView能够执行javaScript
        webSettings.setJavaScriptEnabled(true);
        // 让JavaScript可以自动打开windows
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        // 设置缓存
        webSettings.setAppCacheEnabled(true);
        // 设置缓存模式,一共有四种模式
//        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        // 设置缓存路径
//        webSettings.setAppCachePath("");
        // 支持缩放(适配到当前屏幕)
        webSettings.setSupportZoom(true);
        // 将图片调整到合适的大小
        webSettings.setUseWideViewPort(true);
        // 支持内容重新布局,一共有四种方式
        // 默认的是NARROW_COLUMNS
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        // 设置可以被显示的屏幕控制
        webSettings.setDisplayZoomControls(true);
        // 设置默认字体大小
        webSettings.setDefaultFontSize(12);
        webSettings.setPluginState(WebSettings.PluginState.ON_DEMAND);
        webSettings.setDatabaseEnabled(true);


        //文件权限
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowFileAccessFromFileURLs(true);
        webSettings.setAllowUniversalAccessFromFileURLs(true);


        webSettings.setDomStorageEnabled(true);//开启DOM storage API功能
        webSettings.setDatabaseEnabled(true);//开启database storeage API功能
        String cacheDirPath = getFilesDir().getAbsolutePath()+ "/webcache";//缓存路径
        webSettings.setDatabasePath(cacheDirPath);//设置数据库缓存路径
        webSettings.setAppCachePath(cacheDirPath);//设置AppCaches缓存路径
        webSettings.setAppCacheEnabled(true);


        webSettings.setUserAgentString("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.132 Safari/537.36");
    }

    @Override
    protected void onDestroy() {
        webView.clearCache(true);
        webView.clearFormData();
        webView.clearHistory();
        super.onDestroy();
    }

    private void shareDialog() {
        if (shareDialog == null) {
            shareDialog = DialogPlus.newDialog(this)
                    .setContentHolder(new ViewHolder(R.layout.dialog_share_lmsy)).create();
            View btnFriend = shareDialog.findViewById(R.id.btn_share_friend);
            View btnWechat = shareDialog.findViewById(R.id.btn_share_wechat);
            View btnQQ = shareDialog.findViewById(R.id.btn_share_qq);
            View btnCancel = shareDialog.findViewById(R.id.btn_cancel);


            btnFriend.setOnClickListener(new PerfectClickListener() {
                @Override
                protected void onNoDoubleClick(View v) {
                    shareDialog.dismiss();

                    if (!checkWeiXin()){
                        SysUtils.showToast("请安装微信");
                        return;
                    }
                    shareWx(1);
                }
            });

            btnWechat.setOnClickListener(new PerfectClickListener() {
                @Override
                protected void onNoDoubleClick(View v) {
                    shareDialog.dismiss();

                    if (!checkWeiXin()){
                        SysUtils.showToast("请安装微信");
                        return;
                    }
                    shareWx(0);

                }
            });

            btnQQ.setOnClickListener(new PerfectClickListener() {
                @Override
                protected void onNoDoubleClick(View v) {
                    shareDialog.dismiss();
                    //JIGUANG-JShare: [QQHelper] text is too long, the limit is 40


                    if (!checkQQ()){
                        SysUtils.showToast("请安装QQ");
                        return;
                    }

                    qqShare();
//                    ShareUtil.share(QQ.Name, taskInfoItem.title, taskInfoItem.sub_title, "https://www.baidu.com/", taskInfoItem.thumbnail);

                }
            });


            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    shareDialog.dismiss();
                }
            });
        }
        if (!shareDialog.isShowing()) {
            shareDialog.show();
        }
    }

    private void shareWx(int flag) {
        if (!checkWeiXin()){
            SysUtils.showToast("请安装微信");
            return;
        }
        //初始化一个WXWebpageObject，填写url
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = shareurl;

//用 WXWebpageObject 对象初始化一个 WXMediaMessage 对象
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = title;
        msg.description = subInfo;
        Bitmap thumbBmp = BitmapFactory.decodeResource(getResources(), R.mipmap.logo_smallest);
        msg.thumbData = Util.bmpToByteArray(thumbBmp, true);

//构造一个Req
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("webpage");
        req.message = msg;
        //0   好友  1  朋友圈
        req.scene = flag == 0 ? SendMessageToWX.Req.WXSceneSession : SendMessageToWX.Req.WXSceneTimeline;

//调用api接口，发送数据到微信
        api.sendReq(req);
    }

    IUiListener qqShareListener = new IUiListener() {
        @Override
        public void onCancel() {
            ToastUtils.showShort("取消分享");
        }

        @Override
        public void onComplete(Object response) {
            ToastUtils.showShort("已分享");
        }

        @Override
        public void onError(UiError e) {
        }
    };


    private boolean checkWeiXin() {
        try {
            getPackageManager().getApplicationInfo("com.tencent.mm", PackageManager.GET_UNINSTALLED_PACKAGES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    private boolean checkQQ() {
        try {
            getPackageManager().getApplicationInfo("com.tencent.mobileqq", PackageManager.GET_UNINSTALLED_PACKAGES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }


    private void qqShare() {
        mTencent = Tencent.createInstance(Constans.QQ_SHARE_ID, this);
        final Bundle params = new Bundle();
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
        params.putString(QQShare.SHARE_TO_QQ_TITLE, title);
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY, subInfo);
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, shareurl);
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, shareIcon);
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, getString(R.string.app_name));


        ThreadManager.getMainHandler().post(new Runnable() {

            @Override
            public void run() {
                if (null != mTencent) {
                    mTencent.shareToQQ(OpenWebViewActivity.this, params, qqShareListener);
                }
            }
        });
    }

    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }

}
