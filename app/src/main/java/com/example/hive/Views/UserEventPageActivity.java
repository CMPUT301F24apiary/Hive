package com.example.hive.Views;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.hive.Controllers.FirebaseController;
import com.example.hive.Events.Event;
import com.example.hive.R;

public class UserEventPageActivity extends AppCompatActivity {
    private ImageView eventImageView;
    private TextView eventTitle, eventDetails, eventDescription, participantsCount;
    private Button registerButton, unregisterButton;
    private FirebaseController firebaseController;
    private String eventId; // This will store the scanned event ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_event_page);

        // Initialize FirebaseController
        firebaseController = new FirebaseController();

        // Initialize UI components
        eventImageView = findViewById(R.id.eventImageView);
        eventTitle = findViewById(R.id.eventTitle);
        eventDetails = findViewById(R.id.eventDetails);
        eventDescription = findViewById(R.id.eventDescription);
        participantsCount = findViewById(R.id.participantsCount);
        registerButton = findViewById(R.id.registerButton);
        unregisterButton = findViewById(R.id.unregisterButton);

        // Get the event ID passed from the QR scanner
        Intent intent = getIntent();
        eventId = intent.getStringExtra("SCAN_RESULT");

        if (eventId != null) {
            // Fetch event details using the event ID
            fetchEventDetails(eventId);
        } else {
            Toast.makeText(this, "Invalid event ID", Toast.LENGTH_SHORT).show();
            finish(); // Close the activity if event ID is missing
        }

        // Set up click listeners for Register and Unregister buttons
        setupButtonListeners();
    }

    private void fetchEventDetails(String eventId) {
        firebaseController.getDb().collection("events").document(eventId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Map the document data to an Event object
                        Event event = documentSnapshot.toObject(Event.class);
                        if (event != null) {
                            updateUIWithEventDetails(event);
                        }
                    } else {
                        Toast.makeText(this, "Event not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to fetch event details: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void updateUIWithEventDetails(Event event) {
        // Update the UI components with event details
        eventTitle.setText(event.getTitle());
        eventDetails.setText(event.getStartDate() + " - " + event.getEndDate() + " at " + event.getLocation());
        eventDescription.setText(event.getDescription());
        participantsCount.setText(String.valueOf(event.getNumParticipants()));

        // Load the event image using Glide
        Glide.with(this)
                .load(event.getPosterURL()) // Use getPosterURL here instead of getImageUrl
                .into(eventImageView);
    }


    private void setupButtonListeners() {
        registerButton.setOnClickListener(v -> {
            String userId = "your_user_id_here"; // Replace with actual user ID logic
            firebaseController.addUserToWaitingList(eventId, userId, new FirebaseController.Callback() {
                @Override
                public void onSuccess() {
                    Toast.makeText(UserEventPageActivity.this, "Successfully registered for the event!", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(String errorMessage) {
                    Toast.makeText(UserEventPageActivity.this, "Failed to register: " + errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        });

        unregisterButton.setOnClickListener(v -> {
            String userId = "your_user_id_here"; // Replace with actual user ID logic
            firebaseController.removeUserFromWaitingList(eventId, userId, new FirebaseController.Callback() {
                @Override
                public void onSuccess() {
                    Toast.makeText(UserEventPageActivity.this, "Successfully unregistered from the event!", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(String errorMessage) {
                    Toast.makeText(UserEventPageActivity.this, "Failed to unregister: " + errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        });

    }
}
