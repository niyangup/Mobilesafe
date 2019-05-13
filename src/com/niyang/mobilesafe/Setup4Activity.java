package com.niyang.mobilesafe;

import com.niyang.mobilesafe.util.SpUtil;
import com.niyang.mobilesafe.util.ToastUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class Setup4Activity extends Activity {
	private CheckBox cb_open_safe;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup4);
		cb_open_safe = (CheckBox) findViewById(R.id.cb_open_safe);
		boolean checked = SpUtil.getBoolean(getApplicationContext(), ConstantValue.OPEN_PROTECT, false);
		cb_open_safe.setChecked(checked);
		
		if (checked) {
			cb_open_safe.setText("你已开启防盗保护");
		}else {
			cb_open_safe.setText("你未开启防盗保护");
		}
		
		cb_open_safe.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					cb_open_safe.setText("你已开启防盗保护");
				}else {
					cb_open_safe.setText("你未开启防盗保护");
				}
				SpUtil.putBoolean(getApplicationContext(), ConstantValue.OPEN_PROTECT, isChecked);
			}
		});
	}
	
	public void preBtn(View v) {
		Intent intent = new Intent(Setup4Activity.this, Setup3Activity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.pre_in_anim, R.anim.pre_out_anim);
	}

	public void nextBtn(View v) {
		if (cb_open_safe.isChecked()) {
			Intent intent = new Intent(Setup4Activity.this, SetupOverActivity.class);
			startActivity(intent);
			finish();
			overridePendingTransition(R.anim.next_in_anim, R.anim.next_out_anim);
			SpUtil.putBoolean(getApplicationContext(), ConstantValue.SET_OVER, true);
		}else {
			ToastUtil.show(getApplicationContext(), "请勾选");
		}
		
	}
}
