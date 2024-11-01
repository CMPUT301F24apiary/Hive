package com.example.hive;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class FacilityActivity extends AppCompatActivity {

    private Button editProfileButton;
    private ImageView facilityPoster;
    private TextView facilityNameText, emailLabel, phoneLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.createfacility);

        editProfileButton = findViewById(R.id.btn_edit_profile);
        facilityPoster = findViewById(R.id.facility_poster);
        facilityNameText = findViewById(R.id.facility_name);
        emailLabel = findViewById(R.id.email_label);
        phoneLabel = findViewById(R.id.phone_label);

        FacilityData();

        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(FacilityActivity.this, "Edit Profile clicked", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(FacilityActivity.this, EditFacilityProfileActivity.class);
                startActivityForResult(intent, 1);
            }
        });
    }


    private void FacilityData() {
        SharedPreferences sharedPreferences = getSharedPreferences("FacilityProfile", MODE_PRIVATE);
        String facilityName = sharedPreferences.getString("facilityName", "Facility Name");
        String email = sharedPreferences.getString("email", "facility@google.com");
        String phone = sharedPreferences.getString("phone", "(780)-XXX-XXX");

        facilityNameText.setText(facilityName);
        emailLabel.setText("Email: " + email);
        phoneLabel.setText("Phone: " + phone);

        facilityPoster.setImageResource(R.drawable.image1);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            FacilityData();
        }
    }
}
