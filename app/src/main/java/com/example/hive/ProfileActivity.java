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

    private Button editProfileButton;
    private ImageView backArrow;
    private ImageView profilePicture;  // You missed initializing the profile picture in your earlier code.
    private TextView personNameText, userNameText, emailText, phoneText;
    private String deviceId;

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

        // Initialize the views
        editProfileButton = findViewById(R.id.editProfileButton);
        backArrow = findViewById(R.id.backArrow);
        profilePicture = findViewById(R.id.imageViewProfileImage); // Make sure you have this defined in your layout XML file
        personNameText = findViewById(R.id.personName);
        userNameText = findViewById(R.id.userName);
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
                // Notify the user when the back arrow is clicked
                Toast.makeText(ProfileActivity.this, "Back arrow clicked", Toast.LENGTH_SHORT).show();

                finish();
            }
        });
    }

    /**
     * Converts a Base64 encoded string back to a Bitmap.
     *
     * @param base64Str The Base64 encoded string.
     * @return The decoded Bitmap.
     */
    private Bitmap base64ToBitmap(String base64Str) {
        byte[] decodedBytes = Base64.decode(base64Str, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    /**
     * Loads profile data from Firebase and displays it in the corresponding TextViews.
     */
    public void loadProfileData(String deviceId) {
        FirebaseController controller = new FirebaseController();
        controller.fetchUserByDeviceId(deviceId, new FirebaseController.OnUserFetchedListener() {
            @Override
            public void onUserFetched(User user) {
                if (user != null) {
                    personNameText.setText(user.getUserName());
                    userNameText.setText(user.getUserName());
                    emailText.setText(user.getEmail());
                    phoneText.setText(user.getPhoneNumber());

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
//        SharedPreferences sharedPreferences = getSharedPreferences("UserProfile", MODE_PRIVATE);
//        String personName = sharedPreferences.getString("personName", "Person Name");
//        String userName = sharedPreferences.getString("userName", "User Name");
//        String email = sharedPreferences.getString("email", "user@google.com");
//        String phone = sharedPreferences.getString("phone", "(780) xxx - xxxx");
//        String profilePictureBase64 = sharedPreferences.getString("profilePicture", "");

        // Load profile picture if available
//        if (!profilePictureBase64.isEmpty()) {
//            Bitmap profileBitmap = base64ToBitmap(profilePictureBase64);
//            profilePicture.setImageBitmap(profileBitmap);
//        } else {
//            // Set default profile picture
//            profilePicture.setImageResource(R.drawable.ic_profile);
//        }
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
