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

package com.msadraii.multidex;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.msadraii.multidex.colorpickerdialogue.ColorPickerDialog;
import com.msadraii.multidex.colorpickerdialogue.ColorPickerSwatch;
import com.msadraii.multidex.colorpickerdialogue.ColorPickerUtils;
import com.msadraii.multidex.data.ColorCodeRepository;

/**
 * Created by Mostafa on 3/15/2015.
 */
public class ColorCodeAdapter extends RecyclerView.Adapter<ColorCodeAdapter.ViewHolder> {

    private static final String LOG_TAG = ColorCodeAdapter.class.getSimpleName();

    private Context mAppContext;
    private Context mActivityContext;

    private static final long COLOR_SPIN_DURATION = 100;
    private static final float COLOR_HALF_SPIN_DEGREE = -90;
    private static final int COLOR_DIALOGUE_COLUMNS = 5;
    private static final String COLOR_DIALOGUE_FRAGMENT_TAG = "color_dialogue_fragment";

    public ColorCodeAdapter(Context appContext, Context activityContext) {
        mAppContext = appContext;
        mActivityContext = activityContext;
    }

    @Override
    public ColorCodeAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_color_codes, parent, false);

        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        // GreenDAO addIdProperty() starts at 1 instead of 0, so we need to add 1 to position
        final ColorCode colorCode = ColorCodeRepository.getColorCodeForId(mAppContext, position + 1);
        if (colorCode == null) {
            return;
        }

        // Creates a ColorPickerDialogue and assigns the selected color to the ColorCode
        holder.mFrameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorPickerDialog colorPicker = ColorPickerDialog.newInstance(
                        R.string.color_picker_default_title,
                        ColorPickerUtils.ColorUtils.colorChoices(mAppContext),
                        Color.parseColor(colorCode.getArgb()),
                        COLOR_DIALOGUE_COLUMNS,
                        ColorPickerDialog.SIZE_SMALL
                );
                colorPicker.setOnColorSelectedListener(new ColorPickerSwatch.OnColorSelectedListener() {
                    @Override
                    public void onColorSelected(final int color) {
                        String hexColor = Utils.toHexString(color);
                        if (!colorCode.getArgb().equals(hexColor)) {
                            colorCode.setArgb(hexColor);
                            ColorCodeRepository.insertOrReplace(mAppContext, colorCode);

                            // Spins ImageView while assigning the new color halfway through the spin
                            holder.mImageView.animate()
                                    .rotationYBy(COLOR_HALF_SPIN_DEGREE)
                                    .setDuration(COLOR_SPIN_DURATION)
                                    .withEndAction(new Runnable() {
                                        @Override
                                        public void run() {
                                            // test color change
                                            ((GradientDrawable) holder.mImageView.getBackground())
                                                    .setColor(color);
                                            holder.mImageView.animate()
                                                    .rotationYBy(COLOR_HALF_SPIN_DEGREE)
                                                    .setDuration(COLOR_SPIN_DURATION);
                                        }
                                    });
                        }
                    }
                });
                colorPicker.show(((EditColorCodeActivity) mActivityContext).getFragmentManager(),
                        COLOR_DIALOGUE_FRAGMENT_TAG);
            }
        });
        ((GradientDrawable) holder.mImageView.getBackground())
                .setColor(Color.parseColor(colorCode.getArgb()));

        holder.mEditText.setText(colorCode.getTask());
        holder.mEditText.setHorizontallyScrolling(true);
        holder.mEditText.setFocusable(true);
        holder.mEditText.setClickable(true);
        holder.mEditText.setFocusableInTouchMode(false);
        holder.mEditText.setCursorVisible(false);
//            holder.mEditText.setEnabled(true);
//            holder.mEditText.setKeyListener(null);
        holder.mEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("viewholder", "clicked on text");
                v.setFocusableInTouchMode(true);
                ((EditText) v).setCursorVisible(true);
//                v.setFocusable(true);
//                v.setEnabled(true);
                // TODO get activity's fragment
//                ((EditColorCodeActivity) mActivityContext).getLayoutManager().scrollToPosition(position);
                // or scrollToPositionWithOffset()
            }
        });

        // TODO focus change does not happen when editing text and pressing back button
        holder.mEditText.setTag(colorCode.getTask());
        holder.mEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String text = ((EditText) v).getText().toString();
                    // TODO check if this only happens when text != tag
                    if (!text.equals(v.getTag())) {
                        colorCode.setTask(text);
                        ColorCodeRepository.insertOrReplace(mAppContext, colorCode);
                        Log.d(LOG_TAG, "inserted text= " + text);
                    }
                }
            }
        });
    }

    // Invoked by the layout manager
    @Override
    public int getItemCount() {
        return ColorCodeRepository.getAllColorCodes(mAppContext).size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final FrameLayout mFrameLayout;
        public final ImageView mImageView;
        public final EditText mEditText;

        public ViewHolder(View v) {
            super(v);
            mFrameLayout = (FrameLayout) v.findViewById(R.id.list_item_color_frame_layout);
            mImageView = (ImageView) v.findViewById(R.id.list_item_color_image_ivew);
            mEditText = (EditText) v.findViewById(R.id.list_item_description);
        }
    }
}