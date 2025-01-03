package com.example.hive.AdminImage;

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
 * Selecting "confirm" will call listener method to delete the image. Selecting "cancel" will
 * dismiss the dialog and return user to detail activity.
 *
 * @author Zach
 */
public class ConfirmImageDelete extends DialogFragment {

    /**
     * Position of the image in the array in list activity
     */
    private int position;
    /**
     * String variables for download URL, id of image document in firebase, id of related document
     * in firebase tha references this image
     */
    private String imageUrl, id, relatedDocID;
    /**
     * Instance of the interface implemented in the list activity
     */
    private DeleteImageListener listener;

    /**
     * Constructor for ConfirmImageDelete fragment. Creates a new instance and returns it.
     *
     * @param position
     * int: position of the image in the array in list activity
     * @param imageUrl
     * String: image download URL
     * @param id
     * String: id of image document in firebase
     * @param relatedDocID
     * String: id of related document in firebase tha references this image
     * @return
     * The <code>ConfirmImageDelete</code> instance that was created.
     */
    public static ConfirmImageDelete newInstance(int position, String imageUrl, String id,
                                                 String relatedDocID) {
        ConfirmImageDelete dialog = new ConfirmImageDelete();
        Bundle args = new Bundle();
        args.putInt("position", position);
        args.putString("imageUrl", imageUrl);
        args.putString("id", id);
        args.putString("relatedDocID", relatedDocID);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof DeleteImageListener) {
            listener = (DeleteImageListener) context;
        } else {
            throw new RuntimeException(context.toString() +
                    " must implement ConfirmDeleteListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Get layout file that describes the view of the fragment
        View view = getLayoutInflater().inflate(R.layout.fragment_confirm_image_delete, null);

        // Get references to the confirm and cancel buttons
        Button confirmBtn = view.findViewById(R.id.image_confirm_deletion_button);
        Button cancelBtn = view.findViewById(R.id.image_cancel_deletion_button);

        if (getArguments() != null) {
            position = getArguments().getInt("position");
            imageUrl = getArguments().getString("imageUrl");
            id = getArguments().getString("id");
            relatedDocID = getArguments().getString("relatedDocID");
        }

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

            // If user selects "confirm" call the onDelete method from the listener, and dismiss
            // the dialog
            confirmBtn.setOnClickListener(v -> {
                listener.onDelete(position, imageUrl, id, relatedDocID);
                dialog.dismiss();
            });

        });

        return dialog;
    }
}