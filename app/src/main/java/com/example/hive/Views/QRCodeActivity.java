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

public class QRCodeActivity extends AppCompatActivity {

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

    public void onCloseClicked(android.view.View view) {
        finish();
    }
}