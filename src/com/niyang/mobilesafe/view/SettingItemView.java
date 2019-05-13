package com.niyang.mobilesafe.view;

import com.niyang.mobilesafe.R;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * @author niyang
 *
 */
public class SettingItemView extends RelativeLayout {

	private static final String NAMESPACE = "http://schemas.android.com/apk/res/com.niyang.mobilesafe";
	private CheckBox cb_box;
	private TextView tv_des;
	private String mDestitle;
	private String mDesoff;
	private String mDeson;

	public SettingItemView(Context context) {
		this(context, null);
	}

	public SettingItemView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public SettingItemView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		// xml -> view 将界面的一个item转换成view对象,直接添加到当前SettingItemView对应的view中
		View.inflate(context, R.layout.setting_item_view, this);

		TextView tv_title = (TextView) findViewById(R.id.tv_title);
		tv_des = (TextView) findViewById(R.id.tv_des);
		cb_box = (CheckBox) findViewById(R.id.cb_box);
		cb_box.setClickable(false);

		// 获取自定义以及原声属性的操作,写在此处,AttributeSet arrrs对象中获取
		initAttrs(attrs);
		tv_title.setText(mDestitle);
	}

	/**
	 * 返回属性集合中自定义属性属性值
	 * 
	 * @param attrs
	 *            构造方法中维护好的属性集合
	 */
	private void initAttrs(AttributeSet attrs) {
		mDestitle = attrs.getAttributeValue(NAMESPACE, "destitle");
		mDesoff = attrs.getAttributeValue(NAMESPACE, "desoff");
		mDeson = attrs.getAttributeValue(NAMESPACE, "deson");

		Log.v("TAG", mDestitle);
		Log.v("TAG", mDesoff);
		Log.v("TAG", mDeson);
	}

	/**
	 * @return 返回当前SettingItemView是否被选中, true 开启 false 关闭
	 */
	public boolean isCheck() {
		return cb_box.isChecked();
	}

	/**
	 * @param isCheck
	 *            是否作为开启的变量,由点击过程中做传递
	 */
	public void setCheck(boolean isCheck) {
		cb_box.setChecked(isCheck);
		if (isCheck) {
			tv_des.setText(mDeson);
		} else {
			tv_des.setText(mDesoff);
		}
	}

}
