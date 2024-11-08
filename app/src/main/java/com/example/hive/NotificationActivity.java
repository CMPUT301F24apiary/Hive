package com.example.hive;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

/**
 * NotificationActivity.java
 *
 * This activity displays a list of notifications to the user, providing options to
 * accept, decline, or re-register for specific events. Notifications inform users
 * of their status on event waiting lists and enable quick response actions.
 *
 * <p>Outstanding Issues:
 * - None at this time.</p>
 *
 * @author Aleena
 * @version 1.0
 */

public class NotificationActivity extends AppCompatActivity {

    private final ArrayList<String> notifications = new ArrayList<>();

    /**
     * Called when the activity is first created. Initializes the view, sets up notifications,
     * and configures the event buttons.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
     *                           this Bundle contains the most recent data.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        // Back button functionality
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate back to EventListActivity
                Intent intent = new Intent(NotificationActivity.this, EventListActivity.class);
                startActivity(intent);
                finish(); // Optional: close the NotificationActivity
            }
        });

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

    /**
     * Sets up a notification with accept and decline buttons.
     *
     * @param notificationId  The ID of the TextView that displays the notification message.
     * @param acceptButtonId  The ID of the accept button for the notification.
     * @param declineButtonId The ID of the decline button for the notification.
     * @param message         The notification message to display.
     */
    protected void setupNotification(int notificationId, int acceptButtonId, int declineButtonId, String message) {
        TextView notification = findViewById(notificationId);
        notification.setText(message);

        Button acceptButton = findViewById(acceptButtonId);
        acceptButton.setOnClickListener(v -> acceptEvent(message));

        Button declineButton = findViewById(declineButtonId);
        declineButton.setOnClickListener(v -> declineEvent(message));
    }

    /**
     * Sets up a notification with a re-register button.
     *
     * @param notificationId     The ID of the TextView that displays the notification message.
     * @param reRegisterButtonId The ID of the re-register button for the notification.
     * @param message            The notification message to display.
     */
    protected void setupNotification(int notificationId, int reRegisterButtonId, String message) {
        TextView notification = findViewById(notificationId);
        notification.setText(message);

        Button reRegisterButton = findViewById(reRegisterButtonId);
        reRegisterButton.setOnClickListener(v -> reRegisterEvent(message));
    }

    /**
     * Handles the event acceptance by navigating to the RegistrationActivity.
     *
     * @param eventName The name of the event being accepted.
     */
    protected void acceptEvent(String eventName) {
        Intent intent = new Intent(NotificationActivity.this, RegistrationActivity.class);
        intent.putExtra("eventName", eventName);
        startActivity(intent);
    }

    /**
     * Handles the event decline action.
     *
     * @param eventName The name of the event being declined.
     */
    protected void declineEvent(String eventName) {
        // Handle event decline logic
    }

    /**
     * Handles the re-registration action for the given event.
     *
     * @param eventName The name of the event for which the user wants to re-register.
     */
    protected void reRegisterEvent(String eventName) {
        // Handle re-registration logic
    }
}
