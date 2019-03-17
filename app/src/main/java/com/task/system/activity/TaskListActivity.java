package com.task.system.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.task.system.Constans;
import com.task.system.R;
import com.task.system.adapters.HomeAdapter;
import com.task.system.api.API;
import com.task.system.api.TaskInfo;
import com.task.system.api.TaskService;
import com.task.system.bean.TaskInfoList;
import com.task.system.utils.RecycleViewUtils;
import com.task.system.utils.TUtils;
import com.yc.lib.api.ApiCallBack;
import com.yc.lib.api.ApiConfig;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;

//任务搜索
public class TaskListActivity extends BaseActivity {

    @BindView(R.id.et_input_text)
    EditText etInputText;
    @BindView(R.id.iv_clear)
    ImageView ivClear;
    @BindView(R.id.recycle)
    RecyclerView recycle;
    @BindView(R.id.smartRefresh)
    SmartRefreshLayout smartRefresh;

    private HomeAdapter homeAdapter;
    private int page = 1;
    private String region_id;
    private String category_id;
    private String sort_id;
    private String tags_id;
    private String keywords;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_list);
        ButterKnife.bind(this);
        tvTittle.setText("搜索任务");
        homeAdapter = new HomeAdapter(R.layout.adapter_home_item);
        recycle.addItemDecoration(RecycleViewUtils.getItemDecorationHorizontal());
        recycle.setLayoutManager(new LinearLayoutManager(ApiConfig.context));
        recycle.setAdapter(homeAdapter);

        region_id = SPUtils.getInstance().getString(Constans.LOCATON_CITY_id);
        getTaskList();

        homeAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                page++;
                getTaskList();

            }
        }, recycle);


        etInputText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if (TextUtils.isEmpty(etInputText.getEditableText().toString())) {
                        ToastUtils.showShort("请输入关键字");
                        return true;
                    } else {
                        KeyboardUtils.hideSoftInput(mContext);
                    }
                    keywords = etInputText.getEditableText().toString();
                    page = 1;
                    getTaskList();
                    return true;

                }
                return false;
            }
        });


        homeAdapter.setOnItemClickListener((adapter, view, position) -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable(Constans.PASS_OBJECT, homeAdapter.getData().get(position));
            ActivityUtils.startActivity(bundle, TaskDetailActivity.class);
        });

    }


    //任务列表

    /**
     * region_id
     * * category_id
     * * sort_id
     * * tags_id
     * * page
     * * keywords
     */
    private void getTaskList() {
        HashMap<String, String> maps = new HashMap<>();
        maps.put("page", String.valueOf(page));
        maps.put("page_size", Constans.PAGE_SIZE);
        if (!TextUtils.isEmpty(region_id)) {
            maps.put("region_id", region_id);
        }
        if (!TextUtils.isEmpty(category_id)) {
            maps.put("category_id", category_id);
        }
        if (!TextUtils.isEmpty(sort_id)) {
            maps.put("sort_id", sort_id);
        }
        if (!TextUtils.isEmpty(tags_id)) {
            maps.put("tags_id", tags_id);
        }
        if (!TextUtils.isEmpty(keywords)) {
            maps.put("keywords", keywords);
        }

        Call<TaskInfo> call = ApiConfig.getInstants().create(TaskService.class).getTaskList(TUtils.getParams(maps));

        API.getObject(call, TaskInfoList.class, new ApiCallBack<TaskInfoList>() {
            @Override
            public void onSuccess(int msgCode, String msg, TaskInfoList data) {
                TUtils.dealReqestData(homeAdapter, recycle, data.list, page, smartRefresh);

            }

            @Override
            public void onFaild(int msgCode, String msg) {
                TUtils.dealNoReqestData(homeAdapter, recycle, smartRefresh);

            }
        });
    }

    @OnClick(R.id.iv_clear)
    public void onViewClicked() {
        keywords = "";
        etInputText.setText("");
        etInputText.setHint("请输入差事");
        page = 1;
        getTaskList();
    }
}
