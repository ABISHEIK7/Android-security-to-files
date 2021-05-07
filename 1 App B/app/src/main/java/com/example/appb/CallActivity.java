package com.example.appb;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;

public class CallActivity extends Activity{
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		try {
			finish();
			Intent i = this.getPackageManager().getLaunchIntentForPackage("com.example.appa");
			this.startActivity(i);
		} catch (Exception e) {
		    // TODO Auto-generated catch block
		}
	}

}
