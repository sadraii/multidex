package com.msadraii.multidex;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.msadraii.multidex.data.ColorCodeRepository;
import com.msadraii.multidex.data.EntryRepository;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainFragment extends Fragment {

    private Context appContext;

    public MainFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        appContext = getActivity().getApplicationContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        TextView textView = (TextView) rootView.findViewById(R.id.text);

//        clearTables();
        recreateTables();
        addTestLabels();

        ColorCodeRepository.deleteColorCodeWithIdAndSort(appContext, 4);
        ColorCodeRepository.deleteColorCodeWithIdAndSort(appContext, 10);
        ColorCodeRepository.deleteColorCodeWithIdAndSort(appContext,11);



        ArrayList<ColorCode> arrayList;
        arrayList = (ArrayList) ColorCodeRepository.getAllColorCodes(appContext);
        for (ColorCode l : arrayList) {
            textView.append("\n" + l.getId() + l.getTask());
            Button b = new Button(getActivity());
            b.setText(l.getTask());
            b.setLayoutParams(new ActionBar.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));
            b.setBackgroundColor(Color.parseColor(l.getArgb()));
            LinearLayout ll = (LinearLayout)rootView.findViewById(R.id.fragment_layout);
            ColorDrawable c = (ColorDrawable) b.getBackground();
            textView.append(" " + Integer.toHexString(c.getColor()));
            ll.addView(b);
        }





//        arrayList = (ArrayList)ColorCodeRepository.getAllColorCodes(getActivity());
//        for (Label l : arrayList) {
//            textView.append("\nadded 1\n" + Long.toString(l.getId()));
//        }
//
//        label = new Label();
//        ColorCodeRepository.insertOrReplace(getActivity(), label);
//        label = new Label();
//        ColorCodeRepository.insertOrReplace(getActivity(), label);
//
//        arrayList = (ArrayList)ColorCodeRepository.getAllColorCodes(getActivity());
//        for (Label l : arrayList) {
//            textView.append("\nadded 2\n" + Long.toString(l.getId()));
//        }
//
//        ColorCodeRepository.deleteColorCodeWithIdAndSort(getActivity(), 1);
//
//        arrayList = (ArrayList)ColorCodeRepository.getAllColorCodes(getActivity());
//        for (Label l : arrayList) {
//            textView.append("\ndeleted 1\n" + Long.toString(l.getId()));
//        }
//
//        label = new Label();
//        ColorCodeRepository.insertOrReplace(getActivity(), label);
//        label = new Label();
//        ColorCodeRepository.insertOrReplace(getActivity(), label);
//
//        arrayList = (ArrayList)ColorCodeRepository.getAllColorCodes(getActivity());
//        for (Label l : arrayList) {
//            textView.append("\nadded 2\n" + Long.toString(l.getId()));
//        }

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_main_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_edit_label) {
            startActivity(new Intent(getActivity(), EditLabelActivity.class));
            return true;
        }
        return true;
    }

    private void recreateTables() {
        ColorCodeRepository.dropTable(appContext);
        EntryRepository.dropTable(appContext);
        ColorCodeRepository.createTable(appContext);
        EntryRepository.createTable(appContext);
    }

    private void clearTables() {
        ColorCodeRepository.clearColorCodes(appContext);
        EntryRepository.clearEntries(appContext);
    }

    private void addTestLabels() {
        ColorCode label = new ColorCode();
        label.setArgb("#ff292884");
        label.setTask("Blue1");
        ColorCodeRepository.insertOrReplace(appContext, label);

        label = new ColorCode();
        label.setArgb("#ff17ae07");
        label.setTask("Yellow2");
        ColorCodeRepository.insertOrReplace(appContext, label);

        label = new ColorCode();
        label.setArgb("#ff0b8440");
        label.setTask("Green3");
        ColorCodeRepository.insertOrReplace(appContext, label);

        label = new ColorCode();
        label.setArgb("#ff840805");
        label.setTask("Red4");
        ColorCodeRepository.insertOrReplace(appContext, label);

        label = new ColorCode();
        label.setArgb("#ffa3d1d0");
        label.setTask("Red5");
        ColorCodeRepository.insertOrReplace(appContext, label);

        label = new ColorCode();
        label.setArgb("#ffcc8b17");
        label.setTask("Red6");
        ColorCodeRepository.insertOrReplace(appContext, label);

        label = new ColorCode();
        label.setArgb("#ffcbb0ef");
        label.setTask("Red7");
        ColorCodeRepository.insertOrReplace(appContext, label);

        label = new ColorCode();
        label.setArgb("#ffc6fe60");
        label.setTask("Red8");
        ColorCodeRepository.insertOrReplace(appContext, label);

        label = new ColorCode();
        label.setArgb("#ff0294a8");
        label.setTask("Red9");
        ColorCodeRepository.insertOrReplace(appContext, label);

        label = new ColorCode();
        label.setArgb("#ff5a6eaf");
        label.setTask("Red10");
        ColorCodeRepository.insertOrReplace(appContext, label);

        label = new ColorCode();
        label.setArgb("#fff06233");
        label.setTask("Red11");
        ColorCodeRepository.insertOrReplace(appContext, label);

        label = new ColorCode();
        label.setArgb("#ff462e80");
        label.setTask("Red12");
        ColorCodeRepository.insertOrReplace(appContext, label);

        label = new ColorCode();
        label.setArgb("#ff91f496");
        label.setTask("Red13");
        ColorCodeRepository.insertOrReplace(appContext, label);

        label = new ColorCode();
        label.setArgb("#fff082c3");
        label.setTask("Red14");
        ColorCodeRepository.insertOrReplace(appContext, label);

        label = new ColorCode();
        label.setArgb("#ffcede5b");
        label.setTask("Red15");
        ColorCodeRepository.insertOrReplace(appContext, label);

        label = new ColorCode();
        label.setArgb("#ffebde73");
        label.setTask("Red16");
        ColorCodeRepository.insertOrReplace(appContext, label);

        label = new ColorCode();
        label.setArgb("#FF927024");
        label.setTask("Red17");
        ColorCodeRepository.insertOrReplace(appContext, label);
    }
}
