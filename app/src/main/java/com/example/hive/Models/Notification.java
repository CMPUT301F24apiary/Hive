package com.example.hive.Models;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.PropertyName;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a notification in the Hive application.
 * This class includes details such as the user ID, event ID, content, and type of notification.
 *
 * @author Aleena
 */
public class Notification {
    private String firebaseId; // Firestore document ID
    private String userId;     // The ID of the user this notification belongs to
    @PropertyName("eventid")
    private String eventId;    // The ID of the event related to this notification
    private String content;    // The content of the notification
    private String type;       // Type of notification (e.g., "win", "lose", "re-register")

    /**
     * No-argument constructor for Firestore.
     */
    public Notification() {}

    /**
     * Full constructor for creating a notification with specified details.
     *
     * @param userId  The ID of the user this notification belongs to.
     * @param eventId The ID of the event related to this notification.
     * @param content The content of the notification.
     * @param type    The type of the notification.
     */
    public Notification(String userId, String eventId, String content, String type) {
        this.userId = userId;
        this.eventId = eventId;
        this.content = content;
        this.type = type;
    }

    /**
     * Gets the Firestore document ID.
     *
     * @return The Firestore document ID.
     */
    @Exclude
    public String getFirebaseId() {
        return firebaseId;
    }

    /**
     * Sets the Firestore document ID.
     *
     * @param firebaseId The Firestore document ID.
     */
    public void setFirebaseId(String firebaseId) {
        this.firebaseId = firebaseId;
    }

    /**
     * Gets the user ID this notification belongs to.
     *
     * @return The user ID.
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Sets the user ID this notification belongs to.
     *
     * @param userId The user ID.
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * Gets the event ID related to this notification.
     *
     * @return The event ID.
     */
    @PropertyName("eventid")
    public String getEventId() {
        return eventId;
    }

    /**
     * Sets the event ID related to this notification.
     *
     * @param eventId The event ID.
     */
    @PropertyName("eventid")
    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    /**
     * Gets the content of the notification.
     *
     * @return The content of the notification.
     */
    public String getContent() {
        return content;
    }

    /**
     * Sets the content of the notification.
     *
     * @param content The content of the notification.
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * Gets the type of the notification.
     *
     * @return The type of the notification.
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the type of the notification.
     *
     * @param type The type of the notification.
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Uploads this notification to Firebase Firestore.
     */
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

    /**
     * Converts this notification object to a HashMap for Firebase.
     *
     * @return A map containing the notification data.
     */
    public Map<String, Object> toMap() {
        Map<String, Object> data = new HashMap<>();
        data.put("userId", userId);
        data.put("eventId", eventId);
        data.put("content", content);
        data.put("type", type);
        return data;
    }
}
