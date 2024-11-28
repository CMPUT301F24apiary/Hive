package com.example.hive.DateAndTimePickers;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.CalendarView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.example.hive.Events.DeleteEventListener;

import java.text.DateFormat;
import java.util.Calendar;

public class TimePickerFragment extends DialogFragment {

    // Declare our listener, which is an interface implemented in DeleteEventListener.java
    private TimePickerDialog.OnTimeSetListener listener;

    // Ensure that the calling context implements the methods in our listener interface
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof TimePickerDialog.OnTimeSetListener) {
            listener = (TimePickerDialog.OnTimeSetListener) context;
        } else {
            throw new RuntimeException(context + " must implement TimePickerDialog.OnTimeSetListener");
        }

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        Bundle b = getArguments();
        int hour, minute;
        if (b != null) {
            hour = b.getInt("hr", c.get(Calendar.HOUR_OF_DAY));
            minute = b.getInt("min", c.get(Calendar.MINUTE));
        } else {
            hour = c.get(Calendar.HOUR_OF_DAY);
            minute = c.get(Calendar.MINUTE);
        }

        return new TimePickerDialog(getActivity(), listener, hour, minute, true);
    }
}
