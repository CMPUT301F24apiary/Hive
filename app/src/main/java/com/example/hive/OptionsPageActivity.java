package com.example.hive;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hive.Events.EventDetailActivity;

public class OptionsPageActivity extends AppCompatActivity {

    private String eventId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_optionspage);

        // Get the eventId from the Intent
        eventId = getIntent().getStringExtra("eventId");
        if (eventId == null) {
            finish();
            return;
        }

        // Initialize TextViews
        TextView invitedEntrantsButton = findViewById(R.id.invited_entrants_btn);
        TextView waitingListButton = findViewById(R.id.waiting_list_btn);
        TextView cancelledEntrantsButton = findViewById(R.id.cancelled_entrants_btn);
        TextView participantsButton = findViewById(R.id.participants_btn);
        TextView viewEntrantMapButton = findViewById(R.id.view_entrant_map_btn);

        // Back button logic
        findViewById(R.id.back_button).setOnClickListener(v -> {
            Intent intent = new Intent(OptionsPageActivity.this, EventDetailActivity.class);
            intent.putExtra("eventId", eventId); // Pass eventId back to EventDetailActivity
            startActivity(intent);
            finish();
        });

        // Set click listeners for buttons
        waitingListButton.setOnClickListener(v -> {
            // Navigate to WaitingListActivity
            Intent intent = new Intent(OptionsPageActivity.this, WaitingListActivity.class);
            intent.putExtra("eventId", eventId); // Pass the eventId to WaitingListActivity
            startActivity(intent);
        });

        invitedEntrantsButton.setOnClickListener(v -> {
            // Placeholder for Invited Entrants logic
        });

        cancelledEntrantsButton.setOnClickListener(v -> {
            // Placeholder for Cancelled Entrants logic
        });

        participantsButton.setOnClickListener(v -> {
            // Placeholder for Participants logic
        });

        viewEntrantMapButton.setOnClickListener(v -> {
            // Placeholder for Viewing Entrant Map logic
        });
    }
}
