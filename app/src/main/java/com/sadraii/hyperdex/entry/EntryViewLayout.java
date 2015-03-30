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
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.sadraii.hyperdex.Entry;

/**
 * Created by Mostafa on 3/21/2015.
 */
public class EntryViewLayout extends FrameLayout {
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
    }

    /**
     * The position passed from the fragment associates the EntryView to the fragment Entry object.
     *
     * @param appContext
     * @param position
     */
    public void setPosition(Context appContext, int position) {
        mEntryView.setPosition(appContext, position);
    }

    /**
     * Called by the associated fragment in order to save the {@link Entry} to persistent storage.
     *
     * @return  The {@link Entry} representing this the {@link EntryView}.
     */
    public Entry getEntry() {
        return mEntryView.getEntry();
    }
}
