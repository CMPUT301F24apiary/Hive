package com.example.hive;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class OptionsPageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_optionspage);

        // Initialize buttons
        Button invitedEntrantsButton = findViewById(R.id.invited_entrants_btn);
        Button waitingListButton = findViewById(R.id.waiting_list_btn);
        Button cancelledEntrantsButton = findViewById(R.id.cancelled_entrants_btn);
        Button participantsButton = findViewById(R.id.participants_btn);
        Button viewEntrantMapButton = findViewById(R.id.view_entrant_map_btn);

        // Set click listeners
        invitedEntrantsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Placeholder logic
            }
        });

        waitingListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to WaitingListActivity
                Intent intent = new Intent(OptionsPageActivity.this, WaitingListActivity.class);
                startActivity(intent);
            }
        });

        cancelledEntrantsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Placeholder logic
            }
        });

        participantsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Placeholder logic
            }
        });

        viewEntrantMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Placeholder logic
            }
        });
    }
}
