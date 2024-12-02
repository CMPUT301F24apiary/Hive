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

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.hive.Controllers.FirebaseController;
import com.example.hive.Models.User;
import com.example.hive.Views.AdminProfileViewActivity;

public class ProfileActivity extends AppCompatActivity {

    FirebaseController firebaseController;

    private Button editProfileButton;
    private ImageView backArrow;
    private ImageView profilePicture;  // You missed initializing the profile picture in your earlier code.
    private TextView personNameText, emailText, phoneText;
    private String deviceId;

    /**
     * Called when the activity is starting. This is where most initialization should be done.
     * It sets up the UI and initializes buttons and text fields for displaying profile information.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down, this Bundle contains the data it most recently supplied in onSaveInstanceState. Otherwise, it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        firebaseController = new FirebaseController();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize the views
        editProfileButton = findViewById(R.id.editProfileButton);
        backArrow = findViewById(R.id.backArrow);
        profilePicture = findViewById(R.id.imageViewProfileImage); // Make sure you have this defined in your layout XML file
        personNameText = findViewById(R.id.personName);
        emailText = findViewById(R.id.emailLabel);
        phoneText = findViewById(R.id.phoneLabel);

        deviceId = getIntent().getStringExtra("deviceId");

        loadProfileData(deviceId);

        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Notify the user when the Edit Profile button is clicked
                Toast.makeText(ProfileActivity.this, "Edit Profile clicked", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(ProfileActivity.this, ProfileEditActivity.class);
                intent.putExtra("deviceId", deviceId);
                startActivityForResult(intent, 1);
            }
        });

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(ProfileActivity.this, "Back arrow clicked", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    /**
     * refresh the profile activity when user edits profile.
     */
    @Override
    protected void onResume() {
        super.onResume();
        loadProfileData(deviceId);
    }

    /**
     * Loads profile data from Firebase and displays it in the corresponding TextViews.
     */
    public void loadProfileData(String deviceId) {
        firebaseController.fetchUserByDeviceId(deviceId, new FirebaseController.OnUserFetchedListener() {
            @Override
            public void onUserFetched(User user) {
                if (user != null) {
                    personNameText.setText(user.getUserName());
                    String emailTextVal = "Email: " + user.getEmail();
                    emailText.setText(emailTextVal);
                    String phoneTextVal = "Phone: " + user.getPhoneNumber();
                    phoneText.setText(phoneTextVal);

                    String pfpUrl = user.getProfileImageUrl();

                    Log.d("LoadProfileData", pfpUrl);

                    if (!pfpUrl.isEmpty()) {
                        Glide.with(ProfileActivity.this).load(pfpUrl).circleCrop().into(profilePicture);
                    } else {
                        profilePicture.setImageDrawable(user.getDisplayDrawable());
                    }
                } else {
                    Toast.makeText(ProfileActivity.this, "User is null", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(ProfileActivity.this, "Error fetching user profile (ProfileActivity)",
                        Toast.LENGTH_LONG).show();
            }

        });
    }

    /**
     * Called when returning from the ProfileEditActivity.
     * Reloads the profile data to reflect any changes.
     *
     * @param requestCode The request code used to start the activity.
     * @param resultCode  The result code returned by the child activity.
     * @param data        Any additional data returned by the child activity.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Log.d("ProfileActivity", "Result OK after editing");
            // Reload the profile data if the result is OK
            loadProfileData(deviceId);
        }
    }
}
