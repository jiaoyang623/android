package org.daniel.android.simpledemo;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import org.daniel.android.simpledemo.bean.UserBean;

/**
 * Created by jiaoyang on 3/3/16.
 */
public class DataBindingActivity extends AppCompatActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewDataBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_data_binding);
		UserBean user = new UserBean("Test", "User");
		binding.setVariable(org.daniel.android.simpledemo.BR.user, user);
	}
}
