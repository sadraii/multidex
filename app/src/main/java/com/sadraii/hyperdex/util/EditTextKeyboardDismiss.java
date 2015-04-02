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

package com.sadraii.hyperdex.util;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.EditText;

import com.sadraii.hyperdex.data.ColorCodeRepository;

/**
 * Modified EditText to handle "back" button on soft keyboard.
 */
public class EditTextKeyboardDismiss extends EditText {
    public EditTextKeyboardDismiss(Context context) {
        super(context);
    }

    public EditTextKeyboardDismiss(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EditTextKeyboardDismiss(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * Updates ColorCode description when soft keyboard "back" button is used.
     *
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            setCursorVisible(false);
            ColorCodeRepository.updateColorCodeTask(getContext().getApplicationContext(),
                    (Long) getTag(), getText().toString());
        }
        return super.onKeyPreIme(keyCode, event);
    }
}
