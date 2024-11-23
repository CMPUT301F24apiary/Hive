/**
 * EventListActivity.java
 *
 * This activity displays a list of events to the user. It includes an ImageButton that allows
 * the user to navigate to their profile page.
 *
 * <p>Outstanding Issues:
 * - None at this time.</p>
 *
 * @author Dina
 * @version 1.0
 */
package com.example.hive;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hive.Views.CustomQrScannerActivity;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.bumptech.glide.Glide;
import com.example.hive.AdminEvent.AdminEventListActivity;
import com.example.hive.Controllers.FirebaseController;
import com.example.hive.Models.User;
import com.example.hive.Views.AdminProfileViewActivity;

public class EventListActivity extends AppCompatActivity {

    private ImageButton profileButton;
    private ImageButton notificationBellButton;  // Notification Bell button
    private Button switchRolesButton;  // Switch Roles button
    private Button scanQrCodeButton;  // Scan QR Code button
    private ImageView qrCodeImageView;  // QR Code ImageView

    private User user;

    private FirebaseController firebaseController;
    /**
     * Called when the activity is starting. This is where most initialization should be done.
     * It sets up the UI and initializes the profile button and notification bell button.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down, this Bundle contains the data it most recently supplied in onSaveInstanceState. Otherwise, it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list);

        firebaseController = new FirebaseController();

        // Initialize UI components
        profileButton = findViewById(R.id.profileButton);
        notificationBellButton = findViewById(R.id.notificationBellButton);
        switchRolesButton = findViewById(R.id.switchRolesButton);
        scanQrCodeButton = findViewById(R.id.scanQrCodeButton);
        qrCodeImageView = findViewById(R.id.qrCodeImageView);

        // Set click listeners for each button
        String deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EventListActivity.this, ProfileActivity.class);
                intent.putExtra("deviceId", deviceId);
                startActivity(intent);
            }
        });

        notificationBellButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EventListActivity.this, NotificationActivity.class);
                startActivity(intent);
            }
        });

        switchRolesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EventListActivity.this, RoleSelectionActivity.class);
                startActivity(intent);
            }
        });

        scanQrCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanQrCode();
            }
        });
    }

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
     * Handles the result from the QR code scanner and displays the event details.
     *
     * @param requestCode The request code originally supplied to startActivityForResult().
     * @param resultCode The result code returned by the child activity through its setResult().
     * @param data An Intent that carries the result data.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() != null) {
                // Display scanned QR code content
                Toast.makeText(this, "Event Details:\n" + result.getContents(), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "No QR code scanned", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
