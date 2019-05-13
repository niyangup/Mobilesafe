package com.niyang.mobilesafe.util;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;

public class ServiceUtil {

	/**
	 * @param ctx
	 *            上下文
	 * @param serviceName
	 *            判断是否正在运行的服务
	 * @return true 运行 false 未运行
	 */
	public static boolean isRunning(Context ctx, String serviceName) {
		ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
		// 获取手机正在运行的服务(多少个服务)
		List<RunningServiceInfo> services = am.getRunningServices(1000);
		// 遍历所有的服务,拿到每一个服务的类的名称,和传递进来的类的名称做对比.如果一致,说明服务正在运行s
		for (RunningServiceInfo runningServiceInfo : services) {
			if (runningServiceInfo.service.getClassName().equals(serviceName)) {
				return true;
			}
		}
		return false;
	}
}
