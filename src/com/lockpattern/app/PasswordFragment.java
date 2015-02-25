package com.lockpattern.app;

import com.lockpattern.app.LockPatternView.OnPatterChangeListener;

import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

/*
 * 密码碎片
 */
public class PasswordFragment extends Fragment implements OnPatterChangeListener, android.view.View.OnClickListener{
	
	public static final String TYPE_SETTING = "setting";
	public static final String TYPE_CHECK = "check";
	
	private static final String ARG_TYPE = "type";
	
	private TextView lockHint;
	private LockPatternView lockPatternView;
	private LinearLayout btnLayout;
	private String passwordStr;
	
	
	public static PasswordFragment newInstance(String typeStr){
		PasswordFragment fragment = new PasswordFragment();
		Bundle args = new Bundle();
		args.putString(ARG_TYPE , typeStr);
		fragment.setArguments(args);
		return fragment;
	}
	
	public PasswordFragment(){
		//什么也不写
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater , ViewGroup container , Bundle savedInstanceState) {
		
		View contentView = inflater.inflate(R.layout.fragment_pass , container , false);
		lockHint = (TextView) contentView.findViewById(R.id.fragment_pass_lock_hint);
		lockPatternView =  (LockPatternView) contentView.findViewById(R.id.fragment_pass_lock);
		btnLayout = (LinearLayout) contentView.findViewById(R.id.fragment_pass_btn_layout);
		//设置密码
		if(getArguments() != null){
			if(TYPE_SETTING.equals(getArguments().getString(ARG_TYPE))){
				btnLayout.setVisibility(View.VISIBLE);
			}
		}
		
		contentView.findViewById(R.id.fragment_pass_commit).setOnClickListener(this);
		lockPatternView.setPatterChangeListener(this);
		return contentView;
	}
	
	@Override
	public void onPatterChange(String passwordStr) {
		this.passwordStr = passwordStr;
    	if( ! TextUtils.isEmpty(passwordStr) ){
    		lockHint.setText(passwordStr);
    		//检查密码
    		if(getArguments() != null){
    			if(TYPE_CHECK.equals(getArguments().getString(ARG_TYPE))){
    				SharedPreferences sp = getActivity().getSharedPreferences("sp", Context.MODE_PRIVATE);
    				if(passwordStr.equals(sp.getString("password" , ""))){
    					//检查成功
    					getActivity().startActivity( new Intent(getActivity() , MainActivity.class) );
    					getActivity().finish();
    				}
    				else{
    					//检查失败，密码不对
    					lockHint.setText("密码错误");
    					lockPatternView.resetPoint();
    				}
    			}
    		}
    	}
    	else{
    		lockHint.setText("至少4个图案");
    	}
	}
	
	@Override
	public void onPatterStart(boolean isStart) {
    	if(isStart == true){
    		lockHint.setText("请绘制图案");
    	}
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.fragment_pass_commit:
			SharedPreferences sp = getActivity().getSharedPreferences("sp" , Context.MODE_PRIVATE);
			sp.edit().putString("password", passwordStr).commit();
			getActivity().finish();
			break;
		}
		
	}

}
