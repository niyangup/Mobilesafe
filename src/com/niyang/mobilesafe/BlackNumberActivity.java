package com.niyang.mobilesafe;

import java.util.List;
import java.util.Random;

import com.niyang.mobilesafe.db.dao.BlackNumberDao;
import com.niyang.mobilesafe.db.domain.BlackNumberInfo;
import com.niyang.mobilesafe.util.ToastUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

//1.复用convertView
//2.对findViewById次数的优化,使用ViewHolder
//3.将ViewHolder定义成静态,不会去创建多个对象
//4.listView如果有多个条目,可以去分页算法

public class BlackNumberActivity extends Activity {

	private Button btn_add;
	private ListView lv_blacknumber;
	private List<BlackNumberInfo> balckNumList;
	private String mode = "0";
	private BlackNumberDao dao;
	private int mCount;
	private MyAdapter mAdapter;
	private boolean mIsLoad = false;
	private Handler mHandler = new Handler() {

		public void handleMessage(android.os.Message msg) {
			if (mAdapter == null) {
				mAdapter = new MyAdapter();
				lv_blacknumber.setAdapter(mAdapter);
			} else {
				mAdapter.notifyDataSetChanged();
			}

		};
	};

	class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return balckNumList.size();
		}

		@Override
		public Object getItem(int position) {
			return balckNumList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder = null;
			if (convertView == null) {
				convertView = View.inflate(getApplicationContext(), R.layout.listview_blacknumber_item, null);

				viewHolder = new ViewHolder();
				viewHolder.tv_phone = (TextView) convertView.findViewById(R.id.tv_phone);
				viewHolder.tv_mode = (TextView) convertView.findViewById(R.id.tv_mode);
				viewHolder.iv_delete = (ImageView) convertView.findViewById(R.id.iv_delete);

				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}

			viewHolder.iv_delete.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// 1.删除数据库中的条目
					dao.delete(balckNumList.get(position).phone);
					// 2.删除数据源(集合)中的相应条目
					balckNumList.remove(position);
					// 3.通知数据适配器刷新
					if (mAdapter != null) {
						mAdapter.notifyDataSetChanged();
					}
				}
			});

			viewHolder.tv_phone.setText(balckNumList.get(position).phone);
			switch (balckNumList.get(position).mode) {
			case "1":
				viewHolder.tv_mode.setText("拦截短信");
				break;
			case "2":
				viewHolder.tv_mode.setText("拦截电话");
				break;
			case "3":
				viewHolder.tv_mode.setText("拦截所有");
				break;
			}
			return convertView;
		}
	}

	static class ViewHolder {
		TextView tv_phone;
		TextView tv_mode;
		ImageView iv_delete;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_blacknumber);
		initUI();
		initData();
	}

	private void initData() {
		new Thread() {

			public void run() {
				dao = BlackNumberDao.getInstance(getApplicationContext());
				// dao.insert("110", "1");
				// dao.insert("120", "2");

				// balckNumList = dao.findAll();
				balckNumList = dao.find(0);
				mCount = dao.getCount();
				mHandler.sendEmptyMessage(0);
			};
		}.start();
	}

	private void initUI() {
		btn_add = (Button) findViewById(R.id.btn_add);
		lv_blacknumber = (ListView) findViewById(R.id.lv_blacknumber);

		lv_blacknumber.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// listView处于空闲状态并且Item到达最后一条时 更新ListView
				if (scrollState == OnScrollListener.SCROLL_STATE_IDLE
						&& lv_blacknumber.getLastVisiblePosition() >= balckNumList.size() - 1 && !mIsLoad) {
					// 数据库的总条目数>集合的条目
					if (mCount > balckNumList.size()) {
						new Thread() {
							public void run() {
								dao = BlackNumberDao.getInstance(getApplicationContext());
								List<BlackNumberInfo> moreData = dao.find(balckNumList.size());
								balckNumList.addAll(moreData);
								mHandler.sendEmptyMessage(0);
							};
						}.start();
					}
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

			}
		});

		btn_add.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showDialog();
			}

			private void showDialog() {
				Builder builder = new AlertDialog.Builder(BlackNumberActivity.this);
				final AlertDialog dialog = builder.create();
				View inflate = View.inflate(getApplicationContext(), R.layout.dialog_add_blacknumber, null);
				dialog.setView(inflate, 0, 0, 0, 0);
				dialog.show();

				final EditText et_phone = (EditText) inflate.findViewById(R.id.et_phone);
				Button btn_ok = (Button) inflate.findViewById(R.id.btn_ok);
				Button btn_no = (Button) inflate.findViewById(R.id.btn_no);
				RadioGroup rg_group = (RadioGroup) inflate.findViewById(R.id.rg_group);

				rg_group.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(RadioGroup group, int checkedId) {
						switch (checkedId) {
						case R.id.rb_sms:
							mode = "1";
							break;

						case R.id.rb_phone:
							mode = "2";
							break;

						case R.id.rb_all:
							mode = "3";
							break;
						}
					}
				});

				btn_ok.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						String phone = et_phone.getText().toString().trim();
						if (!TextUtils.isEmpty(phone) && !(mode.equals("0"))) {
							dao.insert(phone, mode);
							BlackNumberInfo info = new BlackNumberInfo();
							info.phone = phone;
							info.mode = mode;
							balckNumList.add(0, info);
							if (mAdapter != null) {
								mAdapter.notifyDataSetChanged();
							}
							dialog.dismiss();
							mode = "0";
						} else {
							ToastUtil.show(getApplicationContext(), "请输入拦截号码或类型");
						}
					}
				});

				btn_no.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});
			}
		});
	}
}
