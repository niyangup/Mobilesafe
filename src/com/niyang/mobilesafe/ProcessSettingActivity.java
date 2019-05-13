package com.niyang.mobilesafe;

import com.niyang.mobilesafe.service.LockScreenService;
import com.niyang.mobilesafe.util.ServiceUtil;
import com.niyang.mobilesafe.util.SpUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class ProcessSettingActivity extends Activity {
	private CheckBox cb_show_system,cb_lock_clean;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_process_setting);
		initSystem();
		initClean();
	}

	/**
	 * 锁屏清理
	 */
	private void initClean() {
		cb_lock_clean = (CheckBox) findViewById(R.id.cb_lock_clean);
		boolean running = ServiceUtil.isRunning(getApplicationContext(), "com.niyang.mobilesafe.service.LockScreenService");
		cb_lock_clean.setChecked(running);
		if (running) {
			
			cb_lock_clean.setText("锁屏清理已开启");
		} else {
			cb_lock_clean.setText("锁屏清理已关闭");
		}
		
		cb_lock_clean.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					cb_show_system.setText("锁屏清理已开启");
					startService(new Intent(ProcessSettingActivity.this, LockScreenService.class));
				} else {
					cb_show_system.setText("锁屏清理已关闭");
					stopService(new Intent(ProcessSettingActivity.this, LockScreenService.class));
				}
			}
		});
	}

	private void initSystem() {
		cb_show_system = (CheckBox) findViewById(R.id.cb_show_system);
		boolean showSystem = SpUtil.getBoolean(getApplicationContext(), ConstantValue.SHOW_SYSTEM, false);
		cb_show_system.setChecked(showSystem);
		if (showSystem) {
			cb_show_system.setText("显示系统进程");
			
		} else {
			cb_show_system.setText("隐藏系统进程");
		}
		
		cb_show_system.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					cb_show_system.setText("显示系统进程");
				} else {
					cb_show_system.setText("隐藏系统进程");
				}
				SpUtil.putBoolean(getApplicationContext(), ConstantValue.SHOW_SYSTEM, isChecked);
			}
		});
	}
}
