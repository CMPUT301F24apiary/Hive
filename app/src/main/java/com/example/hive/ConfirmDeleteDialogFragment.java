package com.example.hive;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
/**
 * DialogFragment for confirming the deletion of all entries in the cancelled list.
 * Displays a confirmation dialog with "Delete" and "Cancel" options.
 *
 * @author HRITTIJA
 */
public class ConfirmDeleteDialogFragment extends DialogFragment {

    /**
     * Interface for handling the "Delete" action in the confirmation dialog.
     */
    public interface ConfirmDeleteListener {
        void onDeleteConfirmed();
    }

    private ConfirmDeleteListener listener;

    /**
     * Constructor to set the listener for the dialog.
     *
     * @param listener to handle the confirmation of the delete action.
     */

    public ConfirmDeleteDialogFragment(ConfirmDeleteListener listener) {
        this.listener = listener;
    }


    /**
     * Creates and returns the confirmation dialog for deleting
     *
     * @param savedInstanceState from previous saved state.
     * @return A Dialog object that displays the confirmation text.
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setTitle("Confirm Deletion")
                .setMessage("Are you sure you want to delete all entries?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    if (listener != null) {
                        listener.onDeleteConfirmed();
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        return builder.create();
    }
}

