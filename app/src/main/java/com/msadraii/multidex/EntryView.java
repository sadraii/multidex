package com.msadraii.multidex;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.google.gson.Gson;
import com.msadraii.multidex.data.ColorCodeRepository;
import com.msadraii.multidex.data.EntryRepository;
import com.msadraii.multidex.data.EntrySegments;

import java.util.ArrayList;

/**
 * Created by Mostafa on 3/21/2015.
 */
public class EntryView extends View implements View.OnTouchListener {
    // TODO: fill these out
    private static final float RADIUS_MULTIPLIER = 0.4f;
    private static final float STROKE_WIDTH = 6f;
    private static final float SUB_GUIDE_LINE_STROKE_WIDTH = 4f;
    private static final char SEGMENT_DELIMITER = ',';
    private static final int DESIRED_WIDTH = 400;
    private static final int DESIRED_HEIGHT = 400;
    private static final int NUMBER_RINGS = 3;
    private static final int NUMBER_SLICES = 24;
    private static final float ARC_SLICE = 360 / NUMBER_SLICES;
    private static final float ARC_MULTIPLIER = ARC_SLICE / NUMBER_RINGS;
    private static final float MAX_DIFFERENCE = 75f;
    private static final float OUTER_RING = 0f;
    private static final float MIDDLE_RING = 150f;
    private static final float INNER_RING = 300f;


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
    }

    public EntryView(Context context, int color) {
        super(context);
        mColor = color;
    }

    public void setPosition(Context appContext, int position) {
        mAppContext = appContext;
        mPosition = position;
    }

    private double angleOfClick(float x, float y) {
        double angle = (Math.atan2(y - mCenterY, x - mCenterX) * 180 / Math.PI) % 360;
        if (angle < 0) {
            angle += 360;
        }
        return angle;
    }

    private double distanceOfClick(float x, float y) {
        return Math.sqrt(Math.pow(x - mCenterX, 2) + Math.pow(y - mCenterY, 2));
    }

    private boolean clickWithinRing(float x, float y, float ringWidth) {
        return (Math.abs(distanceOfClick(x, y) - mRadius + ringWidth) <= MAX_DIFFERENCE);
    }

    // TODO: clean this up so it uses less x,y and passes less values through functions
    @Override
    public boolean onTouch(View view, MotionEvent e) {
        float x = e.getX();
        float y = e.getY();
//        Log.d("touched", e.getX() + " " + e.getY());

//        for (int i = 0; i < 360; i += ARC_SLICE) {
//            double angle = angleOfClick(x, y);
//            if (clickWithinRing(x, y, OUTER_RING)) {
//                Toast.makeText(getContext(), "Outer + " + angleOfClick(x, y), Toast.LENGTH_SHORT).show();
//            } else if (clickWithinRing(x, y, MIDDLE_RING)) {
//                Toast.makeText(getContext(), "Middle + " + angleOfClick(x, y), Toast.LENGTH_SHORT).show();
//            } else if (clickWithinRing(x, y, INNER_RING)) {
//                Toast.makeText(getContext(), "Inner + " + angleOfClick(x, y), Toast.LENGTH_SHORT).show();
//            }
//        }

        for (int i = 3; i < NUMBER_RINGS * NUMBER_SLICES + 1; i += NUMBER_RINGS) {
            double angle = angleOfClick(x, y);
            if (angle >= (i - NUMBER_RINGS) * ARC_MULTIPLIER && angle < i * ARC_MULTIPLIER) {
                if (clickWithinRing(x, y, OUTER_RING)) {
//                    Toast.makeText(getContext(), "" + i, Toast.LENGTH_SHORT).show();
                    if (mEntrySegments.hasSegment(i)) {
                        mEntrySegments.removeSegment(i);
//                        Toast.makeText(getContext(), i + " exists", Toast.LENGTH_SHORT).show();
                    } else {
                        // TODO: remove hardcoded 0
                        mEntrySegments.addSegment(i, 0);
//                        Toast.makeText(getContext(), i + " NOT exists", Toast.LENGTH_SHORT).show();
                    }
                    invalidate();
                } else if (clickWithinRing(x, y, MIDDLE_RING)) {
//                    Toast.makeText(getContext(), "" + (i - 1), Toast.LENGTH_SHORT).show();
                    if (mEntrySegments.hasSegment(i - 1)) {
                        mEntrySegments.removeSegment(i - 1);
//                        Toast.makeText(getContext(), i + " exists", Toast.LENGTH_SHORT).show();
                    } else {
                        // TODO: remove hardcoded 0
                        mEntrySegments.addSegment(i - 1, 4);
//                        Toast.makeText(getContext(), i + " NOT exists", Toast.LENGTH_SHORT).show();
                    }
                    invalidate();
                } else if (clickWithinRing(x, y, INNER_RING)) {
//                    Toast.makeText(getContext(), "" + (i - 2), Toast.LENGTH_SHORT).show();
                    if (mEntrySegments.hasSegment(i - 2)) {
                        mEntrySegments.removeSegment(i - 2);
//                        Toast.makeText(getContext(), i - 2 + " exists", Toast.LENGTH_SHORT).show();
                    } else {
                        // TODO: remove hardcoded 0
                        mEntrySegments.addSegment(i - 2, 3);
//                        Toast.makeText(getContext(), i + " NOT exists", Toast.LENGTH_SHORT).show();
                    }
                    invalidate();
                }
            }
        }



        return false;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        setOnTouchListener(this);

        mMainPath = new Path();
        mSubPath = new Path();

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);

        mPaint2 = new Paint();
        mPaint2.setAntiAlias(true);
        mPaint2.setStyle(Paint.Style.STROKE);

        mPaint3 = new Paint();
        mPaint3.setAntiAlias(true);
        mPaint3.setStyle(Paint.Style.STROKE);

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
            for (int j = NUMBER_RINGS; j < NUMBER_RINGS * NUMBER_SLICES + 1; j += NUMBER_RINGS) {
                // TODO: put these if-else in another loop to create dynamic number of rings
                // TODO: const stroke size
                if (segment == j) {
                    // draw outer ring
                    mCircle.set(mCenterX - mRadius,
                            mCenterY - mRadius,
                            mCenterX + mRadius,
                            mCenterY + mRadius);
                    canvas.drawArc(mCircle, (j - NUMBER_RINGS) * ARC_MULTIPLIER, ARC_SLICE, false, mPaint);
//                    Log.d("drawArc", "( " + (j - 3) * 10 + "," + j * 10 + " ) at +0");
                    break;
                } else if (segment == j - 1) {
                    // draw middle ring
                    mCircle.set(mCenterX - mRadius + 150f,
                            mCenterY - mRadius + 150f,
                            mCenterX + mRadius - 150f,
                            mCenterY + mRadius - 150f);
                    canvas.drawArc(mCircle, (j - NUMBER_RINGS) * ARC_MULTIPLIER, ARC_SLICE, false, mPaint);
//                    Log.d("drawArc", "( " + (j - 3) * 10 + "," + j * 10 + " ) at +150");
                    break;
                } else if (segment == j - 2) {
                    // draw inner ring
                    mCircle.set(mCenterX - mRadius + 300f,
                            mCenterY - mRadius + 300f,
                            mCenterX + mRadius - 300f,
                            mCenterY + mRadius - 300f);
                    canvas.drawArc(mCircle, (j - NUMBER_RINGS) * ARC_MULTIPLIER, ARC_SLICE, false, mPaint);
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

        // testing bounding boxes
//        mPaint.setColor(Color.BLUE);
//        mPaint.setPathEffect(new DashPathEffect(new float[]{10, 10, 10, 10}, 0));
//        canvas.drawRect(mCenterX - mRadius,
//                mCenterY - mRadius,
//                mCenterX + mRadius,
//                mCenterY + mRadius, mPaint);
//        canvas.drawRect(mCenterX - mRadius - 50,
//                mCenterY - mRadius - 50,
//                mCenterX + mRadius + 50,
//                mCenterY + mRadius + 50, mPaint);
//        canvas.drawRect(mCenterX - mRadius + 50,
//                mCenterY - mRadius + 50,
//                mCenterX + mRadius - 50,
//                mCenterY + mRadius - 50, mPaint);
//
//        canvas.drawRect(mCenterX - mRadius + 150,
//                mCenterY - mRadius + 150,
//                mCenterX + mRadius - 150,
//                mCenterY + mRadius - 150, mPaint);
//        canvas.drawRect(mCenterX - mRadius - 50 + 150,
//                mCenterY - mRadius - 50 + 150,
//                mCenterX + mRadius + 50 - 150,
//                mCenterY + mRadius + 50 - 150, mPaint);
//        canvas.drawRect(mCenterX - mRadius + 50 + 150,
//                mCenterY - mRadius + 50 + 150,
//                mCenterX + mRadius - 50 - 150,
//                mCenterY + mRadius - 50 - 150, mPaint);
    }

    // TODO: fix center/width at bottom
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
