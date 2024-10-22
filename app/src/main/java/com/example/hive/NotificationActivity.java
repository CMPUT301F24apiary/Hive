package com.example.hive;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class NotificationActivity extends AppCompatActivity {

    private final ArrayList<String> notifications = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications); // Ensure this matches your layout file

        // Sample notifications to display
        notifications.add("You have been chosen from the waiting list for Christmas Event.");
        notifications.add("You have not been chosen for Christmas Carol.");
        notifications.add("You have been chosen for Autumn Festival.");

        // Get references to TextViews and Buttons from the XML layout
        TextView notification1 = findViewById(R.id.notification1);
        Button acceptButton1 = findViewById(R.id.acceptButton1);
        Button declineButton1 = findViewById(R.id.declineButton1);

        TextView notification2 = findViewById(R.id.notification2);
        Button reRegisterButton2 = findViewById(R.id.reRegisterButton2);

        TextView notification3 = findViewById(R.id.notification3);
        Button acceptButton3 = findViewById(R.id.acceptButton3);
        Button declineButton3 = findViewById(R.id.declineButton3);

        // Set the text for each notification
        notification1.setText(notifications.get(0));
        notification2.setText(notifications.get(1));
        notification3.setText(notifications.get(2));

        // Set click listeners for buttons
        acceptButton1.setOnClickListener(v -> acceptEvent(notifications.get(0)));
        declineButton1.setOnClickListener(v -> declineEvent(notifications.get(0)));
        reRegisterButton2.setOnClickListener(v -> reRegisterEvent(notifications.get(1)));
        acceptButton3.setOnClickListener(v -> acceptEvent(notifications.get(2)));
        declineButton3.setOnClickListener(v -> declineEvent(notifications.get(2)));
    }

    private void acceptEvent(String eventName) {
        // Handle event acceptance
        Intent intent = new Intent(NotificationActivity.this, RegistrationActivity.class);
        intent.putExtra("eventName", eventName);
        startActivity(intent);
    }

    private void declineEvent(String eventName) {
        // Handle event decline logic
    }

    private void reRegisterEvent(String eventName) {
        // Handle re-registration logic
    }
}
