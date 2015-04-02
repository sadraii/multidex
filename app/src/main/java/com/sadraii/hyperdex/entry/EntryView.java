/*
 * Copyright 2015 Mostafa Sadraii
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

package com.sadraii.hyperdex.entry;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sadraii.hyperdex.ColorCode;
import com.sadraii.hyperdex.Entry;
import com.sadraii.hyperdex.MainActivity;
import com.sadraii.hyperdex.data.ColorCodeRepository;
import com.sadraii.hyperdex.data.EntryRepository;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Iterator;

/**
 * TODO: editText
 */
public class EntryView extends View implements View.OnTouchListener {

    private static final String LOG_TAG = EntryView.class.getSimpleName();

    private static final float RADIUS_MULTIPLIER = 0.4f;
    private static final int DESIRED_WIDTH = 400;
    private static final int DESIRED_HEIGHT = 400;

    private static final float RING_STROKE_WIDTH = 150f;
    private static final float MAIN_GUIDELINE_STROKE_WIDTH = 6f;
    private static final float SUB_GUIDELINE_STROKE_WIDTH = 2f;
    private static final float CIRCLE_GUIDELINE_STROKE_WIDTH = 2f;

    private static final float SWIPE_MAX_DIFF = 100f;

    private static final int NUMBER_RINGS = 3;
    private static final int NUMBER_SLICES = 24;
    private static final float SLICE = 360 / NUMBER_SLICES;
    private static final float SLICE_MULTIPLIER = SLICE / NUMBER_RINGS;
    private static final float SLICE_MAX_DIFF = RING_STROKE_WIDTH / 2;
    private enum Ring {
        OUTER (0f),
        MIDDLE (150f),
        INNER (300f),
        NONE (-1f);

        private float offset;
        Ring(float offset) {
            this.offset = offset;
        }
    }

    private Context mContext;
    private Context mAppContext;

    private Bitmap mPathBitmap;
    private Paint mPaint;
    private Path mMainGuidelinePath;
    private Path mSubGuidelinePath;
    private PointF mCenter;
    private Point mViewDimensions;
    private float mRadius;
    private double mOppositeAngle;
    private PointF mStartPath;
    private PointF mEndPath;
    private final RectF mBounds = new RectF();
    private float mCenterOffset;

    private int mPosition;

    private float mInitialClickX;
    private float mInitialClickY;

    private Entry mEntry;
    private long mColorCodeTag;
    private String mColorValue;
    private HashMap<Integer, Long> mEntrySegments;
    private Iterator<Integer> mIterator;

    public EntryView(Context context) {
        super(context);
        mContext = context;
    }

    /**
     * The position passed from the fragment associates the EntryView with the Entry object.
     *
     * @param appContext
     * @param position
     */
    public void setPosition(Context appContext, int position) {
        mAppContext = appContext;
        mPosition = position;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        setOnTouchListener(this);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);

        mMainGuidelinePath = new Path();
        mSubGuidelinePath = new Path();

        mViewDimensions = new Point();
        mCenter = new PointF();
        mStartPath = new PointF();
        mEndPath = new PointF();

