package com.niyang.mobilesafe;

import java.util.ArrayList;
import java.util.List;

import com.niyang.mobilesafe.db.dao.AppLockDao;
import com.niyang.mobilesafe.db.domain.AppInfo;
import com.niyang.mobilesafe.engine.AppInfoProvider;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class AppLockActivity extends Activity {
	private Button btn_unlock;
	private Button btn_locked;
	private LinearLayout ll_unlock;
	private LinearLayout ll_lock;
	private TextView tv_unlock;
	private TextView tv_lock;
	private ListView lv_lock;
	private ListView lv_unlock;
	private List<AppInfo> mAppInfoList;
	private AppLockDao mDao;
	private List<AppInfo> mLockList;
	private List<AppInfo> mUnLockList;
	private MyAdapter mLockAdapter;
	private MyAdapter mUnLockAdapter;
	private Handler mHandler = new Handler() {

		public void handleMessage(Message msg) {
			mLockAdapter = new MyAdapter(true);
			lv_lock.setAdapter(mLockAdapter);

			mUnLockAdapter = new MyAdapter(false);
			lv_unlock.setAdapter(mUnLockAdapter);
		};
	};
	private TranslateAnimation translateAnimation;

	class MyAdapter extends BaseAdapter {
		private boolean isLock;

		/**
		 * @param isLock
		 *            用于区分已加锁和未加锁应用的标识 true 已加锁
		 */
		public MyAdapter(boolean isLock) {
			this.isLock = isLock;
		}

		@Override
		public int getCount() {
			if (isLock) {
				tv_lock.setText("已加锁应用:" + mLockList.size());
				return mLockList.size();
			} else {
				tv_unlock.setText("未加锁应用:" + mUnLockList.size());
				return mUnLockList.size();
			}
		}

		@Override
		public AppInfo getItem(int position) {
			if (isLock) {
				return mLockList.get(position);
			} else {
				return mUnLockList.get(position);
			}
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = View.inflate(getApplicationContext(), R.layout.listview_islock_item, null);
				holder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);
				holder.iv_lock = (ImageView) convertView.findViewById(R.id.iv_lock);
				holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			final AppInfo appInfo = getItem(position);
			final View animationView = convertView;

			holder.iv_icon.setBackgroundDrawable(appInfo.icon);
			holder.tv_name.setText(appInfo.name);
			
			if (isLock) {
				holder.iv_lock.setBackgroundResource(R.drawable.lock);
			} else {
				holder.iv_lock.setBackgroundResource(R.drawable.unlock);
			}

			holder.iv_lock.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					translateAnimation.setAnimationListener(new AnimationListener() {

						// 动画开始的时候执行
						@Override
						public void onAnimationStart(Animation animation) {

						}

						// 动画重复的时候执行
						@Override
						public void onAnimationRepeat(Animation animation) {

						}

						// 动画结束的时候执行
						@Override
						public void onAnimationEnd(Animation animation) {
							animationView.setAnimation(translateAnimation);
							if (isLock) {
								mLockList.remove(appInfo);
								mUnLockList.add(appInfo);

								mDao.delete(appInfo.packageName);

								mLockAdapter.notifyDataSetChanged();
							} else {
								mLockList.add(appInfo);
								mUnLockList.remove(appInfo);

								mDao.insert(appInfo.packageName);

								mUnLockAdapter.notifyDataSetChanged();

							}
						}
					});

				}
			});
			return convertView;
		}

	}

	static class ViewHolder {
		ImageView iv_icon;
		TextView tv_name;
		ImageView iv_lock;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_lock);
		initUI();
		initData();
		initAnimation();
	}

	/**
	 * 初始化平移动画
	 */
	private void initAnimation() {
		translateAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 1,
				Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
		translateAnimation.setDuration(500);

	}

	/**
	 * 区分已加锁与未加锁应用的集合
	 */
	private void initData() {
		new Thread() {
			public void run() {
				// 1.获取所有手机中的应用
				mAppInfoList = AppInfoProvider.getAppInfoList(getApplicationContext());
				mLockList = new ArrayList<>();
				mUnLockList = new ArrayList<>();

				// 3.获取数据库中已加锁应用包名的集合
				mDao = AppLockDao.getInstance(getApplicationContext());
				List<String> lockPackList = mDao.findAll();

				for (AppInfo appinfo : mAppInfoList) {
					// 4.如果循环到应用的包名,在数据库中,则说明是已加锁应用

					if (lockPackList.contains(appinfo.packageName)) {
						mLockList.add(appinfo);
					} else {
						mUnLockList.add(appinfo);
					}
				}
				// 5.告知主线程集合准备完毕
				mHandler.sendEmptyMessage(0);
			};

		}.start();

	}

	private void initUI() {
		btn_unlock = (Button) findViewById(R.id.btn_unlock);
		btn_locked = (Button) findViewById(R.id.btn_locked);

		ll_unlock = (LinearLayout) findViewById(R.id.ll_unlock);
		ll_lock = (LinearLayout) findViewById(R.id.ll_lock);

		tv_unlock = (TextView) findViewById(R.id.tv_unlock);
		tv_lock = (TextView) findViewById(R.id.tv_lock);

		lv_lock = (ListView) findViewById(R.id.lv_lock);
		lv_unlock = (ListView) findViewById(R.id.lv_unlock);

		btn_locked.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 1.已加锁列表显示,未加锁列表隐藏
				ll_lock.setVisibility(View.VISIBLE);
				ll_unlock.setVisibility(View.GONE);
				// 2.未加锁按钮颜色变浅,已加锁颜色变深
				btn_unlock.setBackgroundResource(R.drawable.tab_left_default);
				btn_locked.setBackgroundResource(R.drawable.tab_right_pressed);
			}
		});

		btn_unlock.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ll_lock.setVisibility(View.GONE);
				ll_unlock.setVisibility(View.VISIBLE);

				btn_unlock.setBackgroundResource(R.drawable.tab_left_pressed);
				btn_locked.setBackgroundResource(R.drawable.tab_right_default);
			}
		});

	}
}
