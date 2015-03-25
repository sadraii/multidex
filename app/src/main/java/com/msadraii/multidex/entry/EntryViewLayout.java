package com.msadraii.multidex.entry;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.FrameLayout;

/**
 * Created by Mostafa on 3/21/2015.
 */
public class EntryViewLayout extends FrameLayout { // TODO: remove this class, test if EntryView onClick works
    EntryView mEntryView;

    public EntryViewLayout(Context context) {
        this(context, null, 0);
    }

    public EntryViewLayout(final Context context, final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EntryViewLayout(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
        Log.d("ABOUT TO CALL ENTRYVIEW", "-----------------------");
        mEntryView = new EntryView(context);
        addView(mEntryView);
    }

    public void setPosition(Context appContext, int id) {
        mEntryView.setPosition(appContext, id);
    }
}
