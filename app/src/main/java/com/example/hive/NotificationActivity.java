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
        setContentView(R.layout.activity_notifications);

        // Sample notifications to display
        notifications.add("You have been chosen from the waiting list for Christmas Event.");
        notifications.add("You have not been chosen for Christmas Carol.");
        notifications.add("You have been chosen for Autumn Festival.");
        notifications.add("You have not been chosen for Autumn U-Pick.");

        // Setup the notifications and buttons
        setupNotification(R.id.notification1, R.id.acceptButton1, R.id.declineButton1, notifications.get(0));
        setupNotification(R.id.notification2, R.id.reRegisterButton2, notifications.get(1));
        setupNotification(R.id.notification3, R.id.acceptButton3, R.id.declineButton3, notifications.get(2));
        setupNotification(R.id.notification4, R.id.reRegisterButton4, notifications.get(3));
    }

    private void setupNotification(int notificationId, int acceptButtonId, int declineButtonId, String message) {
        TextView notification = findViewById(notificationId);
        notification.setText(message);

        Button acceptButton = findViewById(acceptButtonId);
        acceptButton.setOnClickListener(v -> acceptEvent(message));

        Button declineButton = findViewById(declineButtonId);
        declineButton.setOnClickListener(v -> declineEvent(message));
    }

    private void setupNotification(int notificationId, int reRegisterButtonId, String message) {
        TextView notification = findViewById(notificationId);
        notification.setText(message);

        Button reRegisterButton = findViewById(reRegisterButtonId);
        reRegisterButton.setOnClickListener(v -> reRegisterEvent(message));
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
