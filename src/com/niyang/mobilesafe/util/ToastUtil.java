package com.niyang.mobilesafe.util;

import android.content.Context;
import android.widget.Toast;

public class ToastUtil {
	/**
	 * 
	 * @param context	ctx 上下文环境
	 * @param msg	打印文本内容
	 */
	public static void show(Context context,String msg) {
		Toast.makeText(context, msg, 0).show();
	}
}
