package com.lockpattern.app;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Window;


public class MainActivity extends FragmentActivity{
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        getSupportFragmentManager().beginTransaction().replace(android.R.id.content , PasswordFragment.newInstance(PasswordFragment.TYPE_SETTING)).commit();
    }

}
