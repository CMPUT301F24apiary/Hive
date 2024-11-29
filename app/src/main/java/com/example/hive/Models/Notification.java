package com.example.hive.Models;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Notification {
    private String firebaseId; // Firestore document ID
    private String userId;     // The ID of the user this notification belongs to
    private String eventId;    // The ID of the event related to this notification
    private String content;    // The content of the notification
    private String type;       // Type of notification (e.g., "win", "lose", "re-register")

    // No-arg constructor for Firestore
    public Notification() {}

    // Full constructor
    public Notification(String userId, String eventId, String content, String type) {
        this.userId = userId;
        this.eventId = eventId;
        this.content = content;
        this.type = type;
    }

    // Getters and setters
    @Exclude
    public String getFirebaseId() {
        return firebaseId;
    }

    public void setFirebaseId(String firebaseId) {
        this.firebaseId = firebaseId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    // Method to upload itself to Firestore
    public void uploadToFirebase() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("notification")
                .add(this)
                .addOnSuccessListener(documentReference -> {
                    this.firebaseId = documentReference.getId();
                })
                .addOnFailureListener(e -> {
                    // Handle failure (e.g., log error or show a user message)
                });
    }

    // Helper to convert object to a HashMap for Firebase
    public Map<String, Object> toMap() {
        Map<String, Object> data = new HashMap<>();
        data.put("userId", userId);
        data.put("eventId", eventId);
        data.put("content", content);
        data.put("type", type);
        return data;
    }
}
