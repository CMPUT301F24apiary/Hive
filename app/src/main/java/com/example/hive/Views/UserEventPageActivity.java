package com.example.hive.Views;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hive.Controllers.FirebaseController;
import com.example.hive.EventListActivity;
import com.example.hive.Events.Event;
import com.example.hive.Models.User;
import com.example.hive.R;

import java.util.Locale;

/**
 * Activity to display detailed information about a specific event and provide the ability to
 * register or unregister for the event. Handles fetching event details from Firestore,
 * updating the UI, and interacting with waiting lists.
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

    /**
     * Initializes the activity, including fetching event details and setting up UI components.
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
     * Fetches event details from Firestore using the event ID.
     *
     * @param eventId The ID of the event to fetch details for.
     */
    private void fetchEventDetails(String eventId) {
        Log.d(TAG, "Fetching event details for Event ID: " + eventId);
        firebaseController.getDb().collection("events").document(eventId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Log.d(TAG, "Event document found: " + documentSnapshot.getData());
                        Event event = documentSnapshot.toObject(Event.class);
                        if (event != null) {
                            Log.d(TAG, "Event object successfully created: " + event.getTitle());
                            updateUIWithEventDetails(event);
                        } else {
                            Log.e(TAG, "Event object is null after Firestore conversion.");
                        }
                    } else {
                        Log.e(TAG, "Event not found in Firestore for Event ID: " + eventId);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error fetching event details: " + e.getMessage());
                });
    }

    /**
     * Updates the UI with the details of the fetched event.
     *
     * @param event The event object containing the details.
     */
    private void updateUIWithEventDetails(Event event) {
        Log.d(TAG, "Updating UI with event details. Event Title: " + event.getTitle());

        if (event.getWaitingListId() == null || event.getWaitingListId().isEmpty()) {
            Log.e(TAG, "Invalid waitingListId in event.");
            Toast.makeText(this, "Invalid event data. Cannot register.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Update UI components
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

        Log.d(TAG, "Event UI updated successfully.");
    }

    /**
     * Sets up click listeners for the Register and Unregister buttons.
     */
    private void setupButtonListeners() {
        String userId = User.getInstance().getDeviceId();
        Log.d(TAG, "User ID for registration: " + userId);

        registerButton.setOnClickListener(v -> {
            if (userId == null || userId.isEmpty()) {
                Toast.makeText(UserEventPageActivity.this, "User ID is missing. Cannot register.", Toast.LENGTH_SHORT).show();
                return;
            }

            firebaseController.getDb().collection("events").document(eventId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            Event event = documentSnapshot.toObject(Event.class);
                            if (event != null) {
                                String waitingListId = event.getWaitingListId(); // Fetch the correct waiting list ID
                                if (waitingListId == null || waitingListId.isEmpty()) {
                                    Toast.makeText(this, "Waiting list ID is invalid.", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                if (Boolean.TRUE.equals(event.getGeolocation())) {
                                    showGeolocationWarning(() -> addUserToWaitingList(waitingListId, userId));
                                } else {
                                    addUserToWaitingList(waitingListId, userId);
                                }
                            } else {
                                Toast.makeText(this, "Failed to fetch event details.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(this, "Event not found.", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("UserEventPageActivity", "Error fetching event: " + e.getMessage());
                        Toast.makeText(this, "Failed to fetch event details.", Toast.LENGTH_SHORT).show();
                    });
        });

        unregisterButton.setOnClickListener(v -> {
            Log.d(TAG, "Unregister button clicked. Event ID: " + eventId);

            if (userId == null || userId.isEmpty()) {
                Toast.makeText(this, "User ID is missing. Cannot unregister.", Toast.LENGTH_SHORT).show();
                return;
            }

            firebaseController.getDb().collection("events").document(eventId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            Event event = documentSnapshot.toObject(Event.class);
                            if (event != null) {
                                String waitingListId = event.getWaitingListId(); // Fetch the correct waiting list ID
                                if (waitingListId == null || waitingListId.isEmpty()) {
                                    Toast.makeText(this, "Waiting list ID is invalid. Cannot unregister.", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                firebaseController.removeUserFromWaitingList(waitingListId, userId, new FirebaseController.Callback() {
                                    @Override
                                    public void onSuccess() {
                                        Log.d(TAG, "Successfully unregistered from event.");
                                        Toast.makeText(UserEventPageActivity.this, "Successfully unregistered from the event!", Toast.LENGTH_SHORT).show();
                                        navigateToEventListActivity();
                                    }

                                    @Override
                                    public void onFailure(String errorMessage) {
                                        Log.e(TAG, "Failed to unregister: " + errorMessage);
                                        Toast.makeText(UserEventPageActivity.this, "Failed to unregister: " + errorMessage, Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else {
                                Toast.makeText(this, "Failed to fetch event details.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(this, "Event not found.", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error fetching event: " + e.getMessage());
                        Toast.makeText(this, "Failed to fetch event details.", Toast.LENGTH_SHORT).show();
                    });
        });

    }

    /**
     * Displays a warning dialog for events requiring geolocation.
     *
     * @param onAcceptAction A Runnable to execute if the user accepts the warning.
     */
    private void showGeolocationWarning(Runnable onAcceptAction) {
        Log.d(TAG, "Displaying geolocation warning dialog.");
        new AlertDialog.Builder(this)
                .setTitle("Geolocation Required")
                .setMessage("This event requires access to your location. Do you want to proceed?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    if (isLocationEnabled()) {
                        Log.d(TAG, "Location services enabled. Proceeding with registration.");
                        onAcceptAction.run();
                    } else {
                        Log.e(TAG, "Location services disabled. Cannot proceed.");
                        Toast.makeText(this, "Please enable location services to proceed.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("No", (dialog, which) -> Log.d(TAG, "Geolocation warning declined by user."))
                .show();
    }

    /**
     * Checks if location services are enabled on the device.
     *
     * @return True if location services are enabled, false otherwise.
     */
    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean isEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        Log.d(TAG, "Location enabled: " + isEnabled);
        return isEnabled;
    }

    /**
     * Adds the user to the event's waiting list.
     *
     * @param waitingListId The ID of the waiting list.
     * @param userId The ID of the user to add.
     */
    private void addUserToWaitingList(String waitingListId, String userId) {
        firebaseController.addUserToWaitingList(waitingListId, userId, new FirebaseController.Callback() {
            @Override
            public void onSuccess() {
                Toast.makeText(UserEventPageActivity.this, "Successfully registered for the event!", Toast.LENGTH_SHORT).show();
                navigateToEventListActivity();
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(UserEventPageActivity.this, "Failed to register: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Navigates the user back to the event list activity.
     */
    private void navigateToEventListActivity() {
        Log.d(TAG, "Navigating to EventListActivity.");
        Intent intent = new Intent(UserEventPageActivity.this, EventListActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}

