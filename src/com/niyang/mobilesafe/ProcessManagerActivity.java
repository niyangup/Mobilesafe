package com.niyang.mobilesafe;

import com.niyang.mobilesafe.engine.ProcessInfoProvider;
import com.niyang.mobilesafe.util.ToastUtil;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class ProcessManagerActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_process_manager);
		long totalSpace = ProcessInfoProvider.getTotalSpace(getApplicationContext());
		Log.v("TAG", ""+totalSpace);
		System.out.println(""+totalSpace);
		ToastUtil.show(getApplicationContext(),  ""+totalSpace);
	}
}
