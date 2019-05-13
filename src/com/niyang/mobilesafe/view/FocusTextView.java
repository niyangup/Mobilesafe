package com.niyang.mobilesafe.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class FocusTextView extends TextView {

	public FocusTextView(Context context) {
		this(context, null);
	}

	public FocusTextView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public FocusTextView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	// 重写获取焦点的方法
	@Override
	public boolean isFocused() {
		return true;
	}

}
