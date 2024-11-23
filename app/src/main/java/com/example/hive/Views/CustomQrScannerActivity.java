package com.example.hive.Views;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hive.R;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.BarcodeView;

public class CustomQrScannerActivity extends AppCompatActivity {
    public BarcodeView barcodeView; // BarcodeView for QR code scanning
    private Button cancelButton;     // Button to cancel the scan

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_qr_scanner);

        // Initialize BarcodeView
        barcodeView = findViewById(R.id.barcode_scanner);

        // Start continuous scanning with a callback for results
        barcodeView.decodeContinuous(new BarcodeCallback() {
            @Override
            public void barcodeResult(BarcodeResult result) {
                if (result != null && result.getText() != null && !result.getText().isEmpty()) {
                    // Handle the scanned QR Code result
                    Toast.makeText(CustomQrScannerActivity.this, "QR Code Scanned: " + result.getText(), Toast.LENGTH_LONG).show();

                    // Return the result to the previous activity
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("SCAN_RESULT", result.getText());
                    setResult(RESULT_OK, returnIntent);
                    finish();
                } else {
                    // Show a message if the scan result is empty
                    Toast.makeText(CustomQrScannerActivity.this, "No QR Code detected. Try again.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void possibleResultPoints(java.util.List<com.google.zxing.ResultPoint> resultPoints) {
                // Handle potential result points if needed (optional)
            }
        });

        // Initialize and set up the Cancel button
        cancelButton = findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Cancel the scanning and return to the previous activity
                setResult(RESULT_CANCELED);
                finish();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        // Resume scanning when the activity is resumed
        if (barcodeView != null) {
            barcodeView.resume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        // Pause scanning when the activity is paused
        if (barcodeView != null) {
            barcodeView.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Release resources tied to BarcodeView
        if (barcodeView != null) {
            barcodeView.pause();
        }
    }
}
