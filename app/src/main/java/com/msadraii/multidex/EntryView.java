package com.msadraii.multidex;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.view.View;

/**
 * Created by Mostafa on 3/21/2015.
 */
public class EntryView extends View {
    // TODO fill these out
    private static final float RADIUS_MULTIPLIER = 0.4f;
    private static final float STROKE_WIDTH = 6f;
    private static final float SUB_GUIDE_LINE_STROKE_WIDTH = 4f;

    private int mColor = 9999999;
    private Bitmap mBitmap;
//    private Bitmap subBitmap;
    private Canvas mCanvas;
//    private Canvas mTempCanvas;
    private Path mMainPath;
    private Path mSubPath;
    private Paint mPaint;
    private Paint mMainStrokePaint;
    private Paint mSubStrokePaint;
    private double mGuideLineAngle;
    private float mCenterX;
    private float mCenterY;
    private float mWidth;
    private float mHeight;
    private float mRadius;
//    private float startX;
//    private float startY;
    private float endX;
    private float endY;

    public EntryView(Context context) {
        super(context);
//        mTempCanvas = new Canvas();
    }

    public EntryView(Context context, int color) {
        super(context);
        mColor = color;
    }

    public void setColor(int color) {
        mColor = color;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        mMainPath = new Path();
        mSubPath = new Path();

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);

        mMainStrokePaint = new Paint();
        mMainStrokePaint.setAntiAlias(true);
        mMainStrokePaint.setStyle(Paint.Style.STROKE);
        mMainStrokePaint.setColor(Color.BLACK);
        mMainStrokePaint.setStrokeWidth(STROKE_WIDTH);

        mSubStrokePaint = new Paint();
        mSubStrokePaint.setAntiAlias(true);
        mSubStrokePaint.setStyle(Paint.Style.STROKE);
        mSubStrokePaint.setColor(Color.LTGRAY);
        mSubStrokePaint.setStrokeWidth(SUB_GUIDE_LINE_STROKE_WIDTH);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // TODO move width stuff to constructor
        mWidth = (float) getWidth();
        mHeight = (float) getHeight();
        if (mBitmap == null) {
            mBitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        }
        mCanvas = canvas;

        mCenterX = mWidth / 2;
        mCenterY = mHeight / 2;
        if (mWidth > mHeight){
            mRadius = mHeight * RADIUS_MULTIPLIER;
        }else{
            mRadius = mWidth * RADIUS_MULTIPLIER;
        }

        mPaint.setStrokeWidth(2f);

        // 4 circles
        mPaint.setColor(Color.BLACK);
        final RectF oval = new RectF();
        oval.set(mCenterX - mRadius - 75f,
                mCenterY - mRadius - 75f,
                mCenterX + mRadius + 75f,
                mCenterY + mRadius + 75f);
        canvas.drawArc(oval, 0f, 360f, false, mPaint);

        mPaint.setColor(Color.BLACK);
        oval.set(mCenterX - mRadius + 75f,
                mCenterY - mRadius + 75f,
                mCenterX + mRadius - 75f,
                mCenterY + mRadius - 75f);
        canvas.drawArc(oval, 0f, 360f, false, mPaint);

        mPaint.setColor(Color.BLACK);
        oval.set(mCenterX - mRadius + 150f + 75f,
                mCenterY - mRadius + 150f + 75f,
                mCenterX + mRadius - 150f - 75f,
                mCenterY + mRadius - 150f - 75f);
        canvas.drawArc(oval, 0f, 360f, false, mPaint);

        mPaint.setColor(Color.BLACK);
        oval.set(mCenterX - mRadius + 300f + 75f,
                mCenterY - mRadius + 300f + 75f,
                mCenterX + mRadius - 300f - 75f,
                mCenterY + mRadius - 300f - 75f);
        canvas.drawArc(oval, 0f, 360f, false, mPaint);



        // shapes
        mPaint.setStrokeWidth(150f);

        mPaint.setColor(Color.BLUE);
        oval.set(mCenterX - mRadius,
                mCenterY - mRadius,
                mCenterX + mRadius,
                mCenterY + mRadius);
        canvas.drawArc(oval, 0f, 90f, false, mPaint);

//        mPaint.setStrokeWidth(10f);
//        float angle = 0f;
//        double degrees = new Float(angle).doubleValue();
//        degrees = Math.toRadians(degrees);
//
//        startX = mCenterX;
//        startY = mCenterY;
//        endX = startX + mRadius * (float)Math.cos(degrees);
//        endY = startY + mRadius * (float)Math.sin(degrees);
//        mMainPath.moveTo(startX, startY);
//        mMainPath.lineTo(endX, endY);
//        canvas.drawPath(mMainPath, mMainStrokePaint);

        mPaint.setColor(Color.LTGRAY);
        oval.set(mCenterX - mRadius + 150f,
                mCenterY - mRadius + 150f,
                mCenterX + mRadius - 150f,
                mCenterY + mRadius - 150f);
        canvas.drawArc(oval, 0f, 90f, false, mPaint);

        mPaint.setColor(Color.GREEN);
        oval.set(mCenterX - mRadius + 300f,
                mCenterY - mRadius + 300f,
                mCenterX + mRadius - 300f,
                mCenterY + mRadius - 300f);
        canvas.drawArc(oval, 0f, 90f, false, mPaint);

        if (mColor != 9999999) {
            mPaint.setColor(mColor);
        } else {
            mPaint.setColor(Color.GREEN);
        }
        oval.set(mCenterX - mRadius,
                mCenterY - mRadius,
                mCenterX + mRadius,
                mCenterY + mRadius);
        canvas.drawArc(oval, 90f, 120f, false, mPaint);



    }
}
