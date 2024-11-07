package com.example.hive;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;

import java.io.ByteArrayOutputStream;
import java.util.Base64;

/**
 * ProfileEditActivity is responsible for allowing the user to edit their profile details,
 * including name, username, email, phone number, and profile picture.
 */
public class ProfileEditActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;

    ImageView profilePicture;
    public EditText personNameInput;
    public EditText userNameInput;
    public EditText emailInput;
    public EditText phoneInput;
    private Button editPictureButton, removePictureButton, saveButton, cancelButton;
    private ImageButton notificationBellButton, notificationChosenBellButton, notificationNotChosenBellButton, notificationOrganizerBellButton;
    private SharedPreferences sharedPreferences;

    /**
     * Called when the activity is first created. Initializes UI components and sets up click listeners for buttons.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
     *                           this Bundle contains the most recent data.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);

        // Initialize input fields
        personNameInput = findViewById(R.id.personNameInput);
        userNameInput = findViewById(R.id.userNameInput);
        emailInput = findViewById(R.id.emailInput);
        phoneInput = findViewById(R.id.phoneInput);

        // Initialize buttons
        profilePicture = findViewById(R.id.profilePicture);
        editPictureButton = findViewById(R.id.editPictureButton);
        removePictureButton = findViewById(R.id.removePictureButton);
        saveButton = findViewById(R.id.saveButton);
        cancelButton = findViewById(R.id.cancelButton);

        // Initialize notification bell buttons
        notificationBellButton = findViewById(R.id.notificationBellButton);
        notificationChosenBellButton = findViewById(R.id.notificationChosenBellButton);
        notificationNotChosenBellButton = findViewById(R.id.notificationNotChosenBellButton);
        notificationOrganizerBellButton = findViewById(R.id.notificationOrganizerBellButton);

        // Set up click listeners for notification bell buttons
        setupNotificationButtons();

        loadProfileData();

        // Edit picture button logic
        editPictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open gallery to choose a picture
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
            }
        });

        // Remove picture button logic
        removePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Reset to default profile picture
                profilePicture.setImageResource(R.drawable.ic_profile);
            }
        });

        // Save button logic
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValidInput()) {
                    saveProfileData();
                    setResult(RESULT_OK);
                    finish();
                }
            }
        });

        // Cancel button logic
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * Handles the result from selecting an image from the gallery.
     *
     * @param requestCode The request code used to start the activity.
     * @param resultCode  The result code returned from the activity.
     * @param data        The intent data containing the selected image URI.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            Glide.with(this)
                    .load(imageUri)
                    .transform(new CircleCrop())
                    .into(profilePicture);
        }
    }

    /**
     * Sets up the click listeners for notification bell buttons to open the NotificationActivity.
     */
    private void setupNotificationButtons() {
        View.OnClickListener openNotificationActivity = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open NotificationActivity
                Intent intent = new Intent(ProfileEditActivity.this, NotificationActivity.class);
                startActivity(intent);
            }
        };

        notificationBellButton.setOnClickListener(openNotificationActivity);
        notificationChosenBellButton.setOnClickListener(openNotificationActivity);
        notificationNotChosenBellButton.setOnClickListener(openNotificationActivity);
        notificationOrganizerBellButton.setOnClickListener(openNotificationActivity);
    }

    /**
     * Validates the input fields to ensure correct email and phone number format.
     *
     * @return True if all inputs are valid, otherwise false.
     */
    private boolean isValidInput() {
        boolean isValid = true;

        String email = emailInput.getText().toString();
        String phone = phoneInput.getText().toString();

        if (!isValidEmail(email)) {
            emailInput.setError("Invalid email: must contain '@'");
            isValid = false;
        } else {
            emailInput.setError(null);  // Clear any previous error
        }

        if (!isValidPhoneNumber(phone)) {
            phoneInput.setError("Invalid phone: must be digits only");
            isValid = false;
        } else {
            phoneInput.setError(null);  // Clear any previous error
        }

        return isValid;
    }

    /**
     * Checks if the provided email is valid.
     *
     * @param email The email address to validate.
     * @return True if the email contains "@" and has correct format, otherwise false.
     */
    private boolean isValidEmail(String email) {
        return email.contains("@") && email.indexOf("@") > 0 && email.indexOf("@") < email.length() - 1;
    }

    /**
     * Checks if the provided phone number is valid.
     *
     * @param phone The phone number to validate.
     * @return True if the phone number contains only digits or is empty, otherwise false.
     */
    private boolean isValidPhoneNumber(String phone) {
        if (phone.isEmpty()) return true; // Phone number is optional
        for (char c : phone.toCharArray()) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Loads the profile data from SharedPreferences and displays it in the input fields.
     */
    public void loadProfileData() {
        sharedPreferences = getSharedPreferences("UserProfile", MODE_PRIVATE);
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
     * Saves the profile data to SharedPreferences for future use.
     */
    public void saveProfileData() {
        String personName = personNameInput.getText().toString();
        String userName = userNameInput.getText().toString();
        String email = emailInput.getText().toString();
        String phone = phoneInput.getText().toString();

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("personName", personName);
        editor.putString("userName", userName);
        editor.putString("email", email);
        editor.putString("phone", phone);
        editor.apply();
    }
}
