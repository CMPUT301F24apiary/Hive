package com.example.hive;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
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
import com.example.hive.Models.User;
import com.example.hive.Views.FirstTimeActivity;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    private ActivityResultLauncher<Intent> roleSelectionLauncher;
    private User currentUser = User.getInstance();
    private FirebaseController firebaseController = new FirebaseController();
    private FirebaseFirestore db = firebaseController.getDb();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        enableEdgeToEdgeMode();

        // Create Notification Channel
        NotificationsController.createNotificationChannel(this);

        // Request Notification Permission for Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestNotificationPermission();
        }

        // Register activity result launcher
        roleSelectionLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent roleSelectionIntent = new Intent(this, RoleSelectionActivity.class);
                        startActivity(roleSelectionIntent);
                        finish();
                    } else {
                        Toast.makeText(this, "Role selection setup failed.", Toast.LENGTH_LONG).show();
                    }
                }
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        handleUserFlow();
    }

    // Method to retrieve the device ID
    public String retrieveDeviceId() {
        return Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    // Enable Edge-to-Edge mode
    private void enableEdgeToEdgeMode() {
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
            currentUser.setDeviceId(deviceId);
            firebaseController.checkUserByDeviceId(deviceId).thenAccept(isUserExisting -> {
                if (isUserExisting) {
                    Intent roleSelectionIntent = new Intent(this, RoleSelectionActivity.class);
                    startActivity(roleSelectionIntent);
                    finish();
                } else {
                    Intent firstTimeIntent = new Intent(this, FirstTimeActivity.class);
                    startActivity(firstTimeIntent);
                    finish(); // Prevent going back to MainActivity
                }
            }).exceptionally(e -> {
                Toast.makeText(this, "Error checking user existence.", Toast.LENGTH_LONG).show();
                return null;
            });
        } else {
            Toast.makeText(this, "Device ID could not be retrieved.", Toast.LENGTH_LONG).show();
        }
    }

    // Request notification permission for Android 13+
    private void requestNotificationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.POST_NOTIFICATIONS)) {
            new AlertDialog.Builder(this)
                    .setTitle("Notification Permission Needed")
                    .setMessage("This app needs notification permission to keep you updated about important events.")
                    .setPositiveButton("OK", (dialog, which) -> ActivityCompat.requestPermissions(
                            MainActivity.this,
                            new String[]{Manifest.permission.POST_NOTIFICATIONS},
                            100
                    ))
                    .setNegativeButton("Cancel", (dialog, which) -> {
                        dialog.dismiss();
                    })
                    .create()
                    .show();
        } else {
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
                Toast.makeText(this, "Notification permission granted.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Notification permission denied. Notifications might not be shown.", Toast.LENGTH_LONG).show();
            }
        }
    }
}
