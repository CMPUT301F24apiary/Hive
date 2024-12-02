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

/**
 * DialogFragment for displaying a time picker in 24-hour format.
 * The activity using this fragment must implement TimePickerDialog.OnTimeSetListener
 * to handle the time selection event.
 *
 * @author Zach
 */
public class TimePickerFragment extends DialogFragment {

    /** Listener that will handle the time selection event */
    private TimePickerDialog.OnTimeSetListener listener;

    /**
     * Called when fragment is attached to a context. Verifies that the context
     * implements the required listener interface.
     *
     * @param context The context to which the fragment is attached
     * @throws RuntimeException if context does not implement TimePickerDialog.OnTimeSetListener
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof TimePickerDialog.OnTimeSetListener) {
            listener = (TimePickerDialog.OnTimeSetListener) context;
        } else {
            throw new RuntimeException(context + " must implement TimePickerDialog.OnTimeSetListener");
        }

    }

    /**
     * Creates the time picker dialog.
     * Uses either the time provided in arguments bundle or the current time as default.
     * Arguments bundle can contain:
     * <ul>
     * <li>"hr": int - The hour to display (0-23)</li>
     * <li>"min": int - The minute to display (0-59)</li>
     * </ul>
     *
     * @param savedInstanceState Bundle containing the saved state
     * @return A new TimePickerDialog instance in 24-hour format
     */
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
