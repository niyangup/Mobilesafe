package com.niyang.mobilesafe;

import com.niyang.mobilesafe.util.SpUtil;
import com.niyang.mobilesafe.util.ToastUtil;
import com.niyang.mobilesafe.view.SettingItemView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.View.OnClickListener;

public class Setup2Activity extends Activity {
	private SettingItemView siv_sim_bound;
	private String serialNumber;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup2);

		initUI();
	}

	private void initUI() {
		siv_sim_bound = (SettingItemView) findViewById(R.id.siv_sim_bound);
		boolean isCheck = SpUtil.getBoolean(getApplicationContext(), ConstantValue.NUMBER_BOUND, false);
		siv_sim_bound.setCheck(isCheck);

		siv_sim_bound.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				boolean check = siv_sim_bound.isCheck();
				
				if (check) {
					// 若勾选了绑定sim卡,再点击下一步之后用sp存储当点sim的序列号
					TelephonyManager manager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
					serialNumber = manager.getSimSerialNumber();
					SpUtil.putString(getApplicationContext(), ConstantValue.NUMBER_SERIAL, serialNumber);
				} else {
					SpUtil.remove(getApplicationContext(), serialNumber);
				}
				
				siv_sim_bound.setCheck(!check);
				SpUtil.putBoolean(getApplicationContext(), ConstantValue.NUMBER_BOUND, !check);
			}
		});
	}

	public void preBtn(View v) {
		Intent intent = new Intent(Setup2Activity.this, Setup1Activity.class);
		startActivity(intent);
		finish();
		
		overridePendingTransition(R.anim.pre_in_anim, R.anim.pre_out_anim);
	}

	public void nextBtn(View v) {
		
		boolean check = siv_sim_bound.isCheck();
		if (check) {
			Intent intent = new Intent(Setup2Activity.this, Setup3Activity.class);
			startActivity(intent);
			finish();
			
			overridePendingTransition(R.anim.next_in_anim, R.anim.next_out_anim);
		}else {
			ToastUtil.show(getApplicationContext(), "sim卡未绑定");
		}
		
	}

}
