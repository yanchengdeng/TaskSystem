package com.task.system.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.task.system.Constans;
import com.task.system.R;
import com.task.system.adapters.BankAdapter;
import com.task.system.api.API;
import com.task.system.api.TaskInfo;
import com.task.system.api.TaskInfoList;
import com.task.system.api.TaskService;
import com.task.system.bean.AcountList;
import com.task.system.utils.PerfectClickListener;
import com.task.system.utils.RecycleViewUtils;
import com.task.system.utils.TUtils;
import com.yc.lib.api.ApiCallBack;
import com.yc.lib.api.ApiCallBackList;
import com.yc.lib.api.ApiConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;

/**
 * Author: 邓言诚  Create at : 2019/3/23  01:25
 * Email: yanchengdeng@gmail.com
 * Describle: 银行、支付宝账号
 */
public class BankListActivity extends BaseActivity {

    @BindView(R.id.recycle)
    RecyclerView recycle;
    @BindView(R.id.btn_withdraw)
    Button btnWithdraw;
    private BankAdapter adapter;

    private int positionSelected = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_list);
        ButterKnife.bind(this);
        adapter = new BankAdapter(R.layout.adapter_bank_item);

        recycle.setLayoutManager(new LinearLayoutManager(this));
        recycle.setAdapter(adapter);


        setTitle("账户列表");
        tvRightFunction.setVisibility(View.VISIBLE);
        tvRightFunction.setText("添加");

        tvRightFunction.setOnClickListener(new PerfectClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                ActivityUtils.startActivityForResult(BankListActivity.this, AddCountActivity.class, 300
                );

            }
        });

        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter ad, View view, int position) {
                for (AcountList.Accouninfo item:adapter.getData()){
                    item.isSelected = false;
                }

                positionSelected = position;
                adapter.getData().get(position).isSelected = true;
                btnWithdraw.setBackground(getResources().getDrawable(R.drawable.normal_submit_btn_red));
                adapter.notifyDataSetChanged();

            }
        });


        adapter.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                showDelteDialog(position);
                return false;
            }
        });

        getBankList();


        btnWithdraw.setOnClickListener(new PerfectClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                if (positionSelected>-1){
                    Intent intent = new Intent();
                    intent.putExtra(Constans.PASS_OBJECT,adapter.getItem(positionSelected));
                    setResult(RESULT_OK,intent);
                    finish();
                }else{
                    ToastUtils.showShort("请选择账号");
                }

            }
        });
    }

    private void showDelteDialog(int position) {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(ApiConfig.context)
                .title("温馨提示")
                .content("确定要解绑该账号？")
                .positiveText("确定").positiveColor(getResources().getColor(R.color.color_blue)).onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                        doDeleteItem(position);

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

    private void doDeleteItem(int position) {
        showLoadingBar("解绑中...");
        HashMap<String, String> hashMap = new HashMap();
        hashMap.put("id",adapter.getItem(position).id);
        Call<TaskInfoList> call = ApiConfig.getInstants().create(TaskService.class).delCard(TUtils.getParams(hashMap));

        API.getList(call, String.class, new ApiCallBackList<String>() {

            @Override
            public void onSuccess(int msgCode, String msg, List<String> data) {
                ToastUtils.showShort("" + msg);
                getBankList();
                dismissLoadingBar();
            }

            @Override
            public void onFaild(int msgCode, String msg) {
                ToastUtils.showShort("" + msg);
                dismissLoadingBar();
            }
        });
    }


    private void getBankList() {
        showLoadingBar();
        Call<TaskInfo> call = ApiConfig.getInstants().create(TaskService.class).getCards(TUtils.getParams());
        API.getObject(call, AcountList.class, new ApiCallBack<AcountList>() {
            @Override
            public void onSuccess(int msgCode, String msg, AcountList data) {
                dismissLoadingBar();
                if (data.list != null && data.list.size() > 0) {
                    adapter.setNewData(data.list);
                    btnWithdraw.setVisibility(View.VISIBLE);
                } else {
                    adapter.setNewData(new ArrayList<>());
                    adapter.setEmptyView(RecycleViewUtils.getEmptyView((Activity) ApiConfig.context, recycle));
                    btnWithdraw.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFaild(int msgCode, String msg) {
                dismissLoadingBar();
                btnWithdraw.setVisibility(View.GONE);
                adapter.setNewData(new ArrayList<>());
                adapter.setEmptyView(RecycleViewUtils.getEmptyView((Activity) ApiConfig.context, recycle));
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 300) {
            if (resultCode == RESULT_OK) {
                getBankList();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
