
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
     * Converts Base64 string to bitmap
     * @param base64Str string to be converted
     * @return
     */
    private Bitmap base64ToBitmap(String base64Str) {
        byte[] decodedBytes = Base64.decode(base64Str, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    /**
     * Shows the facility information.
     */
    private void FacilityData() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserProfile", MODE_PRIVATE);
        String facilityName = sharedPreferences.getString("facilityName", "Facility Name");
        String email = sharedPreferences.getString("facilityEmail", "facility@google.com");
        String phone = sharedPreferences.getString("facilityPhone", "(780) xxx - xxxx");
        String facilityPosterBase64 = sharedPreferences.getString("facility_profile_picture", "");


        facilityNameText.setText(facilityName);
        emailText.setText("Email: "+email);
        phoneText.setText("Phone: "+phone);

        if (!facilityPosterBase64.isEmpty()) {
            Bitmap facilityBitmap = base64ToBitmap(facilityPosterBase64);
            facilityPoster.setImageBitmap(facilityBitmap);
        } else {
            facilityPoster.setImageResource(R.drawable.image1);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            FacilityData();
        }
    }
}