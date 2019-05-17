package com.niyang.mobilesafe.engine;

import java.util.ArrayList;
import java.util.List;

import com.niyang.mobilesafe.db.domain.AppInfo;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

public class AppInfoProvider {

	/**返回当前手机所有的应用相关信息(名称,包名,图标,(手机内存,sd卡),(系统,用户))
	 * @param context	获取包管理者的上下文环境
	 * @return 包含手机安装应用相关信息的集合下
	 */
	public static List<AppInfo> getAppInfoList(Context context) {
		// 1.获取包管理者对象
		PackageManager manager = context.getPackageManager();
		// 2.获取安装在手机上应用程序相关信息的集合
		List<PackageInfo> packageInfoList = manager.getInstalledPackages(0);
		
		List<AppInfo> appInfoList=new ArrayList<>();
		
		for (PackageInfo packageInfo : packageInfoList) {
			AppInfo appInfo = new AppInfo();
			// 3.获取包名
			appInfo.packageName = packageInfo.packageName;
			// 4.获取Manifest下application节点下的信息
			ApplicationInfo applicationInfo = packageInfo.applicationInfo;
			// 5.获取应用的包名
			appInfo.name = applicationInfo.loadLabel(manager).toString()+applicationInfo.uid;
			// 6.获取应用的图标
			appInfo.icon = applicationInfo.loadIcon(manager);
			// 7.判断是否为系统应用
			if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM) {
				//系统应用
				appInfo.isSystem=true;
			}else {
				//非系统应用
				appInfo.isSystem=false;
			}
			
			if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_EXTERNAL_STORAGE) {
				//用户应用
				appInfo.isSdCard=true;
			}else {
				//非用户应用
				appInfo.isSdCard=false;
			}
			appInfoList.add(appInfo);
		}
		
		return appInfoList;
	}

}
