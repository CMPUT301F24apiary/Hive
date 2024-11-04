package com.example.hive;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * This activity is to edit a facility profile
 * author : Hrittija
 */
public class EditFacilityProfileActivity extends AppCompatActivity {
    private static final int PICK_IMAGE = 1;
    private ImageView facilityImageView;
    private EditText facilityNameEditText, emailEditText, phoneEditText;
    private String base64Image = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_facility);

        facilityNameEditText = findViewById(R.id.et_facility);
        emailEditText = findViewById(R.id.et_email);
        phoneEditText = findViewById(R.id.et_phone);
        facilityImageView = findViewById(R.id.img_edit_picture);

        setupButtons();
        facilityData();
    }

    /**
     * Function for all the buttons
     */
    private void setupButtons() {
        findViewById(R.id.btn_edit_picture).setOnClickListener(v -> openImagePicker());
        findViewById(R.id.btn_remove_picture).setOnClickListener(v -> removePicture());
        findViewById(R.id.btn_save).setOnClickListener(v -> saveFacilityData());
        findViewById(R.id.btn_cancel).setOnClickListener(v -> finish());
    }

    /**
     * Function to open the gallery for the facility poster.
     */
    private void openImagePicker() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }

    /**
     * To remove the facility poster.
     */
    private void removePicture() {
        facilityImageView.setImageResource(R.drawable.image1);
        base64Image = "";
    }

    /**
     *Handles the result from an activity that was started for a result
     * @param requestCode to identify who this result is from
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            setImageFromUri(imageUri);
        }
    }

    /**
     * Sets the image for a facility from a specified URI by converting it
     * into a bitmap.
     * @param imageUri organizer's selection of image for the facility profile
     */
    private void setImageFromUri(Uri imageUri) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
            facilityImageView.setImageBitmap(bitmap);
            base64Image = bitmapToBase64(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Converts a Bitmap image to a Base64-encoded string
     * @param bitmap
     * @return
     */
    private String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    /**
     * Converts a base64 string to bitmap
     * @param base64Str
     * @return
     */
    private Bitmap base64ToBitmap(String base64Str) {
        byte[] decodedBytes = Base64.decode(base64Str, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    /**
     * This is to show the data that has been set by the user previously and that
     * is to be edited.
     */
    private void facilityData() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserProfile", MODE_PRIVATE);
        facilityNameEditText.setText(sharedPreferences.getString("facilityName", ""));
        emailEditText.setText(sharedPreferences.getString("facilityEmail", ""));
        phoneEditText.setText(sharedPreferences.getString("facilityPhone", ""));

        String facilityImageViewBase64 = sharedPreferences.getString("facility_profile_picture", "");
        if (!facilityImageViewBase64.isEmpty()) {
            Bitmap profileBitmap = base64ToBitmap(facilityImageViewBase64);
            facilityImageView.setImageBitmap(profileBitmap);
            base64Image = facilityImageViewBase64;
        } else {
            facilityImageView.setImageResource(R.drawable.image1);
        }
    }

    /**
     * Checks if the user inputs are valid or not.
     * @return
     */
    private boolean validateInputs() {
        String facilityName = facilityNameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();

        if (facilityName.isEmpty()) {
            Toast.makeText(this, "Facility name cannot be empty", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!phone.matches("\\d{10}")) { // Checks if the phone is exactly 10 digits
            Toast.makeText(this, "Please enter a valid 10-digit phone number", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }


    /**
     *This saves the new edited information from the user(organizer)
     */
    private void saveFacilityData() {
        if (validateInputs()) {
            String updatedName = facilityNameEditText.getText().toString();
            String updatedEmail = emailEditText.getText().toString();
            String updatedPhone = phoneEditText.getText().toString();

            String facilityImageViewBase64 = bitmapToBase64(((BitmapDrawable) facilityImageView.getDrawable()).getBitmap());

            SharedPreferences sharedPreferences = getSharedPreferences("UserProfile", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("facilityName", updatedName);
            editor.putString("facilityEmail", updatedEmail);
            editor.putString("facilityPhone", updatedPhone);
            editor.putString("facility_profile_picture", facilityImageViewBase64);
            editor.apply();
            setResult(RESULT_OK);
            finish();
        }
    }
}