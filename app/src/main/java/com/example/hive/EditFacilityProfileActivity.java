package com.example.hive;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.example.hive.Controllers.FirebaseController;
import com.example.hive.Models.User;
import com.example.hive.Controllers.FacilityController;
import com.example.hive.Controllers.FirebaseController;
import com.example.hive.Controllers.ImageController;
import com.example.hive.Models.User;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;

/**
 * This activity is to edit a facility profile(Picture, name,email and phone)
 * author : Hrittija
 */
public class EditFacilityProfileActivity extends AppCompatActivity {
    public static final int PICK_IMAGE = 1;
    public ImageView facilityImageView;
    public EditText facilityNameEditText, emailEditText, phoneEditText;
    private Uri pictureUri;
    private String deviceID;
    boolean isEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_facility);

        deviceID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        isEdit = getIntent().getBooleanExtra("isEdit", true);

        facilityNameEditText = findViewById(R.id.et_facility);
        emailEditText = findViewById(R.id.et_email);
        phoneEditText = findViewById(R.id.et_phone);
        facilityImageView = findViewById(R.id.img_edit_picture);

        setupButtons();
        facilityData();
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
    public void removePicture() {
        new FacilityController().getUserFacilityDetails(deviceID, facility -> {
            new ImageController().deleteImageAndUpdateRelatedDoc(facility.getPictureURL(),
                    null, facility.getID(), success -> {
                facilityImageView.setImageDrawable(facility.generateDefaultPic());
            });
        });
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
            // From https://github.com/bumptech/glide/issues/3839, downloaded 2024-11-06
            Glide.with(this)
                    .load(imageUri)
                    .transform(new CircleCrop())
                    .into(facilityImageView);
            pictureUri = imageUri;
        }
    }

//    /**
//     * Sets the image for a facility from a specified URI by converting it
//     * into a bitmap.
//     * @param imageUri organizer's selection of image for the facility profile
//     */
//    private void setImageFromUri(Uri imageUri) {
//        try {
//            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
//            facilityImageView.setImageBitmap(bitmap);
//            base64Image = bitmapToBase64(bitmap);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
    /**
     * Converts a Bitmap image to a Base64-encoded string
     * @param bitmap to be converted
     * @return string
     */
    public String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }
//
//    /**
//     * Converts a base64 string to bitmap
//     * @param base64Str to be converted
//     * @return the converted butmap
//     */
//    public Bitmap base64ToBitmap(String base64Str) {
//        byte[] decodedBytes = Base64.decode(base64Str, Base64.DEFAULT);
//        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
//    }

    /**
     * This is to show the data that has been set by the user previously and that
     * is to be edited.
     */
    public void facilityData() {
        new FirebaseController().fetchUserByDeviceId(deviceID, new FirebaseController.OnUserFetchedListener() {
            @Override
            public void onUserFetched(User user) {
                Log.d("UserFacilityDetails", user.getUserName());
                if (user != null && user.getFacilityID() != null && !user.getFacilityID().isEmpty()) {
                    new FacilityController().getUserFacilityDetails(deviceID, facility -> {
                        facilityNameEditText.setText(facility.getName());
                        emailEditText.setText(facility.getEmail());
                        phoneEditText.setText(facility.getPhone());
                        if (facility.getPictureURL() == null) {
                            facilityImageView.setImageDrawable(facility.generateDefaultPic());
                        } else {
                            Glide.with(EditFacilityProfileActivity.this)
                                    .load(facility.getPictureURL()).circleCrop()
                                    .into(facilityImageView);
                        }
                    });
                } else {
                    facilityImageView.setImageResource(R.drawable.image1);
                }
            }

            @Override
            public void onError(Exception e) {

            }
        });
    }

    /**
     * Checks if the user inputs are valid or not.
     * @return
     */
    public boolean validateInputs() {
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
    public void saveFacilityData() {
        if (validateInputs()) {
            String updatedName = facilityNameEditText.getText().toString();
            String updatedEmail = emailEditText.getText().toString();
            String updatedPhone = phoneEditText.getText().toString();

            FacilityController controller = new FacilityController();

            if (isEdit) {
                HashMap<String, Object> data = new HashMap<>();
                data.put("name", updatedName);
                data.put("email", updatedEmail);
                data.put("phone", updatedPhone);
                data.put("pictureUri", pictureUri);
                controller.editFacility(EditFacilityProfileActivity.this, deviceID, data, success -> {
                    if (success) {
                        Intent result = new Intent();
                        setResult(1, result);
                        finish();
                    } else {
                        Toast.makeText(EditFacilityProfileActivity.this,
                                "Edit failed", Toast.LENGTH_SHORT).show();
                    }
                });

            } else {

                controller.addFacility(EditFacilityProfileActivity.this, deviceID,
                        updatedName, updatedEmail, updatedPhone, pictureUri, success -> {
                            if (success) {
                                Intent result = new Intent(EditFacilityProfileActivity.this, FacilityActivity.class);
                                setResult(1, result);
                                startActivity(result);
                                finish();
                            } else {
                                Toast.makeText(EditFacilityProfileActivity.this,
                                        "Edit failed", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        }
    }
}
