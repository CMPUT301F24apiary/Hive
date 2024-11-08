package com.example.hive.Events;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.view.View;
import android.widget.Button;

import com.example.hive.R;

/**
 * Fragment to display a confirmation message to the user, with a confirm and cancel button.
 * <br/><br/>
 * Selecting "confirm" will call listener method to delete the event. Selecting "cancel" will
 * dismiss the dialog and return user to detail activity.
 *
 * @author Zach
 */
public class ConfirmEventDelete extends DialogFragment {

    // Declare variables for cancel and confirm buttons
    private Button cancelBtn;
    private Button confirmBtn;

    /**
     * Call <code>DialogFragment</code>'s constructor - we have no additional parameters for this
     * class.
     */
    public ConfirmEventDelete() {
        super();
    }

    // Declare our listener, which is an interface implemented in DeleteEventListener.java
    private DeleteEventListener listener;

    // Ensure that the calling context implements the methods in our listener interface
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof DeleteEventListener) {
            listener = (DeleteEventListener) context;
        } else {
            throw new RuntimeException(context + " must implement DeleteEventListener");
        }

    }

    // Main bulk of the logic - what to show when the fragment opens
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        // Get layout file that describes the view of the fragment
        View view = getLayoutInflater().inflate(R.layout.fragment_confirm_event_delete, null);

        // Get references to the confirm and cancel buttons
        confirmBtn = view.findViewById(R.id.event_confirm_deletion_button);
        cancelBtn = view.findViewById(R.id.event_cancel_deletion_button);

        // Declare our dialog builder and attach the newly inflated view to it
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(view);

        // Create the dialog
        AlertDialog dialog = builder.create();

        // Describe the logic for when the buttons are clicked
        dialog.setOnShowListener(dialogInterface -> {

            // If user selects "cancel" just simply dismiss the dialog
            cancelBtn.setOnClickListener(v -> {
                dialog.dismiss();
            });

            // If user selects "confirm" call the deleteEvent method from the listener, and dismiss
            // the dialog
            confirmBtn.setOnClickListener(v -> {
                listener.deleteEvent();
                dialog.dismiss();
            });

        });

        return dialog;
    }
}