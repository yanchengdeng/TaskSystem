package com.task.system.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.blankj.utilcode.util.ToastUtils;
import com.task.system.R;
import com.task.system.bean.UserInfo;
import com.task.system.utils.TUtils;

//我的邀请码
public class MyInviteCodeActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_invite_code);
        setTitle("我的邀请码");

        UserInfo userInfo = TUtils.getUserInfo();
        if (!TextUtils.isEmpty(userInfo.invite_code)){
            ((TextView)findViewById(R.id.tv_invite_code)).setText(userInfo.invite_code);
        }

        findViewById(R.id.btn_copy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (TextUtils.isEmpty(userInfo.invite_code)){
                    return;
                }
                ClipboardManager clipboardManager = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
                clipboardManager.setPrimaryClip(ClipData.newPlainText(null, ((TextView)findViewById(R.id.tv_invite_code)).getText()));
                ToastUtils.showShort("复制成功");
            }
        });
    }
}
