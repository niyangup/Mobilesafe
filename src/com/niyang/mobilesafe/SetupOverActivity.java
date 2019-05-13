package com.niyang.mobilesafe;

import com.niyang.mobilesafe.util.SpUtil;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class SetupOverActivity extends Activity {
	private TextView tv_safe_number;
	private TextView tv_in_setup1;
	private ImageView im_safe_pic;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		boolean set_over = SpUtil.getBoolean(getApplicationContext(), ConstantValue.SET_OVER, false);
		if (set_over) {
			setContentView(R.layout.activity_setup_over);
			initUI();
			initData();
		} else {
			Intent intent = new Intent(getApplicationContext(), Setup1Activity.class);
			startActivity(intent);
			finish();
		}
	}

	private void initData() {
		String phone = SpUtil.getString(getApplicationContext(), ConstantValue.NUMBER_SAFE, "");
		tv_safe_number.setText(phone);
		BitmapFactory.decodeResource(getResources(), R.drawable.lock);
		// 如果勾选绑定则是上锁的图片 未勾选则是未上锁的图片
		boolean isLock = SpUtil.getBoolean(getApplicationContext(), ConstantValue.OPEN_PROTECT, false);
		if (isLock) {
			im_safe_pic.setImageResource(R.drawable.lock);
		} else {
			im_safe_pic.setImageResource(R.drawable.unlock);
		}
		tv_in_setup1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(SetupOverActivity.this, Setup1Activity.class);
				startActivity(intent);
				finish();
			}
		});
	}

	private void initUI() {
		tv_safe_number = (TextView) findViewById(R.id.tv_safe_number);
		tv_in_setup1 = (TextView) findViewById(R.id.tv_in_setup1);
		im_safe_pic = (ImageView) findViewById(R.id.im_safe_pic);
	}
}
