package com.example.hive.Controllers;

import static android.content.ContentValues.TAG;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.hive.Models.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * A singleton class that creates an instance of the Firestore database
 */
public class FirebaseController {
    private FirebaseFirestore db;
    private static boolean is_initialized = false;

    /**
     * Constructor for FirebaseController
     * Uses the boolean to avoid multiple instances
     */
    public FirebaseController() {
        if (!is_initialized) {
            db = FirebaseFirestore.getInstance();
            is_initialized = true;
        } else {
            db = getDb();
        }
    }

    /**
     * this gets the database if it has been initialized
     * otherwise it initializes it so that db is not null.
     * @return the database
     */
    public FirebaseFirestore getDb() {
        if (db == null) {
            this.db = FirebaseFirestore.getInstance();
        }
        return this.db;
    }

    /**
     * Check if a user exists in db based on the device id
     * completable future is async programming thus multiple ops can run concurrently w/o
     * blocking each other or having to wait for tasks in the queue to finish first
     * @param deviceId assume one per user
     * @return boolean to indicate true if deviceId is found else false
     */
    public CompletableFuture<Boolean> checkUserByDeviceId(String deviceId) {
        CollectionReference userRef = db.collection("users");
        Query query = userRef.whereEqualTo("deviceId", deviceId);
        CompletableFuture<Boolean> completableFuture = new CompletableFuture<>();

        query.get().addOnSuccessListener(querySnapshot -> {
            boolean userExists = !querySnapshot.isEmpty();
            completableFuture.complete(userExists);
        }).addOnFailureListener(completableFuture::completeExceptionally);
        return completableFuture;
    }

    /**
     * Check if a user exists in db based on the email
     * completable future is async programming thus multiple ops can run concurrently w/o
     * blocking each other or having to wait for tasks in the queue to finish first
     * @param email email; assume one per user
     * @return boolean to indicate true if email is found else false
     */
    public CompletableFuture<Boolean> checkUserByEmail(String email) {
        CollectionReference userRef = db.collection("users");
        Query query = userRef.whereEqualTo("email", email);
        CompletableFuture<Boolean> completableFuture = new CompletableFuture<>();

        query.get().addOnSuccessListener(querySnapshot -> {
            boolean userExists = !querySnapshot.isEmpty();
            completableFuture.complete(userExists);
        }).addOnFailureListener(completableFuture::completeExceptionally);
        return completableFuture;
    }

