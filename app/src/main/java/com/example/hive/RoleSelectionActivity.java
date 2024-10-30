package com.example.hive;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class RoleSelectionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role_selection);

        Button userButton = findViewById(R.id.userButton);
        Button organizerButton = findViewById(R.id.organizerButton);

        // Navigate to the next screen when "User" is selected
        userButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RoleSelectionActivity.this, EventListActivity.class); // Replace EventListActivity with the actual target activity if different
                startActivity(intent);
            }
        });

        // Organizer button currently does nothing
        organizerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // No action for Organizer button as of now
            }
        });
    }
}