
package com.example.hive.Views;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.hive.Controllers.FirebaseController;
import com.example.hive.Events.OrganizerEventListActivity;
import com.example.hive.Models.User;

import com.example.hive.Controllers.FacilityController;
import com.example.hive.R;

/**
 * This activity is to show the facility profile for an event.
 * @author Hrittija
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

        deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        facilityData();

        ActivityResultLauncher<Intent> editFacilityLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == 1) {
                        facilityData();
                    }
                }
        );

        editFacilityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FacilityActivity.this, EditFacilityProfileActivity.class);
                intent.putExtra("isEdit", true);
                editFacilityLauncher.launch(intent);
            }
        });

        backArrowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(FacilityActivity.this, OrganizerEventListActivity.class);
                startActivity(i);
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

    /**
     * Shows the facility information.
     */
    public void facilityData() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserProfile", MODE_PRIVATE);

        FacilityController facilityControl = new FacilityController();

        new FirebaseController().fetchUserByDeviceId(deviceId, new FirebaseController
                .OnUserFetchedListener() {
            @Override
            public void onUserFetched(User user) {
                if (user != null && user.getFacilityID() != null && !user.getFacilityID().isEmpty()) {
                    facilityControl.getUserFacilityDetails(deviceId, facility -> {
                        // Set the UI with profile data
                        facilityNameText.setText(facility.getName());
                        emailText.setText("Email: " + facility.getEmail());
                        phoneText.setText("Phone: " + facility.getPhone());

                        // Set the facility poster image
                        if (facility.getPictureURL() == null) {
                            facilityPoster.setImageDrawable(facility.generateDefaultPic());
                        } else {
                            Glide.with(FacilityActivity.this).load(facility.getPictureURL())
                                    .circleCrop().into(facilityPoster);
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(FacilityActivity.this, "Error fetching user data.", Toast.LENGTH_SHORT).show();
            }
        });


    }
}
