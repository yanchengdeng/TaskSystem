package com.task.system.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.blankj.utilcode.util.ToastUtils;
import com.task.system.Constans;
import com.task.system.R;
import com.task.system.api.API;
import com.task.system.api.TaskInfo;
import com.task.system.api.TaskService;
import com.task.system.bean.InviteCode;
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

import retrofit2.Call;

//我的邀请码
public class MyInviteCodeActivity extends BaseActivity {

    private boolean isFromRegister;

    //正式数据 切换过来
    private String url, title, subInfo, shareIcon;
    private Tencent mTencent;
    // IWXAPI 是第三方app和微信通信的openApi接口
    private IWXAPI api;

    private InviteCode inviteCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isFromRegister = getIntent().getBooleanExtra(Constans.PASS_STRING, false);
        if (isFromRegister) {
            setContentView(R.layout.activity_my_invite_code_register);
        } else {
            setContentView(R.layout.activity_my_invite_code);
        }
        setTitle("我的邀请码");
        regToWx();


        findViewById(R.id.btn_copy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (TextUtils.isEmpty(((TextView) findViewById(R.id.tv_invite_code)).getText().toString())) {
                    ToastUtils.showShort("没有邀请码");
                    return;
                }
                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                clipboardManager.setPrimaryClip(ClipData.newPlainText(null, ((TextView) findViewById(R.id.tv_invite_code)).getText()));
                ToastUtils.showShort("复制成功");
            }
        });
        if (isFromRegister) {
            getInviteCode();
        } else {
            getLeaderInviteCode();
        }


        findViewById(R.id.btn_share_friend).setOnClickListener(new PerfectClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                if (chekcShare()) {
                    shareWx(1);
                }
            }
        });

        findViewById(R.id.btn_share_wechat).setOnClickListener(new PerfectClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                shareWx(0);
            }
        });

        findViewById(R.id.btn_share_qq).setOnClickListener(new PerfectClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                qqShare();
            }
        });
    }

    private boolean chekcShare() {
        if (inviteCode != null && !TextUtils.isEmpty(inviteCode.title)) {
            url = inviteCode.url;
            title = inviteCode.title;
            subInfo = inviteCode.sub_title;
            shareIcon = inviteCode.thumbnail;
            return true;
        }

        ToastUtils.showShort("获取分享内容失败");
        return false;
    }

    private void regToWx() {
        // 通过WXAPIFactory工厂，获取IWXAPI的实例
        api = WXAPIFactory.createWXAPI(this, Constans.WX_SHARE_APP_ID, true);

        // 将应用的appId注册到微信
        api.registerApp(Constans.WX_SHARE_APP_ID);
    }

    //获取邀请码
    private void getInviteCode() {
        Call<TaskInfo> call = ApiConfig.getInstants().create(TaskService.class).getInviteCode(TUtils.getParams());

        API.getObject(call, InviteCode.class, new ApiCallBack<InviteCode>() {
            @Override
            public void onSuccess(int msgCode, String msg, InviteCode data) {
                dismissLoadingBar();
                ((TextView) findViewById(R.id.tv_invite_code)).setText("" + data.invite_code);
            }

            @Override
            public void onFaild(int msgCode, String msg) {
                ToastUtils.showShort(msg);
            }
        });


    }

    //获取邀请码  只有type=2  代理才有
    //v2.0.1 改为 type=3 的管理权限有
    private void getLeaderInviteCode() {
        showLoadingBar();
        Call<TaskInfo> call = ApiConfig.getInstants().create(TaskService.class).getInviteByAgent(TUtils.getParams());

        API.getObject(call, InviteCode.class, new ApiCallBack<InviteCode>() {
            @Override
            public void onSuccess(int msgCode, String msg, InviteCode data) {
                dismissLoadingBar();
                inviteCode = data;
                ((TextView) findViewById(R.id.tv_invite_code)).setText("" + data.invite_code);
            }

            @Override
            public void onFaild(int msgCode, String msg) {
                dismissLoadingBar();
                ToastUtils.showShort(msg);
            }
        });
    }


    private void shareWx(int flag) {
        //初始化一个WXWebpageObject，填写url
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = url;

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


    private void qqShare() {
        mTencent = Tencent.createInstance(Constans.QQ_SHARE_ID, this);
        final Bundle params = new Bundle();
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
        params.putString(QQShare.SHARE_TO_QQ_TITLE, title);
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY, subInfo);
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, url);
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, shareIcon);
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, getString(R.string.app_name));


        ThreadManager.getMainHandler().post(new Runnable() {

            @Override
            public void run() {
                if (null != mTencent) {
                    mTencent.shareToQQ(MyInviteCodeActivity.this, params, qqShareListener);
                }
            }
        });
    }

    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }

}
