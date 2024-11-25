
package com.example.hive;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * This activity is to show the facility profile for an event.
 * Author: Hrittija
 */

public class FacilityActivity extends AppCompatActivity {

    private Button editFacilityButton;
    private ImageView backArrowButton;
    private ImageView facilityPoster;
    private TextView facilityNameText, emailText, phoneText;
    private String deviceId;

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

        deviceId = getIntent().getStringExtra("deviceId");

        FacilityData();

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


    /**
     * Converts Base64 string to bitmap
     * @param base64Str string to be converted
     * @return the converted bitmap
     */
    private Bitmap base64ToBitmap(String base64Str) {
        byte[] decodedBytes = Base64.decode(base64Str, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    /**
     * Shows the facility information.
     */
    public void FacilityData() {
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

        // Set the facility poster image
        if (!facilityPosterBase64.isEmpty()) {
            Bitmap facilityBitmap = base64ToBitmap(facilityPosterBase64);
            facilityPoster.setImageBitmap(facilityBitmap);
        } else {
            facilityPoster.setImageResource(R.drawable.image1);
        }

        updateProfileStatus();
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            FacilityData();
        }
    }
}
