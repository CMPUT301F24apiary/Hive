/**
 * ProfileActivity.java
 *
 * This activity displays the user's profile information such as name, username, email,
 * and phone number. It allows the user to edit their profile by navigating to the ProfileEditActivity.
 *
 * <p>Outstanding Issues:
 * - None at this time.</p>
 *
 * @author Dina
 * @version 1.0
 */
package com.example.hive;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    private Button editProfileButton;
    private ImageView backArrow;
    private TextView personNameText, userNameText, emailText, phoneText;

    /**
     * Called when the activity is starting. This is where most initialization should be done.
     * It sets up the UI and initializes buttons and text fields for displaying profile information.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down, this Bundle contains the data it most recently supplied in onSaveInstanceState. Otherwise, it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        editProfileButton = findViewById(R.id.editProfileButton);

        backArrow = findViewById(R.id.backArrow);

        personNameText = findViewById(R.id.personName);
        userNameText = findViewById(R.id.userName);
        emailText = findViewById(R.id.emailLabel);
        phoneText = findViewById(R.id.phoneLabel);

        loadProfileData();

        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Notify the user when the Edit Profile button is clicked
                Toast.makeText(ProfileActivity.this, "Edit Profile clicked", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(ProfileActivity.this, ProfileEditActivity.class);
                startActivityForResult(intent, 1);
            }
        });

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Notify the user when the back arrow is clicked
                Toast.makeText(ProfileActivity.this, "Back arrow clicked", Toast.LENGTH_SHORT).show();

                finish();
            }
        });
    }

    /**
     * Loads profile data from SharedPreferences and displays it in the corresponding TextViews.
     */
    private void loadProfileData() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserProfile", MODE_PRIVATE);
        String personName = sharedPreferences.getString("personName", "Person Name");
        String userName = sharedPreferences.getString("userName", "User Name");
        String email = sharedPreferences.getString("email", "user@google.com");
        String phone = sharedPreferences.getString("phone", "(780) xxx - xxxx");

        personNameText.setText(personName);
        userNameText.setText(userName);
        emailText.setText("E-mail: " + email);
        phoneText.setText("Phone: " + phone);
    }

    /**
     * Called when returning from the ProfileEditActivity.
     * Reloads the profile data to reflect any changes.
     *
     * @param requestCode The request code used to start the activity.
     * @param resultCode The result code returned by the child activity.
     * @param data Any additional data returned by the child activity.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            // Reload the profile data if the result is OK
            loadProfileData();
        }
    }
}

