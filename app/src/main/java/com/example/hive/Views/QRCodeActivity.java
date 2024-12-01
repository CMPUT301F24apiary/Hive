package com.example.hive.Views;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hive.R;
/**
 * QRCodeActivity is responsible for displaying a QR code passed via an Intent.
 * It provides a back button to close the activity and handles cases where the QR code fails to load.
 *
 * <p>Features:</p>
 * <ul>
 *     <li>Displays a QR code image in an ImageView.</li>
 *     <li>Handles user interaction for closing the activity.</li>
 *     <li>Logs and displays an error message if the QR code fails to load.</li>
 * </ul>
 *
 * @author Dina
 */
public class QRCodeActivity extends AppCompatActivity {
    /**
     * Initializes the activity, sets up UI elements, and displays the QR code.
     *
     * @param savedInstanceState If the activity is being reinitialized after previously being shut down,
     *                           this Bundle contains the data it most recently supplied in {@link #onSaveInstanceState(Bundle)}.
     *                           <b>Note: Otherwise, it is null.</b>
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code);

        ImageButton backButton = findViewById(R.id.event_back_button);
        ImageView qrImageView = findViewById(R.id.qrImageView);

        backButton.setOnClickListener(view -> {
            finish();
        });

        Intent intent = getIntent();
        Bitmap qrCodeBitmap = intent.getParcelableExtra("qrCode");

        if (qrCodeBitmap != null) {
            qrImageView.setImageBitmap(qrCodeBitmap);
        } else {
            Log.e("QRCodeActivity", "QR Code bitmap is null");
            Toast.makeText(this, "Failed to load QR Code", Toast.LENGTH_LONG).show();
            finish();
        }
    }
    /**
     * Handles the close button click event to finish and close the activity.
     *
     * @param view The view that triggered the click event.
     */
    public void onCloseClicked(android.view.View view) {
        finish();
    }
}