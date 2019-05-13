package com.niyang.mobilesafe.db.dao;

import java.util.ArrayList;
import java.util.List;

import com.niyang.mobilesafe.db.BlackNumberOpenHelper;
import com.niyang.mobilesafe.db.domain.BlackNumberInfo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.LiveFolders;
import android.widget.AutoCompleteTextView.Validator;

public class BlackNumberDao {

	private BlackNumberOpenHelper blackNumberOpenHelper;

	// 单例模式
	private BlackNumberDao(Context context) {
		blackNumberOpenHelper = new BlackNumberOpenHelper(context);
	}

	private static BlackNumberDao blackNumberDao = null;

	public static BlackNumberDao getInstance(Context context) {
		if (blackNumberDao == null) {
			blackNumberDao = new BlackNumberDao(context);
		}
		return blackNumberDao;
	}

	/**
	 * 添加数据到数据库
	 * 
	 * @param phone
	 *            添加到数据库的手机号
	 * @param mode
	 *            被拦截的类型(1:短信 2:电话 3:所有)
	 */
	public void insert(String phone, String mode) {
		SQLiteDatabase db = blackNumberOpenHelper.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put("phone", phone);
		values.put("mode", mode);

		db.insert("blacknumber", null, values);

		db.close();
	}

	/**
	 * 根据phone删除数据
	 * 
	 * @param phone
	 *            被删除的手机号
	 */
	public void delete(String phone) {
		SQLiteDatabase db = blackNumberOpenHelper.getWritableDatabase();

		db.delete("blacknumber", "phone=?", new String[] { phone });

		db.close();
	}

	/**
	 * 根据电话号码更新拦截模式
	 * 
	 * @param phone
	 *            更新拦截模式的号码
	 * @param mode
	 *            要更新为的模式(1:短信 2:电话 3:所有)
	 */
	public void update(String phone, String mode) {
		SQLiteDatabase db = blackNumberOpenHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("mode", mode);

		db.update("blacknumber", values, "phone=?", new String[] { phone });

		db.close();
	}

	/**
	 * @return 查询到数据库中所有的号码以及拦截类型所在的集合
	 */
	public List<BlackNumberInfo> findAll() {
		SQLiteDatabase db = blackNumberOpenHelper.getWritableDatabase();
		Cursor cursor = db.query("blacknumber", new String[] { "phone", "mode" }, null, null, null, null, "_id desc");
		List<BlackNumberInfo> blackNumberInfoList = new ArrayList<>();
		while (cursor.moveToNext()) {
			BlackNumberInfo blackNumberInfo = new BlackNumberInfo();
			blackNumberInfo.phone = cursor.getString(0);
			blackNumberInfo.mode = cursor.getString(1);
			blackNumberInfoList.add(blackNumberInfo);
		}
		cursor.close();
		db.close();

		return blackNumberInfoList;
	}

	public List<BlackNumberInfo> find(int index) {
		SQLiteDatabase db = blackNumberOpenHelper.getWritableDatabase();
		Cursor cursor = db.rawQuery("select phone,mode from blacknumber order by _id desc limit ?,20",
				new String[] { "" + index });
		List<BlackNumberInfo> blackNumberInfoList = new ArrayList<>();
		while (cursor.moveToNext()) {
			BlackNumberInfo blackNumberInfo = new BlackNumberInfo();
			blackNumberInfo.phone = cursor.getString(0);
			blackNumberInfo.mode = cursor.getString(1);
			blackNumberInfoList.add(blackNumberInfo);
		}
		cursor.close();
		db.close();

		return blackNumberInfoList;
	}

	/**
	 * @return 获取数据库中的总条目数,返回0代表没有数据或者异常
	 */
	public int getCount() {
		SQLiteDatabase db = blackNumberOpenHelper.getWritableDatabase();
		Cursor cursor = db.rawQuery("select count(*) from blacknumber", null);
		int count = 0;
		while (cursor.moveToNext()) {
			count = cursor.getInt(0);
		}
		cursor.close();
		db.close();

		return count;
	}

	public String getMode(String phone) {
		SQLiteDatabase db = blackNumberOpenHelper.getWritableDatabase();
		Cursor cursor = db.query("blacknumber", new String[] { "mode" }, "phone=?", new String[] { phone }, null, null,
				null);

		String mode = null;
		if (cursor.moveToNext()) {
			mode = cursor.getString(0);
		}
		cursor.close();
		db.close();

		return mode;
	}
}
