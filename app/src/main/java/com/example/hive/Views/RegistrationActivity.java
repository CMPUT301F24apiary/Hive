package com.example.hive.Views;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hive.R;

/**
 * RegistrationActivity.java
 *
 * This activity manages the registration process for users, providing a screen for
 * signing up or registering for events. It facilitates user interaction with
 * event registration options and displays the relevant information.
 *
 * <p>Outstanding Issues:
 * - None at this time.</p>
 *
 * @author Aleena
 * @version 1.0
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
