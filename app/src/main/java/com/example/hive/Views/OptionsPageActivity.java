package com.example.hive.Views;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hive.Events.EventDetailActivity;
import com.example.hive.R;

/**
 * Activity for displaying various options for an event.
 * This activity allows users to navigate to different sections such as invited entrants, waiting list, participants, etc.
 *
 * @author Aleena
 */
public class OptionsPageActivity extends AppCompatActivity {

    private String eventId;

    /**
     * Initializes the activity, sets up views, and handles button click events.
     *
     * @param savedInstanceState The saved state of the activity, if available.
     */
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
//            Intent intent = new Intent(OptionsPageActivity.this, EventDetailActivity.class);
//            intent.putExtra("eventId", eventId); // Pass eventId back to EventDetailActivity
//            startActivity(intent);
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
            // Navigate to invited entrants activity
            Intent i = new Intent(this, InvitedEntrantsActivity.class);
            i.putExtra("eventId", eventId);
            startActivity(i);
        });

        cancelledEntrantsButton.setOnClickListener(v -> {
            Intent intent = new Intent(OptionsPageActivity.this, CancelledListActivity.class);
            intent.putExtra("eventId", eventId); // Pass the eventId to WaitingListActivity
            startActivity(intent);
        });

        participantsButton.setOnClickListener(v -> {
            Intent intent = new Intent(OptionsPageActivity.this, FinalListActivity.class);
            intent.putExtra("eventId", eventId); // Pass the eventId to WaitingListActivity
            startActivity(intent);
        });

        viewEntrantMapButton.setOnClickListener(v -> {
            Intent intent = new Intent(OptionsPageActivity.this, EntrantMapActivity.class);
            intent.putExtra("eventId", eventId); // Pass the eventId to EntrantMapActivity
            startActivity(intent);
        });
    }
}
