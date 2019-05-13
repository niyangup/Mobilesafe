package com.niyang.mobilesafe;

import java.util.ArrayList;
import java.util.List;

import com.niyang.mobilesafe.db.domain.ProcessInfo;
import com.niyang.mobilesafe.engine.ProcessInfoProvider;
import com.niyang.mobilesafe.util.SpUtil;
import com.niyang.mobilesafe.util.ToastUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.Formatter;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class ProcessManagerActivity extends Activity implements OnClickListener {
	private TextView tv_process_count;
	private TextView tv_memory_count;
	private ListView lv_process;
	private Button btn_choose_all, btn_choose_reverse, btn_clean, btn_setting;
	private int mProcessCount;
	private List<ProcessInfo> mProcessInfoList;
	private List<ProcessInfo> mSystemList;
	private List<ProcessInfo> mCustomerList;
	private MyAdapter mAdapter;
	private TextView tv_des;
	private long mAvailSpace=0;
	protected ProcessInfo mProcessInfo;
	private Handler mHandler = new Handler() {

		public void handleMessage(android.os.Message msg) {
			mAdapter = new MyAdapter();
			lv_process.setAdapter(mAdapter);
			if (tv_des != null && mCustomerList != null) {
				tv_des.setText("用户程序(" + mCustomerList.size() + ")");
			}
		};
	};
	private String mStrTotalSpace;
	

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
			boolean showSystem = SpUtil.getBoolean(getApplicationContext(), ConstantValue.SHOW_SYSTEM, false);
			if (showSystem) {
				return mSystemList.size() + mCustomerList.size() + 2;
			}else {
				return mCustomerList.size()+1;
			}
		}

		@Override
		public ProcessInfo getItem(int position) {
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
					holder.tv_title.setText("用户进程(" + mCustomerList.size() + ")");
				} else {
					holder.tv_title.setText("系统进程(" + mSystemList.size() + ")");
				}
				return convertView;
			} else {
				ViewHolder holder = null;
				if (convertView == null) {
					convertView = View.inflate(getApplicationContext(), R.layout.listview_process_item, null);
					holder = new ViewHolder();
					holder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);
					holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
					holder.tv_memory_info = (TextView) convertView.findViewById(R.id.tv_memory_info);
					holder.cb_box = (CheckBox) convertView.findViewById(R.id.cb_box);
					convertView.setTag(holder);
				} else {
					holder = (ViewHolder) convertView.getTag();
				}
				holder.tv_name.setText(getItem(position).getName());
				holder.iv_icon.setBackgroundDrawable(getItem(position).getIcon());
				String size = Formatter.formatFileSize(getApplicationContext(), getItem(position).getMemSize());
				holder.tv_memory_info.setText(size);

				if (getItem(position).packageName.equals(getPackageName())) {
					holder.cb_box.setVisibility(View.GONE);
				} else {
					holder.cb_box.setVisibility(View.VISIBLE);
				}

				holder.cb_box.setChecked(getItem(position).isCheck);

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
		TextView tv_memory_info;
		CheckBox cb_box;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_process_manager);
		initUI();
		initTitleData();
		intiListData();
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (mAdapter!=null) {
			mAdapter.notifyDataSetChanged();
		}
		
	}

	private void intiListData() {
		getData();
	}

	public void getData() {
		new Thread() {
			public void run() {
				mProcessInfoList = ProcessInfoProvider.getProcessInfo(getApplicationContext());

				mSystemList = new ArrayList<>();
				mCustomerList = new ArrayList<>();
				for (ProcessInfo info : mProcessInfoList) {
					if (info.isSystem) {
						mSystemList.add(info);
					} else {
						mCustomerList.add(info);
					}
				}
				mHandler.sendEmptyMessage(0);
			};
		}.start();
	}

	private void initTitleData() {
		mProcessCount = ProcessInfoProvider.getProcessCount(this);
		tv_process_count.setText("进程总数:" + mProcessCount);

		mAvailSpace = ProcessInfoProvider.getAvailSpace(this);
		String strAvailSpace = Formatter.formatFileSize(this, mAvailSpace);

		long totalSpace = ProcessInfoProvider.getTotalSpace(this);
		mStrTotalSpace = Formatter.formatFileSize(this, totalSpace);

		tv_memory_count.setText("剩余/总共:" + strAvailSpace + "/" + mStrTotalSpace);

	}

	private void initUI() {
		tv_process_count = (TextView) findViewById(R.id.tv_process_count);
		tv_memory_count = (TextView) findViewById(R.id.tv_memory_count);
		lv_process = (ListView) findViewById(R.id.lv_process);

		tv_des = (TextView) findViewById(R.id.tv_des);

		btn_choose_all = (Button) findViewById(R.id.btn_choose_all);
		btn_choose_reverse = (Button) findViewById(R.id.btn_choose_reverse);
		btn_clean = (Button) findViewById(R.id.btn_clean);
		btn_setting = (Button) findViewById(R.id.btn_setting);

		btn_choose_all.setOnClickListener(this);
		btn_choose_reverse.setOnClickListener(this);
		btn_clean.setOnClickListener(this);
		btn_setting.setOnClickListener(this);
		lv_process.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (position == 0 || position == mCustomerList.size() + 1) {
					return;
				} else {
					if (position < mCustomerList.size() + 1) {
						mProcessInfo = mCustomerList.get(position - 1);
					} else {
						mProcessInfo = mSystemList.get(position - mCustomerList.size() - 2);
					}

					if (mProcessInfo != null) {
						if (!mProcessInfo.packageName.equals(getPackageName())) {
							// 状态取反
							mProcessInfo.isCheck = !mProcessInfo.isCheck;
							CheckBox cb_box = (CheckBox) view.findViewById(R.id.cb_box);
							cb_box.setChecked(mProcessInfo.isCheck);
						}
					}
				}
			}
		});

		lv_process.setOnScrollListener(new OnScrollListener() {

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
						tv_des.setText("系统进程(" + mSystemList.size() + ")");
					} else {
						tv_des.setText("用户进程(" + mCustomerList.size() + ")");
					}
				}
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_choose_all:
			selectAll();
			break;

		case R.id.btn_choose_reverse:
			reverse();
			break;
		case R.id.btn_clean:
			cleanProcess();
			break;
		case R.id.btn_setting:
			setting();
			break;
		}
	}

	private void setting() {
		startActivityForResult(new Intent(ProcessManagerActivity.this, ProcessSettingActivity.class),0);
	}

	/**
	 * 清理选中进程
	 */
	private void cleanProcess() {
		List<ProcessInfo> killProcessList = new ArrayList<>();
		for (ProcessInfo processInfo : mCustomerList) {
			if (processInfo.getPackageName().equals(getPackageName())) {
				continue;
			}
			if (processInfo.isCheck) {
				// 被选中 记录需要杀死的用户进程
				killProcessList.add(processInfo);
			}
		}

		for (ProcessInfo processInfo : mSystemList) {
			if (processInfo.isCheck) {
				// 被选中 记录需要杀死的用户进程
				killProcessList.add(processInfo);
			}
		}

		// 循环遍历killProcessList,然后移除mSystemList和mCustomerList中的对象
		long totalReleseSpace = 0;
		for (ProcessInfo processInfo : killProcessList) {
			if (mCustomerList.contains(processInfo)) {
				mCustomerList.remove(processInfo);
			}
			if (mSystemList.contains(processInfo)) {
				mSystemList.remove(processInfo);
			}
			// kill killProcessList中的进程
			ProcessInfoProvider.killProcess(getApplicationContext(), processInfo);
			// 继续释放空间的总大小
			totalReleseSpace += processInfo.memSize;
		}
		// 在集合改变后需要刷新listview
		if (mAdapter != null) {
			mAdapter.notifyDataSetChanged();
		}
		// 更新进程总数
		mProcessCount -= killProcessList.size();
		// 更新内存的可用剩余空间
		mAvailSpace+=totalReleseSpace;
		
		
		//更新进程总数和剩余空间的大小
		tv_process_count.setText("进程总数:"+mProcessCount);
		String size = Formatter.formatFileSize(getApplicationContext(), mAvailSpace);
		tv_memory_count.setText("剩余/总共"+size+"/"+mStrTotalSpace);
		//通过Toast告诉用户,释放了多少空间,kill几个进程
		String format = String.format("kill了%d个进程,释放了%s的空间", killProcessList.size(),size);
		ToastUtil.show(getApplicationContext(), format);
	}

	private void reverse() {
		// 将所有集合中的对象上isCheck字段取反
		for (ProcessInfo processInfo : mCustomerList) {
			if (processInfo.getPackageName().equals(getPackageName())) {
				continue;
			}
			processInfo.isCheck = !processInfo.isCheck;
		}

		for (ProcessInfo processInfo : mSystemList) {

			processInfo.isCheck = !processInfo.isCheck;
		}

		// 2.通知数据适配器刷新
		if (mAdapter != null) {
			mAdapter.notifyDataSetChanged();
		}

	}

	private void selectAll() {
		// 1.将所有的集合中的对象上的isCheck字段设置为true,代表全选,排除当前应用
		for (ProcessInfo processInfo : mCustomerList) {
			if (processInfo.getPackageName().equals(getPackageName())) {
				continue;
			}
			processInfo.isCheck = true;
		}

		for (ProcessInfo processInfo : mSystemList) {

			processInfo.isCheck = true;
		}

		// 2.通知数据适配器刷新
		if (mAdapter != null) {
			mAdapter.notifyDataSetChanged();
		}
	}
}
