package com.example.appa;

import java.io.File;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class HomeActivity extends Activity {
	
	private List<String> item = null;
	private List<String> path = null;
	private String root;
	private TextView myPath;
	String curDirPath = "", text = "";
	File folder;
	SharedPreference sp;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

		Button btnAdd = (Button) findViewById(R.id.button1);
		Button btnRemove = (Button) findViewById(R.id.button2);
		Button button3 = (Button) findViewById(R.id.button3);

		btnAdd.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(HomeActivity.this, AddPermission.class);
				startActivity(i);
			}
		});

		btnRemove.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(HomeActivity.this, RemovePermission.class);
				startActivity(i);
			}
		});

		button3.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(HomeActivity.this, GivePermission.class);
				startActivity(i);
			}
		});
    }
	
	Toast toast;
	public void toast(String str) {
		try {
			toast.cancel();
		} catch (Exception e) {
			// TODO: handle exception
		}
		toast = Toast.makeText(this, str, Toast.LENGTH_LONG);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}

}
