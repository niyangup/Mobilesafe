package com.niyang.mobilesafe.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SpUtil {
	private static SharedPreferences sp;

	/**
	 * 写入boolean变量至sp中
	 * @param ctx	上下文
	 * @param key	存储节点的名称
	 * @param value	存储节点的值 boolean   
	 */
	public static void putBoolean(Context ctx,String key,boolean value) {
		if (sp==null) {
			sp = ctx.getSharedPreferences("config.xml", Context.MODE_PRIVATE);
		}
		sp.edit().putBoolean(key, value).commit();
	}
	
	/**
	 * 读取boolean从sp中
	 * @param ctx	上下文
	 * @param key	存储节点的名称
	 * @param defValue	默认值
	 * @return	默认值或者节点读取到的结果
	 */
	public static boolean getBoolean(Context ctx,String key,boolean defValue) {
		if (sp==null) {
			sp = ctx.getSharedPreferences("config.xml", Context.MODE_PRIVATE);
		}
		return sp.getBoolean(key, defValue);
	}
	
	public static void putString(Context ctx,String key,String value) {
		if (sp==null) {
			sp = ctx.getSharedPreferences("config.xml", Context.MODE_PRIVATE);
		}
		sp.edit().putString(key, value).commit();
	}
	
	public static String getString(Context ctx,String key,String defValue) {
		if (sp==null) {
			sp = ctx.getSharedPreferences("config.xml", Context.MODE_PRIVATE);
		}
		return sp.getString(key, defValue);
	}
	
	/**
	 * 删除sp中的key条目
	 * @param ctx
	 * @param key
	 */
	public static void remove(Context ctx,String key) {
		if (sp==null) {
			sp=ctx.getSharedPreferences("config.xml", Context.MODE_PRIVATE);
		}
		sp.edit().remove(key).commit();
	}
	
	public static void putInt(Context ctx,String key,int value) {
		if (sp==null) {
			sp = ctx.getSharedPreferences("config.xml", Context.MODE_PRIVATE);
		}
		sp.edit().putInt(key, value).commit();
	}
	
	public static int getInt(Context ctx,String key,int defValue) {
		if (sp==null) {
			sp = ctx.getSharedPreferences("config.xml", Context.MODE_PRIVATE);
		}
		return sp.getInt(key, defValue);
	}
}
