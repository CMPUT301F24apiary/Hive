package com.example.hive.Views;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.firestore.SetOptions;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.example.hive.Controllers.FirebaseController;
import com.example.hive.EventListActivity;
import com.example.hive.Events.Event;
import com.example.hive.Models.User;
import com.example.hive.R;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Activity to display detailed information about a specific event and provide the ability to
 * register or unregister for the event. Handles event details, user actions, and location-based
 * registration/unregistration using Firebase Firestore.
 *
 * @author Dina
 */
public class UserEventPageActivity extends AppCompatActivity {

    private static final String TAG = "UserEventPageActivity";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;

    private ImageView eventImageView;
    private TextView eventTitle, eventDetails, eventDescription, participantsCount;
    private Button registerButton, unregisterButton;
    private FirebaseController firebaseController;
    private String eventId;
    private TextView locationTextView, costTextView;
    private TextView dateTextView, timeTextView;
    private LocationManager locationManager;

    /**
     * Initializes the activity, retrieves the event ID from the Intent, and sets up the UI components.
     *
     * @param savedInstanceState The previously saved instance state, if any.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_event_page);

        firebaseController = new FirebaseController();

        // Initialize views
        eventImageView = findViewById(R.id.eventImageView);
        eventTitle = findViewById(R.id.eventTitle);
        eventDetails = findViewById(R.id.eventDetails);
        eventDescription = findViewById(R.id.eventDescription);
        participantsCount = findViewById(R.id.participantsCount);
        locationTextView = findViewById(R.id.locationTextView);
        costTextView = findViewById(R.id.costTextView);
        dateTextView = findViewById(R.id.dateTextView);
        timeTextView = findViewById(R.id.timeTextView);
        registerButton = findViewById(R.id.registerButton);
        unregisterButton = findViewById(R.id.unregisterButton);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // Get event ID from the Intent
        Intent intent = getIntent();
        eventId = intent.getStringExtra("SCAN_RESULT");

        Log.d(TAG, "Event ID received from Intent: " + eventId);

        if (eventId != null) {
            fetchEventDetails(eventId);
        } else {
            Log.e(TAG, "Event ID is null in onCreate.");
            Toast.makeText(this, "Invalid event ID", Toast.LENGTH_SHORT).show();
            finish();
        }

        setupButtonListeners();
    }

    /**
     * Fetches event details from Firestore and updates the UI with the retrieved data.
     *
     * @param eventId The ID of the event to fetch.
     */
    private void fetchEventDetails(String eventId) {
        firebaseController.getDb().collection("events").document(eventId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Event event = documentSnapshot.toObject(Event.class);
                        if (event != null) {
                            updateUIWithEventDetails(event);
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error fetching event details: " + e.getMessage()));
    }

    /**
     * Updates the UI with details of the fetched event.
     *
     * @param event The event object containing the details.
     */
    private void updateUIWithEventDetails(Event event) {
        if (event.getWaitingListId() == null || event.getWaitingListId().isEmpty()) {
            Toast.makeText(this, "Invalid event data.", Toast.LENGTH_SHORT).show();
            return;
        }

        String[] startDateAndTime = event.getDateAndTimeFromMS(event.getStartDateInMS());
        String[] endDateAndTime = event.getDateAndTimeFromMS(event.getEndDateInMS());

        dateTextView.setText(String.format(Locale.ENGLISH, "Date: %s", startDateAndTime[0]));
        timeTextView.setText(String.format(Locale.ENGLISH, "Time: %s - %s",
                startDateAndTime[1], endDateAndTime[1]));
        eventTitle.setText(event.getTitle());
        eventDescription.setText(String.format(Locale.ENGLISH, "Event Description: %s", event.getDescription()));
        participantsCount.setText(String.format(Locale.ENGLISH, "%d", event.getNumParticipants()));
        locationTextView.setText(String.format(Locale.ENGLISH, "Location: %s", event.getLocation()));
        costTextView.setText(String.format(Locale.ENGLISH, "$%s", event.getCost()));

        if (event.getPosterURL() != null && !event.getPosterURL().isEmpty()) {
            Glide.with(this).load(event.getPosterURL()).into(eventImageView);
        }
    }

    /**
     * Sets up listeners for the Register and Unregister buttons.
     */
    private void setupButtonListeners() {
        String userId = User.getInstance().getDeviceId();

        registerButton.setOnClickListener(v -> handleAction(true, userId));
        unregisterButton.setOnClickListener(v -> handleAction(false, userId));
    }

    /**
     * Handles user registration or unregistration for an event, with optional geolocation.
     *
     * @param isRegistration Whether the action is registration (true) or unregistration (false).
     * @param userId         The ID of the user performing the action.
     */
    private void handleAction(boolean isRegistration, String userId) {
        if (userId == null || userId.isEmpty()) {
            Toast.makeText(this, "User ID is missing.", Toast.LENGTH_SHORT).show();
            return;
        }

        firebaseController.getDb().collection("events").document(eventId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Event event = documentSnapshot.toObject(Event.class);
                        if (event != null) {
                            String waitingListId = event.getWaitingListId();
                            if (Boolean.TRUE.equals(event.getGeolocation())) {
                                showGeolocationWarning(() -> {
                                    Location location = getLastKnownLocation();
                                    if (location != null) {
                                        if (isRegistration) {
                                            addUserToWaitingListWithLocation(waitingListId, userId, location);
                                        } else {
                                            removeUserFromWaitingListWithLocation(waitingListId, userId, location);
                                        }
                                    } else {
                                        Toast.makeText(this, "Unable to fetch location.", Toast.LENGTH_SHORT).show();
                                    }
                                }, () -> {
                                    if (isRegistration) {
                                        removeUserFromWaitingList(waitingListId, userId); // Ensure removal on decline
                                    }
                                    Log.d(TAG, "Exiting activity after geolocation decline.");
                                    finish(); // Exit the activity
                                });
                            } else {
                                if (isRegistration) {
                                    addUserToWaitingList(waitingListId, userId);
                                } else {
                                    removeUserFromWaitingList(waitingListId, userId);
                                }
                            }
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error fetching event details: " + e.getMessage()));


    }

    /**
     * Displays a dialog to warn the user about geolocation requirements.
     *
     * @param onAcceptAction Action to perform if the user accepts.
     * @param onDeclineAction Action to perform if the user declines.
     */
    private void showGeolocationWarning(Runnable onAcceptAction, Runnable onDeclineAction) {
        new AlertDialog.Builder(this)
                .setTitle("Geolocation Required")
                .setMessage("This event requires location access. Do you want to proceed?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    Log.d(TAG, "User accepted geolocation access.");
                    onAcceptAction.run();
                })
                .setNegativeButton("No", (dialog, which) -> {
                    Log.d(TAG, "User declined geolocation access. Removing and exiting.");
                    onDeclineAction.run();
                })
                .show();
    }



    /**
     * Registers the user for an event without location.
     *
     * @param waitingListId The ID of the waiting list.
     * @param userId        The ID of the user.
     */
    private void addUserToWaitingList(String waitingListId, String userId) {
        User currentUser = User.getInstance(); // Get the current User instance

        // Create a map of fields to add to the waiting-list document
        Map<String, Object> userData = new HashMap<>();
        userData.put("username", currentUser.getUserName());
        userData.put("email", currentUser.getEmail());
        userData.put("deviceId", currentUser.getDeviceId());

        firebaseController.getDb().collection("events")
                .document(eventId) // Use the correct event ID
                .collection("waiting-list") // Update waiting-list sub-collection
                .document(userId) // Use userId as the document ID for uniqueness
                .set(userData, SetOptions.merge()) // Use SetOptions.merge() to prevent overwriting other fields
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Successfully added user to waiting list.");
                    Toast.makeText(UserEventPageActivity.this, "Successfully registered for the event!", Toast.LENGTH_SHORT).show();
                    navigateToEventListActivity();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to add user to waiting list: " + e.getMessage());
                    Toast.makeText(UserEventPageActivity.this, "Failed to register: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });

        // Add the user to the waiting list using the FirebaseController method
        firebaseController.addUserToWaitingList(waitingListId, userId, new FirebaseController.Callback() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "Successfully added user to waiting list through FirebaseController callback.");
            }

            @Override
            public void onFailure(String errorMessage) {
                Log.e(TAG, "Failed to add user to waiting list through FirebaseController callback: " + errorMessage);
            }
        });
    }





    /**
     * Unregisters the user from an event without location.
     *
     * @param waitingListId The ID of the waiting list.
     * @param userId        The ID of the user.
     */
    private void removeUserFromWaitingList(String waitingListId, String userId) {
        firebaseController.removeUserFromWaitingList(waitingListId, userId, new FirebaseController.Callback() {
            @Override
            public void onSuccess() {
                Toast.makeText(UserEventPageActivity.this, "Unregistered successfully!", Toast.LENGTH_SHORT).show();
                navigateToEventListActivity();
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(UserEventPageActivity.this, "Failed to unregister: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Registers the user for an event with location.
     *
     * @param waitingListId The ID of the waiting list.
     * @param userId        The ID of the user.
     * @param location      The user's location.
     */
    private void addUserToWaitingListWithLocation(String waitingListId, String userId, Location location) {
        Map<String, Object> locationData = new HashMap<>();
        locationData.put("latitude", location.getLatitude());
        locationData.put("longitude", location.getLongitude());

        firebaseController.addUserToWaitingListWithLocation(waitingListId, userId, locationData, new FirebaseController.Callback() {
            @Override
            public void onSuccess() {
                Toast.makeText(UserEventPageActivity.this, "Registered with location successfully!", Toast.LENGTH_SHORT).show();
                navigateToEventListActivity();
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(UserEventPageActivity.this, "Failed to register: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Unregisters the user from an event with location.
     *
     * @param waitingListId The ID of the waiting list.
     * @param userId        The ID of the user.
     * @param location      The user's location.
     */
    private void removeUserFromWaitingListWithLocation(String waitingListId, String userId, Location location) {
        firebaseController.removeUserFromWaitingListWithLocation(waitingListId, userId, new FirebaseController.Callback() {
            @Override
            public void onSuccess() {
                Toast.makeText(UserEventPageActivity.this, "Unregistered with location successfully!", Toast.LENGTH_SHORT).show();
                navigateToEventListActivity();
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(UserEventPageActivity.this, "Failed to unregister: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Retrieves the user's last known location.
     *
     * @return The last known location or null if unavailable.
     */
    private Location getLastKnownLocation() {
        Log.d(TAG, "getLastKnownLocation: Checking location permissions...");
        // Check for location permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "getLastKnownLocation: Location permission not granted.");
            checkLocationPermission();
            return null;
        }

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


    /**
     * Checks and requests location permissions if not already granted.
     */
    private void checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    /**
     * Navigates the user to the EventListActivity.
     */
    private void navigateToEventListActivity() {
        Intent intent = new Intent(UserEventPageActivity.this, EventListActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
