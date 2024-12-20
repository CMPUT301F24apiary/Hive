package com.example.hive.Views;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.hive.R;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
/**
 * CustomQrScannerActivity is responsible for scanning QR codes using the device camera
 * and handling the scanned data. It includes functionality for camera permissions,
 * continuous scanning, and navigation based on the scanned data.
 *
 * <p>Features:</p>
 * <ul>
 *     <li>Requests camera permissions dynamically.</li>
 *     <li>Uses a continuous scanning mode to decode QR codes.</li>
 *     <li>Processes scanned QR code data and navigates to {@link UserEventPageActivity}.</li>
 *     <li>Provides an option to cancel the scanning process.</li>
 * </ul>
 *
 * @author Dina
 */
public class CustomQrScannerActivity extends AppCompatActivity {
    private static final int REQUEST_CAMERA_PERMISSION = 1001;
    private static final String TAG = "CustomQrScannerActivity";
    private DecoratedBarcodeView barcodeView; // Scanner view with laser overlay
    private Button cancelButton;
    /**
     * Called when the activity is first created. Initializes UI components and checks for camera permissions.
     *
     * @param savedInstanceState A Bundle containing the activity's previously saved state, if any.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_qr_scanner);

        // Initialize BarcodeView and Cancel button
        barcodeView = findViewById(R.id.barcode_scanner);
        cancelButton = findViewById(R.id.cancelButton);

        cancelButton.setOnClickListener(v -> {
            // Exit the scanner and cancel the result
            setResult(RESULT_CANCELED);
            finish();
        });

        // Check for camera permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // Request permission if not already granted
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        } else {
            // Start the scanner if permission is already granted
            startScanner();
        }
    }
    /**
     * Starts the QR code scanner in continuous scanning mode.
     * Decodes QR code data and navigates to {@link UserEventPageActivity}.
     */
    private void startScanner() {
        barcodeView.decodeContinuous(result -> {
            if (result != null && result.getText() != null) {
                String scanData = result.getText(); // Directly use the scanned data
                Log.d(TAG, "Decoded QR Code Data: " + scanData);

                // Start UserEventPageActivity directly with the scanned data as event ID
                Intent intent = new Intent(CustomQrScannerActivity.this, UserEventPageActivity.class);
                intent.putExtra("SCAN_RESULT", scanData); // Use scanData directly
                startActivity(intent);

                // Finish the scanner activity
                finish();
            } else {
                Log.e(TAG, "QR Code not detected.");
                Toast.makeText(CustomQrScannerActivity.this, "No QR Code detected. Try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    /**
     * Handles the result of the camera permission request.
     *
     * @param requestCode  The request code passed during the permission request.
     * @param permissions  The requested permissions.
     * @param grantResults The results of the permission requests.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Start the scanner if permission is granted
                startScanner();
            } else {
                // Show a message and close the activity if permission is denied
                Toast.makeText(this, "Camera permission is required to scan QR codes", Toast.LENGTH_SHORT).show();
                setResult(RESULT_CANCELED);
                finish();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    /**
     * Resumes the scanner when the activity is resumed.
     */
    @Override
    protected void onResume() {
        super.onResume();
        if (barcodeView != null) {
            // Resume scanning when the activity resumes
            barcodeView.resume();
        }
    }
    /**
     * Pauses the scanner when the activity is paused.
     */
    @Override
    protected void onPause() {
        super.onPause();
        if (barcodeView != null) {
            // Pause scanning when the activity pauses
            barcodeView.pause();
        }
    }
    /**
     * Releases resources tied to the barcode scanner when the activity is destroyed.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (barcodeView != null) {
            // Release resources tied to BarcodeView
            barcodeView.pause();
        }
    }
}

