package org.daniel.android.workbanch.widget;

import java.util.Stack;

import android.content.Context;
import android.graphics.Rect;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.animation.TranslateAnimation;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.Scroller;

/**
 * 自己写的listview
 * 
 * @author Daniel Jiao <br>
 *         email:yangjiao623@gmail.com
 * @version 创建时间: Nov 6, 2012 10:47:05 AM
 * */
public class SimListView extends ViewGroup implements OnGestureListener {
    public static final int INVALID_POSITION = -1;
    public static final int FOLD_DURATION = 300;
    private static final int INTERVAL = 15;
    private static final long VIBRATOR_DURATION = 23;
    // Widgets
    private Adapter mAdapter = null;
    private GestureDetector mGestureDetector;

    // Variables
    private Vibrator mVibrator;
    private Stack<View> mViewStack = new Stack<View>();
    private FlingRunable mFlingRunable;
    // 滑动到底端的刷新
    private Runnable mScroll2EndRunnable;

    // package
    int mFirstVisiblePosition = 0;
    int mLastVisiblePosition = -1;
    private long mFirstId = 0;

    private OnItemClickListener mOnItemClickListener;
    private OnItemLongClickListener mOnItemLongClickListener;
    private OnItemExpandedListener mOnItemExpandedListener;
    private OnItemFoldedListener mOnItemFoldedListener;

    public SimListView(Context context) {
        super(context);
        init(context);
    }

    public SimListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SimListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        if (isInEditMode()) {
            return;
        }
        mGestureDetector = new GestureDetector(context, this);
        mFlingRunable = new FlingRunable(context);

