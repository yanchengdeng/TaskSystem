package com.task.system.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.ActivityUtils;
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
import com.task.system.bean.TaskInfoItem;
import com.task.system.fragments.FragmentStepInfo;
import com.task.system.fragments.FragmentTaskDetail;
import com.task.system.utils.PerfectClickListener;
import com.task.system.utils.TUtils;
import com.task.system.views.FragmentPagerItems;
import com.yc.lib.api.ApiCallBack;
import com.yc.lib.api.ApiCallBackList;
import com.yc.lib.api.ApiConfig;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);
        ButterKnife.bind(this);

        taskInfoItem = (TaskInfoItem) getIntent().getSerializableExtra(Constans.PASS_OBJECT);
        initData(taskInfoItem);
        tablayout.addTab(tablayout.newTab().setText("任务描述"), 0);
        tablayout.addTab(tablayout.newTab().setText("详细流程"), 1);
        FragmentPagerItemAdapter fragmentPagerItemAdapter = new FragmentPagerItemAdapter(getSupportFragmentManager(), getFragmentsItem());
        viewpage.setAdapter(fragmentPagerItemAdapter);
        tablayout.setupWithViewPager(viewpage);
        getTaskDetail();
    }

    private FragmentPagerItems getFragmentsItem() {
        return FragmentPagerItems.with(mContext).add("任务描述", FragmentTaskDetail.class, getIntent().getExtras())
                .add("详细流程", FragmentStepInfo.class, getIntent().getExtras()).create();
    }

    private void getTaskDetail() {
        HashMap<String, String> maps = new HashMap<>();
        maps.put("task_id", taskInfoItem.id);
        Call<TaskInfo> call = ApiConfig.getInstants().create(TaskService.class).getTaskDetail(TUtils.getParams(maps));

        API.getObject(call, TaskInfoItem.class, new ApiCallBack<TaskInfoItem>() {
            @Override
            public void onSuccess(int msgCode, String msg, TaskInfoItem data) {
                taskInfoItem = data;
                if (data.is_collect==0) {
                    ivCollected.setImageResource(R.mipmap.iv_collect);
                    isCollected = false;
                }else{
                    ivCollected.setImageResource(R.mipmap.iv_collected);
                    isCollected = true;
                }
                initData(data);
            }

            @Override
            public void onFaild(int msgCode, String msg) {

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
        if (!TextUtils.isEmpty(data.order_status_title)){
            tvDoWork.setText(data.order_status_title);
        }
        if ( data.order_status==1 ||data.order_status==2 || data.order_status==3){
            tvDoWork.setBackgroundColor(getResources().getColor(R.color.red));
            tvGiveUpWork.setVisibility(View.VISIBLE);
        }else if (data.order_status==4 || data.order_status==0){
            tvGiveUpWork.setVisibility(View.GONE);
            tvDoWork.setBackgroundColor(getResources().getColor(R.color.red));
        }else{
            tvDoWork.setBackgroundColor(getResources().getColor(R.color.give_up));
            tvGiveUpWork.setVisibility(View.GONE);
        }

    }

    @OnClick({R.id.iv_back, R.id.iv_collected, R.id.iv_share, R.id.tv_custome, R.id.tv_give_up_work, R.id.tv_do_work})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_collected:
                if (isCollected) {
                    doCancleCollected();
                } else {
                    doCollected();
                }
                break;
            case R.id.iv_share:
                shareDialog();
                break;
            case R.id.tv_custome:
                ToastUtils.showShort("客服");
                break;
            case R.id.tv_give_up_work:
                giveUpTask();
                break;
            case R.id.tv_do_work:
                if (taskInfoItem.order_status==0) {
                    applyTask();
                } else if (taskInfoItem.order_status==1 ||taskInfoItem.order_status==2 || taskInfoItem.order_status==3){
                    //做下一步工作
                    ActivityUtils.startActivity(getIntent().getExtras(), DoTaskStepActivity.class);
                }else{
                    ToastUtils.showShort(""+taskInfoItem.order_status_title);
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
                tvGiveUpWork.setVisibility(View.VISIBLE);
                if (data != null) {
                    if (!TextUtils.isEmpty(data.order_id)) {
                        order_id = data.order_id;
                    }
                }
                taskInfoItem.order_status=1;
                tvDoWork.setText("继续工作");
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
                taskInfoItem.order_status=0;
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
//                    ShareUtil.share(WechatMoments.Name, mTitle, mContent, LTool.getShare(LmsyType.ARTICLE, mId, mCover), mCover);

                }
            });

            btnWechat.setOnClickListener(new PerfectClickListener() {
                @Override
                protected void onNoDoubleClick(View v) {
                    shareDialog.dismiss();
//                    ShareUtil.share(Wechat.Name, mTitle, mContent, LTool.getShare(LmsyType.ARTICLE, mId, mCover), mCover);

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


}
