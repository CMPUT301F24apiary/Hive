package com.example.hive.Controllers;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.example.hive.EventListActivity;
import com.google.firebase.Firebase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FieldValue;

import java.util.HashMap;
import java.util.Map;

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
        String notificationId = intent.getStringExtra("notificationId");

        if (eventId == null || userId == null) {
            Log.e(TAG, "Event ID or User ID is missing in the intent.");
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        switch (action) {
            case "ACTION_ACCEPT":
                handleAcceptAction(context, eventId, userId, notificationId);
                break;

            case "ACTION_DECLINE":
                handleDeclineAction(context, eventId, userId, notificationId);
                break;

            case "ACTION_REREGISTER":
                handleReRegisterAction(context, db, eventId, userId, notificationId);
                break;

            default:
                Log.e(TAG, "Unknown action received: " + action);
                break;
        }
    }

    /**
     * Handles the "Accept" action by adding the user to the final participants list.
     */
    private void handleAcceptAction(Context context, String eventId, String userId, String notificationId) {
        new ListController().addUserToFinalList(eventId, userId, success -> {
            if (success) {
                Log.d(TAG, "User added to final participants list for event: " + eventId);
                Intent destinationIntent = new Intent(context, EventListActivity.class);
                destinationIntent.putExtra("eventId", eventId);
                destinationIntent.putExtra("userId", userId);

                // Since we're starting from a BroadcastReceiver, we need to add these flags
                destinationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                NotificationsController.clearNotification(userId, notificationId, deleteSuccess -> {});

                // Start the activity
                context.startActivity(destinationIntent);
            } else {
                Log.e(TAG, "Error adding user to final participants list for event: " + eventId);
            }
        });
    }

    /**
     * Handles the "Decline" action by adding the user to the canceled list.
     */
    private void handleDeclineAction(Context context, String eventId, String userId, String notificationId) {
        new ListController().addUserToCancelledList(eventId, userId, success -> {
            if (success) {
                Log.d(TAG, "User added to cancelled participants list for event: " + eventId);
                Intent destinationIntent = new Intent(context, EventListActivity.class);
                destinationIntent.putExtra("eventId", eventId);
                destinationIntent.putExtra("userId", userId);

                // Since we're starting from a BroadcastReceiver, we need to add these flags
                destinationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                NotificationsController.clearNotification(userId, notificationId, deleteSuccess -> {});

                // Start the activity
                context.startActivity(destinationIntent);
            } else {
                Log.e(TAG, "Error adding user to cancelled participants list for event: " + eventId);
            }
        });
    }

    /**
     * Handles the "Re-register" action by adding the user back to the waiting list.
     */
    private void handleReRegisterAction(Context context, FirebaseFirestore db, String eventId, String userId, String notificationId) {
        FirebaseController fbControl = new FirebaseController();
        new EventController().getSingleEvent(eventId, event -> {
            if (event.getGeolocation()) {
                Location location = getLastKnownLocation(context);
                if (location != null) {
                    Map<String, Object> locationData = new HashMap<>();
                    locationData.put("latitude", location.getLatitude());
                    locationData.put("longitude", location.getLongitude());
                    fbControl.addUserToWaitingListWithLocation(event.getWaitingListId(), userId, locationData, new FirebaseController.Callback() {
                        @Override
                        public void onSuccess() {
                            Log.d(TAG, "User re-registered to waiting list for event: " + eventId);
                            Intent destinationIntent = new Intent(context, EventListActivity.class);
                            destinationIntent.putExtra("eventId", eventId);
                            destinationIntent.putExtra("userId", userId);

                            // Since we're starting from a BroadcastReceiver, we need to add these flags
                            destinationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            NotificationsController.clearNotification(userId, notificationId, success -> {});
                            // Start the activity
                            context.startActivity(destinationIntent);
                        }

                        @Override
                        public void onFailure(String errorMessage) {
                            Log.e(TAG, "Error re-registering user to waiting list | " + errorMessage);
                        }
                    });
                } else {
                    Log.e("Re-register", "Error fetching location");
                }
            } else {
                fbControl.addUserToWaitingList(event.getWaitingListId(), userId, new FirebaseController.Callback() {
                    @Override
                    public void onSuccess() {
                        Log.d(TAG, "User re-registered to waiting list for event: " + eventId);
                        Intent destinationIntent = new Intent(context, EventListActivity.class);
                        destinationIntent.putExtra("eventId", eventId);
                        destinationIntent.putExtra("userId", userId);

                        // Since we're starting from a BroadcastReceiver, we need to add these flags
                        destinationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

                        // Start the activity
                        context.startActivity(destinationIntent);
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        Log.e(TAG, "Error re-registering user to waiting list | " + errorMessage);
                    }
                });
            }
        });
    }
    /**
     * Retrieves the user's last known location.
     *
     * @return The last known location or null if unavailable.
     */
    private Location getLastKnownLocation(Context context) {

        Location location = null;

        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        // Check if GPS provider is enabled
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Log.d(TAG, "getLastKnownLocation: GPS provider is enabled. Fetching location...");
            try {
                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (location != null) {
                    Log.d(TAG, "getLastKnownLocation: GPS Location fetched. Lat: " + location.getLatitude() + ", Lon: " + location.getLongitude());
                    return location;
                } else {
                    Log.w(TAG, "getLastKnownLocation: GPS Location is null.");
                }
            } catch (SecurityException e) {
                Log.e(TAG, "getLastKnownLocation: SecurityException when accessing GPS provider: " + e.getMessage());
            }
        } else {
            Log.w(TAG, "getLastKnownLocation: GPS provider is disabled.");
        }

        // Check if Network provider is enabled
        if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            Log.d(TAG, "getLastKnownLocation: Network provider is enabled. Fetching location...");
            try {
                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if (location != null) {
                    Log.d(TAG, "getLastKnownLocation: Network Location fetched. Lat: " + location.getLatitude() + ", Lon: " + location.getLongitude());
                    return location;
                } else {
                    Log.w(TAG, "getLastKnownLocation: Network Location is null.");
                }
            } catch (SecurityException e) {
                Log.e(TAG, "getLastKnownLocation: SecurityException when accessing Network provider: " + e.getMessage());
            }
        } else {
            Log.w(TAG, "getLastKnownLocation: Network provider is disabled.");
        }

        Log.e(TAG, "getLastKnownLocation: Unable to fetch location. Both GPS and Network providers returned null.");
        return null;
    }
}
