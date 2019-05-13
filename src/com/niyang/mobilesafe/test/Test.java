package com.niyang.mobilesafe.test;

import com.niyang.mobilesafe.db.dao.BlackNumberDao;

import android.test.AndroidTestCase;

public class Test extends AndroidTestCase {
	
	public void insert() {
		BlackNumberDao dao = BlackNumberDao.getInstance(getContext());
		dao.insert("110", "1");
	}
}
