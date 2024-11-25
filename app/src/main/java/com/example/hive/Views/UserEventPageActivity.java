package com.example.hive.Views;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.hive.Controllers.FirebaseController;
import com.example.hive.Events.Event;
import com.example.hive.R;

import java.util.Locale;

public class UserEventPageActivity extends AppCompatActivity {
    private ImageView eventImageView;
    private TextView eventTitle, eventDetails, eventDescription, participantsCount;
    private Button registerButton, unregisterButton;
    private FirebaseController firebaseController;
    private String eventId;
    private TextView locationTextView, costTextView, selectionDateTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_event_page);

        firebaseController = new FirebaseController();

        eventImageView = findViewById(R.id.eventImageView);
        eventTitle = findViewById(R.id.eventTitle);
        eventDetails = findViewById(R.id.eventDetails);
        eventDescription = findViewById(R.id.eventDescription);
        participantsCount = findViewById(R.id.participantsCount);
        registerButton = findViewById(R.id.registerButton);
        unregisterButton = findViewById(R.id.unregisterButton);

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
        Log.d("UserEventPageActivity", "Fetching details for Event ID: " + eventId);

        firebaseController.getDb().collection("events").document(eventId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Log.d("UserEventPageActivity", "Event found: " + documentSnapshot.getData());

                        Event event = documentSnapshot.toObject(Event.class);
                        if (event != null) {
                            Log.d("UserEventPageActivity", "Event successfully parsed: " + event.getTitle());
                            updateUIWithEventDetails(event);
                        } else {
                            Log.e("UserEventPageActivity", "Event object is null.");
                            Toast.makeText(this, "Failed to load event details.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e("UserEventPageActivity", "Event not found in Firebase.");
                        Toast.makeText(this, "Event not found.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("UserEventPageActivity", "Firebase query failed: " + e.getMessage(), e);
                    Toast.makeText(this, "Failed to fetch event details.", Toast.LENGTH_SHORT).show();
                });
    }




    private void updateUIWithEventDetails(Event event) {
        // Update the UI components with event details
        eventTitle.setText(event.getTitle());
        eventDescription.setText(event.getDescription());
        eventDetails.setText("Start: " + event.getStartDate() + " - End: " + event.getEndDate());
        //participantsCount.setText("Participants: " + event.getNumParticipants() + "/" + event.getEntrantLimit());
        locationTextView.setText("Location: " + event.getLocation());
        costTextView.setText("Cost: " + event.getCost());




        // Load the event poster image using Glide if available
        if (event.getPosterURL() != null && !event.getPosterURL().isEmpty()) {
            Glide.with(this)
                    .load(event.getPosterURL())
                    .placeholder(android.R.drawable.ic_menu_gallery) // Replace with your placeholder
                    .error(android.R.drawable.ic_menu_report_image) // Replace with your error image
                    .into(eventImageView);
        } else {
            eventImageView.setImageResource(android.R.drawable.ic_menu_gallery); // Replace with your default image
        }
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
            String userId = "your_user_id_here";
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
