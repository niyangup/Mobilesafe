package com.niyang.mobilesafe.service;

import com.niyang.mobilesafe.db.dao.BlackNumberDao;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.telephony.SmsMessage;

public class BlackNumberService extends Service {

	private InnerSmsRecevier mInnerSmsRecevier;
	private BlackNumberDao mDao;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mDao = BlackNumberDao.getInstance(getApplicationContext());
		IntentFilter filter = new IntentFilter();
		filter.addAction("android.provider.Telephony.SMS_RECEIVED");
		filter.setPriority(1000);
		mInnerSmsRecevier = new InnerSmsRecevier();
		registerReceiver(mInnerSmsRecevier, filter);
	}

	class InnerSmsRecevier extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			
			Object[] objects = (Object[]) intent.getExtras().get("pdus");
			for (Object object : objects) {
				SmsMessage sms = SmsMessage.createFromPdu((byte[]) object);
				String address = sms.getOriginatingAddress();
				String body = sms.getMessageBody();
				
				String mode = mDao.getMode(address);
				
				if (mode.equals("1")|| mode.equals("3")) {
					abortBroadcast();
				}
			}
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mInnerSmsRecevier);
	}

}
