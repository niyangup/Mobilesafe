package com.niyang.mobilesafe;

import com.niyang.mobilesafe.util.SpUtil;
import com.niyang.mobilesafe.util.ToastUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class Setup3Activity extends Activity {
	private EditText et_number;
	private Button btn_number_contact;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup3);
		initUI();
	}

	public void preBtn(View v) {
		Intent intent = new Intent(Setup3Activity.this, Setup2Activity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.pre_in_anim, R.anim.pre_out_anim);
	}

	public void nextBtn(View v) {
		String number = et_number.getText().toString().trim();
		if (!TextUtils.isEmpty(number)) {
			SpUtil.putString(getApplicationContext(), ConstantValue.NUMBER_SAFE, number);
			Intent intent = new Intent(Setup3Activity.this, Setup4Activity.class);
			startActivity(intent);
			finish();
			
			overridePendingTransition(R.anim.next_in_anim, R.anim.next_out_anim);
		}else {
			ToastUtil.show(getApplicationContext(), "号码未填,请填写号码");
		}
		
	}

	private void initUI() {
		et_number = (EditText) findViewById(R.id.et_number);
		String phone = SpUtil.getString(getApplicationContext(), ConstantValue.NUMBER_SAFE, "");
		if (!TextUtils.isEmpty(phone)) {
			et_number.setText(phone);
		}
		btn_number_contact = (Button) findViewById(R.id.btn_number_contact);

		btn_number_contact.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), ContactListActivity.class);
				startActivityForResult(intent, 10);
			}
		});
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode==20) {
			String phone = data.getStringExtra("phone");
			SpUtil.putString(getApplicationContext(), ConstantValue.NUMBER_SAFE, "");
			et_number.setText(phone);
		}
	}
}
