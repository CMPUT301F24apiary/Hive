package com.example.hive.Events;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.hive.R;

public class ConfirmQRCodeDelete extends DialogFragment {
    // Declare variables for cancel and confirm buttons
    private Button cancelButton;
    private Button confirmButton;
    private DeleteQRCodeListener listener;

    /**
     * constructor
     */
    public ConfirmQRCodeDelete() {
        super();
    }

    /**
     * calling context will implement the appropriate listener,
     * @throws RuntimeException if the listener is not implemented
     * @param context
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof DeleteQRCodeListener) {
            listener = (DeleteQRCodeListener) context;
        } else {
            throw new RuntimeException(context + " must implement DeleteQRCodeDelete listener");
        }
    }


    /**
     * This is what is shown when the fragment opens.
     * @param savedInstanceState The last saved instance state of the Fragment,
     * or null if this is a freshly created Fragment.
     *
     * @return
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
//        return super.onCreateDialog(savedInstanceState);
        View view = getLayoutInflater().inflate(R.layout.fragment_confirm_qr_code_delete, null);
        confirmButton = view.findViewById(R.id.event_confirm_deletion_button);
        cancelButton = view.findViewById(R.id.event_cancel_deletion_button);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(dialogInterface -> {
            cancelButton.setOnClickListener(v -> {
                dialog.dismiss();
            });
            confirmButton.setOnClickListener(v -> {
                listener.deleteQRCode();
                dialog.dismiss();
            });
        });
        return dialog;
    }
}
