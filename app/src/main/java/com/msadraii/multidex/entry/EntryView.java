package com.msadraii.multidex.entry;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.msadraii.multidex.ColorCode;
import com.msadraii.multidex.Entry;
import com.msadraii.multidex.MainActivity;
import com.msadraii.multidex.data.ColorCodeRepository;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by Mostafa on 3/21/2015.
 */
public class EntryView extends View implements View.OnTouchListener {
    // TODO: fill these out
    private static final float RADIUS_MULTIPLIER = 0.4f;
    private static final float STROKE_WIDTH = 6f;
    private static final float ARC_STROKE_WIDTH = 150f;
    private static final float SUB_GUIDE_LINE_STROKE_WIDTH = 4f;
    private static final char SEGMENT_DELIMITER = ',';
    private static final int DESIRED_WIDTH = 400;
    private static final int DESIRED_HEIGHT = 400;

    private static final int NUMBER_RINGS = 3;
    private static final int NUMBER_SLICES = 24;
    private static final float ARC_SLICE = 360 / NUMBER_SLICES;
    private static final float ARC_MULTIPLIER = ARC_SLICE / NUMBER_RINGS;

    private static final float MAX_DIFFERENCE = 75f;

//    private static final float OUTER_RING = 0f;
//    private static final float MIDDLE_RING = 150f;
//    private static final float INNER_RING = 300f;
    private enum Ring {
        OUTER (0f),
        MIDDLE (150f),
        INNER (300f),
        NONE (-1);

        private float offset;
        Ring(float offset) {
            this.offset = offset;
        }
    }


    private ColorCode mColorCode;
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
    private Context mContext;
    private int mPosition;
    private Entry mEntry;
    private HashMap<Integer, Integer> mEntrySegments;
    private Set<Integer> mSegmentKeys;
    private Iterator<Integer> mIterator;
    private Context mAppContext;
    final RectF mBounds = new RectF();
    private Ring mRing;

