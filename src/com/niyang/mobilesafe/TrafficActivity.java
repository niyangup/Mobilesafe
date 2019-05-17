package com.niyang.mobilesafe;

import android.app.Activity;
import android.net.TrafficStats;
import android.os.Bundle;

public class TrafficActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_traffic);
		
		long mobileRxBytes = TrafficStats.getMobileRxBytes();
		long mobileTxBytes = TrafficStats.getMobileTxBytes();
		
		long totalRxBytes = TrafficStats.getTotalRxBytes();
		long totalTxBytes = TrafficStats.getTotalTxBytes();
		
	}
}
