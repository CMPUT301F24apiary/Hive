package com.example.hive;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hive.AdminEvent.AdminEventListActivity;
import com.example.hive.Controllers.FirebaseController;
import com.example.hive.Models.User;

import java.util.Set;

/**
 * RoleSelectionActivity.java
 *
 * This activity allows users to select their role as a User, Organizer, or Admin.
 * Depending on the selected role, the activity navigates users to the respective
 * screen for their role, providing a customized experience for each type.
 *
 * <p>Outstanding Issues:
 * - None at this time.</p>
 *
 * @author Aleena
 * @version 1.0
 */
public class RoleSelectionActivity extends AppCompatActivity {
    private FirebaseController firebaseController;
    private static final String TAG = "RoleSelectionActivity";

    /**
     * Called when the activity is first created. Sets up the UI components and handles button clicks
     * to navigate to different activities based on the role selected.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
     *                           this Bundle contains the most recent data.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseController = new FirebaseController();
        String deviceId = deviceId();
        setContentView(R.layout.activity_role_selection);

        // Initialize role selection buttons
        Button userButton = findViewById(R.id.userButton);
        Button organizerButton = findViewById(R.id.organizerButton);
        Button adminButton = findViewById(R.id.adminButton);

        adminButton.setVisibility(View.GONE);

        // fetch user info
        firebaseController.fetchUserByDeviceId(deviceId, new FirebaseController.OnUserFetchedListener() {
            @Override
            public void onUserFetched(User user) {
                if (user.getRoleList().contains("admin")) {
                    adminButton.setVisibility(View.VISIBLE);
                    // Navigate to the AdminEventListActivity when "Admin" is selected
                    adminButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(RoleSelectionActivity.this, AdminEventListActivity.class);
                            startActivity(intent);
                        }
                    });
                }
            }
            @Override
            public void onError(Exception e) {
                Log.e(TAG, "Error fetching user by device id", e);
            }
        });

        // Navigate to the next screen when "User" is selected
        userButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to EventListActivity if "User" role is selected
                Intent intent = new Intent(RoleSelectionActivity.this, EventListActivity.class);
                startActivity(intent);
            }
        });

        // Navigate to the OrganizerEventListActivity when "Organizer" is selected
        organizerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RoleSelectionActivity.this, OrganizerEventListActivity.class);
                startActivity(intent);
            }
        });


    }

    /**
     * return the device id of the user
     * @return
     */
    private String deviceId() {
        return Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
    }
}
