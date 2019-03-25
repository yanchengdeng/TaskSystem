package com.task.system.activity;

import android.graphics.Canvas;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.listener.OnItemSwipeListener;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.task.system.Constans;
import com.task.system.R;
import com.task.system.adapters.CollectedAdapter;
import com.task.system.api.API;
import com.task.system.api.TaskInfoList;
import com.task.system.api.TaskService;
import com.task.system.bean.TaskInfoItem;
import com.task.system.utils.TUtils;
import com.yc.lib.api.ApiCallBackList;
import com.yc.lib.api.ApiConfig;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;

public class MyCollectedActivity extends BaseActivity {

    private static final String TAG = "dyc swipe";
    private CollectedAdapter collectedAdapter;
    private RecyclerView recyclerView;
    private SmartRefreshLayout refreshLayout;

    private int page = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recycle_refresh);
        setTitle("我的收藏");
        recyclerView = findViewById(R.id.recycle);
        refreshLayout = findViewById(R.id.smartRefresh);
        recyclerView.setBackgroundColor(getResources().getColor(R.color.list_divider_color));
        collectedAdapter = new CollectedAdapter(R.layout.adapter_home_item);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(collectedAdapter);


        getCollected();

        collectedAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                page++;
                getCollected();
            }
        }, recyclerView);


        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshLayout) {

                page = 1;
                getCollected();
            }
        });

        collectedAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Bundle bundle = new Bundle();
                bundle.putString(Constans.PASS_STRING, collectedAdapter.getData().get(position).id);
                ActivityUtils.startActivity(bundle, TaskDetailActivity.class);
            }
        });


        collectedAdapter.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                showDelteDialog(position);
                return false;
            }
        });


        OnItemSwipeListener onItemSwipeListener = new OnItemSwipeListener() {
            @Override
            public void onItemSwipeStart(RecyclerView.ViewHolder viewHolder, int pos) {
                Log.d(TAG, "view swiped start: " + pos);
                BaseViewHolder holder = ((BaseViewHolder) viewHolder);
//                holder.setTextColor(R.id.tv, Color.WHITE);
            }

            @Override
            public void clearView(RecyclerView.ViewHolder viewHolder, int pos) {
                Log.d(TAG, "View reset: " + pos);
                BaseViewHolder holder = ((BaseViewHolder) viewHolder);
//                holder.setTextColor(R.id.tv, Color.BLACK);
            }

            @Override
            public void onItemSwiped(RecyclerView.ViewHolder viewHolder, int pos) {
                Log.d(TAG, "View Swiped: " + pos);
            }

            @Override
            public void onItemSwipeMoving(Canvas canvas, RecyclerView.ViewHolder viewHolder, float dX, float dY, boolean isCurrentlyActive) {
                canvas.drawColor(ContextCompat.getColor(MyCollectedActivity.this, R.color.red));
            }
        };


//        //侧滑删除
//        ItemDragAndSwipeCallback mItemDragAndSwipeCallback = new ItemDragAndSwipeCallback(collectedAdapter);
//        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(mItemDragAndSwipeCallback);
//        mItemTouchHelper.attachToRecyclerView(recyclerView);
//
//        mItemDragAndSwipeCallback.setSwipeMoveFlags(ItemTouchHelper.END);
//        collectedAdapter.enableSwipeItem();
//        collectedAdapter.setOnItemSwipeListener(onItemSwipeListener);
    }

    private void getCollected() {
        HashMap<String, String> maps = new HashMap<>();
        maps.put("page", String.valueOf(page));
        maps.put("page_size", Constans.PAGE_SIZE);


        Call<com.task.system.api.TaskInfoList> call = ApiConfig.getInstants().create(TaskService.class).getCollectList(TUtils.getParams(maps));

        API.getList(call, TaskInfoItem.class, new ApiCallBackList<TaskInfoItem>() {
            @Override
            public void onSuccess(int msgCode, String msg, List<TaskInfoItem> data) {
                TUtils.dealReqestData(collectedAdapter, recyclerView, data, page, refreshLayout);
            }

            @Override
            public void onFaild(int msgCode, String msg) {
                TUtils.dealNoReqestData(collectedAdapter, recyclerView, refreshLayout);
            }
        });
    }

    private void showDelteDialog(int position) {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(ApiConfig.context)
                .title("温馨提示")
                .content("确定要取消收藏改任务？")
                .positiveText("确定").positiveColor(getResources().getColor(R.color.color_blue)).onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                        doCancleCollected(position);

                    }
                }).negativeText("取消").negativeColor(getResources().getColor(R.color.color_info)).onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                });

        MaterialDialog dialog = builder.build();
        dialog.show();
    }

    private void doCancleCollected(int positon) {
        HashMap<String, String> maps = new HashMap<>();
        maps.put("task_id", collectedAdapter.getData().get(positon).id);
        maps.put("uid", TUtils.getUserId());
        Call<TaskInfoList> call = ApiConfig.getInstants().create(TaskService.class).cancleCollectTask(TUtils.getParams(maps));

        API.getList(call, String.class, new ApiCallBackList<String>() {
            @Override
            public void onSuccess(int msgCode, String msg, List<String> data) {
                ToastUtils.showShort("" + msg);
                if (collectedAdapter.getData()!=null && collectedAdapter.getData().size()>0){
                    collectedAdapter.remove(positon);
                }else{
                    TUtils.setRecycleEmpty(collectedAdapter,recyclerView);
                }
            }

            @Override
            public void onFaild(int msgCode, String msg) {
                ToastUtils.showShort("" + msg);
            }
        });
    }
}
