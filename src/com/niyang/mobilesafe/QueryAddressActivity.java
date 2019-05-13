package com.niyang.mobilesafe;

import com.niyang.mobilesafe.engine.AddressDao;

import android.app.Activity;
import android.app.DownloadManager.Query;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class QueryAddressActivity extends Activity {
	private EditText et_phone;
	private Button btn_query;
	private TextView tv_result;
	private String address;
	private Handler mHandler = new Handler() {

		public void handleMessage(Message msg) {
			tv_result.setText(address);
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_queryaddress);
		initUI();

	}

	private void initUI() {
		et_phone = (EditText) findViewById(R.id.et_phone);
		btn_query = (Button) findViewById(R.id.btn_query);
		tv_result = (TextView) findViewById(R.id.tv_result);

		et_phone.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				String phone1 = et_phone.getText().toString().trim();
				query(phone1);
			}
		});

		btn_query.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String phone = et_phone.getText().toString().trim();
				if (!TextUtils.isEmpty(phone)) {
					query(phone);
				} else {
					Animation shake = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);
					et_phone.startAnimation(shake);
					Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
					// vibrator.vibrate(new long[] {2000,5000,2000,5000}, -1);
					vibrator.vibrate(200);
				}
			}
		});
	}

	protected void query(final String phone) {
		new Thread() {
			public void run() {
				address = AddressDao.getAddress(phone);
				mHandler.sendEmptyMessage(0);
			};
		}.start();
	}
}