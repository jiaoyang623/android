package org.daniel.android.workbanch.widget;

import android.content.Context;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.Scroller;

/**
 * 用于产生滑动动画
 * 
 * @author Daniel Jiao <br>
 *         email:yangjiao623@gmail.com
 * @version 创建时间: Nov 28, 2012 4:54:33 PM
 * */
public class Scrollable implements Runnable {
	private static final int SCROLL_INTERVAL = 10;

	private Scroller mScroller;
	private int mCurrX = 0;
	private int mCurrY = 0;
	private View mView;

	public Scrollable(Context context) {
		mScroller = new Scroller(context, new Interpolator() {

			@Override
			public float getInterpolation(float input) {
				// input is from 0 to 1;
				return input * (-1.25f * input + 2.25f);
			}
		});
	}

	public void scroll(int distanceX, int distanceY, View view, int duration) {
		mScroller.startScroll(0, 0, distanceX, distanceY, duration);
		mCurrX = 0;
		mCurrY = 0;
		mView = view;
		mView.removeCallbacks(this);
		mView.post(this);
	}

	@Override
	public void run() {

		if (mScroller.computeScrollOffset()) {

			mView.offsetLeftAndRight(mScroller.getCurrX() - mCurrX);
			mView.offsetTopAndBottom(mScroller.getCurrY() - mCurrY);

			System.out.println("scroll run: " + (mScroller.getCurrX() - mCurrX)
					+ "," + (mScroller.getCurrY() - mCurrY));

			mCurrX = mScroller.getCurrX();
			mCurrY = mScroller.getCurrY();

			mView.postDelayed(this, SCROLL_INTERVAL);
		}
	}

}
