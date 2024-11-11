package com.example.hive.Views;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hive.Controllers.FirebaseController;
import com.example.hive.R;
import com.google.firebase.firestore.FirebaseFirestore;

import com.bumptech.glide.Glide;
import com.example.hive.Models.User;

/**
 * This shows a profile page of user, for an admin user
 * i.e. has a delete button.
 */
public class AdminProfileViewActivity extends AppCompatActivity {

    private Button deleteProfileButton;
    private ImageView backArrow;
    private ImageView profilePicture;
    private TextView personNameText, userNameText, emailText, phoneText;
    private String deviceId;
    private FirebaseController firebaseController;


    /**
     * when admin pressed view on user profile, they will be taken to the user profile,
     * where they can delete the profile.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down, this Bundle contains the data it most recently supplied in onSaveInstanceState. Otherwise, it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_admin);
        firebaseController = new FirebaseController();
        FirebaseFirestore db = firebaseController.getDb();

        // Initialize the views
        deleteProfileButton = findViewById(R.id.deleteProfileButton);
        backArrow = findViewById(R.id.backArrow);
        profilePicture = findViewById(R.id.imageViewProfileImage);
        personNameText = findViewById(R.id.personName);
        emailText = findViewById(R.id.emailLabel);
        phoneText = findViewById(R.id.phoneLabel);
        deviceId = getIntent().getStringExtra("deviceId");
        if (deviceId != null) {
            fetchUserProfile();
        } else {
            //Toast.makeText(this, "Error: deviceId is null", Toast.LENGTH_LONG).show();
            Log.d(TAG, "Device ID for user " + deviceId);
            finish(); // go back; close activity
        }


        deleteProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteUserProfile();
                Toast.makeText(com.example.hive.Views.AdminProfileViewActivity.this, "Delete this profile", Toast.LENGTH_SHORT)
                        .show();
            }
        });
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Notify the user when the back arrow is clicked
                Intent i = new Intent(AdminProfileViewActivity.this, AdminProfileViewActivity.class);

                Toast.makeText(com.example.hive.Views.AdminProfileViewActivity.this, "Back arrow clicked", Toast.LENGTH_SHORT).show();
                finish();
                startActivity(i);
            }
        });

    }


    /**
     * Loads profile data from db using deviceId to identify user.
     */
    private void fetchUserProfile() {
        firebaseController.fetchUserByDeviceId(deviceId, new FirebaseController.OnUserFetchedListener() {
            @Override
            public void onUserFetched(User user) {
                if (user != null) {
                    //Toast.makeText(AdminProfileViewActivity.this, "User name: " + user.getUserName(), Toast.LENGTH_LONG).show();
                    Log.d("AdminProfileViewActivity", "Fetched user name: " + user.getUserName());
                    personNameText.setText(user.getUserName());
                    emailText.setText("Email: " + user.getEmail());
                    phoneText.setText("Phone: " + user.getPhoneNumber());

                    // using Glide for image loading
                    if (!user.getProfileImageUrl().isEmpty()) {
                        Glide.with(AdminProfileViewActivity.this).load(user.getProfileImageUrl())
                                .error(R.drawable.ic_profile)
                                .circleCrop()
                                .into(profilePicture);
                    } else {
                        Log.d(TAG, "Device ID: " + user.getDeviceId() + ", User Name: " + user.getUserName());
                        // Drawable userDrawable = user.getDisplayDrawable();
                        profilePicture.setImageDrawable(user.getDisplayDrawable());
                    }

                } else {
                    Toast.makeText(AdminProfileViewActivity.this, "User is null", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(AdminProfileViewActivity.this, "Error fetching user profile (AdminProfileViewActivity)",
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Used to bring up an alert dialog and thereupon delete (or not) a user profile
     */
    private void deleteUserProfile() {
        new AlertDialog.Builder(this).setTitle("Are you sure you want to delete this profile?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    firebaseController.deleteUserByDeviceId(deviceId, new FirebaseController.OnUserDeletedListener() {
                        @Override
                        public void onUserDeleted() {
                            Toast.makeText(AdminProfileViewActivity.this, "Profile deleted", Toast.LENGTH_LONG).show();
                            Intent resultIntent = new Intent();
                            resultIntent.putExtra("deviceId", deviceId); // pass deleted user's deviceId
                            setResult(RESULT_OK, resultIntent);
                            finish();  // exit from the user profile page, return to previous activity i.e. go back
                        }

                        @Override
                        public void onError(Exception e) {
                            Log.e(TAG, "Error deleting user profile", e);
                            //Toast.makeText(AdminProfileViewActivity.this, "Error deleting profile", Toast.LENGTH_LONG).show();
                        }

                    });
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }
}

