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

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
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
import com.example.hive.Controllers.FirebaseController;
import com.example.hive.Controllers.ImageController;
import com.example.hive.Models.User;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
//import java.util.Base64;

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
    private User user;
    private Uri pfpUri = null;
    private String deviceId;

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

        deviceId = getIntent().getStringExtra("deviceId");

        // Set up click listeners for notification bell buttons
        setupNotificationButtons();

        loadProfileData(deviceId);

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
                Log.d("ProfEditRemovePic", "Clicked remove");
                new FirebaseController().getUserDocId(deviceId, docId -> {
                    Log.d("ProfEditRemovePic", docId);
                    new ImageController().deleteImageAndUpdateRelatedDoc(user.getProfileImageUrl(),
                            null, docId, success -> {
                                Log.d("ProfEditRemovePic", success.toString());
                        if (success) {
                            // Reset to default profile picture
                            profilePicture.setImageDrawable(user.getDisplayDrawable());
                        } else {
                            Toast.makeText(ProfileEditActivity.this,
                                    "Profile Picture removal error",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                });
            }
        });

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

        // Set the logic for the Cancel button
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            pfpUri = data.getData();

            // Use Glide to load and crop the selected image as a circle
            // From https://github.com/bumptech/glide/issues/3839, downloaded 2024-11-06

            Glide.with(this)
                    .load(pfpUri)
                    .transform(new CircleCrop())
                    .into(profilePicture);
        }
    }

    /**
     * Sets up the notification bell buttons to toggle between enabled (green bell)
     * and disabled (white bell) states. Saves the state in SharedPreferences.
     */
    void setupNotificationButtons() {
        // Listener to toggle icon and save state
        View.OnClickListener toggleNotificationListener = v -> {
            ImageButton button = (ImageButton) v;
            boolean isEnabled = button.getTag() != null && (boolean) button.getTag();

            // Toggle icon and state
            if (isEnabled) {
                button.setImageResource(R.drawable.bell); // Default white bell
                button.setTag(false);
                Toast.makeText(this, "Notification Disabled", Toast.LENGTH_SHORT).show();
            } else {
                button.setImageResource(R.drawable.bell2); // Filled green bell
                button.setTag(true);
                Toast.makeText(this, "Notification Enabled", Toast.LENGTH_SHORT).show();
            }

            // Save updated state
            saveNotificationPreferences();
        };

        // Attach the listener to each notification button
        notificationChosenBellButton.setOnClickListener(toggleNotificationListener);
        notificationNotChosenBellButton.setOnClickListener(toggleNotificationListener);
        notificationOrganizerBellButton.setOnClickListener(toggleNotificationListener);

        // Load saved preferences
        loadNotificationPreferences();
    }


    // Save the state of each notification preference
    private void saveNotificationPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("NotificationPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putBoolean("notificationBell", (boolean) notificationBellButton.getTag());
        editor.putBoolean("notificationChosen", (boolean) notificationChosenBellButton.getTag());
        editor.putBoolean("notificationNotChosen", (boolean) notificationNotChosenBellButton.getTag());
        editor.putBoolean("notificationOrganizer", (boolean) notificationOrganizerBellButton.getTag());

        editor.apply();
    }

    // Load the saved state of each notification preference
    private void loadNotificationPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("NotificationPreferences", MODE_PRIVATE);

        boolean notificationBell = sharedPreferences.getBoolean("notificationBell", false);
        boolean notificationChosen = sharedPreferences.getBoolean("notificationChosen", false);
        boolean notificationNotChosen = sharedPreferences.getBoolean("notificationNotChosen", false);
        boolean notificationOrganizer = sharedPreferences.getBoolean("notificationOrganizer", false);

        notificationBellButton.setImageResource(notificationBell ? R.drawable.bell2 : R.drawable.bell);
        notificationBellButton.setTag(notificationBell);

        notificationChosenBellButton.setImageResource(notificationChosen ? R.drawable.bell2 : R.drawable.bell);
        notificationChosenBellButton.setTag(notificationChosen);

        notificationNotChosenBellButton.setImageResource(notificationNotChosen ? R.drawable.bell2 : R.drawable.bell);
        notificationNotChosenBellButton.setTag(notificationNotChosen);

        notificationOrganizerBellButton.setImageResource(notificationOrganizer ? R.drawable.bell2 : R.drawable.bell);
        notificationOrganizerBellButton.setTag(notificationOrganizer);
    }


    // Validation methods
    private boolean isValidInput() {
        boolean isValid = true;

        String email = emailInput.getText().toString();
        String phone = phoneInput.getText().toString();

        // Check email format
        if (!isValidEmail(email)) {
            emailInput.setError("Invalid email: must contain '@'");
            isValid = false;
        } else {
            emailInput.setError(null);  // Clear any previous error
        }

        // Check phone format
        if (!isValidPhoneNumber(phone)) {
            phoneInput.setError("Invalid phone: must be digits only");
            isValid = false;
        } else {
            phoneInput.setError(null);  // Clear any previous error
        }

        return isValid;
    }

    private boolean isValidEmail(String email) {
        return email.contains("@") && email.indexOf("@") > 0 && email.indexOf("@") < email.length() - 1;
    }

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
     * Converts the bitmap image to a Base64 encoded string.
     *
     * @param bitmap The Bitmap to convert.
     * @return The Base64 encoded string of the bitmap.
     */
    String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    /**
     * Converts a Base64 encoded string back to a Bitmap.
     *
     * @param base64Str The Base64 encoded string.
     * @return The decoded Bitmap.
     */
    Bitmap base64ToBitmap(String base64Str) {
        byte[] decodedBytes = Base64.decode(base64Str, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }


    /**
     * Loads the profile data from SharedPreferences and populates the input fields.
     * Also loads the profile picture if available.
     */
    public void loadProfileData(String deviceId) {
        FirebaseController controller = new FirebaseController();
        controller.fetchUserByDeviceId(deviceId, new FirebaseController.OnUserFetchedListener() {
            @Override
            public void onUserFetched(User fetchedUser) {
                if (fetchedUser != null) {
                    user = fetchedUser;

                    personNameInput.setText(user.getUserName());
                    userNameInput.setText(user.getUserName());
                    emailInput.setText(user.getEmail());
                    phoneInput.setText(user.getPhoneNumber());

                    String pfpUrl = user.getProfileImageUrl();

                    if (!pfpUrl.isEmpty()) {
                        Glide.with(ProfileEditActivity.this).load(pfpUrl).circleCrop().into(profilePicture);
                    } else {
                        profilePicture.setImageDrawable(user.getDisplayDrawable());
                    }
                } else {
                    Toast.makeText(ProfileEditActivity.this, "User is null", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(ProfileEditActivity.this, "Error fetching user profile (ProfileEditActivity)",
                        Toast.LENGTH_LONG).show();
            }});

//                    sharedPreferences = getSharedPreferences("UserProfile", MODE_PRIVATE);
//        String personName = sharedPreferences.getString("personName", "");
//        String userName = sharedPreferences.getString("userName", "");
//        String email = sharedPreferences.getString("email", "");
//        String phone = sharedPreferences.getString("phone", "");
//        String profilePictureBase64 = sharedPreferences.getString("profilePicture", "");
//
//        // Set the loaded data into the EditText fields
//        personNameInput.setText(personName);
//        userNameInput.setText(userName);
//        emailInput.setText(email);
//        phoneInput.setText(phone);
//
//        // Load profile picture if available
//        if (!profilePictureBase64.isEmpty()) {
//            Bitmap profileBitmap = base64ToBitmap(profilePictureBase64);
//            profilePicture.setImageBitmap(profileBitmap);
//        } else {
//            // Set default profile picture
//            profilePicture.setImageResource(R.drawable.ic_profile);
//        }
    }

    /**
     * Saves the profile data entered by the user into SharedPreferences.
     * Also saves the profile picture as a Base64 string.
     */
    public void saveProfileData() {
        String personName = personNameInput.getText().toString();
        String userName = userNameInput.getText().toString();
        String email = emailInput.getText().toString();
        String phone = phoneInput.getText().toString();

        HashMap<String, Object> data = new HashMap<>();
        data.put("username", userName);
        data.put("name", personName);
        data.put("email", email);
        data.put("phoneNumber", phone);

        FirebaseController fbControl = new FirebaseController();

        if (pfpUri == null) {
            fbControl.updateUserByDeviceId(deviceId, data, didUpdate -> {
                if (didUpdate) {
                    finish();
                } else {
                    Toast.makeText(
                            ProfileEditActivity.this,
                            "Update failed",
                            Toast.LENGTH_SHORT)
                            .show();
                }
            });
        } else {
            ImageController imgControl = new ImageController();
            try {
                imgControl.saveImage(this, pfpUri, "profile picture")
                        .addOnSuccessListener(urlAndID -> {

                            data.put("profileImageUrl", urlAndID.first);
                            fbControl.updateUserByDeviceId(deviceId, data, didUpdate -> {
                                if (didUpdate) {
                                    imgControl.updateImageRef(urlAndID.second, deviceId);
                                    finish();
                                } else {
                                    Toast.makeText(ProfileEditActivity.this,
                                            "Could not update user",
                                            Toast.LENGTH_SHORT).show();
                                }
                            });

                        }).addOnFailureListener(e -> {
                            // Handle the failure of the image upload
                            Toast.makeText(this, "Failed to upload image: " +
                                            e.getMessage() +
                                            "\nUser will still be updated - try uploading the picture again.",
                                    Toast.LENGTH_LONG).show();

                            fbControl.updateUserByDeviceId(deviceId, data, didUpdate -> {
                                if (didUpdate) {
                                    finish();
                                } else {
                                    Toast.makeText(ProfileEditActivity.this,
                                            "Could not update user",
                                            Toast.LENGTH_SHORT).show();
                                }
                            });

                        });
            } catch(Exception e) {
                Toast.makeText(this, "Image upload failed: " + e.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        }


        // Save profile picture
//        BitmapDrawable drawable = (BitmapDrawable) profilePicture.getDrawable();
//        Bitmap bitmap = drawable.getBitmap();
//        String profilePictureBase64 = bitmapToBase64(bitmap);
//
//        SharedPreferences sharedPreferences = getSharedPreferences("UserProfile", MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.putString("personName", personName);
//        editor.putString("userName", userName);
//        editor.putString("email", email);
//        editor.putString("phone", phone);
//        editor.putString("profilePicture", profilePictureBase64);
//        editor.apply();  // Apply the changes
    }
    public void setSharedPreferencesForTesting(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

}