    public EntryView(Context context) {
        super(context);
        mContext = context;
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

    private Ring getHitRing(float x, float y) {
        if (Math.abs(distanceOfClick(x, y) - mRadius + Ring.OUTER.offset) <= MAX_DIFFERENCE) {
            return Ring.OUTER;
        } else if (Math.abs(distanceOfClick(x, y) - mRadius + Ring.MIDDLE.offset) <= MAX_DIFFERENCE) {
            return Ring.MIDDLE;
        } else if (Math.abs(distanceOfClick(x, y) - mRadius + Ring.INNER.offset) <= MAX_DIFFERENCE) {
            return Ring.INNER;
        }
        return Ring.NONE;
    }

    // TODO: clean this up so it uses less x,y and passes less values through functions
    // TODO: ignore swipe clicks
    @Override
    public boolean onTouch(View view, MotionEvent e) {
        float x = e.getX();
        float y = e.getY();
//        Log.d("touched", e.getX() + " " + e.getY());
        int colorCodeId = ((MainActivity) mContext).getSelectedColorCode();

        // Find the segment that was touched and toggle it on/off
        for (int i = NUMBER_RINGS; i < NUMBER_RINGS * NUMBER_SLICES + 1; i += NUMBER_RINGS) {
            double angle = angleOfClick(x, y);
            if (angle >= (i - NUMBER_RINGS) * ARC_MULTIPLIER && angle < i * ARC_MULTIPLIER) {
//                if (clickWithinRing(x, y, OUTER_RING)) {
//                    toggleSegment(i, colorCodeId);
//                } else if (clickWithinRing(x, y, MIDDLE_RING)) {
//                    toggleSegment(i - 1, colorCodeId);
//                } else if (clickWithinRing(x, y, INNER_RING)) {
//                    toggleSegment(i - 2, colorCodeId);
//                }
                switch (getHitRing(x, y)) {
                    case OUTER: {
                        toggleSegment(i, colorCodeId);
                        break;
                    }
                    case MIDDLE: {
                        toggleSegment(i - 1, colorCodeId);
                        break;
                    }
                    case INNER: {
                        toggleSegment(i - 2, colorCodeId);
                        break;
                    }
                }
            }
        }
        return false;
    }

    private void toggleSegment(int key, int colorCodeId) {
        if (mEntrySegments.containsKey(key)) {
            mEntrySegments.remove(key);
        } else {
            mEntrySegments.put(key, colorCodeId);
        }
        invalidate();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        setOnTouchListener(this);

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


//        mEntry = EntryRepository.getEntryForId(mAppContext, mPosition);
        PagerAdapter adapter = ((MainActivity) mContext).getPagerAdapter();
        Fragment frag = ((MainActivity.HyperdexAdapter) adapter).getRegisteredFragment(
                ((MainActivity) mContext).getViewPager().getCurrentItem());
        mEntry = ((EntryFragment) frag).getEntry();

        // Parse the segments from the Entry
        Type hashMapType = new TypeToken<HashMap<Integer, Integer>>() {}.getType();
        mEntrySegments = new Gson().fromJson(mEntry.getSegments(), hashMapType);

    }

    private void setBounds(RectF rect, float offset) {
        rect.set(mCenterX - mRadius + offset,
                mCenterY - mRadius + offset,
                mCenterX + mRadius - offset,
                mCenterY + mRadius - offset);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.d("draw", "onDraw");

        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(ARC_STROKE_WIDTH);

        // loops through segments, coloring them in
        mSegmentKeys = mEntrySegments.keySet();
        for(mIterator = mSegmentKeys.iterator(); mIterator.hasNext(); ) {
            int segment = mIterator.next();

            mColorCode = ColorCodeRepository.getColorCodeForId(mAppContext,
                    mEntrySegments.get(segment));
            mPaint.setColor(Color.parseColor(mColorCode.getArgb()));

            // loops through the slices, from outer ring to inner ring, and colors in any segments
            for (int j = NUMBER_RINGS; j < NUMBER_RINGS * NUMBER_SLICES + 1; j += NUMBER_RINGS) {
                // TODO: put these if-else in another loop to create dynamic number of rings
                // TODO: const stroke size
                if (segment == j) {
                    // draw outer ring
                    setBounds(mBounds, 0f);
                    canvas.drawArc(mBounds, (j - NUMBER_RINGS) * ARC_MULTIPLIER, ARC_SLICE, false, mPaint);
//                    Log.d("drawArc", "( " + (j - 3) * 10 + "," + j * 10 + " ) at +0");
                    break;
                } else if (segment == j - 1) {
                    // draw middle ring
                    setBounds(mBounds, 150f);
                    canvas.drawArc(mBounds, (j - NUMBER_RINGS) * ARC_MULTIPLIER, ARC_SLICE, false, mPaint);
//                    Log.d("drawArc", "( " + (j - 3) * 10 + "," + j * 10 + " ) at +150");
                    break;
                } else if (segment == j - 2) {
                    // draw inner ring
                    setBounds(mBounds, 300f);
                    canvas.drawArc(mBounds, (j - NUMBER_RINGS) * ARC_MULTIPLIER, ARC_SLICE, false, mPaint);
//                    Log.d("drawArc", "( " + (j - 3) * 10 + "," + j * 10 + " ) at +300");
                    break;
                }
            }
        }

        // draw 4 guide circles from outermost to innermost
        mPaint.setStrokeWidth(2f);
        mPaint.setColor(Color.BLACK);

        setBounds(mBounds, -75f);
        canvas.drawArc(mBounds, 0f, 360f, false, mPaint);

        setBounds(mBounds, 75f);
        canvas.drawArc(mBounds, 0f, 360f, false, mPaint);

        setBounds(mBounds, 150f + 75f);
        canvas.drawArc(mBounds, 0f, 360f, false, mPaint);

        setBounds(mBounds, 300f + 75f);
        canvas.drawArc(mBounds, 0f, 360f, false, mPaint);
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

    // TODO: on not pause, update fragment Entry segments
    @Override
    protected Parcelable onSaveInstanceState() {
        return super.onSaveInstanceState();
    }
}
