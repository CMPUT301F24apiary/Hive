package com.example.hive.Controllers;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.util.Log;
import android.util.Pair;

import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Controller class to get and store images. Extends FirebaseController for db access, and utilizes
 * Firebase Storage reference to get and store images.
 *
 * @author Zach
 * @see FirebaseController
 */
public class ImageController extends FirebaseController {

    /**
     * Reference to firestore db
     */
    private final FirebaseFirestore db;
    /**
     * Reference to firebase cloud storage
     */
    private final FirebaseStorage store;

    /**
     * Constructor - call <code>FirebaseController</code>'s constructor and initialize the database
     * reference and storage reference
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
     * <li>facility picture</li>
     * </ul>
     *
     * @return
     * Task that completes with a string which is the download URL of the image. Store this in the
     * event or profile document in firestore.
     */
    public Task<Pair<String, String>> saveImage(Context context, Uri path, String typeOfImage) {
        // Ensure valid image type
        if (!typeOfImage.equals("event poster") &&
            !typeOfImage.equals("profile picture") &&
            !typeOfImage.equals("facility picture")) {
            throw new IllegalArgumentException("Invalid image type: " + typeOfImage + ". " +
                    "Expected 'event poster', 'profile picture', or 'facility picture'.");
        }

        // Ensure image is less than 10MB
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

    /**
     * Function to retrieve all images from firestore
     *
     * @param callback
     * The function to be called once all data has been retrieved
     */
    public void getAllImagesFromDB(OnSuccessListener<ArrayList<HashMap<String, String>>> callback) {
        ArrayList<HashMap<String, String>> data = new ArrayList<>();
        CollectionReference imagesCollection = db.collection("images");

        imagesCollection.get().addOnSuccessListener(queryDocumentSnapshots -> {
            int totalDocs = queryDocumentSnapshots.size();
            if (totalDocs == 0) {
                callback.onSuccess(data);
                return;
            }

            // Track completed tasks
            final int[] completedCount = {0};

            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                String id = doc.getId();
                String url = (String) doc.get("url");
                String imageType = (String) doc.get("type");
                String relatedID = (String) doc.get("relatedDocID");
                HashMap<String, String> newImg = new HashMap<>();
                newImg.put("url", url);
                newImg.put("id", id);
                newImg.put("relatedDocID", relatedID);

                OnSuccessListener<DocumentSnapshot> onRelatedDocSuccess = relatedDoc -> {
                    if (imageType.equals("event poster")) {
                        newImg.put("info", "Event poster - " + relatedDoc.get("title"));
                    } else {
                        newImg.put("info", "Profile picture - " + relatedDoc.get("username"));
                    }

                    synchronized (data) {
                        data.add(newImg);
                        completedCount[0]++;

                        // Check if all tasks are complete
                        if (completedCount[0] == totalDocs) {
                            callback.onSuccess(data);
                        }
                    }
                };

                // Fetch related document based on type
                if (imageType.equals("event poster")) {
                    db.collection("events").document(relatedID).get()
                            .addOnSuccessListener(onRelatedDocSuccess);
                } else {
                    db.collection("users").document(relatedID).get()
                            .addOnSuccessListener(onRelatedDocSuccess);
                }
            }
        }).addOnFailureListener(e -> Log.e("ImageControllerGetAll",
                "Error fetching data", e));
    }


    /**
     * Update image document with ID imgID to store reference to whatever document this image is for
     *
     * @param imgID
     * String: document ID of image
     * @param otherID
     * String: document ID of firebase document where this image is used
     */
    public void updateImageRef(String imgID, String otherID) {
        db.collection("images").document(imgID).update("relatedDocID", otherID);

    }

    public void getImageDocIdByUrl(String url, OnSuccessListener<String> listener) {
        db.collection("images").whereEqualTo("url", url).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                DocumentSnapshot doc = queryDocumentSnapshots.getDocuments().get(0);
                listener.onSuccess(doc.getId());
            }
        });
    }

    /**
     * Handle deletion of image. Utilizes multiple helper functions to do so.
     *
     * @param url
     * String: the download URL of the image
     * @param imgID
     * String: the document ID of the image in firestore
     * @param relatedDocID
     * String: the document ID of the firestore document where this image is used
     * @param callback
     * OnSuccessListener: the function to be called once all deletion is performed
     */
    public void deleteImageAndUpdateRelatedDoc(String url, @Nullable String imgID,
                                               String relatedDocID,
                                               OnSuccessListener<Boolean> callback) {
        if (imgID == null) {
            getImageDocIdByUrl(url, id -> {
                deleteImage(url, id)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            handleRelatedDocument(relatedDocID, callback);
                        } else {
                            Log.e("Delete Image", "Failed to delete image",
                                    task.getException());
                        }
                    });
            });
        } else {
            Log.d("DeleteImage", "ImgID");
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
    }

    /**
     * Private method to delete the image from cloud storage, and the reference document in
     * firestore
     *
     * @param url
     * String: the download URL of the image
     * @param imgID
     * String: the document ID of the firestore document that holds information about this image
     * @return
     * A Task to keep track of when deletion completes
     */
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

    /**
     * Handles removal of the download URL from related document
     *
     * @param relatedDocID
     * String: the document ID of the document that uses the iamge
     * @param callback
     * OnSuccessListener: the function to be called once deletion is done
     */
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
                            // If not in events, check facilities collection
                            checkFacilitiesCollection(relatedDocID, callback);
                        }
                    } else {
                        Log.e("Fetch Document", "Error fetching related document",
                                task.getException());
                    }
                });
    }

    /**
     * Checks if document exists in facilities collection and updates if found
     */
    private void checkFacilitiesCollection(String docID, OnSuccessListener<Boolean> callback) {
        CollectionReference facilitiesCollection = db.collection("facilities");
        facilitiesCollection.document(docID).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        if (documentSnapshot.exists()) {
                            updateFacilityDocument(docID, facilitiesCollection, callback);
                        } else {
                            // If not in facilities, try the users collection
                            updateUserDocument(docID, callback);
                        }
                    } else {
                        Log.e("Fetch Facility", "Error fetching facility document",
                                task.getException());
                    }
                });
    }

    /**
     * Removes the download URL from facility document.
     */
    private void updateFacilityDocument(String docID, CollectionReference facilitiesCollection,
                                        OnSuccessListener<Boolean> callback) {
        facilitiesCollection.document(docID)
                .update("imageUrl", "")  // Adjust field name if different
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        callback.onSuccess(Boolean.TRUE);
                        Log.d("Update Facility", "Facility document updated successfully");
                    } else {
                        Log.e("Update Facility", "Failed to update facility document",
                                task.getException());
                    }
                });
    }

    /**
     * Removes the download URL from event document.
     *
     * @param docID
     * String: the document ID of the event document
     * @param eventCollection
     * CollectionReference: the reference to the events collection in firestore
     * @param callback
     * OnSuccessListener: function to be called once deletion is complete
     */
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

    /**
     * Removes the download URL from user document.
     *
     * @param docID
     * String: the document ID of the user document
     * @param callback
     * OnSuccessListener: function to be called once deletion is complete
     */
    private void updateUserDocument(String docID, OnSuccessListener<Boolean> callback) {
        CollectionReference userCollection = db.collection("users");

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
                        Log.d("Update User", "Document not found in users collection");
                    }
                });
    }



}
