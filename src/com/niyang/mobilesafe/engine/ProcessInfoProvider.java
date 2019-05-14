package com.niyang.mobilesafe.engine;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import com.niyang.mobilesafe.R;
import com.niyang.mobilesafe.db.domain.ProcessInfo;

import android.R.string;
import android.app.ActivityManager;
import android.app.Application;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

public class ProcessInfoProvider {

	/**
	 * @param context
	 *            上下文
	 * @return 返回进程总数
	 */
	public static int getProcessCount(Context context) {
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> appProcesses = am.getRunningAppProcesses();
		return appProcesses.size();
	}

	/**
	 * @param context
	 *            上下文
	 * @return 返回可用内存的大小(bytes)
	 */
	public static long getAvailSpace(Context context) {
		// 1.获取ActivityManager对象
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		// 2.构建存储可用内存的对象
		MemoryInfo memoryInfo = new MemoryInfo();
		// 3.给memoryInfo对象赋(可用内存)值
		am.getMemoryInfo(memoryInfo);
		// 4.获取memoryInfo对象中相应的数据
		return memoryInfo.availMem;
	}

	/**
	 * @param context
	 *            上下文
	 * @return 返回总内存值,byte
	 */
	public static long getTotalSpace(Context context) {
		/*
		 * // 1.获取ActivityManager对象 ActivityManager am = (ActivityManager)
		 * context.getSystemService(Context.ACTIVITY_SERVICE); // 2.构建存储可用内存的对象
		 * MemoryInfo memoryInfo = new MemoryInfo(); // 3.给memoryInfo对象赋(可用内存)值
		 * am.getMemoryInfo(memoryInfo); // 4.获取memoryInfo对象中相应的数据 return
		 * memoryInfo.totalMem;
		 */
		try {
			File file = new File("proc/meminfo");
			FileReader fileReader = new FileReader(file);
			BufferedReader br = new BufferedReader(fileReader);
			String line = br.readLine();
			System.out.println("line:" + line);
			String space = line.substring(line.indexOf(":") + 1, line.indexOf("kB")).trim();
			System.out.println("space:" + space);
			long totalMem = Long.parseLong(space);

			br.close();
			fileReader.close();
			return totalMem * 1024;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * @param context
	 *            上下文
	 * @return 当前手机正在运行的进程的相关进程的集合
	 */
	public static List<ProcessInfo> getProcessInfo(Context context) {
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

		List<ProcessInfo> processInfoList = new ArrayList<>();
		List<RunningAppProcessInfo> runningAppProcesses = am.getRunningAppProcesses();
		PackageManager pm = context.getPackageManager();
		for (RunningAppProcessInfo info : runningAppProcesses) {

			ProcessInfo processInfo = new ProcessInfo();
			// 获取进程的名称=packageName
			processInfo.packageName = info.processName;
			// 获取进程占用的内存大小(传递一个进程对应的pid数组)
			int pid = info.pid;
			android.os.Debug.MemoryInfo[] processMemoryInfo = am.getProcessMemoryInfo(new int[] { pid });
			// 返回数组中索引位置为0的对象,为当前进程的内存信息的对象
			android.os.Debug.MemoryInfo memoryInfo = processMemoryInfo[0];
			// 获取已使用的内存大小
			long totalPrivateDirty = memoryInfo.getTotalPrivateDirty() * 1024;
			processInfo.memSize = totalPrivateDirty;
			// 获取应用名称
			try {
				ApplicationInfo applicationInfo = pm.getApplicationInfo(processInfo.packageName, 0);
				processInfo.name = applicationInfo.loadLabel(pm).toString();
				processInfo.icon = applicationInfo.loadIcon(pm);
				// 判断是否为系统进程
				if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM) {
					processInfo.isSystem = true;
				} else {
					processInfo.isSystem = false;
				}
			} catch (NameNotFoundException e) {
				processInfo.name = info.processName;
				processInfo.icon = context.getResources().getDrawable(R.drawable.ic_launcher);
				processInfo.isSystem = true;
				e.printStackTrace();
			}
			processInfoList.add(processInfo);
		}
		return processInfoList;
	}

	/**
	 * kill进程方法
	 * 
	 * @param context
	 *            上下文环境
	 * @param processInfo
	 *            kill进程的java bean
	 */
	public static void killProcess(Context context, ProcessInfo processInfo) {
		// 1.获取ActivityManager对象
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		// 2.kill相关进程(权限)
		am.killBackgroundProcesses(processInfo.getPackageName());
	}

	/**
	 * @param context
	 *            上下文环境
	 */
	public static void killAll(Context context) {
		// 1.获取ActivityManager对象
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> list = am.getRunningAppProcesses();
		for (RunningAppProcessInfo info : list) {
			// 除了手机卫士以外,其他的进程都需要kill
			if (info.processName.equals(context.getPackageName())) {
				continue;
			}
			am.killBackgroundProcesses(info.processName);
		}

	}
}
