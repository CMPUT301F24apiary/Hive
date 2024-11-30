package com.example.hive.Events;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class ConfirmQRCodeDelete extends DialogFragment {
    // Declare variables for cancel and confirm buttons
    private Button cancelButton;
    private Button confirmBtn;
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
        return super.onCreateDialog(savedInstanceState);
    }
}
