package com.task.system.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;
import com.task.system.Constans;
import com.task.system.R;
import com.task.system.api.API;
import com.task.system.api.TaskInfo;
import com.task.system.api.TaskService;
import com.task.system.bean.SimpleBeanInfo;
import com.task.system.utils.TUtils;
import com.yc.lib.api.ApiCallBack;
import com.yc.lib.api.ApiConfig;

import java.util.HashMap;

import retrofit2.Call;


//打开网页
public class OpenWebViewActivity extends BaseActivity {

    private String name;
    private String url;
    private WebView webView;

    public final static int FILECHOOSER_RESULTCODE = 1;
    public final static int FILECHOOSER_RESULTCODE_FOR_ANDROID_5 = 2;

    private String webType;

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
                getIntergrayGame();
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
        hashMap.put("wheel_id", webType);
        Call<TaskInfo> call = ApiConfig.getInstants().create(TaskService.class).getPlayUl(TUtils.getParams(hashMap));

        API.getObject(call, SimpleBeanInfo.class, new ApiCallBack<SimpleBeanInfo>() {
            @Override
            public void onSuccess(int msgCode, String msg, SimpleBeanInfo data) {
                setTitle(data.title);
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
                    ((TextView)  findViewById(R.id.tv_content)).setText(data.content);
//                    webView.loadDataWithBaseURL("about:blank",data.content, "text/html",  "utf-8", null);
                }
            }

            @Override
            public void onFaild(int msgCode, String msg) {


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
}
