package com.example.hive;

import com.google.firebase.Firebase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

/**
 * Singleton class to initialize connection to firebase and keep it consistent across the app
 * @author Zach
 */
public class FirebaseManager {

    /**
     * Represent instance of this class
     */
    private static FirebaseManager instance = null;

    /**
     * Store connection to firebase firestore database
     */
    private FirebaseFirestore db;

    /**
     * Private constructor initializes connection to firestore
     */
    private FirebaseManager() {
        this.db = FirebaseFirestore.getInstance();
    }

    /**
     * Public method to create an instance if one does not already exist, and return the instance.
     * Ensures only one instance can exist.
     * @return The active instance
     */
    public static synchronized FirebaseManager getInstance() {
        if (instance == null) {
            instance = new FirebaseManager();
        }
        return instance;
    }

    public FirebaseFirestore getDB() {
        return this.db;
    }

}
