/**
 * EventListActivity.java
 *
 * This activity displays a list of events to the user. It includes an ImageButton that allows
 * the user to navigate to their profile page, a notification bell button, a QR code scanner,
 * and a switch roles button. Users can scan QR codes to join event waiting lists or view event
 * details.
 *
 * @author Dina
 * @version 1.2
 */
package com.example.hive;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.hive.Controllers.FirebaseController;
import com.example.hive.Models.User;
import com.example.hive.Views.CustomQrScannerActivity;
import com.example.hive.Views.UserEventPageActivity;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class EventListActivity extends AppCompatActivity {

    private ImageButton profileButton;
    private ImageButton notificationBellButton; // Notification Bell button
    private Button switchRolesButton; // Switch Roles button
    private Button scanQrCodeButton; // Scan QR Code button
    private ImageView qrCodeImageView; // QR Code ImageView

    private static final int REQUEST_CODE_QR_SCAN = 1001;

    /**
     * Called when the activity is starting. This is where most initialization should be done.
     * It sets up the UI and initializes the profile button, notification bell button,
     * QR code scanner, and switch roles button.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being
     *                           shut down, this Bundle contains the data it most recently
     *                           supplied in onSaveInstanceState. Otherwise, it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list);

        // Initialize UI components
        profileButton = findViewById(R.id.profileButton);
        notificationBellButton = findViewById(R.id.notificationBellButton);
        switchRolesButton = findViewById(R.id.switchRolesButton);
        scanQrCodeButton = findViewById(R.id.scanQrCodeButton);
        qrCodeImageView = findViewById(R.id.qrCodeImageView);

        // Set click listeners for each button
        String deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        loadProfileData(deviceId);

        profileButton.setOnClickListener(v -> {
            Intent intent = new Intent(EventListActivity.this, ProfileActivity.class);
            intent.putExtra("deviceId", deviceId);
            startActivity(intent);
        });

        notificationBellButton.setOnClickListener(v -> {
            Intent intent = new Intent(EventListActivity.this, NotificationActivity.class);
            startActivity(intent);
        });

        switchRolesButton.setOnClickListener(v -> {
            Intent intent = new Intent(EventListActivity.this, RoleSelectionActivity.class);
            startActivity(intent);
        });

        scanQrCodeButton.setOnClickListener(v -> scanQrCode());
    }

    /**
     * Called when the activity is resumed. Reloads the profile picture to reflect any changes made in the ProfileEditActivity.
     */
    @Override
    protected void onResume() {
        String deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        super.onResume();
        loadProfileData(deviceId);
    }

    /**
     * Loads profile data from Firebase and displays it in the corresponding TextViews.
     */
    public void loadProfileData(String deviceId) {
        FirebaseController controller = new FirebaseController();
        controller.fetchUserByDeviceId(deviceId, new FirebaseController.OnUserFetchedListener() {
            @Override
            public void onUserFetched(User user) {
                if (user != null) {
                    String pfpUrl = user.getProfileImageUrl();
                    Log.d("LoadProfileData", pfpUrl);

                    if (!pfpUrl.isEmpty()) {
                        Glide.with(EventListActivity.this).load(pfpUrl).circleCrop().into(profileButton);
                    } else {
                        profileButton.setImageDrawable(user.getDisplayDrawable());
                    }
                } else {
                    Toast.makeText(EventListActivity.this, "User is null", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(EventListActivity.this, "Error fetching user profile (ProfileActivity)",
                        Toast.LENGTH_LONG).show();
            }

        });
    }

//    /**
//     * Loads the profile picture from SharedPreferences and updates the profile button image.
//     */
//    private void loadProfilePicture() {
//        SharedPreferences sharedPreferences = getSharedPreferences("UserProfile", MODE_PRIVATE);
//        String profilePictureBase64 = sharedPreferences.getString("profilePicture", "");
//
//        if (!profilePictureBase64.isEmpty()) {
//            // Convert Base64 string to Bitmap and set it on the profile button
//            byte[] decodedBytes = Base64.decode(profilePictureBase64, Base64.DEFAULT);
//            Bitmap profileBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
//            profileButton.setImageBitmap(profileBitmap);
//        } else {
//            // Set default profile picture
//            profileButton.setImageResource(R.drawable.ic_profile); // Replace with your default image resource
//        }
//    }

    /**
     * Initiates a QR code scanner and handles the scanned result.
     */
    private void scanQrCode() {
        IntentIntegrator integrator = new IntentIntegrator(EventListActivity.this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.setPrompt("Scan a QR code");
        integrator.setCameraId(0);
        integrator.setBeepEnabled(true);
        integrator.setCaptureActivity(CustomQrScannerActivity.class); // Custom scanner
        integrator.initiateScan();
    }

    /**
     * Handles the result from the QR code scanner and fetches event details.
     *
     * @param requestCode The request code originally supplied to startActivityForResult().
     * @param resultCode  The result code returned by the child activity through its setResult().
     * @param data        An Intent that carries the result data.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_QR_SCAN && resultCode == RESULT_OK && data != null) {
            String scannedEventId = data.getStringExtra("SCAN_RESULT");
            if (scannedEventId != null && !scannedEventId.isEmpty()) {
                openEventPage(scannedEventId);
            } else {
                Toast.makeText(this, "Invalid QR Code scanned.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Opens the event details page using the scanned event ID.
     *
     * @param eventId The scanned event ID.
     */
    private void openEventPage(String eventId) {
        Intent intent = new Intent(this, UserEventPageActivity.class);
        intent.putExtra("SCAN_RESULT", eventId);
        startActivity(intent);
    }
}