    /**
     * Add new user to db with their: deviceId, userName, role set
     * can also update user information
     * On success and on failure listeners to log errors.
     * @param deviceId device id
     * @param userName user name (not for sign-in but for profile info)
     * @param email user email requested as first time user
     * @param roleSet role (one of entrant, organizer, admin)
     * @param phoneNumber optional, can be null
     * @return Task<Void> note: successful return indicates that addUser was successful.
     */
    public Task<Void> addUser(String deviceId, String userName, String email, String role,
                              List<String> roleSet, @Nullable String phoneNumber,
                              @Nullable String profileImageUrl) {
        Map<String, Object> userData = new HashMap<>();
        userData.put("deviceId", deviceId);
        userData.put("username", userName);
        userData.put("email", email);
        userData.put("role", role);
        userData.put("roleSet", roleSet);  // this is actually a list
        userData.put("phoneNumber", phoneNumber);
        userData.put("profileImageUrl", profileImageUrl);
        //if (phoneNumber != null && !phoneNumber.isEmpty()) {
        //    userData.put("phoneNumber", phoneNumber);
        //}


        return db.collection("users").document(deviceId)
                .set(userData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });
    }

    /**
     * Fetch all users from db
     * @return CompletableFuture<List<Users>> aka a list of users (in the future, when the operation is complete)
     */
    public CompletableFuture<List<User>> fetchAllUsers() {
        db = getDb();
        CollectionReference userRef = db.collection("users");
        CompletableFuture<List<User>> completableFuture = new CompletableFuture<>();

        userRef.get().addOnSuccessListener(queryDocumentSnapshots -> {
            List<User> userList = new ArrayList<>();
            for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                User user = document.toObject(User.class);
                if (user != null) {
                    String userName = document.getString("username");
                    String profileImageUrl = document.getString("profileImageUrl");

                    user.setDeviceId(document.getString("deviceId"));
                    user.setUserName(userName);
                    user.setProfileImageUrl(profileImageUrl);

                    Log.d(TAG, "Username from Firestore: " + userName); // Log the username field
                    Log.d(TAG, "Profile Image URL from Firestore: " + profileImageUrl); // Log the profile image URL

                    userList.add(user);
                }
            }
            completableFuture.complete(userList);
        }).addOnFailureListener(completableFuture::completeExceptionally);
        return completableFuture;
    }

    /**
     * fetch user object using deviceId. Used in AdminProfileViewActivity
     * @param deviceId
     * @param listener
     */
    public void fetchUserByDeviceId(String deviceId, OnUserFetchedListener listener) {
        db.collection("users").whereEqualTo("deviceId", deviceId).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0); // get first queried object that is returned
                        User user = document.toObject(User.class);
                        user.setDeviceId(document.getString("deviceId"));
                        user.setUserName(document.getString("username"));
                        listener.onUserFetched(user);
                    } else {
                        listener.onUserFetched(null);
                    }
                }).addOnFailureListener(listener::onError);
    }

    public void updateUserByDeviceId(String deviceId, HashMap<String, Object> data, OnSuccessListener<Boolean> listener) {
        db.collection("users").whereEqualTo("deviceId", deviceId).get().addOnSuccessListener(docs -> {
            if (!docs.isEmpty()) {
                DocumentSnapshot doc = docs.getDocuments().get(0);
                DocumentReference docRef = doc.getReference();
                docRef.update(data).addOnSuccessListener(unused -> listener.onSuccess(Boolean.TRUE))
                        .addOnFailureListener(e -> listener.onSuccess(Boolean.FALSE));
            }
        });
    }

    public void getUserDocId(String deviceId, OnSuccessListener<String> listener) {
        db.collection("users").whereEqualTo("deviceId", deviceId).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot docs) {
                        DocumentSnapshot doc = docs.getDocuments().get(0);
                        listener.onSuccess(doc.getId());
                    }
                });
    }

    /**
     * interface; listener for user fetching
     */
    public interface OnUserFetchedListener {
        void onUserFetched(User user);
        void onError(Exception e);
    }


    /**
     * Interface; listener for deletion callbacks
     */

    public interface OnUserDeletedListener {
        void onUserDeleted();
        void onError(Exception e);
    }

    public void deleteUserByDeviceId(String deviceId, OnUserDeletedListener listener) {
        Log.d(TAG, "Deleting user with deviceId: " + deviceId);
        db.collection("users")
                .whereEqualTo("deviceId", deviceId).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        String docId = queryDocumentSnapshots.getDocuments().get(0).getId();
                        db.collection("users").document(docId).delete()
                                .addOnSuccessListener(a->listener.onUserDeleted())
                                .addOnFailureListener(listener::onError);
                    } else {
                        Log.e(TAG, "No user found with deviceId: " + deviceId);
                        listener.onError(new Exception("No user found with the given deviceId."));
                    }
                })
                .addOnFailureListener(listener::onError);
    }
    /**
     * Adds a user to the waiting list of a specific event in Firestore.
     *
     * @param waitingListId The ID of the waiting list to which the user should be added.
     * @param userId        The ID of the user to be added.
     * @param callback      A callback to handle success or failure of the operation.
     */
    public void addUserToWaitingList(String waitingListId, String userId, Callback callback) {
        DocumentReference waitingListRef = db.collection("waiting-list").document(waitingListId);

        waitingListRef.update("user-ids", FieldValue.arrayUnion(userId))
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "User added to waiting list successfully.");
                    callback.onSuccess();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to add user to waiting list: " + e.getMessage(), e);
                    callback.onFailure("Failed to add user to waiting list.");
                });
    }

    /**
     * Removes a user from the waiting list of a specific event in Firestore.
     *
     * @param waitingListId The ID of the waiting list from which the user should be removed.
     * @param userId        The ID of the user to be removed.
     * @param callback      A callback to handle success or failure of the operation.
     */
    public void removeUserFromWaitingList(String waitingListId, String userId, Callback callback) {
        DocumentReference waitingListRef = db.collection("waiting-list").document(waitingListId);

        waitingListRef.update("user-ids", FieldValue.arrayRemove(userId))
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "User removed from waiting list successfully.");
                    callback.onSuccess();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to remove user from waiting list: " + e.getMessage(), e);
                    callback.onFailure("Failed to remove user from waiting list.");
                });
    }

    /**
     * Callback interface for Firestore operations.
     */
    public interface Callback {
        void onSuccess();
        void onFailure(String errorMessage);
    }
}



