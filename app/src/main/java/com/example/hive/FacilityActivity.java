
package com.example.hive;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.Settings;
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

/**
 * This activity is to show the facility profile for an event.
 * Author: Hrittija
 */

public class FacilityActivity extends AppCompatActivity {

    private Button editFacilityButton;
    private ImageView backArrowButton;
    private ImageView facilityPoster;
    private TextView facilityNameText, emailText, phoneText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createfacility);

        editFacilityButton = findViewById(R.id.btn_edit_profile);
        backArrowButton = findViewById(R.id.backArrow);
        facilityPoster = findViewById(R.id.facility_poster);
        facilityNameText = findViewById(R.id.facility_name);
        emailText = findViewById(R.id.email_label);
        phoneText = findViewById(R.id.phone_label);

        facilityData();

        editFacilityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FacilityActivity.this, EditFacilityProfileActivity.class);
                startActivityForResult(intent, 1);
            }
        });

        backArrowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * Called when the activity is resumed. Reloads the profile picture to reflect any changes made in the ProfileEditActivity.
     */
    @Override
    protected void onResume() {
        super.onResume();
        facilityData();
    }

    /**
     * This is to keep a track of if the facility profile has been completed
     */
    public void updateProfileStatus() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserProfile", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        String facilityName = sharedPreferences.getString("facilityName", "Facility Name");
        String email = sharedPreferences.getString("facilityEmail", "facility@google.com");
        String phone = sharedPreferences.getString("facilityPhone", "(780) xxx - xxxx");

        if (!facilityName.equals("Facility Name") && !email.equals("facility@google.com") && !phone.equals("(780) xxx - xxxx")) {
            editor.putBoolean("profileComplete", true);
        } else {
            editor.putBoolean("profileComplete", false);
        }

        editor.apply();
    }


//    /**
//     * Converts Base64 string to bitmap
//     * @param base64Str string to be converted
//     * @return the converted bitmap
//     */
//    private Bitmap base64ToBitmap(String base64Str) {
//        byte[] decodedBytes = Base64.decode(base64Str, Base64.DEFAULT);
//        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
//    }

    /**
     * Shows the facility information.
     */
    public void facilityData() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserProfile", MODE_PRIVATE);

        // Get the profile data
        String facilityName = sharedPreferences.getString("facilityName", "Facility Name");
        String email = sharedPreferences.getString("facilityEmail", "facility@google.com");
        String phone = sharedPreferences.getString("facilityPhone", "(780) xxx - xxxx");
        String facilityPosterBase64 = sharedPreferences.getString("facility_profile_picture", "");

        // Set the UI with profile data
        facilityNameText.setText(facilityName);
        emailText.setText("Email: " + email);
        phoneText.setText("Phone: " + phone);

        String deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        loadProfileData(deviceId);

        // Set the facility poster image
//        if (!facilityPosterBase64.isEmpty()) {
//            Bitmap facilityBitmap = base64ToBitmap(facilityPosterBase64);
//            facilityPoster.setImageBitmap(facilityBitmap);
//        } else {
//            facilityPoster.setImageResource(R.drawable.image1);
//        }

        updateProfileStatus();
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
                    String pfpUrl = user.getProfileImageUrl();
                    Log.d("LoadProfileData", pfpUrl);

                    if (!pfpUrl.isEmpty()) {
                        Glide.with(FacilityActivity.this).load(pfpUrl).circleCrop().into(facilityPoster);
                    } else {
                        facilityPoster.setImageDrawable(user.getDisplayDrawable());
                    }
                } else {
                    Toast.makeText(FacilityActivity.this, "User is null", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(FacilityActivity.this, "Error fetching user profile (ProfileActivity)",
                        Toast.LENGTH_LONG).show();
            }

        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            facilityData();
        }
    }
}
