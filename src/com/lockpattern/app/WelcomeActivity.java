package com.lockpattern.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.Window;



public class WelcomeActivity extends FragmentActivity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		//延迟启动
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				SharedPreferences sp = getSharedPreferences("sp" , Context.MODE_PRIVATE);
				String passwordStr = sp.getString("password", "");
				
				if(TextUtils.isEmpty(passwordStr)){ //如果现在还没有设置密码，那就跳到MainActivity里面去设置密码
					startActivity( new Intent(WelcomeActivity.this , MainActivity.class) );
					finish();
				}
				else{ //如果之前已经设置过密码，就开始进行密码检查
					getSupportFragmentManager().beginTransaction().replace(android.R.id.content , PasswordFragment.newInstance(PasswordFragment.TYPE_CHECK)).commit();
				}
			}
			
		}, 1000);
		
		
		
		
	}//onCreate()函数结束
	
}
