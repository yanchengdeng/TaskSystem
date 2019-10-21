package com.task.system.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.blankj.utilcode.util.ActivityUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.task.system.Constans;
import com.task.system.R;
import com.task.system.adapters.HomeAdapter;
import com.task.system.api.API;
import com.task.system.api.TaskInfo;
import com.task.system.api.TaskService;
import com.task.system.bean.AdInfo;
import com.task.system.bean.TaskInfoList;
import com.task.system.common.GlideImageLoader;
import com.task.system.utils.TUtils;
import com.yc.lib.api.ApiCallBack;
import com.yc.lib.api.ApiCallBackList;
import com.yc.lib.api.ApiConfig;
import com.youth.banner.Banner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;

/**
 * Email: dengyc@dadaodata.com
 * FileName: NewUserTaskActivity.java
 * Author: dengyancheng
 * Date: 2019-08-01 23:30
 * Description: 新人任务
 * History:
 */
public class NewUserTaskActivity extends BaseActivity {

    @BindView(R.id.banner)
    Banner banner;
    @BindView(R.id.recycle)
    RecyclerView recycle;

    private HomeAdapter homeAdapter;
    private int page = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user_task);
        ButterKnife.bind(this);

        setTitle("新手任务");
        homeAdapter = new HomeAdapter(R.layout.adapter_home_item);
//        recycle.addItemDecoration(RecycleViewUtils.getItemDecorationHorizontal());
        recycle.setLayoutManager(new LinearLayoutManager(ApiConfig.context));

//        homeAdapter.addHeaderView(headerView);
//        homeAdapter.setHeaderAndEmpty(true);
        recycle.setAdapter(homeAdapter);


        homeAdapter.setOnItemClickListener((adapter, view, position) -> {
            Bundle bundle = new Bundle();
            bundle.putString(Constans.PASS_STRING, homeAdapter.getData().get(position).id);
            ActivityUtils.startActivity(bundle, TaskDetailActivity.class);
        });


        homeAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                page++;
                getTaskList();
            }
        },recycle);



        //设置图片加载器
        banner.setImageLoader(new GlideImageLoader());

        getTaskList();
        getAds();
    }

    private void getAds() {
        HashMap<String, String> maps = new HashMap<>();
        maps.put("uid", TUtils.getUserId());
        maps.put("position", "3");
        Call<com.task.system.api.TaskInfoList> call = ApiConfig.getInstants().create(TaskService.class).getAdList(TUtils.getParams(maps));
        API.getList(call, AdInfo.class, new ApiCallBackList<AdInfo>() {
            @Override
            public void onSuccess(int msgCode, String msg, List<AdInfo> data) {

                List<String> images = new ArrayList<>();
                if (data != null && data.size() > 0) {
                    for (AdInfo item : data) {
                        images.add(item.cover);
                    }
                }
                banner.setImages(images);
                banner.setOnBannerListener(position -> TUtils.openBanner(data.get(position)));
                //banner设置方法全部调用完毕时最后调用
                banner.start();

            }

            @Override
            public void onFaild(int msgCode, String msg) {
//                ToastUtils.showShort(msg);
            }
        });
    }

    /**
     * region_id
     * * category_id
     * * sort_id
     * * tags_id
     * * page
     * * keywords
     */
    private void getTaskList() {
        if (page == 1) {
            showLoadingBar();
        }
        HashMap<String, String> maps = new HashMap<>();
        maps.put("page", String.valueOf(page));
        maps.put("page_size", Constans.PAGE_SIZE);
        maps.put("newuser","1");
//        if (!TextUtils.isEmpty(category_id)) {
//            maps.put("category_id", category_id);
//        }
//        if (!TextUtils.isEmpty(sort_id)) {
//            maps.put("sort_id", sort_id);
//        }
//        if (!TextUtils.isEmpty(tags_id)) {
//            maps.put("tags_id", tags_id);
//        }


        Call<TaskInfo> call = ApiConfig.getInstants().create(TaskService.class).getTaskList(TUtils.getParams(maps));

        API.getObject(call, TaskInfoList.class, new ApiCallBack<TaskInfoList>() {
            @Override
            public void onSuccess(int msgCode, String msg, TaskInfoList data) {
                TUtils.dealReqestData(homeAdapter, recycle, data.list, page);
                dismissLoadingBar();

            }

            @Override
            public void onFaild(int msgCode, String msg) {
                TUtils.dealNoReqestData(homeAdapter, recycle);
                dismissLoadingBar();
            }
        });
    }
}
