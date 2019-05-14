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
		
		elv_common_number.setOnChildClickListener(new OnChildClickListener() {
			
			@Override
			public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
				//打电话
				startCall(mAdapter.getChild(groupPosition, childPosition).number);
				return false;
			}
		});
	}

	protected void startCall(String number) {
		Intent intent = new Intent(Intent.ACTION_CALL);
		intent.setData(Uri.parse("tel:"+number));
		startActivity(intent);
	}

	private void initUI() {
		elv_common_number = (ExpandableListView) findViewById(R.id.elv_common_number);
		
	}
	
	class Myadapter extends BaseExpandableListAdapter{

		@Override
		public int getGroupCount() {
			return mGroup.size();
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			return mGroup.get(groupPosition).childList.size();
		}

		@Override
		public Group getGroup(int groupPosition) {
			
			return mGroup.get(groupPosition);
		}

		@Override
		public Child getChild(int groupPosition, int childPosition) {
			return mGroup.get(groupPosition).childList.get(childPosition);
		}

		@Override
		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		@Override
		public boolean hasStableIds() {
			return false;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
			TextView textView = new TextView(getApplicationContext());
			textView.setText("      "+mGroup.get(groupPosition).name);
			
			textView.setTextColor(Color.RED);
			textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
			return textView;
		}

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

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}
		
	}
}
