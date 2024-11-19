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
    //qr scanner used from https://github.com/zxing/zxing, on 2024-11-18
    private BarcodeView barcodeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_qr_scanner);

        barcodeView = findViewById(R.id.barcode_scanner);
        barcodeView.decodeContinuous(new BarcodeCallback() {
            @Override
            public void barcodeResult(BarcodeResult result) {
                if (result != null) {
                    // Handle QR Code result
                    Toast.makeText(CustomQrScannerActivity.this, "QR Code: " + result.getText(), Toast.LENGTH_LONG).show();

                    // Return the result to the previous activity
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("SCAN_RESULT", result.getText());
                    setResult(RESULT_OK, returnIntent);
                    finish();
                }
            }

            @Override
            public void possibleResultPoints(java.util.List<com.google.zxing.ResultPoint> resultPoints) {
                // No action needed
            }
        });

        Button cancelButton = findViewById(R.id.cancelButton);
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
    protected void onResume() {
        super.onResume();
        barcodeView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        barcodeView.pause();
    }
}
