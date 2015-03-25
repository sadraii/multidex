/*
 * Copyright 2015, Mostafa Sadraii
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
    private static final float RADIUS_MULTIPLIER = 0.4f;
    private static final int DESIRED_WIDTH = 400;
    private static final int DESIRED_HEIGHT = 400;

    private static final float ARC_STROKE_WIDTH = 150f;
    private static final float GUIDE_CIRCLE_STROKE_WIDTH = 2f;

    private static final int NUMBER_RINGS = 3;
    private static final int NUMBER_SLICES = 24;
    private static final float ARC_SLICE = 360 / NUMBER_SLICES;
    private static final float ARC_MULTIPLIER = ARC_SLICE / NUMBER_RINGS;

    private static final float MAX_DIFFERENCE = 75f;
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

    private Context mContext;
    private Context mAppContext;

    private Paint mPaint;
    private float mCenterX;
    private float mCenterY;
    private float mRadius;
    final RectF mBounds = new RectF();

    private int mPosition;

    private ColorCode mColorCode;
    private Entry mEntry;
    private HashMap<Integer, Integer> mEntrySegments;
    private Set<Integer> mSegmentKeys;
    private Iterator<Integer> mIterator;

    public EntryView(Context context) {
        super(context);
        mContext = context;
    }

    public void setPosition(Context appContext, int position) {
        mAppContext = appContext;
        mPosition = position;
    }

    /**
     * Calculate angle to center point.
     * @param x
     * @param y
     * @return
     */
    private double clickAngle(float x, float y) {
        double angle = (Math.atan2(y - mCenterY, x - mCenterX) * 180 / Math.PI) % 360;
        if (angle < 0) {
            angle += 360;
        }
        return angle;
    }

    /**
     * Calculate distance to center point using Pythagorean theorem.
     * @param x
     * @param y
     * @return
     */
    private double clickDistance(float x, float y) {
        return Math.sqrt(Math.pow(x - mCenterX, 2) + Math.pow(y - mCenterY, 2));
    }

    private Ring getHitRing(float x, float y) {
        if (Math.abs(clickDistance(x, y) - mRadius + Ring.OUTER.offset) <= MAX_DIFFERENCE) {
            return Ring.OUTER;
        } else if (Math.abs(clickDistance(x, y) - mRadius + Ring.MIDDLE.offset) <= MAX_DIFFERENCE) {
            return Ring.MIDDLE;
        } else if (Math.abs(clickDistance(x, y) - mRadius + Ring.INNER.offset) <= MAX_DIFFERENCE) {
            return Ring.INNER;
        }
        return Ring.NONE;
    }

    // TODO: ignore swipe clicks
    @Override
    public boolean onTouch(View view, MotionEvent e) {
        float x = e.getX();
        float y = e.getY();
//        Log.d("touched", e.getX() + " " + e.getY());
        int colorCodeId = ((MainActivity) mContext).getSelectedColorCode();

        // Find the segment that was touched and toggle it on/off
        for (int i = NUMBER_RINGS; i < NUMBER_RINGS * NUMBER_SLICES + 1; i += NUMBER_RINGS) {
            double angle = clickAngle(x, y);
            if (angle >= (i - NUMBER_RINGS) * ARC_MULTIPLIER && angle < i * ARC_MULTIPLIER) {
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
                    default:
                        // No rings were clicked. Ignore case.
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

        // Get Entry from fragment
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

            // Loops through the slices, from outer ring to inner ring, and draws any segments
            for (int j = NUMBER_RINGS; j < NUMBER_RINGS * NUMBER_SLICES + 1; j += NUMBER_RINGS) {
                if (segment == j) {
                    // draw outer ring
                    setBounds(mBounds, Ring.OUTER.offset);
                    canvas.drawArc(mBounds, (j - NUMBER_RINGS) * ARC_MULTIPLIER, ARC_SLICE, false,
                            mPaint);
                    break;
                } else if (segment == j - 1) {
                    // draw middle ring
                    setBounds(mBounds, Ring.MIDDLE.offset);
                    canvas.drawArc(mBounds, (j - NUMBER_RINGS) * ARC_MULTIPLIER, ARC_SLICE, false,
                            mPaint);
                    break;
                } else if (segment == j - 2) {
                    // draw inner ring
                    setBounds(mBounds, Ring.INNER.offset);
                    canvas.drawArc(mBounds, (j - NUMBER_RINGS) * ARC_MULTIPLIER, ARC_SLICE, false,
                            mPaint);
                    break;
                }
            }
        }

        // draw 4 guide circles from outermost to innermost
        mPaint.setStrokeWidth(GUIDE_CIRCLE_STROKE_WIDTH);
        mPaint.setColor(Color.BLACK);

        setBounds(mBounds, Ring.OUTER.offset - MAX_DIFFERENCE);
        canvas.drawArc(mBounds, 0f, 360f, false, mPaint);

        setBounds(mBounds, Ring.OUTER.offset + MAX_DIFFERENCE);
        canvas.drawArc(mBounds, 0f, 360f, false, mPaint);

        setBounds(mBounds, Ring.MIDDLE.offset + MAX_DIFFERENCE);
        canvas.drawArc(mBounds, 0f, 360f, false, mPaint);

        setBounds(mBounds, Ring.INNER.offset + MAX_DIFFERENCE);
        canvas.drawArc(mBounds, 0f, 360f, false, mPaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        float width;
        float height;
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            width = Math.min(DESIRED_WIDTH, widthSize);
        } else {
            width = DESIRED_WIDTH;
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            height = Math.min(DESIRED_HEIGHT, heightSize);
        } else {
            height = DESIRED_HEIGHT;
        }

        mCenterX = width / 2;
        mCenterY = height / 2;
        mRadius = (width > height)
                ? height * RADIUS_MULTIPLIER
                : width * RADIUS_MULTIPLIER;

        setMeasuredDimension((int) width, (int) height);
    }

    // TODO: on not pause, update fragment Entry segments
    @Override
    protected Parcelable onSaveInstanceState() {
        return super.onSaveInstanceState();
    }
}