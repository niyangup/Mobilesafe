package com.niyang.mobilesafe.service;

import com.niyang.mobilesafe.ConstantValue;
import com.niyang.mobilesafe.R;
import com.niyang.mobilesafe.engine.AddressDao;
import com.niyang.mobilesafe.util.SpUtil;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.TextView;

public class AddressService extends Service {

	private TelephonyManager mTm;
	private MyListener listener;
	private final WindowManager.LayoutParams mParams = new WindowManager.LayoutParams();
	private View mViewToast;
	private String mAddress;
	private TextView tv_toast;
	private WindowManager mWm;
	private int mScreenWidth;
	private int mScreenHeight;
	private int[] mDrawables;
	private Handler mhandler = new Handler() {

		public void handleMessage(android.os.Message msg) {
			tv_toast.setText(mAddress);
		};
	};
	private InnerOutCallReceiver mInnerOutCallReceiver;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mTm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		listener = new MyListener();
		mTm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
		mWm = (WindowManager) getSystemService(WINDOW_SERVICE);
		mScreenWidth = mWm.getDefaultDisplay().getWidth();
		mScreenHeight = mWm.getDefaultDisplay().getHeight();
		
		IntentFilter intentFilter = new IntentFilter();
		//去电
		intentFilter.addAction(Intent.ACTION_NEW_OUTGOING_CALL);
		
		mInnerOutCallReceiver = new InnerOutCallReceiver();
		registerReceiver(mInnerOutCallReceiver, intentFilter);
		
	}
	
	@Override
	public void onDestroy() {
		if (mTm != null && listener != null) {
			mTm.listen(listener, PhoneStateListener.LISTEN_NONE);
		}
		if (mInnerOutCallReceiver!=null) {
			unregisterReceiver(mInnerOutCallReceiver);
		}
		super.onDestroy();
	}

	class InnerOutCallReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			String phone = getResultData();
			showToast(phone);
		}
		
	}

	class MyListener extends PhoneStateListener {

		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			super.onCallStateChanged(state, incomingNumber);
			switch (state) {
			// 响铃
			case TelephonyManager.CALL_STATE_RINGING:
				Log.v("AddressService", "响铃");
				showToast(incomingNumber);
				break;
			// 空闲
			case TelephonyManager.CALL_STATE_IDLE:
				Log.v("AddressService", "空闲");
				// 挂断电话,窗体需要移除
				if (mWm != null && mViewToast != null) {
					mWm.removeView(mViewToast);
				}
				break;
			// 摘机
			case TelephonyManager.CALL_STATE_OFFHOOK:
				Log.v("AddressService", "摘机");
				break;
			}
		}
	}
		public void showToast(String incomingNumber) {
			final WindowManager.LayoutParams params = mParams;
			params.height = WindowManager.LayoutParams.WRAP_CONTENT;
			params.width = WindowManager.LayoutParams.WRAP_CONTENT;
			params.format = PixelFormat.TRANSLUCENT;
			// 在响铃的时候显示Toast,和电话类型一致
			params.type = WindowManager.LayoutParams.TYPE_PHONE;
			params.setTitle("Toast");
			params.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
					| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
			// | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
			// 将Toast指定在左上角(位置)
			params.gravity = Gravity.LEFT + Gravity.TOP;
			// Toast显示效果(xml文件),将Toast挂载到windowManager窗体上

			mViewToast = View.inflate(getApplicationContext(), R.layout.toast_view, null);
			tv_toast = (TextView) mViewToast.findViewById(R.id.tv_toast);

			mViewToast.setOnTouchListener(new OnTouchListener() {
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

						params.x = params.x + disX;
						params.y = params.y + disY;
						if (params.x < 0) {
							params.x = 0;
						}
						if (params.y < 0) {
							params.y = 0;
						}
						if (params.x > mScreenWidth - mViewToast.getWidth()) {
							params.x = mScreenWidth - mViewToast.getWidth();
						}
						if (params.y > mScreenHeight - mViewToast.getHeight() - 22) {
							params.y = mScreenHeight - mViewToast.getHeight() - 22;
						}

						// 告知窗体Toast需要按照手势的移动,去做位置的更新
						mWm.updateViewLayout(mViewToast, params);

						startX = (int) event.getRawX();
						startY = (int) event.getRawY();

						break;

					// 弹起
					case MotionEvent.ACTION_UP:
						// 存储移动到的位置
						SpUtil.putInt(getApplicationContext(), ConstantValue.LOCATION_X, params.x);
						SpUtil.putInt(getApplicationContext(), ConstantValue.LOCATION_Y, params.y);
						break;
					}
					return true;
				}
			});

			params.x = SpUtil.getInt(getApplicationContext(), ConstantValue.LOCATION_X, 0);
			params.y = SpUtil.getInt(getApplicationContext(), ConstantValue.LOCATION_Y, 0);

			mDrawables = new int[] { R.drawable.call_locate_white, R.drawable.call_locate_orange,
					R.drawable.call_locate_blue, R.drawable.call_locate_gray, R.drawable.call_locate_green };
			int toastStyleIndex = SpUtil.getInt(getApplicationContext(), ConstantValue.TOAST_STYLES, 0);
			tv_toast.setBackgroundResource(mDrawables[toastStyleIndex]);

			mWm.addView(mViewToast, params);
			query(incomingNumber);
		}

		private void query(final String incomingNumber) {
			new Thread() {
				@Override
				public void run() {
					super.run();
					mAddress = AddressDao.getAddress(incomingNumber);
					mhandler.sendEmptyMessage(0);
				}
			}.start();
		}
	
}