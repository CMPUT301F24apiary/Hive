/**
 * EventListActivity.java
 *
 * This activity displays a list of events to the user. It includes an ImageButton that allows
 * the user to navigate to their profile page.
 *
 * <p>Outstanding Issues:
 * - None at this time.</p>
 *
 * @author Dina
 * @version 1.0
 */
package com.example.hive;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.hive.AdminEvent.AdminEventListActivity;
import com.example.hive.Controllers.FirebaseController;
import com.example.hive.Models.User;
import com.example.hive.Views.AdminProfileViewActivity;

public class EventListActivity extends AppCompatActivity {

    // Zach - DEV BUTTON
    private Button eventsButton;

    private ImageButton profileButton;

    private ImageButton notificationBellButton;  // Only one declaration for notificationBellButton
    private Button switchRolesButton;  // New role switch button

    private User user;

    private FirebaseController firebaseController;
    /**
     * Called when the activity is starting. This is where most initialization should be done.
     * It sets up the UI and initializes the profile button and notification bell button.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down, this Bundle contains the data it most recently supplied in onSaveInstanceState. Otherwise, it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list);

        firebaseController = new FirebaseController();

        profileButton = findViewById(R.id.profileButton);
        eventsButton = findViewById(R.id.admin_view_event_list);
        notificationBellButton = findViewById(R.id.notificationBellButton);
        switchRolesButton = findViewById(R.id.switchRolesButton);

        String deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        eventsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(EventListActivity.this, AdminEventListActivity.class);
                startActivity(i);
            }
        });

        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EventListActivity.this, ProfileActivity.class);
                intent.putExtra("deviceId", deviceId);
                startActivity(intent);
            }
        });

        notificationBellButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EventListActivity.this, NotificationActivity.class);
                startActivity(intent);
            }
        });

        switchRolesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EventListActivity.this, RoleSelectionActivity.class);
                startActivity(intent);
            }
        });
    }
}
