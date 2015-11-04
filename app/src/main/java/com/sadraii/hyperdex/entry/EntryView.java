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

    private static final float SWIPE_MAX_DIFF = 100f;

    private static final float RING_STROKE_WIDTH = 150f;
    private static final float MAIN_GUIDELINE_STROKE_WIDTH = 6f;
    private static final float SUB_GUIDELINE_STROKE_WIDTH = 2f;
    private static final float CIRCLE_GUIDELINE_STROKE_WIDTH = 2f;

    private static final float HOUR_RADIUS = 25f;
    private static final int TEXT_SIZE = 32; // TODO: make these dynamic in onMeasure for smaller screens

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

    private final Context mContext;
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
    private int mHourMarkFillColor;
    private int mHourMarkTextColor;

    private float mInitialClickX;
    private float mInitialClickY;

    private int mPosition;
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
        mPaint.setTextSize(TEXT_SIZE);
        mPaint.setTextAlign(Paint.Align.CENTER);

        mMainGuidelinePath = new Path();
        mSubGuidelinePath = new Path();

        mViewDimensions = new Point();
        mCenter = new PointF();
        mStartPath = new PointF();
        mEndPath = new PointF();

        // Parse the segments from the Entry
        mEntry = EntryRepository.getEntryForId(mAppContext, mPosition);
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
     * Finds the segment that was touched and toggles it on/off. It starts counting up from 1 by
     * slice first, then by layer. So for the first slice (say between angles 0 and 30), if there
     * are 3 rings, it would count the inner segment as 1, then the middle as 2, and lastly the
     * outer as 3. Then for the second slice (say between angles 30 and 60), it would be 4, 5, and
     * 6.
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

    // TODO: redo the logic
    private void toggleSegment(int key, long colorTag) {
        if (mEntrySegments.containsKey(key)) {
            if (mEntrySegments.get(key) == colorTag) {
                mEntrySegments.remove(key);
            } else {
                mEntrySegments.put(key, colorTag);
            }
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
            // Loop through the slices, from outer ring to inner ring, and draw any segments
            if (mColorValue != null) {
                mPaint.setColor(Color.parseColor(mColorValue));
                for (int j = NUMBER_RINGS; j < NUMBER_RINGS * NUMBER_SLICES + 1; j += NUMBER_RINGS) {
                    if (segment == j) {
                        setBoundsFromCenter(Ring.OUTER.offset);
                        canvas.drawArc(mBounds, (j - NUMBER_RINGS) * SLICE_MULTIPLIER, SLICE, false,
                                mPaint);
                        break;
                    } else if (segment == j - 1) {
                        setBoundsFromCenter(Ring.MIDDLE.offset);
                        canvas.drawArc(mBounds, (j - NUMBER_RINGS) * SLICE_MULTIPLIER, SLICE, false,
                                mPaint);
                        break;
                    } else if (segment == j - 2) {
                        setBoundsFromCenter(Ring.INNER.offset);
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

        // Cache the guidelines in a bitmap and overlay it on top
        if (mPathBitmap == null) {
            mPathBitmap = Bitmap.createBitmap(mViewDimensions.x, mViewDimensions.y,
                    Bitmap.Config.ARGB_8888);
            Canvas guidelineCanvas = new Canvas(mPathBitmap);

            // Draw 4 guide circles from outermost to innermost
            mPaint.setStrokeWidth(CIRCLE_GUIDELINE_STROKE_WIDTH);
            mPaint.setColor(Color.BLACK);
            setBoundsFromCenter(Ring.OUTER.offset - SLICE_MAX_DIFF);
            guidelineCanvas.drawArc(mBounds, 0f, 360f, false, mPaint);
            setBoundsFromCenter(Ring.OUTER.offset + SLICE_MAX_DIFF);
            guidelineCanvas.drawArc(mBounds, 0f, 360f, false, mPaint);
            setBoundsFromCenter(Ring.MIDDLE.offset + SLICE_MAX_DIFF);
            guidelineCanvas.drawArc(mBounds, 0f, 360f, false, mPaint);
            setBoundsFromCenter(Ring.INNER.offset + SLICE_MAX_DIFF);
            guidelineCanvas.drawArc(mBounds, 0f, 360f, false, mPaint);

            // Draw the radial guidelines at straight angles
            mPaint.setColor(Color.BLACK);
            mPaint.setStrokeWidth(MAIN_GUIDELINE_STROKE_WIDTH);
            drawGuidelines(guidelineCanvas, mMainGuidelinePath, 0d);
            drawGuidelines(guidelineCanvas, mMainGuidelinePath, 90d);
            mPaint.setStrokeWidth(SUB_GUIDELINE_STROKE_WIDTH);
            double[] guideAngles = {15d, 30d, 45d, 60d, 75d, 105d, 120d, 135d, 150d, 165d};
            for (double angle : guideAngles) {
                drawGuidelines(guidelineCanvas, mSubGuidelinePath, angle);
            }

            // Draw the hour marks
            mPaint.setStrokeWidth(MAIN_GUIDELINE_STROKE_WIDTH);
            String[] hours = {"6", "7", "8", "9", "10", "11", "12", "1", "2", "3", "4", "5"};
            double[] hourAngles = {0d, 15d, 30d, 45d, 60d, 75d, 90d, 105d, 120d, 135d, 150d, 165d};
            for (int i = 0; i < hours.length; i++) {
                drawHourMarks(guidelineCanvas, hours[i], hourAngles[i]);
            }

//            mPaint.setColor(Color.BLACK);
//            drawHoursAndGuidelines(guidelineCanvas, mMainGuidelinePath, "6", 0d);
//            drawHoursAndGuidelines(guidelineCanvas, mSubGuidelinePath, "7", 15d);
//            drawHoursAndGuidelines(guidelineCanvas, mSubGuidelinePath, "8", 30d);
//            drawHoursAndGuidelines(guidelineCanvas, mSubGuidelinePath, "9", 45d);
//            drawHoursAndGuidelines(guidelineCanvas, mSubGuidelinePath, "10", 60d);
//            drawHoursAndGuidelines(guidelineCanvas, mSubGuidelinePath, "11", 75d);
//            drawHoursAndGuidelines(guidelineCanvas, mMainGuidelinePath, "12", 90d);
//            drawHoursAndGuidelines(guidelineCanvas, mSubGuidelinePath, "1", 105d);
//            drawHoursAndGuidelines(guidelineCanvas, mSubGuidelinePath, "2", 120d);
//            drawHoursAndGuidelines(guidelineCanvas, mSubGuidelinePath, "3", 135d);
//            drawHoursAndGuidelines(guidelineCanvas, mSubGuidelinePath, "4", 150d);
//            drawHoursAndGuidelines(guidelineCanvas, mSubGuidelinePath, "5", 165d);
        }
        canvas.drawBitmap(mPathBitmap, 0, 0, mPaint);
    }

    /**
     * Draws radial guidelines at the given angle and at the straight angle of the given angle.
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
     * Draws a single radial guideline from the center offset (inner circular guideline) to the
     * outer edge (outer circular guideline).
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

    // TODO: refactor into drawGuidelines()
    /**
     * Draws the hour marks at the given angle and at the straight angle of the given angle.
     *
     * @param canvas
     * @param hour
     * @param angle
     */
    private void drawHourMarks(Canvas canvas, String hour, double angle) {
        mOppositeAngle = (angle + 180d) % 360;
        angle = Math.toRadians(angle);
        mOppositeAngle = Math.toRadians(mOppositeAngle);
        drawSingleHourMark(canvas, hour, angle);
        drawSingleHourMark(canvas, hour, mOppositeAngle);
    }

    /**
     * Draws a single hour mark at the outer edge (outer circular guideline).
     *
     * @param canvas
     * @param hour
     * @param angle
     */
    private void drawSingleHourMark(Canvas canvas, String hour, double angle) {
        // draw the stroke circle
        mEndPath.x = mCenter.x + (mRadius + SLICE_MAX_DIFF) * (float) Math.cos(angle);
        mEndPath.y = mCenter.y + (mRadius + SLICE_MAX_DIFF) * (float) Math.sin(angle);
        setHourBounds(mEndPath);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.BLACK);
        canvas.drawArc(mBounds, 0f, 360f, true, mPaint);

        // draw the fill circle and the text in opposite colors
        mPaint.setStyle(Paint.Style.FILL);
        angle = Math.toDegrees(angle);
        if (angle < 85 || angle > 260) {
            mHourMarkFillColor = Color.WHITE;
            mHourMarkTextColor = Color.BLACK;
        } else {
            mHourMarkFillColor = Color.BLACK;
            mHourMarkTextColor = Color.WHITE;
        }
        mPaint.setColor(mHourMarkFillColor);
        canvas.drawArc(mBounds, 0f, 360f, true, mPaint);
        mPaint.setColor(mHourMarkTextColor);
        canvas.drawText(hour, mEndPath.x, mEndPath.y + mPaint.getTextSize() / 3, mPaint);
    }

//    /**
//     * Draws hour marks and guidelines at the given angle and at the straight angle of the given
//     * angle.
//     *
//     * @param canvas
//     * @param path
//     * @param hour
//     * @param angle
//     */
//    private void drawHoursAndGuidelines(Canvas canvas, Path path, String hour, double angle) {
//        mOppositeAngle = (angle + 180d) % 360;
//        mPaint.setStyle(Paint.Style.STROKE);
//        mPaint.setColor(Color.BLACK);
//        if (angle == 0d || angle == 90d) {
//            mPaint.setStrokeWidth(MAIN_GUIDELINE_STROKE_WIDTH);
//        } else {
//            mPaint.setStrokeWidth(SUB_GUIDELINE_STROKE_WIDTH);
//        }
//        angle = Math.toRadians(angle);
//        mOppositeAngle = Math.toRadians(mOppositeAngle);
//        drawSingleHourAndGuideline(canvas, path, hour, angle);
//        drawSingleHourAndGuideline(canvas, path, hour, mOppositeAngle);
//    }
//
//    /**
//     * Draws a single hour mark and guideline from the center offset (inner circular guideline) to
//     * the outer edge (outer circular guideline).
//     *
//     * @param canvas
//     * @param path
//     * @param hour
//     * @param angle
//     */
//    private void drawSingleHourAndGuideline(Canvas canvas, Path path, String hour, double angle) {
//        mStartPath.x = mCenter.x + mCenterOffset * (float) Math.cos(angle);
//        mStartPath.y = mCenter.y + mCenterOffset * (float) Math.sin(angle);
//        mEndPath.x = mCenter.x + (mRadius + SLICE_MAX_DIFF) * (float) Math.cos(angle);
//        mEndPath.y = mCenter.y + (mRadius + SLICE_MAX_DIFF) * (float) Math.sin(angle);
//        path.moveTo(mStartPath.x, mStartPath.y);
//        path.lineTo(mEndPath.x, mEndPath.y);
//        canvas.drawPath(path, mPaint);
//
////        angle = Math.toRadians(angle);
////        mEndPath.x = mCenter.x + (mRadius + SLICE_MAX_DIFF) * (float) Math.cos(angle);
////        mEndPath.y = mCenter.y + (mRadius + SLICE_MAX_DIFF) * (float) Math.sin(angle);
//        setHourBounds(mEndPath);
//
//        mPaint.setStyle(Paint.Style.STROKE);
//        mPaint.setStrokeWidth(MAIN_GUIDELINE_STROKE_WIDTH);
//        mPaint.setColor(Color.BLACK);
//        canvas.drawArc(mBounds, 0f, 360f, true, mPaint);
//
//        mPaint.setStyle(Paint.Style.FILL);
//        angle = Math.toDegrees(angle);
//        if (angle > 260 || angle < 85) {
//            mPaint.setColor(Color.WHITE);
//            canvas.drawArc(mBounds, 0f, 360f, true, mPaint);
//            mPaint.setColor(Color.BLACK);
//            canvas.drawText(hour, mEndPath.x, mEndPath.y + mPaint.getTextSize() / 3, mPaint);
//        } else {
//            mPaint.setColor(Color.BLACK);
//            canvas.drawArc(mBounds, 0f, 360f, true, mPaint);
//            mPaint.setColor(Color.WHITE);
//            canvas.drawText(hour, mEndPath.x, mEndPath.y + mPaint.getTextSize() / 3, mPaint);
//        }
//
//    }

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
        // Required call for onMeasure()
        setMeasuredDimension((int) width, (int) height);
    }

    /**
     * Called by the associated fragment in order to save the {@link Entry} to persistent storage.
     *
     * @return  The {@link Entry} representing this view.
     */
    public Entry getEntry() {
        Type hashMapType = new TypeToken<HashMap<Integer, Long>>() {}.getType();
//        Log.d("seg:::", new Gson().toJson(mEntrySegments, hashMapType));
        mEntry.setSegments(new Gson().toJson(mEntrySegments, hashMapType));
        return mEntry;
    }

    /**
     * Used for drawing circular guidelines
     *
     * @param offset
     */
    private void setBoundsFromCenter(float offset) {
        mBounds.set(mCenter.x - mRadius + offset,
                mCenter.y - mRadius + offset,
                mCenter.x + mRadius - offset,
                mCenter.y + mRadius - offset);
    }

    private void setHourBounds(PointF center) {
        mBounds.set(center.x - HOUR_RADIUS,
                center.y - HOUR_RADIUS,
                center.x + HOUR_RADIUS,
                center.y + HOUR_RADIUS);
    }
}
