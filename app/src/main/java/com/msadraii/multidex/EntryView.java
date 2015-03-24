package com.msadraii.multidex;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.msadraii.multidex.data.ColorCodeRepository;
import com.msadraii.multidex.data.EntryRepository;
import com.msadraii.multidex.data.EntrySegments;

import java.util.ArrayList;

/**
 * Created by Mostafa on 3/21/2015.
 */
public class EntryView extends View {
    // TODO fill these out
    private static final float RADIUS_MULTIPLIER = 0.4f;
    private static final float STROKE_WIDTH = 6f;
    private static final float SUB_GUIDE_LINE_STROKE_WIDTH = 4f;
    private static final char SEGMENT_DELIMITER = ',';
    private static final int DESIRED_WIDTH = 400;
    private static final int DESIRED_HEIGHT = 400;
    private static final int NUMBER_RINGS = 3;
    private static final int NUMBER_SLICES = 24;
    private static final float ARC_SLICE = 360 / NUMBER_SLICES;


    private int mColor = 9999999;
//    private Bitmap mBitmap;
//    private Bitmap subBitmap;
//    private Canvas mCanvas;
//    private Canvas mTempCanvas;
    private ColorCode mColorCode;
    private Path mMainPath;
    private Path mSubPath;
    private Paint mPaint;
    private Paint mPaint2;
    private Paint mPaint3;
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
//    private float endX;
//    private float endY;
    private int mPosition;
    private Entry mEntry;
    private EntrySegments mEntrySegments;
    private Context mAppContext;
    private ArrayList<Integer> mSegments;
    final RectF mCircle = new RectF();

    public EntryView(Context context) {
        super(context);
//        mTempCanvas = new Canvas();
    }

    public EntryView(Context context, int color) {
        super(context);
        mColor = color;
    }

//    public void setColor(int color) {
//        mColor = color;
//    }

    public void setPosition(Context appContext, int position) {
        mAppContext = appContext;
        mPosition = position;
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

        // Parse the segments from the Entry
        mEntry = EntryRepository.getEntryForId(mAppContext, mPosition);
        Gson gson = new Gson();
        mEntrySegments = gson.fromJson(mEntry.getSegments(), EntrySegments.class);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.d("draw", "onDraw");

        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(150f);

        // loops through segments, coloring them in
        for (int i = 0; i < mEntrySegments.size(); i++) {
            int segment = mEntrySegments.getSegmentIds().get(i);

            mColorCode = ColorCodeRepository.getColorCodeForId(mAppContext,
                    mEntrySegments.getColorCodeId(i));
            mPaint.setColor(Color.parseColor(mColorCode.getArgb()));

            // loops through the slices, from outer ring to inner ring, and colors in any segments
            for (int j = NUMBER_RINGS; j < NUMBER_RINGS * NUMBER_SLICES; j += NUMBER_RINGS) {
                // TODO put these if-else in another loop to create dynamic number of rings
                if (segment == j) {

                    // draw outer ring
                    mCircle.set(mCenterX - mRadius,
                            mCenterY - mRadius,
                            mCenterX + mRadius,
                            mCenterY + mRadius);
                    canvas.drawArc(mCircle, (j - NUMBER_RINGS) * 10, ARC_SLICE, false, mPaint);
//                    Log.d("drawArc", "( " + (j - 3) * 10 + "," + j * 10 + " ) at +0");
                    break;
                } else if (segment == j - 1) {

                    // draw middle ring
                    mCircle.set(mCenterX - mRadius + 150f,
                            mCenterY - mRadius + 150f,
                            mCenterX + mRadius - 150f,
                            mCenterY + mRadius - 150f);
                    canvas.drawArc(mCircle, (j - NUMBER_RINGS) * 10, ARC_SLICE, false, mPaint);
//                    Log.d("drawArc", "( " + (j - 3) * 10 + "," + j * 10 + " ) at +150");
                    break;
                } else if (segment == j - 2) {

                    // draw inner ring
                    mCircle.set(mCenterX - mRadius + 300f,
                            mCenterY - mRadius + 300f,
                            mCenterX + mRadius - 300f,
                            mCenterY + mRadius - 300f);
                    canvas.drawArc(mCircle, (j - NUMBER_RINGS) * 10, ARC_SLICE, false, mPaint);
//                    Log.d("drawArc", "( " + (j - 3) * 10 + "," + j * 10 + " ) at +300");
                    break;
                }
            }
        }

        // draw 4 guide circles
        mPaint.setStrokeWidth(2f);
        mPaint.setColor(Color.BLACK);

        mCircle.set(mCenterX - mRadius - 75f,
                mCenterY - mRadius - 75f,
                mCenterX + mRadius + 75f,
                mCenterY + mRadius + 75f);
        canvas.drawArc(mCircle, 0f, 360f, false, mPaint);

        mPaint.setColor(Color.BLACK);
        mCircle.set(mCenterX - mRadius + 75f,
                mCenterY - mRadius + 75f,
                mCenterX + mRadius - 75f,
                mCenterY + mRadius - 75f);
        canvas.drawArc(mCircle, 0f, 360f, false, mPaint);

        mPaint.setColor(Color.BLACK);
        mCircle.set(mCenterX - mRadius + 150f + 75f,
                mCenterY - mRadius + 150f + 75f,
                mCenterX + mRadius - 150f - 75f,
                mCenterY + mRadius - 150f - 75f);
        canvas.drawArc(mCircle, 0f, 360f, false, mPaint);

        mPaint.setColor(Color.BLACK);
        mCircle.set(mCenterX - mRadius + 300f + 75f,
                mCenterY - mRadius + 300f + 75f,
                mCenterX + mRadius - 300f - 75f,
                mCenterY + mRadius - 300f - 75f);
        canvas.drawArc(mCircle, 0f, 360f, false, mPaint);
    }

    // TODO fix center/width at bottom
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        if (widthMode == MeasureSpec.EXACTLY) {
            mWidth = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            mWidth = Math.min(DESIRED_WIDTH, widthSize);
        } else {
            mWidth = DESIRED_WIDTH;
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            mHeight = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            mHeight = Math.min(DESIRED_HEIGHT, heightSize);
        } else {
            mHeight = DESIRED_HEIGHT;
        }

        mCenterX = mWidth / 2;
        mCenterY = mHeight / 2;
        if (mWidth > mHeight){
            mRadius = mHeight * RADIUS_MULTIPLIER;
        }else{
            mRadius = mWidth * RADIUS_MULTIPLIER;
        }

        setMeasuredDimension((int) mWidth, (int) mHeight);
    }
}
