package com.task.system.fragments;

import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.blankj.utilcode.util.ActivityUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.task.system.Constans;
import com.task.system.R;
import com.task.system.activity.MessageDetailActivity;
import com.task.system.activity.MessageListActivity;
import com.task.system.adapters.MessageAdapter;
import com.task.system.api.API;
import com.task.system.api.TaskInfo;
import com.task.system.api.TaskService;
import com.task.system.bean.MessageItem;
import com.task.system.bean.MessageList;
import com.task.system.utils.RecycleViewUtils;
import com.task.system.utils.TUtils;
import com.yc.lib.api.ApiCallBack;
import com.yc.lib.api.ApiConfig;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.Unbinder;
import retrofit2.Call;

public class FragmentMessage extends BaseFragment {

    @BindView(R.id.recycle)
    RecyclerView recyclerView;
    @BindView(R.id.smartRefresh)
    SmartRefreshLayout smartRefresh;
    Unbinder unbinder;
    private String message_type;

    private MessageAdapter messageAdapter;
    private int page= 1;

    @Override
    protected int getAbsLayoutId() {
        return R.layout.recycle_refresh;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        message_type = getArguments().getString(Constans.PASS_STRING);

        recyclerView.setLayoutManager(new LinearLayoutManager(ApiConfig.context));
        recyclerView.addItemDecoration(RecycleViewUtils.getItemDecorationHorizontal());
        messageAdapter = new MessageAdapter(R.layout.adapter_message_item, new ArrayList<MessageItem>(),message_type);
        recyclerView.setAdapter(messageAdapter);
        messageAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                messageAdapter.getData().get(position).is_read = 1;
                messageAdapter.notifyItemChanged(position);
                Bundle bundle = new Bundle();
                bundle.putSerializable(Constans.PASS_OBJECT, messageAdapter.getData().get(position));
                ActivityUtils.startActivity(bundle, MessageDetailActivity.class);
            }
        });


        smartRefresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                page=1;
                getMessageList();
            }
        });

        messageAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                page++;
                getMessageList();
            }
        },recyclerView);



    }

    @Override
    protected void initData(View view) {
        super.initData(view);

        getMessageList();
    }

    private void getMessageList() {
        if (getActivity() != null) {
            ((MessageListActivity) getActivity()).showLoadingBar();
        }

        HashMap<String, String> maps = new HashMap<>();
        maps.put("page", String.valueOf(page));
        if (!TextUtils.isEmpty(message_type)) {
            maps.put("message_type", message_type);
        }

        Call<TaskInfo> call = ApiConfig.getInstants().create(TaskService.class).getMessageList(TUtils.getParams(maps));

        API.getObject(call, MessageList.class, new ApiCallBack<MessageList>() {
            @Override
            public void onSuccess(int msgCode, String msg, MessageList data) {
                if (getActivity() != null) {
                    ((MessageListActivity) getActivity()).dismissLoadingBar();
                }

                TUtils.dealReqestData(messageAdapter, recyclerView, data.list, page, smartRefresh);
            }

            @Override
            public void onFaild(int msgCode, String msg) {
                if (getActivity() != null) {
                    ((MessageListActivity) getActivity()).dismissLoadingBar();
                }

                TUtils.dealNoReqestData(messageAdapter, recyclerView, smartRefresh);
            }
        });
    }


}
