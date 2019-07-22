package com.task.system.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;
import com.task.system.Constans;
import com.task.system.R;
import com.task.system.adapters.FragmentPagerItemAdapter;
import com.task.system.api.API;
import com.task.system.api.TaskInfo;
import com.task.system.api.TaskInfoList;
import com.task.system.api.TaskService;
import com.task.system.bean.OrderInfo;
import com.task.system.bean.SimpleBeanInfo;
import com.task.system.bean.TaskInfoItem;
import com.task.system.bean.UserInfo;
import com.task.system.event.RefreshUnreadCountEvent;
import com.task.system.fragments.FragmentStepInfo;
import com.task.system.fragments.FragmentTaskDetail;
import com.task.system.utils.PerfectClickListener;
import com.task.system.utils.TUtils;
import com.task.system.utils.ThreadManager;
import com.task.system.utils.Util;
import com.task.system.views.FragmentPagerItems;
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
import com.yc.lib.api.ApiCallBackList;
import com.yc.lib.api.ApiConfig;
import com.yc.lib.api.utils.SysUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;

public class TaskDetailActivity extends BaseSimpleActivity {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_collected)
    ImageView ivCollected;
    @BindView(R.id.iv_share)
    ImageView ivShare;
    @BindView(R.id.tablayout)
    TabLayout tablayout;
    @BindView(R.id.viewpage)
    ViewPager viewpage;
    @BindView(R.id.tv_custome)
    TextView tvCustome;
    @BindView(R.id.tv_give_up_work)
    TextView tvGiveUpWork;
    @BindView(R.id.tv_do_work)
    TextView tvDoWork;
    private TaskInfoItem taskInfoItem;
    private boolean isCollected = false;

    private DialogPlus shareDialog;
    private String order_id;
    private String task_id;


    // IWXAPI 是第三方app和微信通信的openApi接口
    private IWXAPI api;

    private String url, title, subInfo, shareIcon;
    private Tencent mTencent;

    private boolean isClickShareButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);
        ButterKnife.bind(this);
        tvTitle.setText("任务详情");


        Intent intent = getIntent();

        task_id = getIntent().getStringExtra(Constans.PASS_STRING);
        //xieshoujianzhi://detail?id=268
        if (checkHasScheme()) {
            task_id = intent.getDataString().split("id=")[1];
        }

        SysUtils.log(intent.getScheme() + "---" + intent.getDataString());
