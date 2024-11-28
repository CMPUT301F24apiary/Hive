package com.example.hive.Controllers;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
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
        /*
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
        */
    }

    /**
     * Show a notification with a title and message.
     * Includes a check for the `POST_NOTIFICATIONS` permission.
     * @param context The application context.
     * @param notificationId Unique ID for the notification.
     * @param title The title of the notification.
     * @param message The content of the notification.
     */
    public static void showNotification(Context context, int notificationId, String title, String message) {

        // Check if the app has the required permission to post notifications
        Log.d("Test", String.valueOf(Build.VERSION.SDK_INT));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13+ (API level 33)
            if (context.checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS)
                    != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted; log or handle this scenario
                return;
            }
        }


        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)  // Replace with your app's icon
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(notificationId, builder.build());
    }
}
