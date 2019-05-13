package com.niyang.mobilesafe.receiver;

import com.niyang.mobilesafe.ConstantValue;
import com.niyang.mobilesafe.R;
import com.niyang.mobilesafe.service.LocationService;
import com.niyang.mobilesafe.util.SpUtil;
import com.niyang.mobilesafe.util.ToastUtil;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.telephony.SmsMessage;

public class SmsRecevier extends BroadcastReceiver {

	private ComponentName mDeviceAdiminSample;
	private DevicePolicyManager mDPM;

	@Override
	public void onReceive(Context context, Intent intent) {
		mDeviceAdiminSample = new ComponentName(context, DeviceAdminSample.class);
		mDPM = (DevicePolicyManager) context.getSystemService(context.DEVICE_POLICY_SERVICE);
		boolean open_security = SpUtil.getBoolean(context, ConstantValue.OPEN_PROTECT, false);
		if (open_security) {
			Object[] objects = (Object[])intent.getExtras().get("pdus");
			for (Object object : objects) {
				SmsMessage sms=SmsMessage.createFromPdu((byte[])object);
				String address = sms.getOriginatingAddress();
				String body = sms.getMessageBody();
				if (body.contains("#*alarm*#")) {
					MediaPlayer player=MediaPlayer.create(context,R.raw.dark);
					player.setLooping(true);
					player.start();
				}else if (body.contains("#*location*#")) {
					//开启获取位置的服务
					Intent service=new Intent(context, LocationService.class);
					context.startService(service);
				}else if (body.contains("#*lockscreen*#")) {
					if (mDPM.isAdminActive(mDeviceAdiminSample)) {
						//锁屏
						mDPM.lockNow();
						//mDPM.resetPassword("123", 0);
					}else {
						ToastUtil.show(context, "请先激活");
					}
				}else if (body.contains("#*wipedata*#")) {
					if (mDPM.isAdminActive(mDeviceAdiminSample)) {
						mDPM.wipeData(0);
					}else {
						ToastUtil.show(context, "请先激活");
					}
				}else if (body.contains("#*uninstall*#")) {
					Intent i=new Intent("android.intent.aciton.DELETE");
					i.addCategory("android.intent.category.DEFAULT");
					i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					i.setData(Uri.parse(context.getPackageName()));
					context.startActivity(i);
				}
			}
		}

	}

}
