
package com.example.hive;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

public class EditFacilityProfileActivity extends AppCompatActivity {

    private EditText facilityNameEditText, facilityEmailEditText, facilityPhoneEditText;
    private Button saveButton, cancelButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_facility_profile);

        facilityNameEditText = findViewById(R.id.et_facility);
        facilityEmailEditText = findViewById(R.id.et_email);
        facilityPhoneEditText = findViewById(R.id.et_phone);
        saveButton = findViewById(R.id.btn_save);
        cancelButton = findViewById(R.id.btn_cancel);

        FacilityProfileData();

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveFacilityProfileData();
                setResult(RESULT_OK);
                finish();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    private void FacilityProfileData() {
        SharedPreferences sharedPreferences = getSharedPreferences("FacilityProfile", MODE_PRIVATE);
        String facilityName = sharedPreferences.getString("facilityName", "");
        String facilityEmail = sharedPreferences.getString("facilityEmail", "");
        String facilityPhone = sharedPreferences.getString("facilityPhone", "");

        facilityNameEditText.setText(facilityName);
        facilityEmailEditText.setText(facilityEmail);
        facilityPhoneEditText.setText(facilityPhone);
    }


    private void saveFacilityProfileData() {
        String facilityName = facilityNameEditText.getText().toString();
        String facilityEmail = facilityEmailEditText.getText().toString();
        String facilityPhone = facilityPhoneEditText.getText().toString();

        SharedPreferences sharedPreferences = getSharedPreferences("FacilityProfile", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("facilityName", facilityName);
        editor.putString("facilityEmail", facilityEmail);
        editor.putString("facilityPhone", facilityPhone);
        editor.apply();
    }
}
