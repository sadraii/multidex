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
import com.msadraii.multidex.colorpickerdialogue.Utils;
import com.msadraii.multidex.data.ColorCodeRepository;

/**
 * Created by Mostafa on 3/15/2015.
 */
public class ColorCodeAdapter extends RecyclerView.Adapter<ColorCodeAdapter.ViewHolder> {

    private Context mContext;
    private Context mActivityContext;

    private static final long COLOR_SPIN_DURATION = 100;

    public ColorCodeAdapter(Context context, Context activityContext) {
        mContext = context;
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
    public void onBindViewHolder(final ViewHolder holder, int position) {
        // GreenDAO addItProperty() starts at 1 instead of 0, so need to add 1 to position
        final ColorCode colorCode = ColorCodeRepository.getColorCodeForId(mContext, position + 1);
        // TODO if final, use CCR.getcolorcodeforid each time

        holder.mFrameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorPickerDialog colorPicker = ColorPickerDialog.newInstance(
                        R.string.color_picker_default_title,
                        Utils.ColorUtils.colorChoice(mContext),
                        Color.parseColor(colorCode.getArgb()),
                        5,
                        ColorPickerDialog.SIZE_SMALL
                );
                colorPicker.setOnColorSelectedListener(new ColorPickerSwatch.OnColorSelectedListener() {
                    @Override
                    public void onColorSelected(final int color) {
                        // TODO something with color
                        colorCode.setArgb("#" + Integer.toHexString(color));
                        ColorCodeRepository.insertOrReplace(mContext, colorCode);

                        holder.mImageView.animate()
                                .rotationYBy(-90)
                                .setDuration(COLOR_SPIN_DURATION)
                                .withEndAction(new Runnable() {
                                    @Override
                                    public void run() {
                                        // test color change
                                        ((GradientDrawable) holder.mImageView.getBackground())
                                                .setColor(color);
                                        holder.mImageView.animate()
                                                .rotationYBy(-90)
                                                .setDuration(COLOR_SPIN_DURATION);
                                    }
                                });
                    }
                });
                colorPicker.show(((EditLabelActivity) mActivityContext).getFragmentManager(), "col");
            }
        });


        ((GradientDrawable) holder.mImageView.getBackground())
                .setColor(Color.parseColor(colorCode.getArgb()));
        holder.mEditText.setText(colorCode.getTask());
        holder.mEditText.setHorizontallyScrolling(true);
        holder.mEditText.setFocusable(false);
        holder.mEditText.setClickable(true);
        holder.mEditText.setFocusableInTouchMode(false);
//            holder.mEditText.setEnabled(true);
        holder.mEditText.setCursorVisible(false);
//            holder.mEditText.setKeyListener(null);
        holder.mEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("viewholder", "clicked on text");
                v.setFocusable(true);
                v.setFocusableInTouchMode(true);
                ((EditText) v).setCursorVisible(true);
//                    v.setEnabled(true);
            }
        });
    }

    // Invoked by the layout manager
    @Override
    public int getItemCount() {
        return ColorCodeRepository.getAllColorCodes(mContext).size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final FrameLayout mFrameLayout;
        public final ImageView mImageView;
        public final EditText mEditText;

        public ViewHolder(View v) {
            super(v);
            mImageView = (ImageView) v.findViewById(R.id.list_item_color_image_ivew);

            mFrameLayout = (FrameLayout) v.findViewById(R.id.list_item_color_frame_layout);
//            mFrameLayout.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Log.d("viewholder", "clicked on frame");
//
//                }
//            });

            mEditText = (EditText) v.findViewById(R.id.list_item_description);

        }
    }
}