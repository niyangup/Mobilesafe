package com.niyang.mobilesafe;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.niyang.mobilesafe.util.SpUtil;
import com.niyang.mobilesafe.util.StreamUtil;
import com.niyang.mobilesafe.util.ToastUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 启动页面
 * 
 * @author niyang
 *
 */
public class SplashActivity extends Activity {

	protected static final int VERSION_UPDATE = 100;
	protected static final int URL_ERROR = 101;
	protected static final int IO_ERROR = 102;
	protected static final int JSONE_ERROR = 103;
	protected static final int ENTER_HOME = 104;
	private TextView mTv;
	private String mVersionName;
	private String mVersionDes;
	private String mVersionCode;
	private String mDownloadUri;
	private RelativeLayout mRl_root;
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			int type = msg.what;
			if (type == URL_ERROR) {
				ToastUtil.show(SplashActivity.this, "URL错误");
				enter_home();
			} else if (type == IO_ERROR) {
				ToastUtil.show(SplashActivity.this, "IO错误");
				enter_home();
			} else if (type == ENTER_HOME) {
				enter_home();
			} else if (type == JSONE_ERROR) {
				ToastUtil.show(SplashActivity.this, "JSON解析错误");
				enter_home();
			} else if (type == VERSION_UPDATE) {
				Builder builder = new AlertDialog.Builder(SplashActivity.this);
				builder.setIcon(R.drawable.ic_launcher);
				builder.setTitle("升级提醒");
				builder.setMessage(mVersionDes);
				builder.setPositiveButton("是", new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// 开始下载
						downloadApk();
					}
				});
				builder.setNegativeButton("否", new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// 点击"否"后跳转到HomeActicit页面
						enter_home();
					}

				});
				builder.show();
			} else {
				enter_home();
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		mRl_root = (RelativeLayout) findViewById(R.id.rl_root);
		mTv = (TextView) findViewById(R.id.tv_version_name);
		initData();
		initDB();
		if (SpUtil.getBoolean(getApplicationContext(), ConstantValue.OPEN_UPDATE, false)) {
			checkVersionCode();
		} else {
			// 发送消息4秒后处理当前ENTER_HOME状态吗指向的消息
			handler.sendEmptyMessageDelayed(ENTER_HOME, 4000);
		}

		initAnimation();

		if (!SpUtil.getBoolean(getApplicationContext(), ConstantValue.HAS_SHORTCUT, false)) {
			initShortCut();
		}
	}

	private void initShortCut() {
		// 1.给Intent添加数据 名称,图标
		Intent intent = new Intent();
		intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");

		intent.putExtra(Intent.EXTRA_SHORTCUT_ICON,
				BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher));
		intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "黑马卫士");

		// 点击图标后跳转到homeActivity
		Intent shortCutIntnet = new Intent("android.intent.action.HOME");
		shortCutIntnet.addCategory("android.intent.category.DEFAULT");
		intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortCutIntnet);

		// 2.发送广播
		sendBroadcast(intent);
		SpUtil.putBoolean(getApplicationContext(), ConstantValue.HAS_SHORTCUT, true);
	}

	private void initDB() {
		// 1.归属地数据库拷贝
		initAddressDB("address.db");
		// 2.常用号码数据库拷贝
		initAddressDB("commonnum.db");

	}

	private void initAddressDB(String dbName) {
		// 将数据库复制到文件中
		File files = getFilesDir();
		File file = new File(files, dbName);
		if (file.exists()) {
			return;
		} else {
			InputStream open = null;
			FileOutputStream fos = null;
			try {
				open = getAssets().open(dbName);
				fos = new FileOutputStream(file);
				byte[] buffer = new byte[1024];
				int len;
				while ((len = open.read(buffer)) != -1) {
					fos.write(buffer, 0, len);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (open != null && fos != null) {
					try {
						open.close();
						fos.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}

	}

	private void initAnimation() {
		AlphaAnimation animation = new AlphaAnimation(0, 1);
		animation.setDuration(3000);
		mRl_root.startAnimation(animation);
	}

	/**
	 * 初始化页面的版本号
	 */
	private void initData() {
		mTv.setText("版本名称:" + getVersionName());
	}

	/**
	 * 获取本地版本名称
	 * 
	 * @return 版本名称
	 */
	private String getVersionName() {
		try {
			PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
			return packageInfo.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 获取本地版本号
	 * 
	 * @return 本地版本号
	 */
	private int getVersionCode() {
		try {
			PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
			return packageInfo.versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * 检查服务器的版本号与本地是否相同
	 */
	private void checkVersionCode() {
		new Thread() {
			public void run() {
				long startTime = System.currentTimeMillis();
				Message msg = Message.obtain();
				try {
					URL url = new URL("http://192.168.43.191:8081/update.json");
					HttpURLConnection conn = (HttpURLConnection) url.openConnection();
					conn.setConnectTimeout(2000);
					conn.setReadTimeout(2000);
					int code = conn.getResponseCode();
					if (code == 200) {
						InputStream in = conn.getInputStream();
						String json = StreamUtil.readStream(in);
						in.close();
						Log.v("TAG", json);
						JSONObject js = new JSONObject(json);
						mVersionName = js.getString("versionName");
						mVersionDes = js.getString("versionDes");
						mVersionCode = js.getString("versionCode");
						mDownloadUri = js.getString("downloadUri");

						if (Integer.parseInt(mVersionCode) > getVersionCode()) {
							// 更新对话框
							msg.what = VERSION_UPDATE;
						}
					}
				} catch (MalformedURLException e) {
					e.printStackTrace();
					msg.what = URL_ERROR;
				} catch (IOException e) {
					e.printStackTrace();
					msg.what = IO_ERROR;
				} catch (JSONException e) {
					e.printStackTrace();
					msg.what = JSONE_ERROR;
				} finally {
					long endTime = System.currentTimeMillis();
					long time = endTime - startTime;
					Log.v("TAG", "time " + time);
					if (time < 4000) {
						try {
							Thread.sleep(4000 - time);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					handler.sendMessage(msg);
				}
			};
		}.start();

	}

	/**
	 * 跳转到HomeActivity页面
	 */
	public void enter_home() {
		Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
		startActivity(intent);
		finish();
	}

	public void downloadApk() {
		String path = "data/data/com.example.mobilesafe" + File.separator + "mobilesafe.apk";
		HttpUtils httpUtils = new HttpUtils();
		// 下载url 目标文件存放路径
		httpUtils.download(mDownloadUri, path, new RequestCallBack<File>() {

			@Override
			public void onSuccess(ResponseInfo<File> responseInfo) {
				// 下载成功
				File result = responseInfo.result;
				Log.v("TAG", "下载成功");
				installApk(result);
			}

			@Override
			public void onFailure(HttpException httpException, String arg1) {
				// 下载失败
				Log.v("TAG", "下载失败");
			}

			@Override
			public void onStart() {
				// 下载刚开始
				super.onStart();
				Log.v("TAG", "下载开始");
			}

			public void onLoading(long total, long current, boolean isUploading) {
				Log.v("TAG", "total" + total);
				Log.v("TAG", "current" + current);
				Log.v("TAG", "下载中");
			};
		});
	}

	/**
	 * 安装Apk
	 * 
	 * @param file
	 */
	protected void installApk(File file) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivityForResult(intent, 666);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		enter_home();
		super.onActivityResult(requestCode, resultCode, data);
	}
}
