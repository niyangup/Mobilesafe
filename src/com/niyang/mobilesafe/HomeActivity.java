package com.niyang.mobilesafe;

import com.niyang.mobilesafe.util.Md5Util;
import com.niyang.mobilesafe.util.SpUtil;
import com.niyang.mobilesafe.util.ToastUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

public class HomeActivity extends Activity {
	private LinearLayout mLl_one;
	private LinearLayout mLl_two;
	private LinearLayout mLl_three;
	private LinearLayout mLl_four;
	private LinearLayout mLl_five;
	private LinearLayout mLl_six;
	private LinearLayout mLl_seven;
	private LinearLayout mLl_eight;
	private LinearLayout mLl_nine;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		initView();
		mLl_one.setOnClickListener(new MyListener());
		mLl_two.setOnClickListener(new MyListener());
		mLl_three.setOnClickListener(new MyListener());
		mLl_four.setOnClickListener(new MyListener());
		mLl_nine.setOnClickListener(new MyListener());
		mLl_eight.setOnClickListener(new MyListener());
	}

	public void showDialog() {
		// 判断本地是否有存储密码(sp 字符串)
		String psd = SpUtil.getString(HomeActivity.this, ConstantValue.MOBILE_SAFE_PWD, "");
		// 如果Sp里没有密码,则可判定为第一次进入 需要设置密码和确认密码
		if (TextUtils.isEmpty(psd)) {
			showSetPsdDialog();
		} else {
			// 如果有密码,则只需要确认密码即可
			showConfirmPsdDialog();
		}

	}

	/**
	 * 确认密码的对话框
	 */
	private void showConfirmPsdDialog() {
		Builder builder = new AlertDialog.Builder(HomeActivity.this);
		final AlertDialog dialog = builder.create();
		final View view = View.inflate(getApplicationContext(), R.layout.dialog_confirm_psd, null);
		dialog.setView(view);
		dialog.show();

		Button btn_cancel = (Button) view.findViewById(R.id.btn_cancel2);
		Button btn_submit = (Button) view.findViewById(R.id.btn_submit2);

		btn_submit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				EditText et_psd = (EditText) view.findViewById(R.id.et_psd);
				String psd = et_psd.getText().toString().trim();
				// 将密码使用Md5加密后与本地密码比对
				String encoder = Md5Util.encoder(psd);

				String confirmPsd = SpUtil.getString(HomeActivity.this, ConstantValue.MOBILE_SAFE_PWD, "");
				if (!TextUtils.isEmpty(encoder)) {
					if (encoder.equals(confirmPsd)) {
						// 进入应用手机防盗模块
						// Intent intent = new Intent(HomeActivity.this, TestActivity.class);
						Intent intent = new Intent(HomeActivity.this, SetupOverActivity.class);
						startActivity(intent);
						dialog.dismiss();
					} else {
						ToastUtil.show(HomeActivity.this, "确认密码错误");
					}
				} else {
					// 提示用户输入有空的情况
					ToastUtil.show(HomeActivity.this, "请输入密码");
				}
			}
		});

		btn_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
	}

	/**
	 * 设置密码的对话框
	 */
	
	private void showSetPsdDialog() {
		Builder builder = new AlertDialog.Builder(HomeActivity.this);
		final AlertDialog dialog = builder.create();
		final View view = View.inflate(getApplicationContext(), R.layout.dialog_set_psd, null);
		dialog.setView(view);
		dialog.show();

		Button btn_cancel = (Button) view.findViewById(R.id.btn_cancel);
		Button btn_submit = (Button) view.findViewById(R.id.btn_submit);

		btn_submit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				EditText et_confirm_pas = (EditText) view.findViewById(R.id.et_confirm_psd);
				EditText et_set_pas = (EditText) view.findViewById(R.id.et_set_pas);

				String psd = et_confirm_pas.getText().toString().trim();
				String confirmPsd = et_set_pas.getText().toString().trim();

				if (!TextUtils.isEmpty(psd) && !TextUtils.isEmpty(confirmPsd)) {
					if (psd.equals(confirmPsd)) {
						// 进入应用手机防盗模块
						// 将密码使用Md5加密后存储
						SpUtil.putString(getApplicationContext(), ConstantValue.MOBILE_SAFE_PWD,
								Md5Util.encoder(confirmPsd));
						// Intent intent = new Intent(HomeActivity.this, TestActivity.class);
						Intent intent = new Intent(HomeActivity.this, SetupOverActivity.class);
						startActivity(intent);
						dialog.dismiss();
					} else {
						ToastUtil.show(HomeActivity.this, "确认密码错误");
					}
				} else {
					// 提示用户输入有空的情况
					ToastUtil.show(HomeActivity.this, "用户名或密码为空");
				}
			}
		});

		btn_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
	}

	public void initView() {
		mLl_one = (LinearLayout) findViewById(R.id.ll_one);
		mLl_two = (LinearLayout) findViewById(R.id.ll_two);
		mLl_three = (LinearLayout) findViewById(R.id.ll_three);
		mLl_four = (LinearLayout) findViewById(R.id.ll_four);
		mLl_five = (LinearLayout) findViewById(R.id.ll_five);
		mLl_six = (LinearLayout) findViewById(R.id.ll_six);
		mLl_seven = (LinearLayout) findViewById(R.id.ll_seven);
		mLl_eight = (LinearLayout) findViewById(R.id.ll_eight);
		mLl_nine = (LinearLayout) findViewById(R.id.ll_nine);
	}

class MyListener implements OnClickListener{

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		
		case R.id.ll_one:
			showDialog();
			break;
		case R.id.ll_two:
			startActivity(new Intent(HomeActivity.this, BlackNumberActivity.class));
			break;
		case R.id.ll_three:
			startActivity(new Intent(HomeActivity.this, AppManagerActivity.class));
			break;
		case R.id.ll_four:
			startActivity(new Intent(HomeActivity.this, ProcessManagerActivity.class));
			break;
		case R.id.ll_eight:
			startActivity(new Intent(HomeActivity.this, AToolActivity.class));
			break;
		case R.id.ll_nine:
			Intent intent = new Intent(HomeActivity.this, SettingActivity.class);
			startActivity(intent);
			break;

		
		}
	}
	
}
}
