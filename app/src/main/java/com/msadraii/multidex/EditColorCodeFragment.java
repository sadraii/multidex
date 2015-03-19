package com.msadraii.multidex;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A placeholder fragment containing a simple view.
 */
public class EditColorCodeFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    public EditColorCodeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_edit_color_codes, container, false);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.labels_recycler_view);
        mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

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


        mAdapter = new ColorCodeAdapter(getActivity().getApplicationContext(), getActivity());
        mRecyclerView.setAdapter(mAdapter);

        return rootView;
    }

    public RecyclerView.LayoutManager getLayoutManager() {
        return mLayoutManager;
    }
}
