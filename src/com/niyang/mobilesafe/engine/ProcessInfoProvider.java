package com.niyang.mobilesafe.engine;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;

import android.R.string;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;

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
	 * @param context	上下文
	 * @return 返回总内存值,byte
	 */
	public static long getTotalSpace(Context context) {
	/*	// 1.获取ActivityManager对象
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		// 2.构建存储可用内存的对象
		MemoryInfo memoryInfo = new MemoryInfo();
		// 3.给memoryInfo对象赋(可用内存)值
		am.getMemoryInfo(memoryInfo);
		// 4.获取memoryInfo对象中相应的数据
		return memoryInfo.totalMem;
	*/
		try {
			File file=new File("proc/meminfo");
			FileReader fileReader=new FileReader(file);
			BufferedReader br = new BufferedReader(fileReader);
			String line = br.readLine();
			System.out.println("line:"+line);
			String space = line.substring(line.indexOf(":")+1, line.indexOf("kB")).trim();
			System.out.println("space:"+space);
			long totalMem = Long.parseLong(space);
			
			br.close();
			fileReader.close();
			return totalMem*1024;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
		
	}
}
