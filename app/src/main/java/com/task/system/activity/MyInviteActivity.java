package com.task.system.activity;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.blankj.utilcode.util.ToastUtils;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;
import com.task.system.Constans;
import com.task.system.R;
import com.task.system.api.API;
import com.task.system.api.TaskInfo;
import com.task.system.api.TaskService;
import com.task.system.bean.InviteCode;
import com.task.system.bean.TaskInfoItem;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;

/**
 * Email: dengyc@dadaodata.com
 * FileName: MyInviteActivity.java
 * Author: dengyancheng
 * Date: 2019-08-11 16:02
 * Description: 我的邀请
 * History:
 */
public class MyInviteActivity extends BaseActivity {

    @BindView(R.id.tv_invide_code)
    TextView tvInvideCode;
    @BindView(R.id.btnInvite)
    TextView btnInvite;
    @BindView(R.id.btnSpread)
    TextView btnSpread;
    @BindView(R.id.tv_nums)
    TextView tvNums;
    @BindView(R.id.tv_nums_tips)
    TextView tvNumsTips;
    @BindView(R.id.tv_money)
    TextView tvMoney;
    @BindView(R.id.tv_money_tips)
    TextView tvMoneyTips;


    //正式数据 切换过来
    private String url, title, subInfo, shareIcon;
    private Tencent mTencent;
    // IWXAPI 是第三方app和微信通信的openApi接口
    private IWXAPI api;

    private InviteCode inviteCode;

    private DialogPlus shareDialog;

    private TaskInfoItem taskInfoItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_invite);
        ButterKnife.bind(this);
        setTitle("我的邀请");
        taskInfoItem  = new TaskInfoItem();
        taskInfoItem.title = "测试分享xx";
        taskInfoItem.sub_title = "测试分享";
        url = "https://www.baidu.com";


        getInviteCode();
        regToWx();


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
                ((TextView) findViewById(R.id.tv_invide_code)).setText("" + data.invite_code);
            }

            @Override
            public void onFaild(int msgCode, String msg) {
                ToastUtils.showShort(msg);
            }
        });


    }


    @OnClick({R.id.tv_invide_code, R.id.btnInvite, R.id.btnSpread})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_invide_code:
                break;
            case R.id.btnInvite:
                shareDialog();
                break;
            case R.id.btnSpread:
                shareDialog();
                break;
        }
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
                    if (!TextUtils.isEmpty(taskInfoItem.sub_title)) {
                        if (taskInfoItem.sub_title.length() > 40) {
                            taskInfoItem.sub_title = taskInfoItem.sub_title.substring(0, 40);
                        }
                    }

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
                    mTencent.shareToQQ(MyInviteActivity.this, params, qqShareListener);
                }
            }
        });
    }

    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }


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


}
