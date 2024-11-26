package com.example.hive;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.hive.Controllers.FirebaseController;
import com.example.hive.Models.User;
import com.example.hive.Views.FirstTimeActivity;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    // Variables for role selection and Firebase
    private ActivityResultLauncher<Intent> roleSelectionLauncher;
    private User currentUser = User.getInstance();
    private FirebaseController firebaseController = new FirebaseController();
    private FirebaseFirestore db = firebaseController.getDb();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

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

        // Retrieve the device ID
        String deviceId = retrieveDeviceId();
        if (deviceId != null && !deviceId.isEmpty()) {
            // Save the device ID to the User singleton
            currentUser.setDeviceId(deviceId);

            // Check if the user already exists in Firestore
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

        // Other developer/debugging buttons can remain here
        // Uncomment and add as needed
        /*
        Button eventsButton = findViewById(R.id.view_events_button);
        eventsButton.setOnClickListener(v -> {
            Intent i = new Intent(MainActivity.this, AdminEventListActivity.class);
            startActivity(i);
        });
        */
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
}



