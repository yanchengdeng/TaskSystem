package com.task.system.activity;

import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.ActivityUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.task.system.Constans;
import com.task.system.R;
import com.task.system.adapters.PopulorAdapter;
import com.task.system.api.API;
import com.task.system.api.TaskInfo;
import com.task.system.api.TaskInfoIgnoreBody;
import com.task.system.api.TaskInfoList;
import com.task.system.api.TaskService;
import com.task.system.bean.SignInfo;
import com.task.system.bean.SignTotal;
import com.task.system.bean.SimpleBeanInfo;
import com.task.system.utils.TUtils;
import com.yc.lib.api.ApiCallBack;
import com.yc.lib.api.ApiCallBackList;
import com.yc.lib.api.ApiConfig;
import com.yc.lib.api.utils.SysUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;

import static android.support.v7.widget.DividerItemDecoration.VERTICAL;

/**
 * Email: dengyc@dadaodata.com
 * FileName: SignActivity.java
 * Author: dengyancheng
 * Date: 2019-08-01 22:58
 * Description:  签到页面
 * History:
 */
public class SignActivity extends BaseActivity {

    @BindView(R.id.iv_image)
    ImageView ivImage;
    @BindView(R.id.tv_rule)
    TextView tvRule;
    @BindView(R.id.ll_week)
    LinearLayout llWeek;
    @BindView(R.id.iv_sign_one)
    ImageView ivSignOne;
    @BindView(R.id.iv_sign_two)
    ImageView ivSignTwo;
    @BindView(R.id.iv_sign_three)
    ImageView ivSignThree;
    @BindView(R.id.iv_sign_four)
    ImageView ivSignFour;
    @BindView(R.id.iv_sign_five)
    ImageView ivSignFive;
    @BindView(R.id.iv_sign_six)
    ImageView ivSignSix;
    @BindView(R.id.iv_sign_seven)
    ImageView ivSignSeven;
    @BindView(R.id.ll_sign_icons)
    LinearLayout llSignIcons;
    @BindView(R.id.tv_week_sign_num)
    TextView tvWeekSignNum;
    @BindView(R.id.tv_go_sign)
    TextView tvGoSign;
    @BindView(R.id.do_share)
    TextView doShare;
    @BindView(R.id.do_award)
    TextView doAward;
    @BindView(R.id.recycle)
    RecyclerView recycle;

