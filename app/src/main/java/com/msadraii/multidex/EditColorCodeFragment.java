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
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.msadraii.multidex.colorpickerdialogue.ColorPickerUtils;
import com.msadraii.multidex.data.ColorCodeRepository;

/**
 * A placeholder fragment containing a simple view.
 */
public class EditColorCodeFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private Context appContext;

    public EditColorCodeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_edit_color_codes, container, false);

        appContext = getActivity().getApplicationContext();

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.labels_recycler_view);
        mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new ColorCodeAdapter(appContext, getActivity());

        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (mRecyclerView.getChildAt(0) != null && mRecyclerView.getChildAt(0).getTop() == 0) {
//                    Log.d(LOG_TAG, "at item 0");
                    EditColorCodeActivity.setActionBarShadow(false);
                } else {
                    EditColorCodeActivity.setActionBarShadow(true);
                }
            }
        });

        ImageButton fabImageButton = (ImageButton) rootView.findViewById(R.id.fab_image_button);
        fabImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("fab", "new button");
                ColorCode colorCode = new ColorCode();
                colorCode.setArgb(ColorPickerUtils.ColorUtils.randomColor(appContext));
                colorCode.setTask("");
                ColorCodeRepository.insertOrReplace(appContext, colorCode);
                int position = Utils.safeLongToInt(colorCode.getId());
                mAdapter.notifyItemInserted(position);
                mRecyclerView.smoothScrollToPosition(position);
            }
        });

        mRecyclerView.setAdapter(mAdapter);

        return rootView;
    }

//    public RecyclerView.LayoutManager getLayoutManager() {
//        return mLayoutManager;
//    }
}
