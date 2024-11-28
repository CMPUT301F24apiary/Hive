package com.example.hive;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.hive.Controllers.FirebaseController;
import com.example.hive.Controllers.NotificationsController;
import com.example.hive.Controllers.InvitedController;
import com.example.hive.Models.User;
import com.example.hive.Views.FirstTimeActivity;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private ActivityResultLauncher<Intent> roleSelectionLauncher;
    private User currentUser = User.getInstance();
    private FirebaseController firebaseController = new FirebaseController();
    private InvitedController invitedController = new InvitedController();
    private FirebaseFirestore db = firebaseController.getDb();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        enableEdgeToEdgeMode();

        // Log to indicate the start of onCreate
        Log.d(TAG, "onCreate: Starting the MainActivity");

        // Create Notification Channel
        NotificationsController.createNotificationChannel(this);
        Log.d(TAG, "onCreate: Notification channel created");

        // Request Notification Permission for Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Log.d(TAG, "onCreate: Android version is 13 or above, requesting notification permission");
            requestNotificationPermission();
        } else {
            Log.d(TAG, "onCreate: Android version is below 13, no need to request notification permission");
        }

        // Register activity result launcher
        roleSelectionLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Log.d(TAG, "onCreate: Role selection completed successfully");
                        Intent roleSelectionIntent = new Intent(this, RoleSelectionActivity.class);
                        startActivity(roleSelectionIntent);
                        finish();
                    } else {
                        Log.e(TAG, "onCreate: Role selection setup failed");
                        Toast.makeText(this, "Role selection setup failed.", Toast.LENGTH_LONG).show();
                    }
                }
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: Resumed MainActivity");
        handleUserFlow();

        // Check if user has any pending notifications (winning/losing/re-registering)
        checkForPendingNotifications();
    }

    // Method to retrieve the device ID
    public String retrieveDeviceId() {
        Log.d(TAG, "retrieveDeviceId: Retrieving device ID");
        return Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    // Enable Edge-to-Edge mode
    private void enableEdgeToEdgeMode() {
        Log.d(TAG, "enableEdgeToEdgeMode: Enabling edge-to-edge UI");
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    // Handle user flow based on whether user exists
    private void handleUserFlow() {
        String deviceId = retrieveDeviceId();
        if (deviceId != null && !deviceId.isEmpty()) {
            Log.d(TAG, "handleUserFlow: Device ID retrieved: " + deviceId);
            currentUser.setDeviceId(deviceId);
            firebaseController.checkUserByDeviceId(deviceId).thenAccept(isUserExisting -> {
                if (isUserExisting) {
                    Log.d(TAG, "handleUserFlow: User exists, navigating to RoleSelectionActivity");
                    Intent roleSelectionIntent = new Intent(this, RoleSelectionActivity.class);
                    startActivity(roleSelectionIntent);
                    finish();
                } else {
                    Log.d(TAG, "handleUserFlow: User does not exist, navigating to FirstTimeActivity");
                    Intent firstTimeIntent = new Intent(this, FirstTimeActivity.class);
                    startActivity(firstTimeIntent);
                    finish();
                }
            }).exceptionally(e -> {
                Log.e(TAG, "handleUserFlow: Error checking user existence", e);
                Toast.makeText(this, "Error checking user existence.", Toast.LENGTH_LONG).show();
                return null;
            });
        } else {
            Log.e(TAG, "handleUserFlow: Device ID could not be retrieved");
            Toast.makeText(this, "Device ID could not be retrieved.", Toast.LENGTH_LONG).show();
        }
    }

    // Check for any pending notifications for the current user
    private void checkForPendingNotifications() {
        String deviceId = currentUser.getDeviceId();
        String eventID = "YOUR_EVENT_ID_HERE"; // Replace with the actual event ID

        if (deviceId != null && !deviceId.isEmpty()) {
            invitedController.checkIfUserIsInvited(eventID, deviceId, isInvited -> {
                if (isInvited) {
                    // User is invited, notify them to accept or decline
                    invitedController.notifyUserWin(this, deviceId);
                } else {
                    // Handle if user was not invited (e.g. lost or re-register case)
                    invitedController.getWaitingListUIDs(eventID, waitingList -> {
                        if (waitingList.contains(deviceId)) {
                            // User was not chosen, notify them they were not selected
                            invitedController.notifyUserLose(this, deviceId);
                        } else {
                            // User is being re-invited due to someone declining
                            invitedController.notifyUserReRegister(this, deviceId);
                        }
                    });
                }
            });
        }
    }

    // Request notification permission for Android 13+
    private void requestNotificationPermission() {
        Log.d(TAG, "requestNotificationPermission: Requesting notification permission");
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.POST_NOTIFICATIONS)) {
            Log.d(TAG, "requestNotificationPermission: Showing rationale for notification permission");
            new AlertDialog.Builder(this)
                    .setTitle("Notification Permission Needed")
                    .setMessage("This app needs notification permission to keep you updated about important events.")
                    .setPositiveButton("OK", (dialog, which) -> ActivityCompat.requestPermissions(
                            MainActivity.this,
                            new String[]{Manifest.permission.POST_NOTIFICATIONS},
                            100
                    ))
                    .setNegativeButton("Cancel", (dialog, which) -> {
                        Log.d(TAG, "requestNotificationPermission: User denied the permission rationale");
                        dialog.dismiss();
                    })
                    .create()
                    .show();
        } else {
            Log.d(TAG, "requestNotificationPermission: Directly requesting notification permission");
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.POST_NOTIFICATIONS},
                    100
            );
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "onRequestPermissionsResult: Notification permission granted");
                Toast.makeText(this, "Notification permission granted.", Toast.LENGTH_SHORT).show();
            } else {
                Log.e(TAG, "onRequestPermissionsResult: Notification permission denied");
                Toast.makeText(this, "Notification permission denied. Notifications might not be shown.", Toast.LENGTH_LONG).show();
            }
        }
    }
}