        mEntry = EntryRepository.getEntryForId(mAppContext, mPosition);
        // Parse the segments from the Entry
        if (mEntry.hasSegments()) {
            Type hashMapType = new TypeToken<HashMap<Integer, Long>>() {}.getType();
            mEntrySegments = new Gson().fromJson(mEntry.getSegments(), hashMapType);
        } else {
            mEntrySegments = new HashMap<>();
        }
    }

    /**
     * Handles clicks and ignores swipes.
     *
     * @param view
     * @param e
     * @return
     */
    @Override
    public boolean onTouch(View view, MotionEvent e) {
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                mInitialClickX = e.getX();
                mInitialClickY = e.getY();
                break;
            }
            case MotionEvent.ACTION_UP: {
                final float dX = Math.abs(mInitialClickX - e.getX());
                final float dY = Math.abs(mInitialClickY - e.getY());

                if (dX < SWIPE_MAX_DIFF && dY < SWIPE_MAX_DIFF) {
                    findSegmentAndToggle();
                }
                break;
            }
        }
        return true;
    }

    /**
     * Finds the segment that was touched and toggles it on/off
     */
    private void findSegmentAndToggle() {
        int selectedColorId = ((MainActivity) mContext).getSelectedColorCodeId();
        ColorCode colorCode = ColorCodeRepository.getColorCode(mAppContext, selectedColorId);
        long colorTag = colorCode.getTag();

        for (int i = NUMBER_RINGS; i < NUMBER_RINGS * NUMBER_SLICES + 1; i += NUMBER_RINGS) {
            double angle = clickAngle(mInitialClickX, mInitialClickY);

            if (angle >= (i - NUMBER_RINGS) * SLICE_MULTIPLIER && angle < i * SLICE_MULTIPLIER) {
                switch (ringClicked(mInitialClickX, mInitialClickY)) {
                    case OUTER: {
                        toggleSegment(i, colorTag);
                        break;
                    }
                    case MIDDLE: {
                        toggleSegment(i - 1, colorTag);
                        break;
                    }
                    case INNER: {
                        toggleSegment(i - 2, colorTag);
                        break;
                    }
                    default:
                        // No rings were clicked. Ignore case.
                }
            }
        }
    }

    private void toggleSegment(int key, long colorTag) {
        if (mEntrySegments.containsKey(key)) {
            mEntrySegments.remove(key);
        } else {
            mEntrySegments.put(key, colorTag);
        }
        invalidate();
    }

    /**
     * Returns which ring was clicked using a Ring enum.
     *
     * @param x
     * @param y
     * @return
     */
    private Ring ringClicked(float x, float y) {
        if (Math.abs(clickDistance(x, y) - mRadius + Ring.OUTER.offset) <= SLICE_MAX_DIFF) {
            return Ring.OUTER;
        } else if (Math.abs(clickDistance(x, y) - mRadius + Ring.MIDDLE.offset) <= SLICE_MAX_DIFF) {
            return Ring.MIDDLE;
        } else if (Math.abs(clickDistance(x, y) - mRadius + Ring.INNER.offset) <= SLICE_MAX_DIFF) {
            return Ring.INNER;
        }
        return Ring.NONE;
    }

    /**
     * Returns the click angle from center point.
     *
     * @param x
     * @param y
     * @return
     */
    private double clickAngle(float x, float y) {
        double angle = (Math.atan2(y - mCenter.y, x - mCenter.x) * 180 / Math.PI);
        if (angle < 0) {
            angle += 360;
        }
        return angle;
    }

    /**
     * Returns the distance to center point using Pythagorean theorem.
     *
     * @param x
     * @param y
     * @return
     */
    private double clickDistance(float x, float y) {
        return Math.sqrt(Math.pow(x - mCenter.x, 2) + Math.pow(y - mCenter.y, 2));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(RING_STROKE_WIDTH);

        // Loop through segments, coloring them in
        mIterator = mEntrySegments.keySet().iterator();
        while (mIterator.hasNext()) {
            int segment = mIterator.next();
            // Get the segment's color
            mColorCodeTag = mEntrySegments.get(segment);
            mColorValue = ColorCodeRepository.getValueForTag(mAppContext, mColorCodeTag);

            if (mColorValue != null) {
                mPaint.setColor(Color.parseColor(mColorValue));
                // Loop through the slices, from outer ring to inner ring, and draw any segments
                for (int j = NUMBER_RINGS; j < NUMBER_RINGS * NUMBER_SLICES + 1; j += NUMBER_RINGS) {
                    if (segment == j) {
                        setBounds(Ring.OUTER.offset);
                        canvas.drawArc(mBounds, (j - NUMBER_RINGS) * SLICE_MULTIPLIER, SLICE, false,
                                mPaint);
                        break;
                    } else if (segment == j - 1) {
                        setBounds(Ring.MIDDLE.offset);
                        canvas.drawArc(mBounds, (j - NUMBER_RINGS) * SLICE_MULTIPLIER, SLICE, false,
                                mPaint);
                        break;
                    } else if (segment == j - 2) {
                        setBounds(Ring.INNER.offset);
                        canvas.drawArc(mBounds, (j - NUMBER_RINGS) * SLICE_MULTIPLIER, SLICE, false,
                                mPaint);
                        break;
                    }
                }
            } else {
                // Respective ColorCode has been deleted, remove segment from Entry
                mIterator.remove();
            }
        }

        // Cache the guidelines in a bitmap and overlay them on top
        if (mPathBitmap == null) {
            mPathBitmap = Bitmap.createBitmap(mViewDimensions.x, mViewDimensions.y,
                    Bitmap.Config.ARGB_8888);
            Canvas linesCanvas = new Canvas(mPathBitmap);

            // Draw 4 guide circles from outermost to innermost
            mPaint.setStrokeWidth(CIRCLE_GUIDELINE_STROKE_WIDTH);
            mPaint.setColor(Color.BLACK);

            setBounds(Ring.OUTER.offset - SLICE_MAX_DIFF);
            linesCanvas.drawArc(mBounds, 0f, 360f, false, mPaint);

            setBounds(Ring.OUTER.offset + SLICE_MAX_DIFF);
            linesCanvas.drawArc(mBounds, 0f, 360f, false, mPaint);

            setBounds(Ring.MIDDLE.offset + SLICE_MAX_DIFF);
            linesCanvas.drawArc(mBounds, 0f, 360f, false, mPaint);

            setBounds(Ring.INNER.offset + SLICE_MAX_DIFF);
            linesCanvas.drawArc(mBounds, 0f, 360f, false, mPaint);

            // Draw the guidelines at straight angles
            mPaint.setColor(Color.BLACK);
            mPaint.setStrokeWidth(MAIN_GUIDELINE_STROKE_WIDTH);
            drawGuidelines(linesCanvas, mMainGuidelinePath, 0d);
            drawGuidelines(linesCanvas, mMainGuidelinePath, 90d);

            mPaint.setStrokeWidth(SUB_GUIDELINE_STROKE_WIDTH);
            drawGuidelines(linesCanvas, mSubGuidelinePath, 15d);
            drawGuidelines(linesCanvas, mSubGuidelinePath, 30d);
            drawGuidelines(linesCanvas, mSubGuidelinePath, 45d);
            drawGuidelines(linesCanvas, mSubGuidelinePath, 60d);
            drawGuidelines(linesCanvas, mSubGuidelinePath, 75d);
            drawGuidelines(linesCanvas, mSubGuidelinePath, 105d);
            drawGuidelines(linesCanvas, mSubGuidelinePath, 120d);
            drawGuidelines(linesCanvas, mSubGuidelinePath, 135d);
            drawGuidelines(linesCanvas, mSubGuidelinePath, 150d);
            drawGuidelines(linesCanvas, mSubGuidelinePath, 165d);
        }
        canvas.drawBitmap(mPathBitmap, 0, 0, mPaint);
    }

    /**
     * Draws guidelines at the given angle and at the straight angle of the given angle.
     *
     * @param canvas
     * @param path
     * @param angle
     */
    private void drawGuidelines(Canvas canvas, Path path, double angle) {
        mOppositeAngle = (angle + 180d) % 360;
        angle = Math.toRadians(angle);
        mOppositeAngle = Math.toRadians(mOppositeAngle);
        drawSingleGuideline(canvas, path, angle);
        drawSingleGuideline(canvas, path, mOppositeAngle);
    }

    /**
     * Draws a single guideline from the center offset (inner circular guideline) to the outer
     * edge (outer circular guideline).
     *
     * @param canvas
     * @param path
     * @param angle
     */
    private void drawSingleGuideline(Canvas canvas, Path path, double angle) {
        mStartPath.x = mCenter.x + mCenterOffset * (float) Math.cos(angle);
        mStartPath.y = mCenter.y + mCenterOffset * (float) Math.sin(angle);
        mEndPath.x = mCenter.x + (mRadius + SLICE_MAX_DIFF) * (float) Math.cos(angle);
        mEndPath.y = mCenter.y + (mRadius + SLICE_MAX_DIFF) * (float) Math.sin(angle);
        path.moveTo(mStartPath.x, mStartPath.y);
        path.lineTo(mEndPath.x, mEndPath.y);
        canvas.drawPath(path, mPaint);
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

        mCenter.x = width / 2;
        mCenter.y = height / 2;
        mRadius = (width > height)
                ? height * RADIUS_MULTIPLIER
                : width * RADIUS_MULTIPLIER;

        // Calculate the empty space in the middle
        float rightSide = mCenter.x + mRadius - Ring.INNER.offset - SLICE_MAX_DIFF;
        float leftSide = mCenter.x - mRadius + Ring.INNER.offset + SLICE_MAX_DIFF;
        mCenterOffset = (rightSide - leftSide) / 2;

        // For creating the guidelines bitmap
        mViewDimensions.x = (int) width;
        mViewDimensions.y = (int) height;

        setMeasuredDimension((int) width, (int) height);
    }

    /**
     * Called by the associated fragment in order to save the {@link Entry} to persistent storage.
     *
     * @return  The {@link Entry} representing this view.
     */
    public Entry getEntry() {
        Type hashMapType = new TypeToken<HashMap<Integer, Long>>() {}.getType();
        mEntry.setSegments(new Gson().toJson(mEntrySegments, hashMapType));
        return mEntry;
    }

    /**
     * Used for drawing circular guidelines
     *
     * @param offset
     */
    private void setBounds(float offset) {
        mBounds.set(mCenter.x - mRadius + offset,
                mCenter.y - mRadius + offset,
                mCenter.x + mRadius - offset,
                mCenter.y + mRadius - offset);
    }
}
