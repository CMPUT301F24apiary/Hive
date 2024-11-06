package com.example.hive.Controllers;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

/**
 * Controller class to get and store events. Extends FirebaseController for db access, and utilizes
 * Firebase Storage reference to get and store images.
 *
 * @author Zach
 */
public class ImageController extends FirebaseController {

    // Database reference
    private final FirebaseFirestore db;
    private final FirebaseStorage store;

    /**
     * Constructor - call <code>FirebaseController</code>'s constructor and initialize the database
     * reference
     */
    public ImageController() {
        super();
        this.db = super.getDb();
        this.store = FirebaseStorage.getInstance();
    }

    /**
     * Method to store a new image in Firebase Storage. Limits size of image to 10MB so as to not
     * run out of storage in firebase.
     *
     * @param context
     * The context from which this method was called
     * @param path
     * The URI path to the image on device
     * @param typeOfImage
     * What the image is used for - valid types are:
     * <ul>
     * <li>event poster</li>
     * <li>profile picture</li>
     * </ul>
     *
     * @return
     * Task that completes with a string which is the download URL of the image. Store this in the
     * event or profile document in firestore.
     */
    public Task<String> saveImage(Context context, Uri path, String typeOfImage) {
        if (!typeOfImage.equals("event poster") && !typeOfImage.equals("profile picture")) {
            throw new IllegalArgumentException("Invalid image type: " + typeOfImage + ". " +
                    "Expected 'event poster' or 'profile picture'.");
        }

        long fileSize = 0;
        try (Cursor cursor = context.getContentResolver().query(path, null, null,
                null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
                if (sizeIndex != -1) {
                    fileSize = cursor.getLong(sizeIndex);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to check file size", e);
        }

        if (fileSize > 10 * 1024 * 1024) {
            throw new IllegalArgumentException("File size exceeds 10MB limit.");
        }

        StorageReference storeRef = store.getReference();
        StorageReference imageRef = storeRef.child("images/" + path.getLastPathSegment());

        UploadTask upload = imageRef.putFile(path);

        return upload.continueWithTask(task -> {
            if (!task.isSuccessful()) {
                throw task.getException();
            }
            return imageRef.getDownloadUrl();
        }).continueWithTask(downloadTask -> {
            if (!downloadTask.isSuccessful()) {
                throw downloadTask.getException();
            }

            Uri downloadURI = downloadTask.getResult();
            CollectionReference collRef = db.collection("images");

            HashMap<String, String> data = new HashMap<>();
            data.put("type", typeOfImage);
            data.put("url", downloadURI.toString());

            return collRef.add(data).continueWith(task -> downloadURI.toString());
        });
    }


}
