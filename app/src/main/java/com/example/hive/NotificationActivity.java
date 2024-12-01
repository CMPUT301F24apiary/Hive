package com.example.hive;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.hive.Controllers.EventController;
import com.example.hive.Controllers.FirebaseController;
import com.example.hive.Controllers.ListController;
import com.example.hive.Controllers.NotificationsController;
import com.example.hive.Models.Notification;
import com.example.hive.Models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NotificationActivity extends AppCompatActivity {


    private static final String TAG = NotificationActivity.class.getSimpleName();
    private LinearLayout notificationsContainer;
    private ArrayList<Notification> notifications = new ArrayList<>();
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        userId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        // Initialize views
        notificationsContainer = findViewById(R.id.notificationsContainer);
        ImageButton backButton = findViewById(R.id.backButton);

        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(NotificationActivity.this, EventListActivity.class);
            startActivity(intent);
            finish();
        });

        loadNotifications();
    }

    private void loadNotifications() {

        if (userId == null || userId.isEmpty()) {
            Log.e(TAG, "User ID is null or empty. Cannot load notifications.");
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        new ListController().fetchNotifications(userId, pendingNotifications -> {
            notifications = pendingNotifications;
            displayNotifications();
        });
    }

    // Method to get the current user ID, for example using Firebase Authentication.
    private String getCurrentUserId() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            return currentUser.getUid();
        } else {
            Log.e(TAG, "Current user is null.");
            return null;
        }
    }


    private void displayNotifications() {
        notificationsContainer.removeAllViews();

        if (notifications.isEmpty()) {
            findViewById(R.id.noNotificationsMessage).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.noNotificationsMessage).setVisibility(View.GONE);
            for (Notification notification : notifications) {
                addNotificationView(notification);
            }
        }
    }

    private void addNotificationView(Notification notification) {
        View notificationView = getLayoutInflater().inflate(R.layout.notification_item, notificationsContainer, false);

        TextView notificationTextView = notificationView.findViewById(R.id.notification_text);
        Button actionButton1 = notificationView.findViewById(R.id.action_button_1);
        Button actionButton2 = notificationView.findViewById(R.id.action_button_2);

        notificationTextView.setText(notification.getContent());

        switch (notification.getType()) {
            case "win":
                actionButton1.setText("Accept");
                actionButton2.setText("Decline");
                actionButton1.setOnClickListener(v -> acceptEvent(notification));
                actionButton2.setOnClickListener(v -> declineEvent(notification));
                break;
            case "lose":
                actionButton1.setText("Re-register");
                actionButton2.setVisibility(View.GONE);
                actionButton1.setOnClickListener(v -> reRegisterEvent(notification));
                break;
            default:
                actionButton2.setVisibility(View.GONE);
                actionButton1.setText("Clear");
                actionButton1.setOnClickListener(v -> {
                    NotificationsController.clearNotification(userId, notification.getFirebaseId(), success -> {
                        loadNotifications();
                    });
                });
        }

        notificationsContainer.addView(notificationView);
    }

    private void acceptEvent(Notification notification) {
        new ListController().addUserToFinalList(notification.getEventId(), userId, success -> {
            if (success) {
                NotificationsController.clearNotification(userId, notification.getFirebaseId(), deleteSuccess -> {
                    if (deleteSuccess) {
                        Log.d("AcceptEvent", "Cleared notification");
                        loadNotifications();
                    }
                });
            } else {
                Log.e("AcceptEvent", "Adding user to final list failed");
            }
        });
    }

    private void declineEvent(Notification notification) {
        new ListController().addUserToCancelledList(notification.getEventId(), userId, success -> {
            if (success) {
                NotificationsController.clearNotification(userId, notification.getFirebaseId(), deleteSuccess -> {
                    if (deleteSuccess) {
                        Log.d("AcceptEvent", "Cleared notification");
                        loadNotifications();
                    }
                });
            } else {
                Log.e("DeclineEvent", "Adding user to cancelled list failed");
            }
        });
    }

    private void reRegisterEvent(Notification notification) {
        new EventController().getSingleEvent(notification.getEventId(), event -> {
            if (event.getGeolocation()) {
                Location location = getLastKnownLocation();
                if (location != null) {
                    Map<String, Object> locationData = new HashMap<>();
                    locationData.put("latitude", location.getLatitude());
                    locationData.put("longitude", location.getLongitude());
                    new FirebaseController().addUserToWaitingListWithLocation(event.getWaitingListId(), userId, locationData, new FirebaseController.Callback() {
                        @Override
                        public void onSuccess() {
                            NotificationsController.clearNotification(userId, notification.getFirebaseId(), deleteSuccess -> {
                                if (deleteSuccess) {
                                    Log.d("AcceptEvent", "Cleared notification");
                                    loadNotifications();
                                }
                            });
                        }

                        @Override
                        public void onFailure(String errorMessage) {
                            Log.d("ReRegister", "Error adding back to wait list");
                        }
                    });
                } else {
                    Log.e("ReRegister", "Error fetching location");
                }
            } else {
                new FirebaseController().addUserToWaitingList(event.getWaitingListId(), userId, new FirebaseController.Callback() {
                    @Override
                    public void onSuccess() {
                        NotificationsController.clearNotification(userId, notification.getFirebaseId(), deleteSuccess -> {
                            if (deleteSuccess) {
                                Log.d("AcceptEvent", "Cleared notification");
                                loadNotifications();
                            }
                        });
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        Log.d("ReRegister", "Error adding back to wait list");
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
    private Location getLastKnownLocation() {

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        Location location = null;

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