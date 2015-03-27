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

package com.msadraii.multidex.colorcode;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.msadraii.multidex.ColorCode;
import com.msadraii.multidex.R;
import com.msadraii.multidex.util.SimpleDividerItemDecoration;
import com.msadraii.multidex.util.SwipeDismissRecyclerViewTouchListener;
import com.msadraii.multidex.dialogues.ColorPickerUtils;
import com.msadraii.multidex.data.ColorCodeRepository;

/**
 * A placeholder fragment containing a simple view.
 */
public class EditColorCodeFragment extends Fragment {
    private static final String LOG_TAG = EditColorCodeFragment.class.getSimpleName();

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private Context appContext;

    public EditColorCodeFragment() {
    }

    @Override
    public void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(LOG_TAG, "onSaveInstanceState()");
        // TODO: write to DB
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onActivityCreated()");
        // TODO: read DB
//        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
//        if (savedInstanceState != null) {
//            mLocation = savedInstanceState.getString(LOCATION_KEY);
//        }
        super.onActivityCreated(savedInstanceState);
    }

    // TODO: save instance state / write to DB on save
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_edit_color_codes, container, false);

        appContext = getActivity().getApplicationContext();

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.labels_recycler_view);
        mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));
        mRecyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new ColorCodeAdapter(appContext, getActivity());

//        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//                if (mRecyclerView.getChildAt(0) != null && mRecyclerView.getChildAt(0).getTop() == 0) {
////                    Log.d(LOG_TAG, "at item 0");
//                    EditColorCodeActivity.setActionBarShadow(false);
//                } else {
//                    EditColorCodeActivity.setActionBarShadow(true);
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
                                int size = ColorCodeRepository.getAllColorCodes(appContext).size();
                                if (size == 1) {
                                    Toast.makeText(appContext,
                                            R.string.toast_cannot_delete_list_item,
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
                                    ColorCodeRepository.deleteColorCodeAndSort(
                                            appContext,
                                            position);
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

        ImageButton fabImageButton = (ImageButton) rootView.findViewById(R.id.fab_image_button);
        fabImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorCode colorCode = ColorCodeRepository.initColorCode(
                        appContext,
                        ColorPickerUtils.ColorUtils.randomColor(appContext),
                        ""
                );
                ColorCodeRepository.insertOrReplace(appContext, colorCode);
                ((ColorCodeAdapter) mAdapter).setNewlyInserted(colorCode.getId().intValue());
                mRecyclerView.smoothScrollToPosition(colorCode.getId().intValue());
            }
        });

        mRecyclerView.setAdapter(mAdapter);

        return rootView;
    }

//    public RecyclerView.LayoutManager getLayoutManager() {
//        return mLayoutManager;
//    }
}
