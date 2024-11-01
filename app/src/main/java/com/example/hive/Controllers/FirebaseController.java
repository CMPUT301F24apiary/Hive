package com.example.hive.Controllers;

import static android.content.ContentValues.TAG;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

/**
 * A singleton class that creates an instance of the Firestore database
 */
public class FirebaseController {
    private FirebaseFirestore db;
    private static boolean is_initialized = false;

    /**
     * Constructor for FirebaseController
     * Uses the boolean to avoid multiple instances
     * @param db database
     */
    public FirebaseController(FirebaseFirestore db) {
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

    /**
     * Check if a user exists in based on the device id
     * @param deviceId device id; assume one per user
     * @return QuerySnapshot of all documents assoc with device id
     */
    public Task<QuerySnapshot> getUserByDeviceId(String deviceId) {
        return db.collection("users")
                .whereEqualTo("deviceId", deviceId)
                .get();
    }

    /**
     * Add new user to db with their:
     * On success and on failure listeners to log errors.
     * @param deviceId device id
     * @param userName user name (not for sign-in but for profile info)
     * @param role role (one of entrant, organizer, admin)
     * @return Task<Void> note: successful return indicates that addUser was successful.
     */
    public Task<Void> addUser(String deviceId, String userName, String role) {
        Map<String, Object> userData = new HashMap<>();
        userData.put("deviceId", deviceId);
        userData.put("username", userName);
        userData.put("role", role);
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