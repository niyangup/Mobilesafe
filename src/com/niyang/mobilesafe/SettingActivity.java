package com.niyang.mobilesafe;

import com.niyang.mobilesafe.service.AddressService;
import com.niyang.mobilesafe.service.BlackNumberService;
import com.niyang.mobilesafe.util.ServiceUtil;
import com.niyang.mobilesafe.util.SpUtil;
import com.niyang.mobilesafe.view.SettingClickView;
import com.niyang.mobilesafe.view.SettingItemView;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class SettingActivity extends Activity {
	private String[] toastStyles;
	private int mToastStyle;
	private SettingClickView scv_toast_style;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);

		initUpdate();
		initAddress();
		initToastStyle();
		initLocation();
		initBlackNumber();
	}

	private void initBlackNumber() {
		final SettingItemView siv_blacknumber = (SettingItemView) findViewById(R.id.siv_blacknumber);
		boolean isRunning = ServiceUtil.isRunning(getApplicationContext(), "com.niyang.mobilesafe.service.BlackNumberService");
		siv_blacknumber.setCheck(isRunning);
		
		siv_blacknumber.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				boolean check = siv_blacknumber.isCheck();
				siv_blacknumber.setCheck(!check);
				if (!check) {
					startService(new Intent(SettingActivity.this, BlackNumberService.class));
				}else {
					stopService(new Intent(SettingActivity.this, BlackNumberService.class));
				}
				
			}
		});
	}

	private void initLocation() {
		SettingClickView scv_location = (SettingClickView) findViewById(R.id.scv_location);
		scv_location.setTitle("归属地提示框的位置");
		scv_location.setDes("设置归属地提示框的位置");
		scv_location.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(SettingActivity.this, ToastLocationActivity.class));
			}
		});
	}

	private void initToastStyle() {
		scv_toast_style = (SettingClickView) findViewById(R.id.scv_toast_style);
		scv_toast_style.setTitle("设置归属地显示风格");
		toastStyles = new String[] { "透明", "橙色", "蓝色", "灰色", "绿色" };
		mToastStyle = SpUtil.getInt(getApplicationContext(), ConstantValue.TOAST_STYLES, 0);
		// 通过索引,获取字符串数组中的文字,显示给描述内容控件
		scv_toast_style.setDes(toastStyles[mToastStyle]);
		scv_toast_style.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showToastDialog();
			}
		});
	}

	/**
	 * 创建显示样式的对话框
	 */
	protected void showToastDialog() {
		Builder builder = new AlertDialog.Builder(SettingActivity.this);
		builder.setIcon(R.drawable.ic_launcher);
		builder.setTitle("请选择归属地样式");
		/*
		 * 1.String[]类型描述颜色文字数组 2.弹出对话框时候的选中条目索引值 3.点击某一个条目后触发的点击事件(1.记录索引值 2.关闭对话框
		 * 3.显示选中色值文字)
		 */
		builder.setSingleChoiceItems(toastStyles, mToastStyle, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {// which 选中的索引值
				SpUtil.putInt(getApplicationContext(), ConstantValue.TOAST_STYLES, which);
				// TODO
				dialog.dismiss();
				scv_toast_style.setDes(toastStyles[which]);

			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.show();
	}

	private void initAddress() {
		// 电话归属地
		final SettingItemView siv_address = (SettingItemView) findViewById(R.id.siv_address);
		boolean isRunning = ServiceUtil.isRunning(this, "com.niyang.mobilesafe.service.AddressService");
		siv_address.setCheck(isRunning);
		siv_address.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 如果之前时未选中,点击后变为选中,如果之前为选中,点击之后变成未选中
				boolean isCheck = siv_address.isCheck();
				siv_address.setCheck(!isCheck);
				if (!isCheck) {
					startService(new Intent(SettingActivity.this, AddressService.class));
				} else {
					stopService(new Intent(SettingActivity.this, AddressService.class));
				}
			}
		});
	}

	private void initUpdate() {
		// 自动更新
		final SettingItemView siv_update = (SettingItemView) findViewById(R.id.siv_update);
		// 获取已有的开关状态,用作显示
		boolean open_update = SpUtil.getBoolean(getApplicationContext(), ConstantValue.OPEN_UPDATE, false);
		siv_update.setCheck(open_update);

		// 为整个Item设置点击监听
		siv_update.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 如果之前时未选中,点击后变为选中,如果之前为选中,点击之后变成未选中
				boolean isCheck = siv_update.isCheck();
				siv_update.setCheck(!isCheck);
				SpUtil.putBoolean(getApplicationContext(), ConstantValue.OPEN_UPDATE, !isCheck);
			}
		});
	}

}
