package com.niyang.mobilesafe;

import java.util.List;

import com.niyang.mobilesafe.engine.CommonnumDao;
import com.niyang.mobilesafe.engine.CommonnumDao.Child;
import com.niyang.mobilesafe.engine.CommonnumDao.Group;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.TextView;

public class CommonNumberActivity extends Activity {
	private ExpandableListView elv_common_number;
	private List<Group> mGroup;
	private Myadapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_number_query);
		initUI();
		initData();
	}

	private void initData() {
		CommonnumDao commonnumDao = new CommonnumDao();
		mGroup = commonnumDao.getGroup();
		mAdapter = new Myadapter();
		elv_common_number.setAdapter(mAdapter);

		// 子选项的点击监听
		elv_common_number.setOnChildClickListener(new OnChildClickListener() {

			@Override
			public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition,
					long id) {
				// 点击子选项后拨出电话
				startCall(mAdapter.getChild(groupPosition, childPosition).number);
				return false;
			}
		});
	}

	protected void startCall(String number) {
		Intent intent = new Intent(Intent.ACTION_CALL);
		intent.setData(Uri.parse("tel:" + number));
		startActivity(intent);
	}

	private void initUI() {
		elv_common_number = (ExpandableListView) findViewById(R.id.elv_common_number);

	}

	class Myadapter extends BaseExpandableListAdapter {

		// 获取分组的个数
		@Override
		public int getGroupCount() {
			return mGroup.size();
		}

		// 获取该分组下子选项的个数
		@Override
		public int getChildrenCount(int groupPosition) {
			return mGroup.get(groupPosition).childList.size();
		}

		// 获取指定索引下的Group实例
		@Override
		public Group getGroup(int groupPosition) {

			return mGroup.get(groupPosition);
		}

		// 获取指定索引下的Child实例
		@Override
		public Child getChild(int groupPosition, int childPosition) {
			return mGroup.get(groupPosition).childList.get(childPosition);
		}

		// 获取指定分组的id
		@Override
		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		// 获取指定索引下的子选项id
		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		// 分组和子选项是否持有稳定的ID, 就是说底层数据的改变会不会影响到它们
		@Override
		public boolean hasStableIds() {
			return false;
		}

		/**
		 *
		 * 获取显示指定组的视图对象
		 *
		 * @param groupPosition
		 *            组位置
		 * @param isExpanded
		 *            该组是展开状态还是伸缩状态
		 * @param convertView
		 *            重用已有的视图对象
		 * @param parent
		 *            返回的视图对象始终依附于的视图组
		 */
		// 获取显示指定分组的视图
		@Override
		public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
			TextView textView = new TextView(getApplicationContext());
			textView.setText("      " + mGroup.get(groupPosition).name);

			textView.setTextColor(Color.RED);
			textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
			return textView;
		}

		/**
		 *
		 * 获取一个视图对象，显示指定组中的指定子元素数据。
		 *
		 * @param groupPosition
		 *            组位置
		 * @param childPosition
		 *            子元素位置
		 * @param isLastChild
		 *            子元素是否处于组中的最后一个
		 * @param convertView
		 *            重用已有的视图(View)对象
		 * @param parent
		 *            返回的视图(View)对象始终依附于的视图组
		 * @return
		 * @see android.widget.ExpandableListAdapter#getChildView(int, int, boolean,
		 *      android.view.View, android.view.ViewGroup)
		 */

		// 取得显示给定分组给定子位置的数据用的视图
		@Override
		public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView,
				ViewGroup parent) {
			View view = View.inflate(getApplicationContext(), R.layout.elv_child_item, null);
			TextView tv_name = (TextView) view.findViewById(R.id.tv_name);
			TextView tv_number = (TextView) view.findViewById(R.id.tv_number);
			String name = getChild(groupPosition, childPosition).name;
			tv_name.setText(name);
			String number = getChild(groupPosition, childPosition).number;
			tv_number.setText(number);

			return view;
		}

		// 指定位置上的子元素是否可选中 若为false,即使设置了onClickListener也无效
		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}

	}
}
