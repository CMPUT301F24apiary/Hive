package com.example.hive;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hive.AdminEvent.AdminEventListActivity;

/**
 * RoleSelectionActivity allows users to select their role as a User, Organizer, or Admin.
 * Based on the selected role, it navigates the user to the appropriate screen.
 */
public class RoleSelectionActivity extends AppCompatActivity {

    /**
     * Called when the activity is first created. Sets up the UI components and handles button clicks
     * to navigate to different activities based on the role selected.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
     *                           this Bundle contains the most recent data.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role_selection);

        // Initialize role selection buttons
        Button userButton = findViewById(R.id.userButton);
        Button organizerButton = findViewById(R.id.organizerButton);
        Button adminButton = findViewById(R.id.adminButton);

        // Navigate to the next screen when "User" is selected
        userButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to EventListActivity if "User" role is selected
                Intent intent = new Intent(RoleSelectionActivity.this, EventListActivity.class);
                startActivity(intent);
            }
        });

        // Navigate to the OrganizerEventListActivity when "Organizer" is selected
        organizerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RoleSelectionActivity.this, OrganizerEventListActivity.class);
                startActivity(intent);
            }
        });

        // Navigate to the AdminEventListActivity when "Admin" is selected
        adminButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RoleSelectionActivity.this, AdminEventListActivity.class);
                startActivity(intent);
            }
        });
    }
}
