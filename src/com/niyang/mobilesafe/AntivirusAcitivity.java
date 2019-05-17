package com.niyang.mobilesafe;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.niyang.mobilesafe.engine.VirusDao;
import com.niyang.mobilesafe.util.Md5Util;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class AntivirusAcitivity extends Activity {
	private ImageView iv_scanning;
	private TextView tv_name;
	private ProgressBar pb_bar;
	private LinearLayout ll_add_text;
	private int index = 0;
	private List<ScanInfo> mVirusInfoList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_anit_virus);

		initUI();
		initAnimation();
		checkVirus();
	}

	private void checkVirus() {
		new Thread() {
			@Override
			public void run() {
				super.run();
				List<String> virusList = VirusDao.getVirusList();
				mVirusInfoList = new ArrayList<>();
				List<ScanInfo> scanInfoList = new ArrayList<>();
				// 1.获取所有应用程序的签名文件的md5码
				PackageManager pm = getPackageManager();

				// 2.获取手机所有应用的签名文件

				// PackageManager.GET_SIGNATURES 获取已安装应用的签名文件
				// PackageManager.GET_UNINSTALLED_PACKAGES 获取卸载完了的应用残余的文件
				List<PackageInfo> packageInfoList = pm
						.getInstalledPackages(PackageManager.GET_SIGNATURES + PackageManager.GET_UNINSTALLED_PACKAGES);
				final int size = packageInfoList.size();
				pb_bar.setMax(size);

				// 3.遍历所有的签名文件并转化为md5码
				for (PackageInfo packageInfo : packageInfoList) {
					Signature[] signatures = packageInfo.signatures;
					Signature signature = signatures[0];
					String string = signature.toCharsString();
					String encoder = Md5Util.encoder(string);
					// 比对是否为病毒
					final ScanInfo info = new ScanInfo();
					if (virusList.contains(encoder)) {
						// 记录病毒
						info.isVirus = true;
						mVirusInfoList.add(info);
					} else {
						info.isVirus = false;
					}
					info.packageName = packageInfo.packageName;
					info.name = packageInfo.applicationInfo.loadLabel(pm).toString();
					scanInfoList.add(info);

					SystemClock.sleep(50 + new Random().nextInt(100));

					// 更新进度条
					index++;
					pb_bar.setProgress(index);

					runOnUiThread(new Runnable() {
						public void run() {
							if (index < size) {
								tv_name.setText(info.name);
								TextView textView = new TextView(AntivirusAcitivity.this);
								if (info.isVirus) {
									textView.setTextColor(Color.RED);
									textView.setText("发现病毒:" + info.name);
								} else {
									textView.setTextColor(Color.BLACK);
									textView.setText("扫描安全:" + info.name);
								}
								ll_add_text.addView(textView, 0);
							} else {
								tv_name.setText("扫描完成");
								// 停止动画
								iv_scanning.clearAnimation();
								unInstallVirus();
							}
						}

						private void unInstallVirus() {
							for (ScanInfo info : mVirusInfoList) {
								String packageName = info.packageName;
								Intent intent = new Intent("android.intent.action.DELETE");
								intent.addCategory("android.intent.category.DEFAULT");
								intent.setData(Uri.parse("package:" + packageName));
								startActivity(intent);
							}
						}
					});
				}
			}
		}.start();
	}

	class ScanInfo {
		public boolean isVirus;
		public String packageName;
		public String name;
	}

	private void initAnimation() {
		RotateAnimation rotateAnimation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f,
				Animation.RELATIVE_TO_SELF, 0.5f);
		rotateAnimation.setDuration(2000);
		rotateAnimation.setRepeatCount(Animation.INFINITE);
		rotateAnimation.setFillAfter(true);
		iv_scanning.startAnimation(rotateAnimation);
	}

	private void initUI() {
		iv_scanning = (ImageView) findViewById(R.id.iv_scanning);
		tv_name = (TextView) findViewById(R.id.tv_name);
		pb_bar = (ProgressBar) findViewById(R.id.pb_bar);
		ll_add_text = (LinearLayout) findViewById(R.id.ll_add_text);

	}
}
