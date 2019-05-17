package com.niyang.mobilesafe.global;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.lang.Thread.UncaughtExceptionHandler;

import android.app.Application;

public class MyApplication extends Application {
	@Override
	public void onCreate() {
		super.onCreate();
		//捕获全局异常
		Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {
			
			@Override
			public void uncaughtException(Thread thread, Throwable ex) {
				//在获取到了未捕获的异常后,调用的方法
				String path = getFilesDir().getPath();
//				String path="/data/data/com.niyang.mobilesafe";
				File file=new File(path,"error.log");
				try {
					PrintWriter writer=new PrintWriter(file);
					ex.printStackTrace(writer);
					writer.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
				//上传日志到服务器
				System.exit(0);
				
			}
		});
	}
}
