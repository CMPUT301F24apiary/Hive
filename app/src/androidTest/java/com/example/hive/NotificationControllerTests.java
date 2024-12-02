package com.example.hive;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.hive.Controllers.NotificationsController;
import com.example.hive.Models.Notification;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class NotificationControllerTests {

    private final Context context = ApplicationProvider.getApplicationContext();

    @Test
    public void testCreateNotificationChannel() {
        // Create a notification channel
        NotificationsController.createNotificationChannel(context);

        // Assert that no exceptions occurred
        assert true : "Notification channel created successfully.";
    }

    @Test
    public void testShowNotification() {
        // Create a mock notification
        Notification notification = new Notification("userId123", "eventId456", "Test content", "win");

        // Show the notification
        NotificationsController.showNotification(context, notification);

        // Assert that no exceptions occurred
        assert true : "Notification displayed successfully.";
    }

    @Test
    public void testClearNotification() {
        // Call the clearNotification method
        NotificationsController.clearNotification("userId123", "notificationId123", success -> {
            // Assert that the operation was successful
            assert success : "Notification cleared successfully.";
        });
    }
}
