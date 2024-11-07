package com.example.hive;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;

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

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hive.AdminEvent.AdminEventListActivity;

public class EventListActivity extends AppCompatActivity {

    // Zach - DEV BUTTON
    private Button eventsButton;

    private ImageButton profileButton;

    private ImageButton notificationBellButton;

    private ImageButton notificationBellButton;  // New notification bell button
    private Button switchRolesButton;  // New role switch button

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

        profileButton = findViewById(R.id.profileButton);

        eventsButton = findViewById(R.id.admin_view_event_list);
        notificationBellButton = findViewById(R.id.notificationBellButton);

        eventsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(EventListActivity.this, AdminEventListActivity.class);
                startActivity(i);
            }
        });

        notificationBellButton = findViewById(R.id.notificationBellButton);
        switchRolesButton = findViewById(R.id.switchRolesButton);

        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EventListActivity.this, ProfileActivity.class);
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
