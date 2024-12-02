package com.example.hive;

import com.example.hive.Controllers.FirebaseController;
import com.example.hive.Models.QRCode;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.WriterException;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import android.graphics.Bitmap;
import android.util.Log;

public class QRCodeTests {
    private FirebaseController firebaseController;
    private FirebaseFirestore db;

    @Before
    public void setUp() {
        db = firebaseController.getDb();
    }

    @Test
    public void testGenerateQRCode() {
        QRCode qrCode = new QRCode("test-event-vidhi1");
        try {
            Bitmap qrBitmap = qrCode.generateQRCode(300, 300);
            assertNotNull("QR Code should not be null", qrBitmap);
        } catch (WriterException e) {
            Log.e("QRCodeTest", "QR generation failed", e);
        }
    }

    @Test
    public void testSaveQRCodeToDb() {
        String eventId = "test-event-vidhi1";
        QRCode qrCode = new QRCode(eventId);
        Bitmap qrBitmap = null;
        try {
            qrBitmap = qrCode.generateQRCode(300,300);
        } catch (Exception e) {
            Log.e("QRCodeTest", "QR generation failed", e);
        }
        QRCode.saveQRToDb(qrBitmap, eventId);
        db.collection("events").document(eventId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    String qrCodeBase64 = documentSnapshot.getString("qrCode");
                    assertTrue("QR Code should be saved", qrCodeBase64 != null && !qrCodeBase64.isEmpty());
                })
                .addOnFailureListener(e -> {
                    Log.e("QRCodeTest", "Error saving QR Code to Firestore", e);
                });
    }

    @Test
    public void testRetrieveQRCodeFromDb() {
        String eventId = "test-event-vidhi1";
        QRCode.retrieveQRCodeFromDb(eventId, new QRCode.QRCodeCallback() {
            @Override
            public void onQRCodeRetrieved(String qrCodeBase64) {
                assertTrue("QR code retrieved", qrCodeBase64 != null && !qrCodeBase64.isEmpty());
            }

            @Override
            public void onError(Exception e) {
                assertTrue("no error while generating QR code pls", false);
            }
        });
    }

    @Test
    public void testRemoveQRCodeFromDb() {
        String eventId = "test-event-vidhi1";
        QRCode.removeQRCodeFromDb(eventId);
        db.collection("events").document(eventId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    String qrCodeBase64 = documentSnapshot.getString("qrCode");
                    assertTrue("QR Code should be removed or null", qrCodeBase64 == null || qrCodeBase64.isEmpty());
                })
                .addOnFailureListener(e -> {
                    assertTrue("Error removing QR code", false);
                });
    }
}
