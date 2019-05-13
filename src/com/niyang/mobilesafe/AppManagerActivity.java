package com.niyang.mobilesafe;

import java.util.ArrayList;
import java.util.List;

import com.niyang.mobilesafe.db.domain.AppInfo;
import com.niyang.mobilesafe.engine.AppInfoProvider;
import com.niyang.mobilesafe.util.ToastUtil;

import android.app.Activity;
import android.app.AliasActivity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.format.Formatter;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

public class AppManagerActivity extends Activity implements OnClickListener {
	private TextView tv_user_space;
	private TextView tv_sd_space;
	private ListView lv_app_mananger;
	private MyAdapter mAdapter;
	private TextView tv_des;
	private AppInfo mAppInfo;

	private List<AppInfo> mSystemList;
	private List<AppInfo> mCustomerList;

	private List<AppInfo> mAppInfoList;
	private Handler mHandler = new Handler() {

		public void handleMessage(android.os.Message msg) {
			mAdapter = new MyAdapter();
			lv_app_mananger.setAdapter(mAdapter);

			if (tv_des != null && mCustomerList != null) {
				tv_des.setText("用户程序(" + mCustomerList.size() + ")");
			}
		};
	};
	private PopupWindow mPopupWindow;

	class MyAdapter extends BaseAdapter {

		@Override
		public int getViewTypeCount() {
			return super.getViewTypeCount() + 1;
		}

		@Override
		public int getItemViewType(int position) {
			if (position == 0 || position == mCustomerList.size() + 1) {
				return 0;
			} else {
				return 1;
			}
		}

		@Override
		public int getCount() {
			return mSystemList.size() + mCustomerList.size() + 2;
		}

