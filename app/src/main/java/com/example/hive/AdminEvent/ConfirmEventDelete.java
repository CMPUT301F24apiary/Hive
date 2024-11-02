package com.example.hive.AdminEvent;

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
 * @author Zach
 */
public class ConfirmEventDelete extends DialogFragment {

    private Button cancelBtn;
    private Button confirmBtn;

    public ConfirmEventDelete() {
        super();
    }

    // Declare our listener, which is an interface implemented in BookViewListener.java
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

        confirmBtn = view.findViewById(R.id.event_confirm_deletion_button);
        cancelBtn = view.findViewById(R.id.event_cancel_deletion_button);

        // Declare our dialog builder and set title and buttons depending on the mode
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(view);

        // Create the dialog
        AlertDialog dialog = builder.create();

        // Describe the logic for when the positive button is clicked
        dialog.setOnShowListener(dialogInterface -> {

            // Set listener depending on if we are in edit or view mode
            cancelBtn.setOnClickListener(v -> {
                dialog.dismiss();
            });

            confirmBtn.setOnClickListener(v -> {
                listener.deleteEvent();
                dialog.dismiss();
            });

        });

        return dialog;
    }
}