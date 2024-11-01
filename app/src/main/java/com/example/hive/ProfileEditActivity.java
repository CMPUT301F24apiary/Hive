/**
 * ProfileEditActivity.java
 *
 * This activity allows users to edit their profile information such as name, username,
 * email, and phone number. Changes are saved to SharedPreferences and can be applied to
 * update the user's profile.
 *
 * <p>Outstanding Issues:
 * - None at this time.</p>
 *
 * @author Dina
 * @version 1.0
 */
package com.example.hive;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

public class ProfileEditActivity extends AppCompatActivity {

    private EditText personNameInput, userNameInput, emailInput, phoneInput;
    private Button saveButton, cancelButton;

    /**
     * Called when the activity is starting. This is where most initialization should be done.
     * It sets up the UI and initializes input fields and buttons.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down, this Bundle contains the data it most recently supplied in onSaveInstanceState. Otherwise, it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);

        //Initialize input fields
        personNameInput = findViewById(R.id.personNameInput);
        userNameInput = findViewById(R.id.userNameInput);
        emailInput = findViewById(R.id.emailInput);
        phoneInput = findViewById(R.id.phoneInput);

        //Initialize buttons
        saveButton = findViewById(R.id.saveButton);
        cancelButton = findViewById(R.id.cancelButton);


        loadProfileData();

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProfileData();
                setResult(RESULT_OK);
                finish();
            }
        });

        // Set the logic for the Cancel button
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * Loads the profile data from SharedPreferences and populates the input fields.
     */
    private void loadProfileData() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserProfile", MODE_PRIVATE);
        String personName = sharedPreferences.getString("personName", "");
        String userName = sharedPreferences.getString("userName", "");
        String email = sharedPreferences.getString("email", "");
        String phone = sharedPreferences.getString("phone", "");

        // Set the loaded data into the EditText fields
        personNameInput.setText(personName);
        userNameInput.setText(userName);
        emailInput.setText(email);
        phoneInput.setText(phone);
    }

    /**
     * Saves the profile data entered by the user into SharedPreferences.
     */
    private void saveProfileData() {
        String personName = personNameInput.getText().toString();
        String userName = userNameInput.getText().toString();
        String email = emailInput.getText().toString();
        String phone = phoneInput.getText().toString();

        SharedPreferences sharedPreferences = getSharedPreferences("UserProfile", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("personName", personName);
        editor.putString("userName", userName);
        editor.putString("email", email);
        editor.putString("phone", phone);
        editor.apply();  // Apply the changes
    }
}