		@Override
		public AppInfo getItem(int position) {
			if (position == 0 || position == mCustomerList.size() + 1) {
				return null;
			} else {

				if (position < mCustomerList.size() + 1) {
					return mCustomerList.get(position - 1);
				} else {
					return mSystemList.get(position - mCustomerList.size() - 2);
				}
			}
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			int type = getItemViewType(position);
			if (type == 0) {
				ViewTitleHolder holder = null;
				if (convertView == null) {
					convertView = View.inflate(getApplicationContext(), R.layout.listview_app_item_title, null);
					holder = new ViewTitleHolder();

					holder.tv_title = (TextView) convertView.findViewById(R.id.tv_titile);
					convertView.setTag(holder);
				} else {
					holder = (ViewTitleHolder) convertView.getTag();
				}
				if (position == 0) {
					holder.tv_title.setText("用户应用(" + mCustomerList.size() + ")");
				} else {
					holder.tv_title.setText("系统应用(" + mSystemList.size() + ")");
				}
				return convertView;
			} else {
				ViewHolder holder = null;
				if (convertView == null) {
					convertView = View.inflate(getApplicationContext(), R.layout.listview_process_item, null);
					holder = new ViewHolder();
					holder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);
					holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
					holder.tv_path = (TextView) convertView.findViewById(R.id.tv_path);
					convertView.setTag(holder);
				} else {
					holder = (ViewHolder) convertView.getTag();
				}
				holder.tv_name.setText(getItem(position).getName());
				holder.iv_icon.setBackgroundDrawable(getItem(position).getIcon());

				if (getItem(position).isSdCard) {
					holder.tv_path.setText("sd卡应用");
				} else {
					holder.tv_path.setText("手机应用");
				}
				return convertView;
			}

		}
	}

	static class ViewTitleHolder {
		TextView tv_title;
	}

	static class ViewHolder {
		ImageView iv_icon;
		TextView tv_name;
		TextView tv_path;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_manager);
		initTitle();
		initList();
	}

	private void initList() {
		lv_app_mananger = (ListView) findViewById(R.id.lv_app_mananger);
		tv_des = (TextView) findViewById(R.id.tv_des);

		lv_app_mananger.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				// 滚动过程中调用
				// firstVisibleItem 第一个可见条目
				// visibleItemCount 当前一个屏幕的可见条目
				// totalItemCount 总条目数

				if (mCustomerList != null && mSystemList != null) {
					if (firstVisibleItem >= mCustomerList.size() + 1) {
						tv_des.setText("系统应用(" + mSystemList.size() + ")");
					} else {
						tv_des.setText("用户应用(" + mCustomerList.size() + ")");
					}
				}
			}
		});

		lv_app_mananger.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (position == 0 || position == mCustomerList.size() + 1) {
					return;
				} else {
					if (position < mCustomerList.size() + 1) {
						mAppInfo = mCustomerList.get(position - 1);
					} else {
						mAppInfo = mSystemList.get(position - mCustomerList.size() - 2);
					}
					showPopupWindow(view);
				}
			}
		});

	}

	protected void showPopupWindow(View view) {
		View v = View.inflate(getApplicationContext(), R.layout.popupwindow_layout, null);
		TextView tv_uninstall = (TextView) v.findViewById(R.id.tv_uninstall);
		TextView tv_start = (TextView) v.findViewById(R.id.tv_start);
		TextView tv_share = (TextView) v.findViewById(R.id.tv_share);

		tv_share.setOnClickListener(this);
		tv_start.setOnClickListener(this);
		tv_uninstall.setOnClickListener(this);

		AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
		alphaAnimation.setDuration(200);
		alphaAnimation.setFillAfter(true);

		ScaleAnimation scaleAnimation = new ScaleAnimation(0, 1, 0, 1, Animation.RELATIVE_TO_SELF, 0.5f,
				Animation.RELATIVE_TO_SELF, 0.5f);
		scaleAnimation.setDuration(200);
		scaleAnimation.setFillAfter(true);

		AnimationSet animationSet = new AnimationSet(true);
		animationSet.addAnimation(scaleAnimation);
		animationSet.addAnimation(alphaAnimation);

		// 为控件设置动画
		v.setAnimation(animationSet);

		mPopupWindow = new PopupWindow(v, LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT, true);
		// 设置一个透明背景
		mPopupWindow.setBackgroundDrawable(new ColorDrawable());
		mPopupWindow.showAsDropDown(view, 70, -view.getHeight());

	}

	private void initTitle() {
		tv_user_space = (TextView) findViewById(R.id.tv_user_space);
		tv_sd_space = (TextView) findViewById(R.id.tv_sd_space);

		// 获取总大小
		// long totalSpace = Environment.getDataDirectory().getTotalSpace();

		// 获取可用大小
		long userSpace = Environment.getDataDirectory().getUsableSpace();
		long sdSpace = Environment.getExternalStorageDirectory().getUsableSpace();

		// 格式化大小
		String user = Formatter.formatFileSize(getApplicationContext(), userSpace);
		String sd = Formatter.formatFileSize(getApplicationContext(), sdSpace);
		tv_user_space.setText("磁盘可用:" + user);
		tv_sd_space.setText("sd卡可用:" + sd);
	}
	
	//TODO卸载刷新
	@Override
	protected void onResume() {
		super.onResume();
		getData();
	}

	public void getData() {
		new Thread() {
			public void run() {
				mAppInfoList = AppInfoProvider.getAppInfoList(getApplicationContext());
				mSystemList = new ArrayList<>();
				mCustomerList = new ArrayList<>();
				for (AppInfo appInfo : mAppInfoList) {
					if (appInfo.isSystem) {
						mSystemList.add(appInfo);
					} else {
						mCustomerList.add(appInfo);
					}
				}
				mHandler.sendEmptyMessage(0);
			};
		}.start();
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_uninstall:
			if (mAppInfo.isSystem) {
				ToastUtil.show(getApplicationContext(), "此应用不能卸载");
			}else {
				//跳转到系统卸载管理界面
				Intent intent = new Intent("android.intent.action.DELETE");
				intent.addCategory("android.intent.category.DEFAULT");
				intent.setData(Uri.parse("package:"+mAppInfo.getPackageName()));
				startActivity(intent);
			}
			break;
		case R.id.tv_start:
			PackageManager pm = getPackageManager();
			//通过Launch开启指定包名的intent,开启应用
			Intent intent = pm.getLaunchIntentForPackage(mAppInfo.getPackageName());
			//有可能是系统等非应用,不可开启,为避免空指针,进行容错处理
			if (intent!=null) {
				startActivity(intent);
			}else {
				ToastUtil.show(getApplicationContext(), "此应用不能被开启");
			}
			
			break;
			//分享第三方
		case R.id.tv_share:
			Intent sendIntent= new Intent(Intent.ACTION_SEND);
			sendIntent.putExtra(Intent.EXTRA_TEXT, "分享一个应用,应用名称为"+mAppInfo.getName());
			sendIntent.setType("text/plain");
			startActivity(sendIntent);
			break;
		}
		//点击窗体后 关闭
		if (mPopupWindow!=null) {
			mPopupWindow.dismiss();
		}
	}
}
