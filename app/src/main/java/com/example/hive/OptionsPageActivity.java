package com.example.hive;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hive.Events.EventDetailActivity;

public class OptionsPageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_optionspage);

        // Initialize TextViews
        TextView invitedEntrantsButton = findViewById(R.id.invited_entrants_btn);
        TextView waitingListButton = findViewById(R.id.waiting_list_btn);
        TextView cancelledEntrantsButton = findViewById(R.id.cancelled_entrants_btn);
        TextView participantsButton = findViewById(R.id.participants_btn);
        TextView viewEntrantMapButton = findViewById(R.id.view_entrant_map_btn);

        // Back button logic
        findViewById(R.id.back_button).setOnClickListener(v -> {
            Intent intent = new Intent(OptionsPageActivity.this, EventDetailActivity.class);
            startActivity(intent);
            finish();
        });

        // Set click listeners for buttons
        invitedEntrantsButton.setOnClickListener(v -> {
            // Placeholder logic for Invited Entrants
        });

        waitingListButton.setOnClickListener(v -> {
            // Navigate to WaitingListActivity
            Intent intent = new Intent(OptionsPageActivity.this, WaitingListActivity.class);
            startActivity(intent);
        });

        cancelledEntrantsButton.setOnClickListener(v -> {
            // Placeholder logic for Cancelled Entrants
        });

        participantsButton.setOnClickListener(v -> {
            // Placeholder logic for Participants
        });

        viewEntrantMapButton.setOnClickListener(v -> {
            // Placeholder logic for Viewing Entrant Map
        });
    }
}