//        initData(taskInfoItem);
        tablayout.addTab(tablayout.newTab().setText("任务描述"), 0);
        tablayout.addTab(tablayout.newTab().setText("详细流程"), 1);
        getTaskDetail();
        regToWx();
        getShareInfo();


    }

    private boolean checkHasScheme(){
        return (!TextUtils.isEmpty(getIntent().getDataString()) && getIntent().getDataString().contains("id=")) ;
    }

    private void regToWx() {
        // 通过WXAPIFactory工厂，获取IWXAPI的实例
        api = WXAPIFactory.createWXAPI(this, Constans.WX_SHARE_APP_ID, true);

        // 将应用的appId注册到微信
        api.registerApp(Constans.WX_SHARE_APP_ID);
    }

    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }

    private FragmentPagerItems getFragmentsItem(Bundle bundle) {
        return FragmentPagerItems.with(mContext).add("任务描述", FragmentTaskDetail.class, bundle)
                .add("详细流程", FragmentStepInfo.class, bundle).create();
    }

    private void getTaskDetail() {
        showLoadingBar();
        HashMap<String, String> maps = new HashMap<>();
        if (!TextUtils.isEmpty(TUtils.getUserId())) {
            maps.put("uid", TUtils.getUserId());
        }
        maps.put("task_id", task_id);
        Call<TaskInfo> call = ApiConfig.getInstants().create(TaskService.class).getTaskDetail(TUtils.getParams(maps));

        API.getObject(call, TaskInfoItem.class, new ApiCallBack<TaskInfoItem>() {
            @Override
            public void onSuccess(int msgCode, String msg, TaskInfoItem data) {
                dismissLoadingBar();
                taskInfoItem = data;
                title = data.title;
                subInfo = data.sub_title + "";

                if (data.is_collect == 0) {
                    ivCollected.setImageResource(R.mipmap.iv_collect);
                    isCollected = false;
                } else {
                    ivCollected.setImageResource(R.mipmap.iv_collected);
                    isCollected = true;
                }
                initData(data);
            }

            @Override
            public void onFaild(int msgCode, String msg) {
                dismissLoadingBar();

            }
        });
    }


    private void getShareInfo() {
        showLoadingBar();
        HashMap<String, String> maps = new HashMap<>();
        maps.put("uid", TUtils.getUserId());
        maps.put("task_id", task_id);
        Call<TaskInfo> call = ApiConfig.getInstants().create(TaskService.class).getTaskShare(TUtils.getParams(maps));

        API.getObject(call, SimpleBeanInfo.class, new ApiCallBack<SimpleBeanInfo>() {
            @Override
            public void onSuccess(int msgCode, String msg, SimpleBeanInfo data) {
                dismissLoadingBar();
                if (!TextUtils.isEmpty(data.url)) {
                    url = data.url;
                    title = data.title;
                    subInfo = data.sub_title;
                    shareIcon = data.thumbnail;
                }
            }

            @Override
            public void onFaild(int msgCode, String msg) {
                if (isClickShareButton) {
                    SysUtils.showToast("" + msg);
                    isClickShareButton = false;
                }
                dismissLoadingBar();
            }
        });
    }

    /**
     * 0——待申请
     * 1——待工作
     * 2——待提交 // 待审核--客服审核
     * 3——待审核 // 待审核--客户审核
     * 4——已通过
     * 5——未通过
     * 6——已作废
     * 7——已超时
     */
    private void initData(TaskInfoItem data) {
        if (!TextUtils.isEmpty(data.order_status_title)) {
            tvDoWork.setText(data.order_status_title);
        }
        if (data.order_status == 1) {
            tvDoWork.setBackgroundColor(getResources().getColor(R.color.red));
            tvGiveUpWork.setVisibility(View.VISIBLE);
        } else if (data.order_status == 0) {
            tvGiveUpWork.setVisibility(View.GONE);
            tvDoWork.setBackgroundColor(getResources().getColor(R.color.red));
        } else {
            if (taskInfoItem.is_apply == 1) {
                tvDoWork.setBackgroundColor(getResources().getColor(R.color.red));
                tvGiveUpWork.setVisibility(View.VISIBLE);
            } else {
                tvDoWork.setBackgroundColor(getResources().getColor(R.color.give_up));
                tvGiveUpWork.setVisibility(View.GONE);
            }
        }

        order_id = data.order_id;

        Bundle bundle = new Bundle();
        bundle.putSerializable(Constans.PASS_OBJECT, data);
        FragmentPagerItemAdapter fragmentPagerItemAdapter = new FragmentPagerItemAdapter(getSupportFragmentManager(), getFragmentsItem(bundle));
        viewpage.setAdapter(fragmentPagerItemAdapter);
        tablayout.setupWithViewPager(viewpage);

    }

    @OnClick({R.id.iv_back, R.id.iv_collected, R.id.iv_share, R.id.tv_custome, R.id.tv_give_up_work, R.id.tv_do_work})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_collected:
               if (Util.checkLogin(mContext)) {
                   if (isCollected) {
                       doCancleCollected();
                   } else {
                       doCollected();
                   }
               }
                break;
            case R.id.iv_share:
                if (!TextUtils.isEmpty(url)) {
                    shareDialog();
                } else {
                    isClickShareButton = true;
                    getShareInfo();
                }
                break;
            case R.id.tv_custome:
                TUtils.openKf();
                break;
            case R.id.tv_give_up_work:
                if (Util.checkLogin(mContext)) {
                    giveUpTask();
                }
                break;
            case R.id.tv_do_work:
                if (Util.checkLogin(mContext)) {
                    if (taskInfoItem == null) {
                        getTaskDetail();
                        return;
                    }
                    if (taskInfoItem.order_status == 0) {
                        applyTask();
                    } else if (taskInfoItem.order_status == 1) {
                        //做下一步工作
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(Constans.PASS_OBJECT, taskInfoItem);
                        ActivityUtils.startActivityForResult(bundle, TaskDetailActivity.this, DoTaskStepActivity.class, 100);
                    } else if (taskInfoItem.is_apply == 1) {
                        applyTask();
                    } else {
                        ToastUtils.showShort("" + taskInfoItem.order_status_title);
                    }
                }
                break;
        }
    }

    //申请任务
    private void applyTask() {
        HashMap<String, String> maps = new HashMap<>();
        maps.put("task_id", taskInfoItem.id);
        maps.put("uid", TUtils.getUserId());
        Call<TaskInfo> call = ApiConfig.getInstants().create(TaskService.class).applyTaskOperate(TUtils.getParams(maps));

        API.getObject(call, OrderInfo.class, new ApiCallBack<OrderInfo>() {
            @Override
            public void onSuccess(int msgCode, String msg, OrderInfo data) {
                ToastUtils.showShort("" + msg);
                EventBus.getDefault().post(new RefreshUnreadCountEvent());
                tvGiveUpWork.setVisibility(View.VISIBLE);
                if (data != null) {
                    if (!TextUtils.isEmpty(data.order_id)) {
                        order_id = data.order_id;
                        taskInfoItem.order_id = order_id;
                    }
                }
                taskInfoItem.order_status = 1;
                tvDoWork.setText("待工作");
            }

            @Override
            public void onFaild(int msgCode, String msg) {
                ToastUtils.showShort("" + msg);
            }
        });
    }


    //放弃任务
    private void giveUpTask() {
        HashMap<String, String> maps = new HashMap<>();
        maps.put("task_id", taskInfoItem.id);
        maps.put("uid", TUtils.getUserId());
        maps.put("order_id", order_id);
        Call<TaskInfoList> call = ApiConfig.getInstants().create(TaskService.class).giveUpTaskOperate(TUtils.getParams(maps));

        API.getList(call, String.class, new ApiCallBackList<String>() {
            @Override
            public void onSuccess(int msgCode, String msg, List<String> data) {
                ToastUtils.showShort("" + msg);
                tvGiveUpWork.setVisibility(View.GONE);
                tvDoWork.setText("待申请");
                taskInfoItem.order_status = 0;
//                getTaskDetail();
            }

            @Override
            public void onFaild(int msgCode, String msg) {
                ToastUtils.showShort("" + msg);
            }
        });
    }

    private void doCollected() {
        HashMap<String, String> maps = new HashMap<>();
        maps.put("task_id", taskInfoItem.id);
        maps.put("uid", TUtils.getUserId());
        Call<TaskInfoList> call = ApiConfig.getInstants().create(TaskService.class).collectTask(TUtils.getParams(maps));

        API.getList(call, String.class, new ApiCallBackList<String>() {
            @Override
            public void onSuccess(int msgCode, String msg, List<String> data) {
                ToastUtils.showShort("" + msg);
                ivCollected.setImageResource(R.mipmap.iv_collected);
                isCollected = true;
            }

            @Override
            public void onFaild(int msgCode, String msg) {
                ToastUtils.showShort("" + msg);
            }
        });
    }


    private void doCancleCollected() {
        HashMap<String, String> maps = new HashMap<>();
        maps.put("task_id", taskInfoItem.id);
        maps.put("uid", TUtils.getUserId());
        Call<TaskInfoList> call = ApiConfig.getInstants().create(TaskService.class).cancleCollectTask(TUtils.getParams(maps));

        API.getList(call, String.class, new ApiCallBackList<String>() {
            @Override
            public void onSuccess(int msgCode, String msg, List<String> data) {
                ToastUtils.showShort("" + msg);
                ivCollected.setImageResource(R.mipmap.iv_collect);
                isCollected = false;
            }

            @Override
            public void onFaild(int msgCode, String msg) {
                ToastUtils.showShort("" + msg);
            }
        });
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
                    shareWx(1);
                }
            });

            btnWechat.setOnClickListener(new PerfectClickListener() {
                @Override
                protected void onNoDoubleClick(View v) {
                    shareDialog.dismiss();
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
                    mTencent.shareToQQ(TaskDetailActivity.this, params, qqShareListener);
                }
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 100) {
            if (resultCode == RESULT_OK) {
                if (taskInfoItem != null) {
                    taskInfoItem.order_status = 3;
                    tvDoWork.setText("待审核");
                    tvDoWork.setBackgroundColor(getResources().getColor(R.color.give_up));
                    tvGiveUpWork.setVisibility(View.GONE);
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {

        if (checkHasScheme()) {
            if (AppUtils.isAppRunning("com.task.system")) {
                UserInfo userInfo = TUtils.getUserInfo();
                if (userInfo != null && !TextUtils.isEmpty(userInfo.user_type)) {
                    ActivityUtils.startActivity(MainActivity.class);
                } else {
                    ActivityUtils.startActivity(LoginActivity.class);
                }
            }
        }else{
            super.onBackPressed();
        }
    }
}
