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

import com.mostafasadraii.multidex.R;
import com.msadraii.multidex.data.LabelRepository;


/**
 * Created by Mostafa on 3/15/2015.
 */
public class LabelAdapter extends RecyclerView.Adapter<LabelAdapter.ViewHolder> {

    private Context mContext;

    public LabelAdapter(Context context) {
        mContext = context;
    }

    @Override
    public LabelAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.label_list_item, parent, false);

        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // GreenDAO addItProperty() starts at 1 instead of 0
        Label label = LabelRepository.getLabelForId(mContext, position + 1);

        ((GradientDrawable) holder.mImageView.getBackground())
                .setColor(Color.parseColor(label.getArgb()));
        holder.mTextView.setText(label.getTask());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return LabelRepository.getAllLabels(mContext).size();
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
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