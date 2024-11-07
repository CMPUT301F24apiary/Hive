package com.example.hive;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

/**
 * RegistrationActivity handles the registration process for users.
 * This activity displays the registration screen where users can sign up or register for events.
 */
public class RegistrationActivity extends AppCompatActivity {

    /**
     * Called when the activity is first created. Sets the content view to the registration layout.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
     *                           this Bundle contains the most recent data.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
    }
}
