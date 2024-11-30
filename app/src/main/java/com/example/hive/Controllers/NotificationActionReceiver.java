package com.example.hive.Controllers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FieldValue;

/**
 * This class handles actions for notifications such as "Accept", "Decline", and "Re-register".
 */
public class NotificationActionReceiver extends BroadcastReceiver {

    private static final String TAG = "NotificationActionReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null || intent.getAction() == null) {
            Log.e(TAG, "Received null intent or action.");
            return;
        }

        String action = intent.getAction();
        String eventId = intent.getStringExtra("eventId");
        String userId = intent.getStringExtra("userId");

        if (eventId == null || userId == null) {
            Log.e(TAG, "Event ID or User ID is missing in the intent.");
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        switch (action) {
            case "ACTION_ACCEPT":
                handleAcceptAction(db, eventId, userId);
                break;

            case "ACTION_DECLINE":
                handleDeclineAction(db, eventId, userId);
                break;

            case "ACTION_REREGISTER":
                handleReRegisterAction(db, eventId, userId);
                break;

            default:
                Log.e(TAG, "Unknown action received: " + action);
                break;
        }
    }

    /**
     * Handles the "Accept" action by adding the user to the final participants list.
     */
    private void handleAcceptAction(FirebaseFirestore db, String eventId, String userId) {
        db.collection("events").document(eventId).update("finalParticipants", FieldValue.arrayUnion(userId))
                .addOnSuccessListener(aVoid -> Log.d(TAG, "User added to final participants list for event: " + eventId))
                .addOnFailureListener(e -> Log.e(TAG, "Error adding user to final participants list", e));
    }

    /**
     * Handles the "Decline" action by adding the user to the canceled list.
     */
    private void handleDeclineAction(FirebaseFirestore db, String eventId, String userId) {
        db.collection("events").document(eventId).update("cancelledParticipants", FieldValue.arrayUnion(userId))
                .addOnSuccessListener(aVoid -> Log.d(TAG, "User added to cancelled list for event: " + eventId))
                .addOnFailureListener(e -> Log.e(TAG, "Error adding user to cancelled list", e));
    }

    /**
     * Handles the "Re-register" action by adding the user back to the waiting list.
     */
    private void handleReRegisterAction(FirebaseFirestore db, String eventId, String userId) {
        db.collection("waiting-list").document(eventId).update("user-ids", FieldValue.arrayUnion(userId))
                .addOnSuccessListener(aVoid -> Log.d(TAG, "User re-registered to waiting list for event: " + eventId))
                .addOnFailureListener(e -> Log.e(TAG, "Error re-registering user to waiting list", e));
    }
}
