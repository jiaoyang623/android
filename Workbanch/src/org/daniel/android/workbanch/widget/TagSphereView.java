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
 * @version 创建时间: Jan 22, 2013 2:07:37 PM
 * */
public class TagSphereView extends View {
    private static final float mR = 300;
    private List<Tag> mTagList = new ArrayList<TagSphereView.Tag>();
    private Paint mPaint;
    private MatrixState mMatrixState;

    public TagSphereView(Context context) {
        super(context);
        init(context);
    }

    public TagSphereView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TagSphereView(Context context, AttributeSet attrs, int defStyle) {
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

        initPoints(textList);
    }

    private float mLastX, mLastY;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() != MotionEvent.ACTION_DOWN) {
            float dx = event.getX() - mLastX;
            float dy = event.getY() - mLastY;

            mMatrixState.rotate(dx, dy);

            invalidate();
        }

        mLastX = event.getX();
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
            from[0] = tag.x;
            from[1] = tag.y;
            from[2] = tag.z;

            mMatrixState.convert(from, to);

            // deep [0, 1]
            float deep = to[2] / mR / 2 + 0.5f;
            int alpha = (int) (deep * 0xff);
            mPaint.setColor(Color.argb(alpha, 0, 0, 0));
            mPaint.setTextSize((int) (20 + 20 * deep));

            canvas.drawText(tag.text, to[0], to[1], mPaint);
        }

        canvas.restore();
    }

    private class Tag {
        final float x, y, z;
        final String text;

        public Tag(String text, float x, float y, float z) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.text = text;
        }

    }

    private static class Point {
        public final float x, y, z;

        public Point(float x, float y, float z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public static Point[] get20() {
            Point[] ps = new Point[20];

            ps[0] = new Point(0.0f, 0.28355026945067996f, 0.742344242941071f);
            ps[1] = new Point(-0.45879397349039114f, 0.45879397349039114f,
                    0.45879397349039114f);
            ps[2] = new Point(-0.28355026945067996f, 0.742344242941071f, 0.0f);
            ps[3] = new Point(0.28355026945067996f, 0.742344242941071f, 0.0f);
            ps[4] = new Point(0.45879397349039114f, 0.45879397349039114f,
                    0.45879397349039114f);
            ps[5] = new Point(0.742344242941071f, 0.0f, 0.28355026945067996f);
            ps[6] = new Point(0.742344242941071f, 0.0f, -0.28355026945067996f);
            ps[7] = new Point(0.45879397349039114f, 0.45879397349039114f,
                    -0.45879397349039114f);
            ps[8] = new Point(0.0f, 0.28355026945067996f, -0.742344242941071f);
            ps[9] = new Point(0.0f, -0.28355026945067996f, -0.742344242941071f);
            ps[10] = new Point(0.45879397349039114f, -0.45879397349039114f,
                    -0.45879397349039114f);
            ps[11] = new Point(0.28355026945067996f, -0.742344242941071f, 0.0f);
            ps[12] = new Point(-0.28355026945067996f, -0.742344242941071f, 0.0f);
            ps[13] = new Point(-0.45879397349039114f, -0.45879397349039114f,
                    0.45879397349039114f);
            ps[14] = new Point(0.0f, -0.28355026945067996f, 0.742344242941071f);
            ps[15] = new Point(0.45879397349039114f, -0.45879397349039114f,
                    0.45879397349039114f);
            ps[16] = new Point(-0.742344242941071f, 0.0f, 0.28355026945067996f);
            ps[17] = new Point(-0.742344242941071f, 0.0f, -0.28355026945067996f);
            ps[18] = new Point(-0.45879397349039114f, 0.45879397349039114f,
                    -0.45879397349039114f);
            ps[19] = new Point(-0.45879397349039114f, -0.45879397349039114f,
                    -0.45879397349039114f);

            return ps;
        }
    }

    private void initPoints(List<String> tags) {
        mTagList.clear();

        Point[] ps = Point.get20();

        for (int i = 0; i < ps.length && i < tags.size(); i++) {
            Point p = ps[i];
            Tag tag = new Tag(tags.get(i), p.x * mR, p.y * mR, p.z * mR);
            mTagList.add(tag);
        }
    }

    private class MatrixState {
        private float[] mMatrix;
        private float[] mProjMatrix;
        private float[] mVMatrix;
        private float[] mFinalMatrix;

        public MatrixState() {
            mMatrix = new float[16];
            Matrix.setRotateM(mMatrix, 0, 0, 1, 0, 0);
            mProjMatrix = new float[16];
            mVMatrix = new float[16];
            mFinalMatrix = new float[16];

            Matrix.frustumM(mProjMatrix, 0, -25, 25, -25, 25, 20, 250);
            Matrix.setLookAtM(mVMatrix, 0, 0, 0f, -40, 0f, 0f, 0f, 0f, 1.0f,
                    0.0f);

            rotate(0, 0);
        }

        public void rotate(float dx, float dy) {
            Matrix.rotateM(mMatrix, 0, dx, 0, -1, 0);
            Matrix.rotateM(mMatrix, 0, dy, 1, 0, 0);
            // 正交变透视
            // Matrix.multiplyMM(mFinalMatrix, 0, mVMatrix, 0, mMatrix, 0);
            // Matrix.multiplyMM(mFinalMatrix, 0, mProjMatrix, 0, mFinalMatrix,
            // 0);
            System.arraycopy(mMatrix, 0, mFinalMatrix, 0, mMatrix.length);
        }

        public void convert(float[] from, float[] to) {
            for (int i = 0; i < 3; i++) {
                int line = i * 4;
                to[i] = from[0] * mFinalMatrix[line] + from[1]
                        * mFinalMatrix[line + 1] + from[2]
                        * mFinalMatrix[line + 2] + mFinalMatrix[line + 3];
            }
        }
    }

}
