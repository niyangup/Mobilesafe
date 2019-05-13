package com.niyang.mobilesafe.engine;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import org.xmlpull.v1.XmlSerializer;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.SystemClock;
import android.util.Xml;

public class SmsBackUp {
	private static int index = 0;

	public static void backup(Context context, String path, callBack back) {
		try {
			File file = new File(path);
			Cursor cursor = context.getContentResolver().query(Uri.parse("content://sms/"),
					new String[] { "address", "date", "type", "body" }, null, null, null);
			FileOutputStream fos = new FileOutputStream(file);

			XmlSerializer newSerializer = Xml.newSerializer();
			newSerializer.setOutput(fos, "UTF-8");
			newSerializer.startDocument("UTF-8", true);
			newSerializer.startTag(null, "smss");

			if (back != null) {
				back.setMax(cursor.getCount());
			}

			while (cursor.moveToNext()) {
				// 数据源
				String address = cursor.getString(0);
				String date = cursor.getString(1);
				String type = cursor.getString(2);
				String body = cursor.getString(3);

				// <sms>
				newSerializer.startTag(null, "sms");

				// <address>
				newSerializer.startTag(null, "address");
				newSerializer.text(address);
				newSerializer.endTag(null, "address");

				// date
				newSerializer.startTag(null, "date");
				newSerializer.text(date);
				newSerializer.endTag(null, "date");

				// type
				newSerializer.startTag(null, "type");
				newSerializer.text(type);
				newSerializer.endTag(null, "type");

				// body
				newSerializer.startTag(null, "body");
				newSerializer.text(body);
				newSerializer.endTag(null, "body");

				newSerializer.endTag(null, "sms");
				index++;
				Thread.sleep(500);
				if (back!=null) {
					back.setProgress(index);
				}
			}
			newSerializer.endTag(null, "smss");
			newSerializer.endDocument();
			fos.close();
			cursor.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	// 回调
	// 1.定义一个接口
	// 2.定义接口中未实现的业务逻辑方法
	// 3.传递一个实现了此接口的类的对象,接口的实现类,一定实现了上述的实现方法
	// 4.获取传递进来的对象,在合适的地方做方法的调用

	public interface callBack {
		public void setMax(int max);

		public void setProgress(int index);
	}

}
