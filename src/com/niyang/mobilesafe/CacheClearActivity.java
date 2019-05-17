package com.niyang.mobilesafe;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Random;

import com.niyang.mobilesafe.util.ToastUtil;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.PackageStats;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.os.SystemClock;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class CacheClearActivity extends Activity {
	protected static final int UPDATE_CACHE_APP = 100;
	private Button btn_clean;
	private ProgressBar pb_bar;
	private TextView tv_name;
	private LinearLayout ll_add_text;
	private PackageManager mPm;
	private int mIndex = 0;

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case UPDATE_CACHE_APP:
				View view = View.inflate(getApplicationContext(), R.layout.linearlayout_item, null);

				ImageView iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
				TextView tv_name1 = (TextView) view.findViewById(R.id.tv_name);
				TextView tv_memory_info = (TextView) view.findViewById(R.id.tv_memory_info);
				ImageView iv_delete = (ImageView) view.findViewById(R.id.iv_delete);

				final CacheInfo cacheInfo = (CacheInfo) msg.obj;
				iv_icon.setBackgroundDrawable(cacheInfo.icon);
				tv_name1.setText(cacheInfo.name);

				String size = Formatter.formatFileSize(getApplicationContext(), cacheInfo.cacheSize);
				tv_memory_info.setText(size);

				ll_add_text.addView(view, 0);

				iv_delete.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {

						Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
						intent.setData(Uri.parse("package:" + cacheInfo.packageName));
						startActivity(intent);
					}
				});

				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cache_clear);
		initUI();
		initData();
	}

	/**
	 * 遍历手机所有的应用,获取有缓存的应用,用作显示
	 */
	private void initData() {
		new Thread() {

			@Override
			public void run() {
				super.run();
				mPm = getPackageManager();
				// 2.获取安装在手机的所有应用
				final List<PackageInfo> installedPackages = mPm.getInstalledPackages(0);
				// 3.给进度条设置最大值
				pb_bar.setMax(installedPackages.size());
				// 4.遍历每一个应用,获取有缓存的应用信息(应用名称,图标,缓存大小,包名)
				for (PackageInfo packageInfo : installedPackages) {
					final String packageName = packageInfo.packageName;
					getPackageCache(packageName);

					mIndex++;
					pb_bar.setProgress(mIndex);

					runOnUiThread(new Runnable() {
						public void run() {
							String name;
							try {
								if (mIndex < installedPackages.size()) {

									name = mPm.getApplicationInfo(packageName, 0).loadLabel(mPm).toString();
									tv_name.setText(name);
								} else {
									tv_name.setText("扫描完成");
								}
							} catch (NameNotFoundException e) {
								e.printStackTrace();
							}
						}
					});
					SystemClock.sleep(100 + new Random().nextInt(50));

				}

			}
		}.start();
	}

	class CacheInfo {
		public String name;
		public Drawable icon;
		public String packageName;
		public long cacheSize;
	}

	/**
	 * 通过包名获取此包名指向应用的缓存信息
	 * 
	 * @param packageInfo
	 */
	public void getPackageCache(String packageName) {
		IPackageStatsObserver.Stub mStub = new IPackageStatsObserver.Stub() {

			@Override
			public void onGetStatsCompleted(PackageStats pStats, boolean succeeded) throws RemoteException {
				long caCheSize = pStats.cacheSize;
				if (caCheSize > 0) {
					Message msg = Message.obtain();
					msg.what = UPDATE_CACHE_APP;
					CacheInfo info = null;
					try {
						info = new CacheInfo();
						info.cacheSize = caCheSize;
						info.packageName = pStats.packageName;
						info.name = mPm.getApplicationInfo(pStats.packageName, 0).loadLabel(mPm).toString();
						info.icon = mPm.getApplicationInfo(pStats.packageName, 0).loadIcon(mPm);
					} catch (NameNotFoundException e) {
						e.printStackTrace();
					}
					msg.obj = info;
					mHandler.sendMessage(msg);

				} else {

				}
			}
		};

		try {
			Class<?> clazz = Class.forName("android.content.pm.PackageManager");
			Method method = clazz.getMethod("getPackageSizeInfo", String.class, IPackageStatsObserver.class);
			method.invoke(mPm, packageName, mStub);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void initUI() {
		btn_clean = (Button) findViewById(R.id.btn_clean);
		pb_bar = (ProgressBar) findViewById(R.id.pb_bar);
		tv_name = (TextView) findViewById(R.id.tv_name);
		ll_add_text = (LinearLayout) findViewById(R.id.ll_add_text);
		
		btn_clean.setVisibility(View.GONE);

		btn_clean.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					Class<?> clazz = Class.forName("android.content.pm.PackageManager");
					Method method = clazz.getMethod("freeStorageAndNotify", long.class, IPackageDataObserver.class);
					method.invoke(mPm, Long.MAX_VALUE, new IPackageDataObserver.Stub() {
						@Override
						public void onRemoveCompleted(String packageName, boolean succeeded) throws RemoteException {

							runOnUiThread(new Runnable() {
								public void run() {
									ll_add_text.removeAllViews();
								}
							});

						}
					});

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
