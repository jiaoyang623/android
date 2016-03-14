package org.daniel.android.simpledemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

/**
 * Created by jiaoyang on 2016/2/24.
 */
public class GlideActivity extends AppCompatActivity implements View.OnClickListener {
	private ImageView mImageView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_glide);
		mImageView = (ImageView) findViewById(R.id.image);
		findViewById(R.id.add).setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.add:
				Glide.with(this).load("http://img3.imgtn.bdimg.com/it/u=4245198817,693717552&fm=21&gp=0.jpg").into(mImageView);
				break;
		}
	}
}
