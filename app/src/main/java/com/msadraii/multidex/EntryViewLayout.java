package com.msadraii.multidex;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

/**
 * Created by Mostafa on 3/21/2015.
 */
public class EntryViewLayout extends FrameLayout implements View.OnTouchListener {
    EntryView mEntryView;

    public EntryViewLayout(Context context) {
        this(context, null, 0);
    }

    public EntryViewLayout(final Context context, final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EntryViewLayout(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
        mEntryView = new EntryView(context);
        addView(mEntryView);
        mEntryView.setOnTouchListener(this);
    }

    public void setColor(int color) {
        mEntryView.setColor(color);
    }

    @Override
    public boolean onTouch(View view, MotionEvent e) {
        Log.d("touched", e.getX() + " " + e.getY());
//        Toast.makeText(getContext(), "Mock", Toast.LENGTH_SHORT).show();
        return false;
    }
}
