package com.task.system.recieves;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.task.system.Constans;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

public class AppRegister extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		final IWXAPI api = WXAPIFactory.createWXAPI(context, null,false);

		// 将该app注册到微信
		api.registerApp(Constans.WX_SHARE_APP_ID);
	}
}
