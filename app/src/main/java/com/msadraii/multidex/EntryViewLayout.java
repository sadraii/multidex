package com.msadraii.multidex;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

/**
 * Created by Mostafa on 3/21/2015.
 */
public class EntryViewLayout extends FrameLayout implements View.OnClickListener {
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
        mEntryView.setOnClickListener(this);
    }

    public void setColor(int color) {
        mEntryView.setColor(color);
    }

    @Override
    public void onClick(View v) {
        Toast.makeText(getContext(), "Mock", Toast.LENGTH_SHORT).show();
    }
}
