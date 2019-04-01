package com.task.system.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.blankj.utilcode.util.FragmentUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.task.system.R;
import com.task.system.fragments.PercenterFragment;

public class PercentCenterActivity extends BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_percent_center);
        setTitle("个人中心");

        ivBack.setVisibility(View.GONE);

        FragmentUtils.replace(getSupportFragmentManager(),new PercenterFragment(),R.id.content);

    }


    @Override
    public void onBackPressed() {
        doubleClickExist();
    }

    private long mExitTime;

    /****
     * 连续两次点击退出
     */
    private boolean doubleClickExist() {
        if ((System.currentTimeMillis() - mExitTime) > 2000) {
            ToastUtils.showShort(R.string.double_click_exit);
            mExitTime = System.currentTimeMillis();
            return true;
        } else {
            Intent home = new Intent(Intent.ACTION_MAIN);
            home.addCategory(Intent.CATEGORY_HOME);
            startActivity(home);
//            AppManager.getAppManager().AppExit(this);
//            finish();
        }
        return false;
    }
}
