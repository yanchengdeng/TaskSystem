package com.task.system.activity;

import android.os.Bundle;

import com.task.system.R;
import com.task.system.adapters.CollectedAdapter;

public class MyCollectedActivity extends BaseActivity {

    private CollectedAdapter collectedAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recycle);
        setTitle("我的收藏");

        collectedAdapter = new CollectedAdapter(R.layout.adapter_home_item);


    }
}
