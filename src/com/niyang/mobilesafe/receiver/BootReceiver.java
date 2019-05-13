package com.niyang.mobilesafe.receiver;

import com.niyang.mobilesafe.ConstantValue;
import com.niyang.mobilesafe.util.SpUtil;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.v("TAG", "手机重启了");
		TelephonyManager manager=(TelephonyManager)context.getSystemService(context.TELEPHONY_SERVICE);
		String serialNumber = manager.getSimSerialNumber();
		String number = SpUtil.getString(context, ConstantValue.NUMBER_SERIAL, "");
		
		if (!serialNumber.equals(number)) {
			SmsManager sms = SmsManager.getDefault();
			sms.sendTextMessage("5551212", null, "sim change!!!", null, null);
		}
	}

}
