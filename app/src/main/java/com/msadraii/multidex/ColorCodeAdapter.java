package com.msadraii.multidex;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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
        holder.mTextView.setText(colorCode.getTask());
    }

    // Invoked by the layout manager
    @Override
    public int getItemCount() {
        return ColorCodeRepository.getAllColorCodes(mContext).size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final ImageView mImageView;
        public final TextView mTextView;

        public ViewHolder(View v) {
            super(v);
            mImageView = (ImageView) v.findViewById(R.id.list_item_color_view);
            mTextView = (TextView) v.findViewById(R.id.list_item_description);
        }
    }
}