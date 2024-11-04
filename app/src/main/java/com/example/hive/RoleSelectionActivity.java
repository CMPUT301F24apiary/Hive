package com.example.hive;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hive.AdminEvent.AdminEventListActivity;

public class RoleSelectionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role_selection);

        Button userButton = findViewById(R.id.userButton);
        Button organizerButton = findViewById(R.id.organizerButton);
        Button adminButton = findViewById(R.id.adminButton);

        // Navigate to the next screen when "User" is selected
        userButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RoleSelectionActivity.this, EventListActivity.class); // Replace EventListActivity with the actual target activity if different
                startActivity(intent);
            }
        });

        organizerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RoleSelectionActivity.this, OrganizerEventListActivity.class);
                startActivity(intent);
            }
        });

        adminButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RoleSelectionActivity.this, AdminEventListActivity.class);
                startActivity(intent);
            }
        });
    }
}
