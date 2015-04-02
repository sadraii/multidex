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
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.sadraii.hyperdex.R;
import com.sadraii.hyperdex.data.ColorCodeRepository;
import com.sadraii.hyperdex.dialogues.ColorPickerUtils;
import com.sadraii.hyperdex.util.SimpleDividerItemDecoration;
import com.sadraii.hyperdex.util.SwipeDismissRecyclerViewTouchListener;

/**
 * TODO: description
 */
public class ColorCodeFragment extends Fragment {

    private static final String LOG_TAG = ColorCodeFragment.class.getSimpleName();

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private Context mAppContext;

    public ColorCodeFragment() {
    }

    public static ColorCodeFragment newInstance() {
        return new ColorCodeFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_edit_color_codes, container, false);

        mAppContext = getActivity().getApplicationContext();

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.labels_recycler_view);
        mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setFocusableInTouchMode(true);
        mRecyclerView.requestFocus();

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new ColorCodeAdapter(mAppContext, getActivity());

//        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//                if (mRecyclerView.getChildAt(0) != null && mRecyclerView.getChildAt(0).getTop() == 0) {
////                    Log.d(LOG_TAG, "at item 0");
//                    ColorCodeActivity.setActionBarShadow(false);
//                } else {
//                    ColorCodeActivity.setActionBarShadow(true);
//                }
//            }
//        });

        SwipeDismissRecyclerViewTouchListener touchListener =
                new SwipeDismissRecyclerViewTouchListener(
                        mRecyclerView,
                        new SwipeDismissRecyclerViewTouchListener.DismissCallbacks() {
                            // Do not delete last color code
                            @Override
                            public boolean canDismiss(int position) {
                                if (ColorCodeRepository.size(mAppContext) == 1) {
                                    Toast.makeText(mAppContext,
                                            R.string.toast_cannot_delete_color,
                                            Toast.LENGTH_SHORT)
                                            .show();
                                    return false;
                                }
                                return true;
                            }

                            @Override
                            public void onDismiss(RecyclerView recyclerView,
                                                  int[] reverseSortedPositions) {
                                for (int position : reverseSortedPositions) {
                                    ColorCodeRepository.deleteColorCodeAndSort(mAppContext, position);
                                }
                                // Do not call notifyItemRemoved for every item, it will cause gaps
                                // on deleting items
                                mAdapter.notifyDataSetChanged();
                            }
                        });
        mRecyclerView.setOnTouchListener(touchListener);
        // Setting this scroll listener is required to ensure that during ListView scrolling,
        // we don't look for swipes.
        mRecyclerView.setOnScrollListener(touchListener.makeScrollListener());

        // Add a new ColorCode with a random unused color unless all colors are used.
        ImageButton fabImageButton = (ImageButton) rootView.findViewById(R.id.fab_image_button);
        fabImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String randomColor = ColorPickerUtils.ColorUtils.randomColor(mAppContext);
                if (randomColor != null) {
                    long colorCodeId = ColorCodeRepository.addNextColorCode(mAppContext,
                            randomColor, "").getId();

                    ((ColorCodeAdapter) mAdapter).setNewlyInserted((int) colorCodeId);
                    mRecyclerView.smoothScrollToPosition((int) colorCodeId);
                } else {
                    Toast.makeText(mAppContext,
                            R.string.toast_cannot_add_color,
                            Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });

        mRecyclerView.setAdapter(mAdapter);

        return rootView;
    }
}
