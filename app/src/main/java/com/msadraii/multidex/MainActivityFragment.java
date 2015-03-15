package com.msadraii.multidex;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mostafasadraii.multidex.R;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        TextView textView = (TextView) rootView.findViewById(R.id.text);
//        LabelRepository.clearLabels(getActivity());
//        LabelRepository.dropTable(getActivity());
//        LabelRepository.createTable(getActivity());

//        Label label = new Label();
//        LabelRepository.insertOrReplace(getActivity(), label);
//        ArrayList<Label> arrayList = new ArrayList<Label>();

//        LabelRepository.deleteLabelWithId(getActivity(), 10);
//
//        arrayList = (ArrayList)LabelRepository.getAllLabels(getActivity());
//        for (Label l : arrayList) {
//            textView.append("\n" + Long.toString(l.getId()));
//        }

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
}
