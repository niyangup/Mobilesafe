package com.niyang.mobilesafe.service;

import android.app.Service;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.util.Log;

public class LocationService extends Service {

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		// 获取手机经纬度坐标
		//1.获取位置管理员对象
		LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
		//2.用最优的方式获取经纬度坐标
		Criteria criteria=new Criteria();
		//允许花费
		criteria.setCostAllowed(true);
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		String bestProvider = lm.getBestProvider(criteria, true);
		lm.requestLocationUpdates(bestProvider, 0, 0, new MyLocationListener());
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
	

}
class MyLocationListener implements LocationListener{

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		//GPS状态发生切换的事件监听
	}
	
	@Override
	public void onProviderEnabled(String provider) {
		//GPS开启的时候
	}
	
	@Override
	public void onProviderDisabled(String provider) {
		//GSP关闭的时候
	}
	
	@Override
	public void onLocationChanged(Location location) {
		//位置发生变化时
		//经度
		double latitude = location.getLatitude();
		//纬度
		double longitude = location.getLongitude();
		
		SmsManager manager = SmsManager.getDefault();
		manager.sendTextMessage("5556", null, "latitude:"+latitude+", longitude:"+longitude, null, null);
		Log.v("TAG", "latitude:"+latitude+", longitude:"+longitude);
	
	}
	
}
