package com.niyang.mobilesafe;

import java.io.File;

import com.niyang.mobilesafe.engine.SmsBackUp;
import com.niyang.mobilesafe.engine.SmsBackUp.callBack;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class AToolActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_atool);
		//电话归属地查询方法
		initAddress();
		//短息备份方法
		initBackup();
		//常用号码查询
		initCommonNumberQuery();
	}

	private void initCommonNumberQuery() {
		TextView tv_number_query = (TextView) findViewById(R.id.tv_number_query);
		tv_number_query.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(getApplicationContext(), CommonNumberActivity.class));
			}
		});
		
	}

	private void initBackup() {
		TextView tv_sms_backup = (TextView) findViewById(R.id.tv_sms_backup);
		tv_sms_backup.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showSmsBackUpDialog();
			}
		});

	}

	protected void showSmsBackUpDialog() {
		final ProgressDialog dialog = new ProgressDialog(AToolActivity.this);
		dialog.setTitle("短信备份");
		dialog.setIcon(R.drawable.ic_launcher);
		dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		dialog.show();

		new Thread() {
			public void run() {
				// String path = Environment.getDataDirectory().getAbsolutePath() +
				// File.separator + "sms.xml";
				String path = getFilesDir().getAbsolutePath() + File.separator + "sms.xml";
				SmsBackUp.backup(getApplicationContext(), path, new callBack() {
					
					@Override
					public void setProgress(int index) {
						dialog.setProgress(index);
					}
					
					@Override
					public void setMax(int max) {
						dialog.setMax(max);
					}
				});
				dialog.dismiss();
			};
		}.start();
	}

	private void initAddress() {
		TextView tv_query_phone_address = (TextView) findViewById(R.id.tv_query_phone_address);
		tv_query_phone_address.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(AToolActivity.this, QueryAddressActivity.class));
			}
		});
	}

}
