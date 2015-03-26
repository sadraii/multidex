package com.msadraii.multidex.dialogues;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import java.util.Calendar;

/**
 * Created by Mostafa on 3/26/2015.
 */
public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {
    private DatePickerDialog.OnDateSetListener mListener;

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Calendar c = Calendar.getInstance();
        return new DatePickerDialog(
                getActivity(),
                this,
                c.get(Calendar.YEAR),
                c.get(Calendar.MONTH),
                c.get(Calendar.DAY_OF_MONTH)
        );
    }

    public void setOnDateSetListener(DatePickerDialog.OnDateSetListener listener) {
        mListener = listener;
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        // Do something with the date chosen by the user
        if (mListener != null) {
            mListener.onDateSet(view, year, month, day);
        }

        // TODO: handle default ondateset behavior

        dismiss();
    }
}