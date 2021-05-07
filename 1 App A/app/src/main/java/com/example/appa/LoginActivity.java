package com.example.appa;

import java.io.File;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity {
	
	EditText etPassword, etCPassword;
	String curDirPath = "", text = "";
	File folder;
	SharedPreference sp;
	Button btnLogin;
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		if(!isNetworkAvailable(this)){
			toast("Please connect internet and try again");
			finish();
		}
	}
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        if(sp.getString(getApplicationContext(), "password").equals(""))
        	signup();
        else
        	login();

    }
    
    public void login(){
        setContentView(R.layout.login);

        etPassword = (EditText) findViewById(R.id.etPassword);    
        btnLogin = (Button) findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(etPassword.getText().toString().equals(sp.getString(getApplicationContext(), "password"))){
					toast("Login successfully");		
					finish();
					Intent i = new Intent(LoginActivity.this, HomeActivity.class);
					startActivity(i);
				}else{
					toast("Invalid password");
				}
			}
		});
    }
        
    public void signup(){
        setContentView(R.layout.signup);

        etPassword = (EditText) findViewById(R.id.etPassword);
        etCPassword = (EditText) findViewById(R.id.etCPassword);
        btnLogin = (Button) findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(etPassword.getText().toString().length()<4 && etCPassword.getText().toString().length()<4){
					toast("Password minimun length is 4");
				}else if(!etPassword.getText().toString().equals(etCPassword.getText().toString().trim())){
					toast("Miss match password");
				}else{
					toast("Signup successfully");
					sp.putString(getApplicationContext(), "password", etPassword.getText().toString());
					login();					
				}
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

	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager connec = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connec.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}
}
