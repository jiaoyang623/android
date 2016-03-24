package org.daniel.android.simpledemo.widgets;

import android.content.Context;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

/**
 * Created by jiaoyang on 2016/3/15.
 */
public class CoverFlowListView extends ListView implements AbsListView.OnScrollListener {
	public CoverFlowListView(Context context) {
		super(context);
		init(context);
	}

	public CoverFlowListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public CoverFlowListView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}

	private void init(Context context) {
		setChildrenDrawingOrderEnabled(true);
		setOnScrollListener(this);
		mCamera = new Camera();
	}

	private Camera mCamera;

	@Override
	protected int getChildDrawingOrder(int childCount, int i) {
		if (i < childCount / 2) {
			return i;
		} else {
			return childCount + childCount / 2 - i - 1;
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {

	}

	@Override
	protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
		int i = indexOfChild(child);
		int count = getChildCount();
		canvas.save();
		mCamera.save();
		if (i < count / 2) {
			mCamera.rotateX(-30);
//			mCamera.translate(0, -10, 0);
		} else {
			mCamera.rotateX(30);
//			mCamera.translate(0, 10, 0);
		}
		mCamera.applyToCanvas(canvas);
		mCamera.restore();
		boolean result = super.drawChild(canvas, child, drawingTime);
		canvas.restore();
		return result;
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//		int count = getChildCount();
//		for (int i = 0; i < count; i++) {
//			if (i < count / 2) {
//				getChildAt(i).offsetTopAndBottom(-1);
//			} else {
//				getChildAt(i).offsetTopAndBottom(1);
//			}
//		}
	}
}
