package com.example.hive.Controllers;

import static android.content.ContentValues.TAG;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

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
     * Check if a user exists in db based on the device id
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
    public Task<Void> addUser(String deviceId, String userName, String email, List<String> roleSet, @Nullable String phoneNumber) {
        Map<String, Object> userData = new HashMap<>();
        userData.put("deviceId", deviceId);
        userData.put("username", userName);
        userData.put("email", email);
        userData.put("role", roleSet);
        if (phoneNumber != null && !phoneNumber.isEmpty()) {
            userData.put("phoneNumber", phoneNumber);
        }

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


}
