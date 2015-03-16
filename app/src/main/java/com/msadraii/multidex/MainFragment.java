package com.msadraii.multidex;

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

import com.mostafasadraii.multidex.R;

import java.util.ArrayList;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainFragment extends Fragment {

    public MainFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        TextView textView = (TextView) rootView.findViewById(R.id.text);

//        clearTables();
//        recreateTables();
//        addTestLabels();







//        Label label = new Label();
//        LabelRepository.insertOrReplace(getActivity(), label);

        ArrayList<Label> arrayList;
        arrayList = (ArrayList)LabelRepository.getAllLabels(getActivity());
        for (Label l : arrayList) {
            textView.append("\n" + l.getTask());
            Button b = new Button(getActivity());
            b.setText(l.getTask());
            b.setLayoutParams(new ActionBar.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));
            b.setBackgroundColor(Color.parseColor(l.getArgb()));
            LinearLayout ll = (LinearLayout)rootView.findViewById(R.id.fragment_layout);
            ColorDrawable c = (ColorDrawable) b.getBackground();
            textView.append(Integer.toHexString(c.getColor()));
            ll.addView(b);
        }

//        arrayList = (ArrayList)LabelRepository.getAllLabels(getActivity());
//        for (Label l : arrayList) {
//            textView.append("\nadded 1\n" + Long.toString(l.getId()));
//        }
//
//        label = new Label();
//        LabelRepository.insertOrReplace(getActivity(), label);
//        label = new Label();
//        LabelRepository.insertOrReplace(getActivity(), label);
//
//        arrayList = (ArrayList)LabelRepository.getAllLabels(getActivity());
//        for (Label l : arrayList) {
//            textView.append("\nadded 2\n" + Long.toString(l.getId()));
//        }
//
//        LabelRepository.deleteLabelWithId(getActivity(), 1);
//
//        arrayList = (ArrayList)LabelRepository.getAllLabels(getActivity());
//        for (Label l : arrayList) {
//            textView.append("\ndeleted 1\n" + Long.toString(l.getId()));
//        }
//
//        label = new Label();
//        LabelRepository.insertOrReplace(getActivity(), label);
//        label = new Label();
//        LabelRepository.insertOrReplace(getActivity(), label);
//
//        arrayList = (ArrayList)LabelRepository.getAllLabels(getActivity());
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
//        return super.onOptionsItemSelected(item);
    }

    private void recreateTables() {
        LabelRepository.dropTable(getActivity());
        EntryRepository.dropTable(getActivity());
        LabelRepository.createTable(getActivity());
        EntryRepository.createTable(getActivity());
    }

    private void clearTables() {
        LabelRepository.clearLabels(getActivity());
        EntryRepository.clearEntries(getActivity());
    }

    private void addTestLabels() {
        Label label = new Label();
        label.setArgb("#ff292884");
        label.setTask("Blue");
        LabelRepository.insertOrReplace(getActivity(), label);

        Label label2 = new Label();
        label2.setArgb("#ff17ae07");
        label2.setTask("Yellow");
        LabelRepository.insertOrReplace(getActivity(), label2);

        Label label3 = new Label();
        label3.setArgb("#ff0b8440");
        label3.setTask("Green");
        LabelRepository.insertOrReplace(getActivity(), label3);

        Label label4 = new Label();
        label4.setArgb("#ff840805");
        label4.setTask("Red");
        LabelRepository.insertOrReplace(getActivity(), label4);


    }
}
