package org.daniel.android.workbanch.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

/**
 * 主view可以左右移动的三栏式View。如果需要在内部添加View，需要遵守“左-右-中”的方法
 * 
 * @author Daniel Jiao <br>
 *         email:yangjiao623@gmail.com
 * @version 创建时间: Nov 19, 2012 3:16:54 PM
 * */
public class SlideLayout extends ViewGroup {
    private static final int SCROLL_DURATION = 500;
    // private static final int SCROLL_INTERVAL = 10;
    private static final int CHILD_COUND = 3;
    private static final int SCROLL_MIDDLE = 0;
    private static final int SCROLL_RIGHT = 1;
    private static final int SCROLL_LEFT = 2;
    // 滑动之后露出的边沿长度
    private static final double SCROLL_EDGE = 0.1;
    private static final int SCROLL_THREATHOLD = 10;

    // variables
    private int mScrollState = SCROLL_MIDDLE;
    private float x0 = 0;
    private float y0 = 0;
    private float xFromDown = 0;
    private boolean isDetected = false;
    private boolean isIntercepted = false;

    // Widgets
    private View mLeftView;
    private View mRightView;
    private View mMiddleView;

    private Scrollable mScrollable;

    /**
     * 调用setViews()添加View
     * */
    public SlideLayout(Context context) {
        super(context);
        init(context);
    }

    /**
     * 内部需要存在三个View
     * */
    public SlideLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    /**
     * 内部需要存在三个View
     * */
    public SlideLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        mScrollable = new Scrollable(context);
    }

    @Override
    protected void onFinishInflate() {
        initSubViews();
    }

    public void setViews(View left, View right, View middle) {
        removeAllViews();

        super.addView(left);
        super.addView(right);
        super.addView(middle);

        initSubViews();
    }

    private void initSubViews() {
        int count = getChildCount();
        if (count != CHILD_COUND) {
            throw new IllegalArgumentException(
                    "there should be 3 views in: left, right, middle");
        }

        mLeftView = getChildAt(0);
        mRightView = getChildAt(1);
        mMiddleView = getChildAt(2);

        mLeftView.setVisibility(INVISIBLE);
        mRightView.setVisibility(INVISIBLE);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mLeftView == null || mRightView == null || mMiddleView == null) {
            System.out.println("null view");
            return;
        }
        int innerWidthMeasureSpec = MeasureSpec
                .makeMeasureSpec(
                        (int) (MeasureSpec.getSize(widthMeasureSpec) * (1.01 - SCROLL_EDGE)),
                        MeasureSpec.getMode(widthMeasureSpec));
        mLeftView.measure(innerWidthMeasureSpec, heightMeasureSpec);
        mRightView.measure(innerWidthMeasureSpec, heightMeasureSpec);
        mMiddleView.measure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (mLeftView == null || mRightView == null || mMiddleView == null) {
            System.out.println("onLayout null view");
            return;
        }

        mLeftView.layout(l, t, (int) (r * (1.01 - SCROLL_EDGE)), b);
        mRightView.layout((int) (r * (SCROLL_EDGE - 0.1)), t, r, b);
        mMiddleView.layout(l, t, r, b);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            x0 = ev.getX();
            xFromDown = x0;
            y0 = ev.getY();
            removeCallbacks(mScrollable);
            isIntercepted = false;

            if (x0 >= mMiddleView.getLeft() && x0 <= mMiddleView.getRight()) {
                // 点击主页内
                isDetected = false;
            } else {
                // 点击主页外
                isDetected = true;
            }

            return false;

        } else {
            if (isDetected) {
                return isIntercepted;
            }
            if (Math.abs(xFromDown - ev.getX()) > SCROLL_THREATHOLD) {
                isIntercepted = Math.abs((x0 - ev.getX()) / (y0 - ev.getY())) >= 1;
                isDetected = true;
            }

            return isIntercepted;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float distanceX = event.getX() - x0;
        x0 = event.getX();
        boolean is2Left = event.getX() - xFromDown <= 0;

        if (event.getAction() == MotionEvent.ACTION_UP) {
            smoothScroll(is2Left);
        } else {
            offsetMiddle((int) distanceX);
        }

        return true;
    }

    // 根据状态启动滑动动画
    private void smoothScroll(boolean is2Left) {
        int to = 0;
        switch (mScrollState) {
            case SCROLL_LEFT:
                if (is2Left) {
                    to = (int) (getWidth() * (SCROLL_EDGE - 1));
                } else {
                    to = 0;
                    mScrollState = SCROLL_MIDDLE;
                }
                break;
            case SCROLL_RIGHT:
                if (is2Left) {
                    to = 0;
                    mScrollState = SCROLL_MIDDLE;
                } else {
                    to = (int) (getWidth() * (1 - SCROLL_EDGE));
                }
                break;
            default:
                if (is2Left) {
                    to = (int) (getWidth() * (SCROLL_EDGE - 1));
                    mScrollState = SCROLL_LEFT;
                } else {
                    to = (int) (getWidth() * (1 - SCROLL_EDGE));
                    mScrollState = SCROLL_RIGHT;
                }

                break;
        }
        mScrollable.scroll(to - mMiddleView.getLeft(), 0, mMiddleView,
                SCROLL_DURATION);
    }

    // 滑动动画
    // private class Scrollable implements Runnable {
    // private Scroller mScroller;
    // private int mCurrX = 0;
    //
    // public Scrollable(Context context) {
    // mScroller = new Scroller(getContext(), new Interpolator() {
    //
    // @Override
    // public float getInterpolation(float input) {
    // // input is from 0 to 1;
    // return input * (-1.25f * input + 2.25f);
    // }
    // });
    // }
    //
    // public void scroll(int distance) {
    // mScroller.startScroll(0, 0, distance, 0, SCROLL_DURATION);
    // mCurrX = 0;
    // removeCallbacks(this);
    // post(this);
    // }
    //
    // @Override
    // public void run() {
    // if (mScroller.computeScrollOffset()) {
    // offsetMiddle(mScroller.getCurrX() - mCurrX);
    // mCurrX = mScroller.getCurrX();
    // postDelayed(this, SCROLL_INTERVAL);
    // }
    // }
    // }

    // 控制左右滑动
    private void offsetMiddle(int delta) {
        int oldMidPos = mMiddleView.getLeft();
        mMiddleView.offsetLeftAndRight(delta);
        invalidate();
        int midPos = mMiddleView.getLeft();
        if (midPos != 0 && oldMidPos * midPos <= 0) {
            setPannel(delta > 0);
        }
    }

    // 控制显示哪个pannel
    private void setPannel(boolean isLeft) {
        mLeftView.setVisibility(isLeft ? VISIBLE : INVISIBLE);
        mRightView.setVisibility(isLeft ? INVISIBLE : VISIBLE);
    }

}
