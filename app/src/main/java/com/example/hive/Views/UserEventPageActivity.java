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

import com.bumptech.glide.Glide;
import com.example.hive.Controllers.FirebaseController;
import com.example.hive.EventListActivity;
import com.example.hive.Events.Event;
import com.example.hive.Models.User;
import com.example.hive.R;

import java.util.Locale;

public class UserEventPageActivity extends AppCompatActivity {
    private ImageView eventImageView;
    private TextView eventTitle, eventDetails, eventDescription, participantsCount;
    private Button registerButton, unregisterButton;
    private FirebaseController firebaseController;
    private String eventId;
    private TextView locationTextView, costTextView;
    private TextView dateTextView, timeTextView;

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

        if (eventId != null) {
            Log.d("UserEventPageActivity", "Event ID: " + eventId);
            fetchEventDetails(eventId);
        } else {
            Toast.makeText(this, "Invalid event ID", Toast.LENGTH_SHORT).show();
            finish();
        }

        setupButtonListeners();
    }

    private void fetchEventDetails(String eventId) {
        firebaseController.getDb().collection("events").document(eventId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Log.d("UserEventPageActivity", "Document Snapshot: " + documentSnapshot.getData());
                        Event event = documentSnapshot.toObject(Event.class);
                        if (event != null) {
                            updateUIWithEventDetails(event);
                        } else {
                            Log.e("UserEventPageActivity", "Event object is null.");
                            Toast.makeText(this, "Failed to load event details.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e("UserEventPageActivity", "Event document not found in Firestore.");
                        Toast.makeText(this, "Event not found.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("UserEventPageActivity", "Error fetching event details: " + e.getMessage());
                    Toast.makeText(this, "Failed to fetch event details.", Toast.LENGTH_SHORT).show();
                });
    }

    private void updateUIWithEventDetails(Event event) {
        Log.d("UserEventPageActivity", "Event Details: " + event.getTitle());
        Log.d("UserEventPageActivity", "Waiting List ID: " + event.getWaitingListId());

        if (event.getWaitingListId() == null || event.getWaitingListId().isEmpty()) {
            Log.e("UserEventPageActivity", "Invalid waitingListId in event.");
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
        participantsCount.setText(String.format(Locale.ENGLISH, "Participants: %d", event.getNumParticipants()));
        locationTextView.setText(String.format(Locale.ENGLISH, "Location: %s", event.getLocation()));
        costTextView.setText(String.format(Locale.ENGLISH, "$%s", event.getCost()));

        // Save the waitingListId for later actions
        this.eventId = event.getWaitingListId();

        // Load image using Glide
        if (event.getPosterURL() != null && !event.getPosterURL().isEmpty()) {
            Glide.with(this).load(event.getPosterURL()).into(eventImageView);
        }
    }

    private void setupButtonListeners() {
        String userId = User.getInstance().getDeviceId();

        registerButton.setOnClickListener(v -> {
            if (userId == null || userId.isEmpty()) {
                Toast.makeText(UserEventPageActivity.this, "User ID is missing. Cannot register.", Toast.LENGTH_SHORT).show();
                Log.e("UserEventPageActivity", "User ID is null or empty.");
                return;
            }

            // Fetch event details to check geolocation requirement
            firebaseController.getDb().collection("events").document(eventId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            Event event = documentSnapshot.toObject(Event.class);
                            if (event != null) {
                                Log.d("UserEventPageActivity", "Geolocation required: " + event.isGeolocationRequired());
                                if (Boolean.TRUE.equals(event.isGeolocationRequired())) {
                                    showGeolocationWarning(() -> addUserToWaitingList(userId));
                                } else {
                                    addUserToWaitingList(userId);
                                }
                            } else {
                                Log.e("UserEventPageActivity", "Event object is null.");
                                Toast.makeText(this, "Failed to fetch event details.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.e("UserEventPageActivity", "Event document not found in Firestore.");
                            Toast.makeText(this, "Event not found.", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("UserEventPageActivity", "Error fetching event details: " + e.getMessage());
                        Toast.makeText(this, "Failed to fetch event details.", Toast.LENGTH_SHORT).show();
                    });
        });

        unregisterButton.setOnClickListener(v -> {
            if (userId == null || userId.isEmpty()) {
                Toast.makeText(UserEventPageActivity.this, "User ID is missing. Cannot unregister.", Toast.LENGTH_SHORT).show();
                Log.e("UserEventPageActivity", "User ID is null or empty.");
                return;
            }

            firebaseController.removeUserFromWaitingList(eventId, userId, new FirebaseController.Callback() {
                @Override
                public void onSuccess() {
                    Toast.makeText(UserEventPageActivity.this, "Successfully unregistered from the event!", Toast.LENGTH_SHORT).show();
                    navigateToEventListActivity();
                }

                @Override
                public void onFailure(String errorMessage) {
                    Toast.makeText(UserEventPageActivity.this, "Failed to unregister: " + errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void showGeolocationWarning(Runnable onAcceptAction) {
        new AlertDialog.Builder(this)
                .setTitle("Geolocation Required")
                .setMessage("This event requires access to your location. Do you want to proceed?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    if (isLocationEnabled()) {
                        onAcceptAction.run();
                    } else {
                        Toast.makeText(this, "Please enable location services to proceed.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private void addUserToWaitingList(String userId) {
        firebaseController.addUserToWaitingList(eventId, userId, new FirebaseController.Callback() {
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

    private void navigateToEventListActivity() {
        Intent intent = new Intent(UserEventPageActivity.this, EventListActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
