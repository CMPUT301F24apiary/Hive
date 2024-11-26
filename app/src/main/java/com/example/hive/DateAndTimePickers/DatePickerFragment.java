package com.example.hive.DateAndTimePickers;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

public class DatePickerFragment extends DialogFragment {

    private DatePickerDialog.OnDateSetListener listener;

    // Ensure that the calling context implements the methods in our listener interface
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof DatePickerDialog.OnDateSetListener) {
            listener = (DatePickerDialog.OnDateSetListener) context;
        } else {
            throw new RuntimeException(context + " must implement DatePickerDialog.OnDateSetListener");
        }

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker.
        final Calendar c = Calendar.getInstance();
        int year, month, day;
        Bundle b = getArguments();
        if (b != null) {
            year = b.getInt("year", c.get(Calendar.YEAR));
            month = b.getInt("month", c.get(Calendar.MONTH));
            day = b.getInt("day", c.get(Calendar.DAY_OF_MONTH));
        } else {
            year = c.get(Calendar.YEAR);
            month = c.get(Calendar.MONTH);
            day = c.get(Calendar.DAY_OF_MONTH);
        }

        // Create a new instance of DatePickerDialog and return it.
        return new DatePickerDialog(requireContext(), listener, year, month, day);
    }

}
