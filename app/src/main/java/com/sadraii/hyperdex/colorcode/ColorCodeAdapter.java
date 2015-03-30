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

package com.sadraii.hyperdex.colorcode;

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
import android.widget.TextView;
import android.widget.Toast;

import com.sadraii.hyperdex.ColorCode;
import com.sadraii.hyperdex.R;
import com.sadraii.hyperdex.data.ColorCodeRepository;
import com.sadraii.hyperdex.dialogues.ColorPickerDialog;
import com.sadraii.hyperdex.dialogues.ColorPickerSwatch;
import com.sadraii.hyperdex.dialogues.ColorPickerUtils;
import com.sadraii.hyperdex.util.Utils;

import java.util.ArrayList;

/**
 * TODO: description
 */
public class ColorCodeAdapter extends RecyclerView.Adapter<ColorCodeAdapter.ViewHolder> {

    private static final String LOG_TAG = ColorCodeAdapter.class.getSimpleName();

    private Context mAppContext;
    private Context mActivityContext;
    private boolean mNewlyAdded = false;

    private static final long SPIN_DURATION = 100;
    private static final float HALF_SPIN_DEGREE = -90;
    private static final int DIALOGUE_COLUMNS = 5;
    private static final String DIALOGUE_FRAGMENT_TAG = "dialogue_fragment";

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

    /**
     * Replace the contents of a view (invoked by the layout manager).
     *
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final ColorCode colorCode = ColorCodeRepository.getColorCode(mAppContext, position);
        if (colorCode == null) {
            return;
        }

        holder.textView.setText(
                ColorPickerUtils.ColorUtils.colorName(mAppContext, colorCode.getArgb()));

        // Creates a ColorPickerDialogue and assigns the selected color to the ColorCode
        holder.frameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int colorInt = Color.parseColor(colorCode.getArgb());

                ColorPickerDialog colorPicker = ColorPickerDialog.newInstance(
                        R.string.color_picker_default_title,
                        ColorPickerUtils.ColorUtils.colorChoices(mAppContext),
                        colorInt,
                        DIALOGUE_COLUMNS,
                        ColorPickerDialog.SIZE_SMALL
                );
                colorPicker.setOnColorSelectedListener(new ColorPickerSwatch.OnColorSelectedListener() {
                    @Override
                    public void onColorSelected(final int color) {
                        String colorString = Utils.toHexString(color);

                        if (colorInt != color) {
                            // Color picked must be unique
                            ArrayList<String> colorCodeValues =
                                    ColorCodeRepository.getAllColorValues(mAppContext);
                            for (String value : colorCodeValues) {
                                if (colorString.equals(value)) {
                                    Toast.makeText(mAppContext,
                                            R.string.toast_cannot_change_color,
                                            Toast.LENGTH_SHORT)
                                            .show();
                                    return;
                                }
                            }
                            // Save the new color
                            colorCode.setArgb(colorString);
                            ColorCodeRepository.insertOrReplace(mAppContext, colorCode);
                            // Set color name
                            holder.textView.setText(
                                    ColorPickerUtils.ColorUtils.colorName(
                                            mAppContext, colorString));

                            animateColorView(holder.imageView, color);
                        }
                    }
                });
                colorPicker.show(((ColorCodeActivity) mActivityContext).getFragmentManager(),
                        DIALOGUE_FRAGMENT_TAG);
            }
        });

        // If the view is newly added by FAB, make the background transparent so that the new color
        // can 'spin in' from nothingness.
        final int colorInt = Color.parseColor(colorCode.getArgb());
        if (mNewlyAdded) {
            ((GradientDrawable) holder.imageView.getBackground())
                    .setColor(Color.parseColor("#00000000"));
            animateColorView(holder.imageView, colorInt);
            mNewlyAdded = false;
        } else {
            ((GradientDrawable) holder.imageView.getBackground())
                    .setColor(colorInt);
        }

        holder.editText.setText(colorCode.getTask());
        holder.editText.setHorizontallyScrolling(true);
        holder.editText.setFocusable(true);
        holder.editText.setClickable(true);
        holder.editText.setFocusableInTouchMode(false);
        holder.editText.setCursorVisible(false);
//            holder.editText.setEnabled(true);
//            holder.editText.setKeyListener(null);
        holder.editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("viewholder", "clicked on text");
                v.setFocusableInTouchMode(true);
                ((EditText) v).setCursorVisible(true);
//                v.setFocusable(true);
//                v.setEnabled(true);
                // TODO: get activity's fragment
//                ((ColorCodeActivity) mActivityContext).getLayoutManager().scrollToPosition(position);
                // or scrollToPositionWithOffset()
            }
        });

        // TODO: focus change does not happen when editing text and pressing back button
        holder.editText.setTag(colorCode.getTask());
        holder.editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                EditText editText = (EditText) v;
                if (!hasFocus) {
                    editText.setCursorVisible(false);
                    String text = editText.getText().toString();
                    // TODO: check if this only happens when text != tag
                    if (!text.equals(editText.getTag())) {
                        colorCode.setTask(text);
                        ColorCodeRepository.insertOrReplace(mAppContext, colorCode);
                        Log.d(LOG_TAG, "inserted text= " + text);
                    }
                } else {
                    editText.setCursorVisible(true);
                }
            }
        });
    }

    // Invoked by the layout manager
    @Override
    public int getItemCount() {
        return ColorCodeRepository.size(mAppContext);
    }

    /**
     * Called by FAB when a new color is added.
     *
     * @param position
     */
    public void setNewlyInserted(int position) {
        mNewlyAdded = true;
        notifyItemInserted(position);
    }

    /**
     * Spins the ImageView while assigning the new color halfway through the spin.
     *
     * @param imageView
     * @param color
     */
    private void animateColorView(final ImageView imageView, final int color) {
        imageView.animate()
                .rotationYBy(HALF_SPIN_DEGREE)
                .setDuration(SPIN_DURATION)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        // test color change
                        ((GradientDrawable) imageView.getBackground())
                                .setColor(color);
                        imageView.animate()
                                .rotationYBy(HALF_SPIN_DEGREE)
                                .setDuration(SPIN_DURATION);
                    }
                });
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final FrameLayout frameLayout;
        public final ImageView imageView;
        public final EditText editText;
        public final TextView textView;

        public ViewHolder(View view) {
            super(view);
            frameLayout = (FrameLayout) view.findViewById(R.id.list_item_color_frame_layout);
            imageView = (ImageView) view.findViewById(R.id.list_item_color_image_ivew);
            editText = (EditText) view.findViewById(R.id.list_item_description);
            textView = (TextView) view.findViewById(R.id.list_item_color_name);
        }
    }
}