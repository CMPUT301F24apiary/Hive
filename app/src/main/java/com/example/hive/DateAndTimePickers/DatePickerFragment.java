package com.example.hive.DateAndTimePickers;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

/**
 * DialogFragment for displaying a date picker.
 * The activity using this fragment must implement DatePickerDialog.OnDateSetListener
 * to handle the date selection event.
 *
 * @author Zach
 */
public class DatePickerFragment extends DialogFragment {

    /** Listener that will handle the date selection event */
    private DatePickerDialog.OnDateSetListener listener;

    /**
     * Called when fragment is attached to a context. Verifies that the context
     * implements the required listener interface.
     *
     * @param context The context to which the fragment is attached
     * @throws RuntimeException if context does not implement DatePickerDialog.OnDateSetListener
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof DatePickerDialog.OnDateSetListener) {
            listener = (DatePickerDialog.OnDateSetListener) context;
        } else {
            throw new RuntimeException(context + " must implement DatePickerDialog.OnDateSetListener");
        }

    }

    /**
     * Creates the date picker dialog.
     * Uses either the date provided in arguments bundle or the current date as default.
     * Arguments bundle can contain:
     * - "year": int - The year to display
     * - "month": int - The month to display (0-11)
     * - "day": int - The day of month to display
     *
     * @param savedInstanceState Bundle containing the saved state
     * @return A new DatePickerDialog instance
     */
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
