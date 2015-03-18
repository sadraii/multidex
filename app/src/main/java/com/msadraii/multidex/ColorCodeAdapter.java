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

import com.msadraii.multidex.data.ColorCodeRepository;

/**
 * Created by Mostafa on 3/15/2015.
 */
public class ColorCodeAdapter extends RecyclerView.Adapter<ColorCodeAdapter.ViewHolder> {

    private Context mContext;

    public ColorCodeAdapter(Context context) {
        mContext = context;
    }

    @Override
    public ColorCodeAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_color_codes, parent, false);

        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // GreenDAO addItProperty() starts at 1 instead of 0, so need to add 1 to position
        ColorCode colorCode = ColorCodeRepository.getColorCodeForId(mContext, position + 1);

        ((GradientDrawable) holder.mImageView.getBackground())
                .setColor(Color.parseColor(colorCode.getArgb()));
        holder.mEditText.setText(colorCode.getTask());
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
            mFrameLayout = (FrameLayout) v.findViewById(R.id.list_item_color_frame_layout);
            mFrameLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("viewholder", "clicked on frame");
                }
            });

            mImageView = (ImageView) v.findViewById(R.id.list_item_color_image_ivew);

            mEditText = (EditText) v.findViewById(R.id.list_item_description);
            mEditText.setFocusable(false);
            mEditText.setClickable(true);
            mEditText.setFocusableInTouchMode(false);
//            mEditText.setEnabled(true);
            mEditText.setCursorVisible(false);
//            mEditText.setKeyListener(null);
            mEditText.setOnClickListener(new View.OnClickListener() {
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
    }
}