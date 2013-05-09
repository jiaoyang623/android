package org.daniel.android.workbanch.widget;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.opengl.Matrix;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * 类说明
 * 
 * @author Daniel Jiao <br>
 *         email:yangjiao623@gmail.com
 * @version 创建时间: Jan 25, 2013 5:52:08 PM
 * */
public class TagWheelView extends View {
    private static final int R = 300;
    private List<Tag> mTagList = new ArrayList<TagWheelView.Tag>();
    private MatrixState mMatrixState = new MatrixState();
    private Paint mPaint;

    public TagWheelView(Context context) {
        super(context);
        init(context);
    }

    public TagWheelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TagWheelView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        mPaint = new Paint();
        mPaint.setAntiAlias(false);
        mPaint.setStrokeWidth(5);

        mPaint.setColor(0xaa000000);
        mPaint.setTextSize(40);

        mMatrixState = new MatrixState();

        List<String> textList = new ArrayList<String>(20);
        for (int i = 0; i < 20; i++) {
            textList.add("P" + i);
        }

        setTags(textList);
    }

    private float mLastY;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() != MotionEvent.ACTION_DOWN) {
            float dy = event.getY() - mLastY;

            mMatrixState.rotate(dy);

            invalidate();
        }

        mLastY = event.getY();

        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        canvas.translate(getWidth() / 2, getHeight() / 2);

        float[] from = new float[3];
        float[] to = new float[3];
        for (Tag tag : mTagList) {
            from[0] = 0;
            from[1] = tag.y;
            from[2] = tag.z;

            mMatrixState.convert(from, to);

            // deep [0, 1]
            final float deep = to[2] / R / 2 + 0.5f;
            final int alpha = (int) (deep * 0xff);
            mPaint.setColor(Color.argb(alpha, 0, 0, 0));
            mPaint.setTextSize((int) (20 + 20 * deep));

            canvas.drawText(tag.text, to[0], to[1], mPaint);
        }

        canvas.restore();
    }

    public void setTags(List<String> tags) {
        mTagList.clear();
        if (tags == null || tags.size() == 0) {
            return;
        }
        int size = tags.size();
        final float deltaAngle = 3.1416f * 2 / size;
        for (int i = 0; i < size; i++) {
            Tag tag = new Tag(tags.get(i), i * deltaAngle, R);
            mTagList.add(tag);
        }
    }

    private class Tag {
        final String text;
        final float y, z;

        public Tag(String tag, float angle, float r) {
            this.text = tag;
            this.y = (float) (r * Math.sin(angle));
            this.z = (float) (r * Math.cos(angle));
        }
    }

    private class MatrixState {
        private float[] mMatrix;

        public MatrixState() {
            mMatrix = new float[16];
            Matrix.setRotateM(mMatrix, 0, 0, 1, 0, 0);
        }

        public void rotate(float dy) {
            Matrix.rotateM(mMatrix, 0, dy, 1, 0, 0);
        }

        public void convert(float[] from, float[] to) {
            for (int i = 0; i < 3; i++) {
                int line = i * 4;
                to[i] = from[0] * mMatrix[line] + from[1] * mMatrix[line + 1]
                        + from[2] * mMatrix[line + 2] + mMatrix[line + 3];
            }
        }
    }

}