        mVibrator = (Vibrator) context
                .getSystemService(Context.VIBRATOR_SERVICE);
    }

    public Adapter getAdapter() {
        return mAdapter;
    }

    public void setAdapter(Adapter adapter) {
        mAdapter = adapter;

        removeAllViews();
        mFirstVisiblePosition = 0;
        mLastVisiblePosition = -1;

        while (!addViewToBottom())
            ;
        invalidate();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (mAdapter == null) {
            return;
        }

        invalidateViews();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            removeCallbacks(mFlingRunable);
        }

        return mGestureDetector.onTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        mGestureDetector.onTouchEvent(ev);

        return true;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public void onLongPress(MotionEvent e) {

        int pos = pointToPosition((int) e.getX(), (int) e.getY());
        if (mOnItemLongClickListener != null && pos != INVALID_POSITION) {
            mOnItemLongClickListener.onLongClick(this, getChildAt(pos), pos
                    + mFirstVisiblePosition);
            mVibrator.vibrate(VIBRATOR_DURATION);
        }
    }

    public void setOnItemLongClickListener(OnItemLongClickListener l) {
        mOnItemLongClickListener = l;
    }

    public void setOnItemClickListener(OnItemClickListener l) {
        mOnItemClickListener = l;
    }

    public void setOnItemExpandedListener(OnItemExpandedListener l) {
        mOnItemExpandedListener = l;
    }

    public void setOnItemFoldedListener(OnItemFoldedListener l) {
        mOnItemFoldedListener = l;
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        int pos = pointToPosition((int) e.getX(), (int) e.getY());
        if (mOnItemClickListener != null && pos != INVALID_POSITION) {
            mOnItemClickListener.onClick(this, getChildAt(pos), pos
                    + mFirstVisiblePosition);
            return true;
        } else {
            return false;
        }
    }

    /**
     * 得到是相对于View的位置，要得到在adapter中的位置，需要加上mFirstVisiblePosition
     * */
    int pointToPosition(int x, int y) {
        Rect frame = new Rect();

        final int count = getChildCount();
        for (int i = count - 1; i >= 0; i--) {
            final View child = getChildAt(i);
            child.getHitRect(frame);
            if (frame.contains(x, y)) {
                return i;
            }
        }
        return INVALID_POSITION;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
            float distanceY) {
        moveViews((int) -distanceY);

        return true;
    }

    /**
     * 这里处理View的发放和回收
     * */
    protected void moveViews(int deltaY) {
        if (getChildCount() == 0) {
            return;
        }
        // 判断是否能向上移动
        View firstView = getChildAt(0);
        boolean isAtTop = mFirstVisiblePosition == 0
                && (firstView == null || firstView.getTop() >= 0);
        if (isAtTop && deltaY > 0) {
            return;
        }

        // 判断是否能向下移动
        View lastView = getChildAt(getChildCount() - 1);

        boolean isAtBottom = mLastVisiblePosition == mAdapter.getCount() - 1
                && (lastView == null || lastView.getBottom() <= getBottom());

        if (isAtBottom && deltaY < 0) {
            return;
        }

        // 将要移入的View显示出来
        showViewIn(deltaY);

        // 移动
        if (deltaY < 0) {
            // 上移
            deltaY = Math.max(getHeight()
                    - getChildAt(getChildCount() - 1).getBottom(), deltaY);
        } else {
            // 下移
            deltaY = Math.min(-getChildAt(0).getTop(), deltaY);
        }
        offsetAllChildrenView(deltaY);
        // 将已经移出的View回收
        hideViewsOut();

        mFirstId = mAdapter.getItemId(mFirstVisiblePosition);

        invalidate();
    }

    private void offsetAllChildrenView(int deltaY) {
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            getChildAt(i).offsetTopAndBottom(deltaY);
        }
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
            float velocityY) {
        mFlingRunable.fling((int) velocityY);

        return false;
    }

    private class FlingRunable implements Runnable {
        private Scroller mScroller;
        private int lastPosition = 0;

        public FlingRunable(Context context) {
            mScroller = new Scroller(context);
        }

        public void fling(int velocityY) {
            removeCallbacks(mFlingRunable);
            mScroller.fling(0, 0, 0, velocityY, -Integer.MAX_VALUE,
                    Integer.MAX_VALUE, -Integer.MAX_VALUE, Integer.MAX_VALUE);
            lastPosition = 0;
            post(mFlingRunable);
        }

        public void run() {
            boolean isScrolling = mScroller.computeScrollOffset();
            int delta = lastPosition - mScroller.getCurrY();
            moveViews(-delta);
            lastPosition = mScroller.getCurrY();
            if (isScrolling) {
                postDelayed(this, INTERVAL);
            }
        }
    }

    // 将移出视野的View删除
    void hideViewsOut() {
        // 回收前面的View
        for (View v = getChildAt(0); v.getBottom() <= 0; v = getChildAt(0)) {
            detachViewFromParent(v);
            mViewStack.push(v);
            mFirstVisiblePosition++;
        }

        // 回收后面的View
        for (View v = getChildAt(getChildCount() - 1); v.getTop() >= getBottom(); v = getChildAt(getChildCount() - 1)) {
            detachViewFromParent(v);
            mViewStack.push(v);
            mLastVisiblePosition--;
        }
    }

    /**
     * 向页面添加View
     * */
    void showViewIn(int delta) {
        // add to top
        View firstView = getChildAt(0);
        if (firstView != null && firstView.getTop() + delta > -1) {
            while (!addViewToTop())
                ;
        }

        // add to bottom
        View lastView = getChildAt(getChildCount() - 1);

        if (lastView != null && lastView.getBottom() + delta < getHeight()) {
            while (!addViewToBottom())
                ;
        }

    }

    /**
     * @return is first view in screen
     * */
    private boolean addViewToTop() {
        if (isAdapterEmpty() || mFirstVisiblePosition <= 0) {
            return true;
        }

        mFirstVisiblePosition--;

        View view = mAdapter.getView(mFirstVisiblePosition,
                mViewStack.size() > 0 ? mViewStack.pop() : null, this);
        measureView(view);

        int firstChildIndex = 0;
        int bottom = getChildAt(0).getTop();

        addViewInLayout(view, firstChildIndex, generateDefaultLayoutParams());
        int top = bottom - view.getMeasuredHeight();
        view.layout(0, top, getWidth(), bottom);

        return top < 0;
    }

    /**
     * @return is last child in the screen
     * */
    private boolean addViewToBottom() {
        // 没有数据可以加载了
        if (isAdapterEmpty() || mLastVisiblePosition + 1 >= mAdapter.getCount()) {
            return true;
        }

        mLastVisiblePosition++;

        View view = mAdapter.getView(mLastVisiblePosition,
                mViewStack.size() > 0 ? mViewStack.pop() : null, this);

        measureView(view);

        int lastChildIndex = getChildCount();
        addViewInLayout(view, lastChildIndex, generateDefaultLayoutParams());

        int top = 0;
        if (lastChildIndex > 0) {
            top = getChildAt(lastChildIndex - 1).getBottom();
        }
        int bottom = top + view.getMeasuredHeight();

        view.layout(0, top, getWidth(), bottom);

        return bottom > getHeight();
    }

    private void measureView(View view) {
        int heightMeasureSpec = MeasureSpec.makeMeasureSpec(0,
                MeasureSpec.UNSPECIFIED);
        int widthMeasureSpec = MeasureSpec.makeMeasureSpec(getWidth(),
                MeasureSpec.AT_MOST);

        LayoutParams params = view.getLayoutParams();
        if (params == null) {
            params = new LayoutParams(LayoutParams.MATCH_PARENT,
                    LayoutParams.WRAP_CONTENT);
            view.setLayoutParams(params);
        }

        view.measure(widthMeasureSpec, heightMeasureSpec);
    }

    private boolean isAdapterEmpty() {
        return mAdapter == null || mAdapter.getCount() <= 0;
    }

    @Override
    public void removeAllViews() {
        mViewStack.clear();
        mFirstVisiblePosition = 0;
        mLastVisiblePosition = -1;
        super.removeAllViews();
    }

    /**
     * 刷新内容
     * */
    public void invalidateViews() {
        removeCallbacks(mFlingRunable);
        // 如果没有数据，则删除所有View
        if (mAdapter == null || mAdapter.getCount() <= 0) {
            removeAllViews();
            return;
        }
        // 刷新数据
        View top = getChildAt(0);
        int firstDelta = top == null ? 0 : top.getTop();
        // 回收View
        for (int i = getChildCount() - 1; i >= 0; i--) {
            View v = getChildAt(i);
            detachViewFromParent(i);
            mViewStack.push(v);
            mLastVisiblePosition--;
        }

        // 寻找第一个刷新后第一个位置
        int pos = findPositionById(mFirstId);
        pos = pos == INVALID_POSITION ? mFirstVisiblePosition : pos;

        if (pos < mAdapter.getCount()) {
            mFirstVisiblePosition = pos;
            mLastVisiblePosition = pos - 1;
            addViewToBottom();
            getChildAt(0).offsetTopAndBottom(firstDelta);

            recoverFromTop();
        } else {
            mFirstVisiblePosition = mAdapter.getCount() - 1;
            mLastVisiblePosition = mFirstVisiblePosition - 1;
            addViewToBottom();
            offsetAllChildrenView(getHeight() - getChildAt(0).getBottom());

            recoverFromBottom();
        }

        // 页面重绘
        invalidate();
    }

    /**
     * 通过id寻找位置
     * */
    private int findPositionById(long id) {
        if (mAdapter == null || mAdapter.getCount() == 0) {
            return INVALID_POSITION;
        }
        int count = mAdapter.getCount();
        for (int i = 0; i < count; i++) {
            if (id == mAdapter.getItemId(i)) {
                return i;
            }
        }

        return INVALID_POSITION;
    }

    private void recoverFromTop() {
        while (getChildAt(getChildCount() - 1).getBottom() < getHeight()) {
            if (mLastVisiblePosition < mAdapter.getCount() - 1) {
                addViewToBottom();
            } else {
                int dist = getHeight()
                        - getChildAt(getChildCount() - 1).getBottom();
                offsetAllChildrenView(dist);
                recoverFromBottom();
                break;
            }
        }
    }

    private void recoverFromBottom() {
        while (getChildAt(0).getTop() > 0) {
            if (mFirstVisiblePosition > 0) {
                addViewToTop();
            } else {
                // 移动到顶端
                offsetAllChildrenView(-getChildAt(0).getTop());
                break;
            }
        }
    }

    /**
     * 展开某一项
     * 
     * */
    public void expand(final int position) {
        // 检验Posiotn
        final int pos = position - mFirstVisiblePosition;
        final int childCount = getChildCount();
        if (!isPositionIn(position)) {
            return;
        }
        // 现将下面的View移动上来
        View v = getChildAt(pos);
        final int delta = v.getHeight();
        offsetChildren(pos + 1, childCount - 1, -delta);
        // 补齐下面的空白
        showViewIn(0);
        invalidate();

        // 开始移动的动画
        final Scroller scroller = new Scroller(getContext());
        scroller.startScroll(0, 0, 0, delta, FOLD_DURATION);
        post(new Runnable() {
            private int lastPos = 0;

            @Override
            public void run() {
                if (scroller.computeScrollOffset()) {
                    int y = scroller.getCurrY();
                    offsetChildren(pos + 1, getChildCount() - 1, y - lastPos);
                    lastPos = y;
                    invalidate();
                    postDelayed(this, INTERVAL);
                } else {
                    offsetChildren(pos + 1, getChildCount() - 1,
                            scroller.getFinalY() - lastPos);
                    hideViewsOut();
                    invalidate();

                    if (mOnItemExpandedListener != null) {
                        mOnItemExpandedListener.onExpanded(position);
                    }
                }
            }
        });
    }

    private boolean isPositionIn(int position) {
        final int pos = position - mFirstVisiblePosition;
        return pos >= 0 && pos < getChildCount();
    }

    /**
     * 上下移动子View
     * */
    private void offsetChildren(int fromPos, int toPos, int deltaY) {
        for (int i = fromPos; i <= toPos; i++) {
            getChildAt(i).offsetTopAndBottom(deltaY);
        }
    }

    /**
     * 折叠某一项
     * 
     * */
    public void fold(final int position) {
        final int pos = position - mFirstVisiblePosition;

        if (!isPositionIn(position)) {
            return;
        }

        final int delta = getChildAt(pos).getHeight();

        // 开始移动的动画
        final Scroller scroller = new Scroller(getContext());
        scroller.startScroll(0, 0, 0, -delta, FOLD_DURATION);
        post(new Runnable() {
            private int lastPos = 0;

            @Override
            public void run() {
                if (scroller.computeScrollOffset()) {
                    int y = scroller.getCurrY();
                    offsetChildren(pos + 1, getChildCount() - 1, y - lastPos);
                    lastPos = y;
                    showViewIn(0);
                    invalidate();

                    postDelayed(this, INTERVAL);
                } else {
                    offsetChildren(pos + 1, getChildCount() - 1,
                            scroller.getFinalY() - lastPos);
                    if (mOnItemFoldedListener != null) {
                        mOnItemFoldedListener.onFolded(position);
                    }
                    invalidateViews();
                }
            }
        });
    }

    public void expand2(int position) {
        if (!isPositionIn(position)) {
            return;
        }

        int pos = position - mFirstVisiblePosition;

        View v = getChildAt(pos);
        int h = v.getHeight();
        TranslateAnimation anim = new TranslateAnimation(0, 0, -h, 0);
        anim.setDuration(FOLD_DURATION);
        int count = getChildCount();
        for (int i = pos + 1; i < count; i++) {
            getChildAt(i).startAnimation(anim);
        }
    }

    public void fold2(final int position) {
        // 删除数据
        if (mOnItemFoldedListener != null) {
            mOnItemFoldedListener.onFolded(position);
        }

        if (!isPositionIn(position)) {
            return;
        }

        // 添加动画
        int pos = position - mFirstVisiblePosition;

        View v = getChildAt(pos);
        int h = v.getHeight();
        TranslateAnimation anim = new TranslateAnimation(0, 0, h, 0);
        anim.setDuration(FOLD_DURATION);
        int count = getChildCount();
        for (int i = pos; i < count; i++) {
            getChildAt(i).startAnimation(anim);
        }
    }

    /**
     * 将item从fromPos移动到toPos的动画
     * */
    public void move(final int fromPos, final int toPos) {
        if (!isPositionIn(fromPos) || !isPositionIn(toPos) || fromPos == toPos) {
            return;
        }
        // 将内容从上面移到下面
        boolean downMove = fromPos < toPos;
        int upper = (downMove ? fromPos : toPos) - mFirstVisiblePosition;
        int lower = (downMove ? toPos : fromPos) - mFirstVisiblePosition;
        int height = getChildAt(upper).getHeight();
        int dist = downMove ? height : -height;

        TranslateAnimation anim = new TranslateAnimation(0, 0, dist, 0);
        anim.setDuration(FOLD_DURATION);

        if (downMove) {
            lower--;
        } else {
            upper++;
        }
        for (int i = upper; i <= lower; i++) {
            getChildAt(i).startAnimation(anim);
        }
    }

    /**
     * 平滑地滑到底端
     * 
     * @param speed
     *            滑动速度，即两次刷新之间的移动距离
     * */
    public void smoothToEnd(final int duration) {
        if (mAdapter.getCount() == 0) {
            return;
        }

        if (mScroll2EndRunnable != null) {
            removeCallbacks(mScroll2EndRunnable);
        }

        final int unitHight = getChildAt(0).getHeight();
        final int distance = (mAdapter.getCount() - mLastVisiblePosition)
                * unitHight;

        mScroll2EndRunnable = new Runnable() {
            private long t = System.currentTimeMillis();

            @Override
            public void run() {
                if (mLastVisiblePosition != mAdapter.getCount() - 1
                        || getChildAt(getChildCount() - 1).getBottom() > getHeight()) {
                    // 移动
                    long dt = System.currentTimeMillis() - t;
                    int speed = -distance * (int) dt / duration;
                    speed = Math.min(-10, speed);
                    moveViews(speed);
                    postDelayed(this, INTERVAL);
                } else {
                    mScroll2EndRunnable = null;
                    // 移回原来位置
                }
            }
        };
        post(mScroll2EndRunnable);
    }

    /** 点击事件 */
    public interface OnItemClickListener {
        void onClick(View parent, View view, int position);
    }

    /** 长击事件 */
    public interface OnItemLongClickListener {
        void onLongClick(View parent, View view, int position);
    }

    /** 添加的动画 */
    public interface OnItemExpandedListener {
        /**
         * @param position
         *            adapter中的位置
         * */
        void onExpanded(int position);
    }

    /** 删除的动画 */
    public interface OnItemFoldedListener {
        /**
         * @param position
         *            adapter中的位置
         * */
        void onFolded(int position);
    }

}
