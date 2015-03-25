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
