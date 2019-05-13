package com.niyang.mobilesafe;

import com.niyang.mobilesafe.util.ToastUtil;

import android.app.Activity;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class TestActivity extends Activity {
	long[] mHits = new long[3];
	private Button mBtn;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// TextView textView = new TextView(this);
		// textView.setText("TestActivity");
		setContentView(R.layout.activity_test);
		mBtn = (Button) findViewById(R.id.button1);
		
		mBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
				mHits[mHits.length - 1] = SystemClock.uptimeMillis();
				if (mHits[mHits.length - 1] - mHits[0] < 500) {
					Log.v("hah", "双击了");
					ToastUtil.show(getApplicationContext(), "3击了");
				}
			}
		});
	}
}
