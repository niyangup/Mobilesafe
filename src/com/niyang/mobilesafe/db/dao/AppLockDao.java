package com.niyang.mobilesafe.db.dao;

import java.util.ArrayList;
import java.util.List;

import com.niyang.mobilesafe.db.AppLockOpenHelper;
import com.niyang.mobilesafe.db.BlackNumberOpenHelper;
import com.niyang.mobilesafe.db.domain.BlackNumberInfo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.LiveFolders;
import android.widget.AutoCompleteTextView.Validator;

public class AppLockDao {

	private AppLockOpenHelper appLockOpenHelper;

	// 单例模式
	private AppLockDao(Context context) {
		appLockOpenHelper = new AppLockOpenHelper(context);
	}

	private static AppLockDao appLockDao = null;

	public static AppLockDao getInstance(Context context) {
		if (appLockDao == null) {
			appLockDao = new AppLockDao(context);
		}
		return appLockDao;
	}
	
	public void insert(String packagename) {
		SQLiteDatabase db = appLockOpenHelper.getWritableDatabase();
		ContentValues values=new ContentValues();
		values.put("packagename", packagename);
		
		db.insert("applock", null, values);
		
		db.close();
	}
	
	public void delete(String packagename) {
		SQLiteDatabase db = appLockOpenHelper.getWritableDatabase();
		ContentValues values=new ContentValues();
		values.put("packagename", packagename);
		db.delete("applock", "packagename=?", new String[] {packagename});
		
		db.close();
	}
	
	public List<String> findAll() {
		SQLiteDatabase db = appLockOpenHelper.getWritableDatabase();
		List<String> lockPackageList=new ArrayList<>();
		Cursor cursor = db.query("applock", new String[] {"packagename"}, null, null, null, null, null);
		while (cursor.moveToNext()) {
			lockPackageList.add(cursor.getString(0));
		}
		cursor.close();
		db.close();
		
		return lockPackageList;
	}
}
