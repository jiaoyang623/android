package org.daniel.android.simpledemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.ScaleAnimation;

/**
 * Created by jiaoyang on 2016/3/8.
 */
public class AnimationActivity extends AppCompatActivity implements View.OnClickListener, Interpolator {
	private View mView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_animation);
		mView = findViewById(R.id.switch1);
	}

	@Override
	public void onClick(View v) {
		ScaleAnimation anim = new ScaleAnimation(0.5f, 1.5f, 0.5f, 1.5f, 0.5f, 0.5f);
		anim.setDuration(2 * 1000);
		anim.setInterpolator(this);
		mView.startAnimation(anim);
	}

	@Override
	public float getInterpolation(float input) {
		if (input < 0.2) {
			return 0.5f;
		} else if (input < 0.4) {
			return -2.5f * input + 1;
		} else if (input < 0.6) {
			return 5 * input - 2;
		} else if (input < 0.8) {
			return 2.5f * (1 - input);
		} else {
			return 0.5f;
		}
	}
}
