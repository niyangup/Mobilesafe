package com.niyang.mobilesafe;

import com.niyang.mobilesafe.util.SpUtil;
import com.niyang.mobilesafe.util.ToastUtil;

import android.app.Activity;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Toast;

public class ToastLocationActivity extends Activity {
	private ImageView im_drag;
	private Button btn_top;
	private Button btn_bottom;
	private WindowManager mWM;
	private long startTime = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_toast_location);
		initUI();
	}

	private void initUI() {
		im_drag = (ImageView) findViewById(R.id.im_drag);
		btn_top = (Button) findViewById(R.id.btn_top);
		btn_bottom = (Button) findViewById(R.id.btn_bottom);
		mWM = (WindowManager) getSystemService(WINDOW_SERVICE);
		final int mScreenWidth = mWM.getDefaultDisplay().getWidth();
		final int mScreenHeight = mWM.getDefaultDisplay().getHeight();

		int location_x = SpUtil.getInt(getApplicationContext(), ConstantValue.LOCATION_X, 0);
		int location_y = SpUtil.getInt(getApplicationContext(), ConstantValue.LOCATION_Y, 0);

		LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);

		layoutParams.leftMargin = location_x;
		layoutParams.topMargin = location_y;

		im_drag.setLayoutParams(layoutParams);

		if (location_y > mScreenHeight / 2) {
			btn_bottom.setVisibility(View.INVISIBLE);
			btn_top.setVisibility(View.VISIBLE);
		} else {
			btn_bottom.setVisibility(View.VISIBLE);
			btn_top.setVisibility(View.INVISIBLE);
		}
		im_drag.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (startTime != 0) {
					if (System.currentTimeMillis() - startTime < 500) {
						Log.v("hah", "双击了");
						int left = mScreenWidth / 2 - im_drag.getWidth() / 2;
						int top = mScreenHeight / 2 - im_drag.getHeight() / 2;
						int right = mScreenWidth / 2 + im_drag.getWidth() / 2;
						int bottom = mScreenHeight / 2 + im_drag.getHeight() / 2;

						im_drag.layout(left, top, right, bottom);
						SpUtil.putInt(getApplicationContext(), ConstantValue.LOCATION_X, im_drag.getLeft());
						SpUtil.putInt(getApplicationContext(), ConstantValue.LOCATION_Y, im_drag.getTop());
					}
				}
				startTime = System.currentTimeMillis();

			}
		});
		
		im_drag.setOnTouchListener(new OnTouchListener() {

			private int startX;
			private int startY;

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				// 按下
				case MotionEvent.ACTION_DOWN:
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					break;

				// 移动
				case MotionEvent.ACTION_MOVE:
					int moveX = (int) event.getRawX();
					int moveY = (int) event.getRawY();

					int disX = moveX - startX;
					int disY = moveY - startY;

					int left = im_drag.getLeft() + disX;
					int top = im_drag.getTop() + disY;
					int rigth = im_drag.getRight() + disX;
					int bottom = im_drag.getBottom() + disY;

					if (left < 0) {
						return true;
					}
					if (rigth > mScreenWidth) {
						return true;
					}
					if (top < 0) {
						return true;
					}
					if (bottom > mScreenHeight - 22) {
						return true;
					}

					if (top > mScreenHeight / 2) {
						btn_bottom.setVisibility(View.INVISIBLE);
						btn_top.setVisibility(View.VISIBLE);
					} else {
						btn_bottom.setVisibility(View.VISIBLE);
						btn_top.setVisibility(View.INVISIBLE);
					}

					im_drag.layout(left, top, rigth, bottom);

					startX = (int) event.getRawX();
					startY = (int) event.getRawY();

					break;

				// 弹起
				case MotionEvent.ACTION_UP:
					// 存储移动到的位置
					SpUtil.putInt(getApplicationContext(), ConstantValue.LOCATION_X, im_drag.getLeft());
					SpUtil.putInt(getApplicationContext(), ConstantValue.LOCATION_Y, im_drag.getTop());
					break;
				}

				return false;
			}
		});
	}
}
