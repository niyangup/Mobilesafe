package com.niyang.mobilesafe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class ContactListActivity extends Activity {
	private ListView lv_contact;
	private List<HashMap<String, String>> contactList = new ArrayList<HashMap<String, String>>();
	private Handler mHandler=new Handler() {
		public void handleMessage(android.os.Message msg) {
			String[] from={"phone","name"};
			int[] to={R.id.contact_phone,R.id.contact_name};
			SimpleAdapter adapter=new SimpleAdapter(ContactListActivity.this, contactList, 
					R.layout.contactlist_item, from, to);
			lv_contact.setAdapter(adapter);
		}; 
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contactlist);
		initUI();
		initData();
	}

	

	private void initData() {
		//由于可能数据较多 造成耗时,因此放入子线程运行
		new Thread() {
			@Override
			public void run() {
				// 获取内容解析者对象
				ContentResolver contentResolver = getContentResolver();
				// 做查询系统联系人数据库表过程(读取联系人权限)
				Cursor cursor = contentResolver.query(Uri.parse("content://com.android.contacts/raw_contacts"),
						new String[] { "contact_id" }, null, null, null);
				//使用前先清空
				contactList.clear();
				while (cursor.moveToNext()) {
					String id = cursor.getString(0);
					// Log.v("TAG", "id="+id);
					// 根据用户唯一性id值,查询data表和mimetype表生成的视图,获取data以及mimetype字段
					Cursor indexCuror = contentResolver.query(Uri.parse("content://com.android.contacts/data"),
							new String[] { "data1", "mimetype" }, "raw_contact_id = ?", new String[] { id }, null);
					// 循环获取每一个联系人的电话号码以及姓名,数据类型
					HashMap<String, String> hashMap = new HashMap<String, String>();
					while (indexCuror.moveToNext()) {
						String data = indexCuror.getString(0);
						String type = indexCuror.getString(1);

						if (type.equals("vnd.android.cursor.item/phone_v2")) {
							if (!TextUtils.isEmpty(data)) {
								hashMap.put("phone", data);
							}
						} else if (type.equals("vnd.android.cursor.item/name")) {
							if (!TextUtils.isEmpty(data)) {
								hashMap.put("name", data);
							}
						}
					}
					indexCuror.close();
					contactList.add(hashMap);
				}
				cursor.close();
				mHandler.sendEmptyMessage(0);
			}
		}.start();
	}

	private void initUI() {
		lv_contact = (ListView) findViewById(R.id.lv_contact);
		lv_contact.setOnItemClickListener(new OnItemClickListener() {
			
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				HashMap<String,String> hashMap = contactList.get(position);
				String phone = hashMap.get("phone");
				Intent intent=new Intent();
				intent.putExtra("phone", phone);
				setResult(20, intent);
				finish();
			}
		});
	}
}
