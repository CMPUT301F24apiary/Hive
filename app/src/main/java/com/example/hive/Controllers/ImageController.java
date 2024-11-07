package com.example.hive.Controllers;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.util.Log;
import android.util.Pair;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
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
    public Task<Pair<String, String>> saveImage(Context context, Uri path, String typeOfImage) {
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
            Log.d("ERROR Checking File Size", e.toString());
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

            return collRef.add(data).continueWith(task -> {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                String documentId = task.getResult().getId();
                return new Pair<>(downloadURI.toString(), documentId);
            });
        });
    }

    public void getAllImagesFromDB(OnSuccessListener<ArrayList<HashMap<String, String>>> callback) {
        ArrayList<HashMap<String, String>> data = new ArrayList<>();
        CollectionReference imagesCollection = db.collection("images");

        imagesCollection.get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                String id = doc.getId();
                String url = (String) doc.get("url");
                String imageType = (String) doc.get("type");
                String relatedID = (String) doc.get("relatedDocID");
                HashMap<String, String> newImg = new HashMap<String, String>();
                newImg.put("url", url);
                newImg.put("info", imageType.substring(0, 1).toUpperCase() +
                        imageType.substring(1));
                newImg.put("id", id);
                newImg.put("relatedDocID", relatedID);
                data.add(newImg);
            }
            // Notify the callback with the fetched data
            callback.onSuccess(data);
        }).addOnFailureListener(e -> Log.e("ImageControllerGetAll",
                "Error fetching data", e));
    }

    public void updateImageRef(String imgID, String otherID) {
        CollectionReference imagesCollection = db.collection("images");

        imagesCollection.document(imgID).update("relatedDocID", otherID);
    }

    public void deleteImageAndUpdateRelatedDoc(String url, String imgID, String relatedDocID,
                                               OnSuccessListener<Boolean> callback) {
        deleteImage(url, imgID)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        handleRelatedDocument(relatedDocID, callback);
                    } else {
                        Log.e("Delete Image", "Failed to delete image",
                                task.getException());
                    }
                });
    }

    private Task<Void> deleteImage(String url, String imgID) {
        StorageReference photoRef = store.getReferenceFromUrl(url);
        CollectionReference imgCollection = db.collection("images");
        // Delete the image in Cloud Storage
        return photoRef.delete().continueWithTask(task -> {
            if (task.isSuccessful()) {
                // Delete the document from imgCollection
                return imgCollection.document(imgID).delete();
            } else {
                throw task.getException();
            }
        });
    }

    private void handleRelatedDocument(String relatedDocID, OnSuccessListener<Boolean> callback) {
        CollectionReference eventCollection = db.collection("events");
        // Check if the document exists in the event collection
        eventCollection.document(relatedDocID).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        if (documentSnapshot.exists()) {
                            // Update the field in the events collection document
                            updateEventDocument(relatedDocID, eventCollection, callback);
                        } else {
                            // If not in events collection, try the images collection
                            updateUserDocument(relatedDocID, callback);
                        }
                    } else {
                        Log.e("Fetch Document", "Error fetching related document",
                                task.getException());
                    }
                });
    }

    private void updateEventDocument(String docID, CollectionReference eventCollection,
                                     OnSuccessListener<Boolean> callback) {
        eventCollection.document(docID)
                .update("poster", "")
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        callback.onSuccess(Boolean.TRUE);
                        Log.d("Update Event", "Event document updated successfully");
                    } else {
                        Log.e("Update Event", "Failed to update event document",
                                task.getException());
                    }
                });
    }

    private void updateUserDocument(String docID, OnSuccessListener<Boolean> callback) {
        CollectionReference userCollection = db.collection("user");

        userCollection.document(docID).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult().exists()) {
                        userCollection.document(docID)
                                .update("profileImageUrl", "")
                                .addOnCompleteListener(updateTask -> {
                                    if (updateTask.isSuccessful()) {
                                        callback.onSuccess(Boolean.TRUE);
                                        Log.d("Update User",
                                                "User document updated successfully");
                                    } else {
                                        callback.onSuccess(Boolean.FALSE);
                                        Log.e("Update User",
                                                "Failed to update user document",
                                                updateTask.getException());
                                    }
                                });
                    } else {
                        Log.d("Update User", "Document not found in images collection");
                    }
                });
    }



}