    private PopulorAdapter populorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign);
        ButterKnife.bind(this);
        setTitle("每日领红包");

        populorAdapter = new PopulorAdapter(R.layout.adapter_populor_item, new ArrayList<>());
        recycle.addItemDecoration(new DividerItemDecoration(this, VERTICAL));
        recycle.setLayoutManager(new LinearLayoutManager(this));
        recycle.setAdapter(populorAdapter);


        getUserSign();
        getSignList();

        getPopularList();

        populorAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Bundle bundle = new Bundle();
                bundle.putString(Constans.PASS_STRING, populorAdapter.getData().get(position).id);
                ActivityUtils.startActivity(bundle, TaskDetailActivity.class);
            }
        });

    }

    /**
     * 签到情况
     * 请求模式|mode=check时，仅检测今日是否签到
     * 当mode=check时，返回的code值，2=签到成功 3=未签到
     */
    private void getUserSign() {
        HashMap<String, String> maps = new HashMap<>();
        maps.put("mode", "check");
        Call<TaskInfoIgnoreBody> call = ApiConfig.getInstants().create(TaskService.class).userSign(TUtils.getParams(maps));
        API.getObjectIgnoreBody(call, new ApiCallBack<SimpleBeanInfo>() {
            @Override
            public void onSuccess(int msgCode, String msg, SimpleBeanInfo data) {
                tvGoSign.setText(msg);

            }

            @Override
            public void onFaild(int msgCode, String msg) {
//                tvGoSign.setText(msg);
            }
        });
    }


    /***
     * 去签到
     */
    private void doSign() {
        Call<TaskInfoIgnoreBody> call = ApiConfig.getInstants().create(TaskService.class).userSign(TUtils.getParams());
        API.getObjectIgnoreBody(call, new ApiCallBack<SimpleBeanInfo>() {
            @Override
            public void onSuccess(int msgCode, String msg, SimpleBeanInfo data) {
                tvGoSign.setText(msg);
                SysUtils.showToast(msg);
                getSignList();


            }

            @Override
            public void onFaild(int msgCode, String msg) {
//                tvGoSign.setText(msg);
            }
        });
    }


    /**
     * 人气排行
     */
    private void getPopularList() {
        Call<TaskInfoList> call = ApiConfig.getInstants().create(TaskService.class).getPopulatityTaskLst(TUtils.getParams());
        API.getList(call, SimpleBeanInfo.class, new ApiCallBackList<SimpleBeanInfo>() {
            @Override
            public void onSuccess(int msgCode, String msg, List<SimpleBeanInfo> data) {
                TUtils.dealReqestData(populorAdapter, recycle, data);

            }

            @Override
            public void onFaild(int msgCode, String msg) {
                TUtils.dealNoReqestData(populorAdapter, recycle);
            }
        });
    }

    //签到记录
    private void getSignList() {
        Call<TaskInfo> call = ApiConfig.getInstants().create(TaskService.class).userSignList(TUtils.getParams());

        API.getObject(call, SignTotal.class, new ApiCallBack<SignTotal>() {
            @Override
            public void onSuccess(int msgCode, String msg, SignTotal data) {
                if (data != null && data.data != null && data.data.size() > 0) {
                    tvWeekSignNum.setText("当前有" + data.total + "人参加");
                    fillSignStasus(data.data);
                }
            }

            @Override
            public void onFaild(int msgCode, String msg) {

            }
        });

    }


    private void fillSignStasus(List<SignInfo> data) {
        for (SignInfo item : data) {
            if (item.week_n==0) {
                ivSignOne.setImageResource(getSignIcon(item.is_sign));
//                ivSignOne.setVisibility(item.is_sign==0?View.INVISIBLE:View.VISIBLE);
            } else if (item.week_n==1) {
                ivSignTwo.setImageResource(getSignIcon(item.is_sign));
//                ivSignTwo.setVisibility(item.is_sign==0?View.INVISIBLE:View.VISIBLE);
            } else if (item.week_n==2) {
                ivSignThree.setImageResource(getSignIcon(item.is_sign));
//                ivSignThree.setVisibility(item.is_sign==0?View.INVISIBLE:View.VISIBLE);
            } else if (item.week_n==3) {
                ivSignFour.setImageResource(getSignIcon(item.is_sign));
//                ivSignFour.setVisibility(item.is_sign==0?View.INVISIBLE:View.VISIBLE);
            } else if (item.week_n==4) {
                ivSignFive.setImageResource(getSignIcon(item.is_sign));
//                ivSignFive.setVisibility(item.is_sign==0?View.INVISIBLE:View.VISIBLE);
            } else if (item.week_n==5) {
                ivSignSix.setImageResource(getSignIcon(item.is_sign));
//                ivSignSix.setVisibility(item.is_sign==0?View.INVISIBLE:View.VISIBLE);
            } else if (item.week_n==6) {
                ivSignSeven.setImageResource(getSignIcon(item.is_sign));
//                ivSignSeven.setVisibility(item.is_sign==0?View.INVISIBLE:View.VISIBLE);
            }
        }
    }


   /* private void fillSignStasus(List<SignInfo> data) {
        for (SignInfo item : data) {
            if (item.week_n.equals("1")) {
                ivSignOne.setImageResource(getSignIcon(item.is_sign));
                ivSignOne.setVisibility(item.is_sign==0?View.INVISIBLE:View.VISIBLE);
            } else if (item.week_n.equals("2")) {
                ivSignTwo.setImageResource(getSignIcon(item.is_sign));
                ivSignTwo.setVisibility(item.is_sign==0?View.INVISIBLE:View.VISIBLE);
            } else if (item.week_n.equals("3")) {
                ivSignThree.setImageResource(getSignIcon(item.is_sign));
                ivSignThree.setVisibility(item.is_sign==0?View.INVISIBLE:View.VISIBLE);
            } else if (item.week_n.equals("4")) {
                ivSignFour.setImageResource(getSignIcon(item.is_sign));
                ivSignFour.setVisibility(item.is_sign==0?View.INVISIBLE:View.VISIBLE);
            } else if (item.week_n.equals("5")) {
                ivSignFive.setImageResource(getSignIcon(item.is_sign));
                ivSignFive.setVisibility(item.is_sign==0?View.INVISIBLE:View.VISIBLE);
            } else if (item.week_n.equals("6")) {
                ivSignSix.setImageResource(getSignIcon(item.is_sign));
                ivSignSix.setVisibility(item.is_sign==0?View.INVISIBLE:View.VISIBLE);
            } else if (item.week_n.equals("7")) {
                ivSignSeven.setImageResource(getSignIcon(item.is_sign));
                ivSignSeven.setVisibility(item.is_sign==0?View.INVISIBLE:View.VISIBLE);
            }
        }
    }
*/
    private int getSignIcon(Double is_sign) {
        if (is_sign == 2) {
            return R.mipmap.icon_signed;
        } else if (is_sign == 0) {
            return R.mipmap.icon_no_sign;
        }
        return android.R.color.transparent;
    }



    @OnClick({R.id.tv_rule, R.id.tv_go_sign, R.id.do_share, R.id.do_award})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_rule:
                break;
            case R.id.tv_go_sign:
                doSign();
                break;
            case R.id.do_share:
                ActivityUtils.startActivity(MyInviteCodeActivity.class);
                break;
            case R.id.do_award:
                ActivityUtils.startActivity(IntegralLotteryActivity.class);
                break;
        }
    }
}
