package com.example.hive.Controllers;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class NotificationsController {

    public static final String CHANNEL_ID = "HiveNotifications";
    private static final String CHANNEL_NAME = "Hive App Notifications";
    private static final String CHANNEL_DESCRIPTION = "Notifications for Hive app";

    /**
     * Create the notification channel.
     * Should be called once during app initialization (e.g., in MainActivity or a custom Application class).
     */
    public static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription(CHANNEL_DESCRIPTION);

            NotificationManager manager = context.getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    /**
     * Show a notification with a title and message.
     * Includes a check for the `POST_NOTIFICATIONS` permission.
     *
     * @param context        The application context.
     * @param notificationId Unique ID for the notification.
     * @param title          The title of the notification.
     * @param message        The content of the notification.
     */
    public static void showNotification(Context context, int notificationId, String title, String message) {
        // Check if the app has the required permission to post notifications
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13+ (API level 33)
            if (context.checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS)
                    != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                // Log permission denial and return
                Log.e("NotificationsController", "Permission to post notifications is not granted.");
                return;
            }
        }

        // Create the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)  // Replace with your app's actual icon
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true); // Notification will disappear when clicked

        // Check if the channel ID is properly set for Android O+ versions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription(CHANNEL_DESCRIPTION);
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }

        // Show the notification, with a try-catch block to handle potential SecurityException
        try {
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.notify(notificationId, builder.build());
            Log.d("NotificationsController", "Notification with ID: " + notificationId + " has been posted.");
        } catch (SecurityException e) {
            Log.e("NotificationsController", "Failed to post notification due to missing permissions", e);
        }
    }

    /**
     * Show a notification with a title and message, using a unique ID based on the current time.
     * This method is suitable for handling dynamic scenarios such as winning, losing, and re-registering.
     *
     * @param context The application context.
     * @param title   The title of the notification.
     * @param message The content of the notification.
     */
    public static void showNotification(Context context, String title, String message, String type, String eventTitle) {
        // Check if the app has the required permission to post notifications
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13+ (API level 33)
            if (context.checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS)
                    != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                // Log permission denial and return
                Log.e("NotificationsController", "Permission to post notifications is not granted.");
                return;
            }
        }

        // Create the notification with the appropriate actions (Accept/Decline or Re-register)
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)  // Replace with your app's icon
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH) // Use high priority for system tray notifications
                .setAutoCancel(true); // Notification will disappear when clicked

        // Adding action buttons for notifications
        Intent intentAccept = new Intent(context, NotificationActionReceiver.class);
        Intent intentDecline = new Intent(context, NotificationActionReceiver.class);
        Intent intentReRegister = new Intent(context, NotificationActionReceiver.class);

        PendingIntent pendingIntentAccept = null;
        PendingIntent pendingIntentDecline = null;
        PendingIntent pendingIntentReRegister = null;

        // Setting intents depending on the type of notification
        switch (type) {
            case "win":
                // Adding Accept and Decline buttons
                intentAccept.setAction("ACTION_ACCEPT");
                intentAccept.putExtra("eventTitle", eventTitle);
                pendingIntentAccept = PendingIntent.getBroadcast(context, 0, intentAccept, PendingIntent.FLAG_UPDATE_CURRENT);

                intentDecline.setAction("ACTION_DECLINE");
                intentDecline.putExtra("eventTitle", eventTitle);
                pendingIntentDecline = PendingIntent.getBroadcast(context, 1, intentDecline, PendingIntent.FLAG_UPDATE_CURRENT);

                builder.addAction(android.R.drawable.ic_input_add, "Accept", pendingIntentAccept);
                builder.addAction(android.R.drawable.ic_delete, "Decline", pendingIntentDecline);
                break;

            case "lose":
                // Adding Re-register button
                intentReRegister.setAction("ACTION_REREGISTER");
                intentReRegister.putExtra("eventTitle", eventTitle);
                pendingIntentReRegister = PendingIntent.getBroadcast(context, 2, intentReRegister, PendingIntent.FLAG_UPDATE_CURRENT);

                builder.addAction(android.R.drawable.ic_menu_rotate, "Re-register", pendingIntentReRegister);
                break;
        }

        // Show the notification, with a try-catch block to handle potential SecurityException
        try {
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            int notificationId = (int) System.currentTimeMillis(); // Generate a unique ID using the current time
            notificationManager.notify(notificationId, builder.build());
            Log.d("NotificationsController", "Dynamic notification posted with title: " + title);
        } catch (SecurityException e) {
            Log.e("NotificationsController", "Failed to post notification due to missing permissions", e);
        }
    }
}