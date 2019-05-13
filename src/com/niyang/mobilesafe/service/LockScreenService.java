package com.niyang.mobilesafe.service;

import com.niyang.mobilesafe.engine.ProcessInfoProvider;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

public class LockScreenService extends Service {

	private InnerReceiver innerReceiver;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	@Override
	public void onCreate() {
		IntentFilter intentFilter=new IntentFilter(Intent.ACTION_SCREEN_OFF);
		innerReceiver = new InnerReceiver();
		registerReceiver(innerReceiver, intentFilter);
		super.onCreate();
	}
	
	@Override
	public void onDestroy() {
		if (innerReceiver!=null) {
			unregisterReceiver(innerReceiver);
		}
		super.onDestroy();
	}
	
	class InnerReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			ProcessInfoProvider.killAll(context);
		}
		
	}
}
