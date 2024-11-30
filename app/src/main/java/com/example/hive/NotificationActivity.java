package com.example.hive;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hive.Models.Notification;
import com.example.hive.Models.User;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;

public class NotificationActivity extends AppCompatActivity {


    private static final String TAG = NotificationActivity.class.getSimpleName();
    private LinearLayout notificationsContainer;
    private ArrayList<Notification> notifications = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        // Initialize views
        notificationsContainer = findViewById(R.id.notificationsContainer);
        ImageButton backButton = findViewById(R.id.backButton);

        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(NotificationActivity.this, EventListActivity.class);
            startActivity(intent);
            finish();
        });

        loadNotifications();
    }

    private void loadNotifications() {
        String userId = getCurrentUserId(); // Retrieve the current user's ID

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(userId).collection("notifications")
                .orderBy("timestamp", Query.Direction.DESCENDING) // Order by the newest first
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    notifications.clear();
                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                        Notification notification = document.toObject(Notification.class);
                        if (notification != null) {
                            notification.setFirebaseId(document.getId());
                            notifications.add(notification);
                        }
                    }
                    displayNotifications();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to load notifications", e);
                });
    }

    private void displayNotifications() {
        notificationsContainer.removeAllViews();

        if (notifications.isEmpty()) {
            findViewById(R.id.noNotificationsMessage).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.noNotificationsMessage).setVisibility(View.GONE);
            for (Notification notification : notifications) {
                addNotificationView(notification);
            }
        }
    }

    private void addNotificationView(Notification notification) {
        View notificationView = getLayoutInflater().inflate(R.layout.notification_item, notificationsContainer, false);

        TextView notificationTextView = notificationView.findViewById(R.id.notification_text);
        Button actionButton1 = notificationView.findViewById(R.id.action_button_1);
        Button actionButton2 = notificationView.findViewById(R.id.action_button_2);

        notificationTextView.setText(notification.getContent());

        switch (notification.getType()) {
            case "win":
                actionButton1.setText("Accept");
                actionButton2.setText("Decline");
                actionButton1.setOnClickListener(v -> acceptEvent(notification));
                actionButton2.setOnClickListener(v -> declineEvent(notification));
                break;
            case "lose":
                actionButton1.setText("Re-register");
                actionButton2.setVisibility(View.GONE);
                actionButton1.setOnClickListener(v -> reRegisterEvent(notification));
                break;
        }

        notificationsContainer.addView(notificationView);
    }

    private void acceptEvent(Notification notification) {
        // Handle acceptance logic, such as updating Firestore and navigating to a registration activity
    }

    private void declineEvent(Notification notification) {
        // Handle decline logic, such as updating Firestore
    }

    private void reRegisterEvent(Notification notification) {
        // Handle re-registration logic, such as updating Firestore
    }

    private String getCurrentUserId() {
        return User.getInstance().getDeviceId(); // Example to retrieve user ID from current user
    }
}
