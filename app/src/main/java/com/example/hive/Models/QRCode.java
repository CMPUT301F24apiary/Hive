package com.example.hive.Models;

import android.graphics.Bitmap;
import android.util.Base64;
import android.util.Log;

import com.google.firebase.Firebase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

/**
 * store QR Code in db. QR code generation function is in Event.java.
 * This class is to convert QR code from bitmap -> base64,
 * store it in the db, and make it retrievable from the db
 */
public class QRCode {

    /**
     * Convert QR code bitmap to string
     * @param bitmap
     * @return base64 string
     */
    private static String convertBitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return Base64.encodeToString(stream.toByteArray(), Base64.DEFAULT);
    }

    /**
     * Saves the qr string to database.
     * Note that the eventId is the auto generated id of the event document in the db.
     * @param qrCodeBitmap
     * @param eventId
     */
    public static void saveQRToDb(Bitmap qrCodeBitmap, String eventId) {
        String qrCodeBase64 = convertBitmapToBase64(qrCodeBitmap);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("events").document(eventId)
                .set(new HashMap<String, Object>() {{
                    put("qrCode", qrCodeBase64);
                }}, SetOptions.merge())
                .addOnSuccessListener(v -> Log.d("Firestore", "QR Code saved to db"))
                .addOnFailureListener(e -> Log.e("Firestore", "Error saving QR code to db"));
    }

    public static void retriveQRCodeFromDb(String eventId, QRCodeCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("events").document(eventId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String qrCodeBase64 = documentSnapshot.getString("qrCode");
                        callback.onQRCodeRetrieved(qrCodeBase64);
                    } else {
                        callback.onError(new Exception("Document DNE"));
                    }
                })
                .addOnFailureListener(callback::onError);
    }

    /**
     * Can use onQRCodeRetrieved to handle the base64 str
     */
    public interface QRCodeCallback {
        void onQRCodeRetrieved(String qrCodeBase64);
        void onError(Exception e);
    }
}
