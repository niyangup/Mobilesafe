package com.niyang.mobilesafe.engine;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class CommonnumDao {
	// 1.指定访问数据库的路径
	public static String path = "data/data/com.niyang.mobilesafe/files/commonnum.db";
	public List<Group> getGroup() {
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
		List<Group> groupList=new ArrayList<>();
		Cursor cursor = db.query("classlist", new String[] {"name","idx"}, null, null, null, null, null);
		while (cursor.moveToNext()) {
			Group group = new Group();
			
			String name = cursor.getString(0);
			String index=cursor.getString(1);
			
			group.name=name;
			group.idx=index;
			group.childList=getChild(group.idx);
			
			groupList.add(group);
		}
		cursor.close();
		db.close();
		return groupList;
	}
	//获取每一个组中孩子的节点
	public List<Child> getChild(String index) {
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
		List<Child> childList=new ArrayList<>();
//		Cursor cursor = db.query("classlist", new String[] {"name","idx"}, null, null, null, null, null);
		Cursor cursor = db.rawQuery("select * from table"+index+";", null);
		while (cursor.moveToNext()) {
			Child child = new Child();
			
			String id = cursor.getString(0);
			String number=cursor.getString(1);
			String name = cursor.getString(2);
			
			child._id=id;
			child.name=name;
			child.number=number;
			childList.add(child);
		}
		cursor.close();
		db.close();
		return  childList;
		
	}
	
	public class Group{
		public String name;
		public String idx;
		public List<Child> childList;
	}
	public class Child{
		public String name;
		public String _id;
		public String number;
	}
}
