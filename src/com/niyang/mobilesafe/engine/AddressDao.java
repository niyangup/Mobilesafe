package com.niyang.mobilesafe.engine;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class AddressDao {
	// 1.指定访问数据库的路径
	public static String path = "data/data/com.niyang.mobilesafe/files/address.db";
	private static String mAddress = "未知号码";

	// 2.开启数据库,进行访问
	public static String getAddress(String phone) {
		String regularExpression = "^1[3-8]\\d{9}";
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.NO_LOCALIZED_COLLATORS);
		if (phone.matches(regularExpression)) {
			phone = phone.substring(0, 7);
			// SQLiteDatabase openDatabase = SQLiteDatabase.openDatabase(path, null,
			// SQLiteDatabase.OPEN_READONLY);

			// 3.数据库查询
			Cursor cursor = db.query("phones", new String[] { "region_id", "type" }, "number=?", new String[] { phone },
					null, null, null);
			// 4.查到即可
			if (cursor.moveToNext()) {
				String region_id = cursor.getString(0);
				String type = cursor.getString(1);// 类型,表示电信移动或联通 以 1,2,3的形式表现
				String type2 = getType(type);// 将1 2 3 转换为移动 联通 电信的形式
				Log.v("AddressDao", "region_id =" + region_id + ", type=" + type);
				// 5.通过通过phones查到的region_id,作为外键查询region表
				Cursor query = db.query("regions", new String[] { "province", "city" }, "id=?",
						new String[] { region_id }, null, null, null);
				if (query.moveToNext()) {
					String province = query.getString(0);
					String city = query.getString(1);
					// 最终省份加上城市的地址
					mAddress = province + city + type2;
					Log.v("AddressDao", "address =" + mAddress);
				} else {
					mAddress = "未知号码";
				}
			}
		} else {
			int length = phone.length();
			switch (length) {
			case 3:
				mAddress = "报警电话";
				break;

			case 4:
				mAddress = "模拟器";
				break;

			case 5:
				mAddress = "服务电话";
				break;

			case 7:
			case 8:
				mAddress = "本地电话";
				break;

			case 11:

				Cursor query = db.query("regions", new String[] { "province", "city" }, "area_code=?",
						new String[] { phone.substring(0, 3) }, null, null, null);
				if (query.moveToNext()) {
					String province = query.getString(0);
					String city = query.getString(1);
					mAddress = province + city;
				} else {
					mAddress = "未知号码";
				}
				break;

			case 12:
				Cursor cursor = db.query("regions", new String[] { "province", "city" }, "area_code=?",
						new String[] { phone.substring(1, 4) }, null, null, null);
				if (cursor.moveToNext()) {
					String province = cursor.getString(0);
					String city = cursor.getString(1);
					mAddress = province + city;
				} else {
					mAddress = "未知号码";
				}
				break;

			default:
				mAddress = "未知号码";
				break;
			}
		}
		return mAddress;

	}

	private static String getType(String type) {
		String type2 = null;
		switch (type) {
		case "1":
			type2 = "移动";
			break;
		case "2":
			type2 = "联通";
			break;
		case "3":
			type2 = "电信";
			break;
		}
		return type2;
	}

}
