package com.example.hive;

import android.content.Context;
import android.content.Intent;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.hive.Controllers.NotificationActionReceiver;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.BeforeClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertNotNull;

@RunWith(AndroidJUnit4.class)
public class NotificationActionReceiverTests {

    private static FirebaseFirestore db;
    private Context context;
    private NotificationActionReceiver receiver;

    @BeforeClass
    public static void setUpOnce() {
        // Point to Firebase Emulator (initialize only once for all tests)
        db = FirebaseFirestore.getInstance();
        db.useEmulator("10.0.2.2", 8080);
    }

    @Before
    public void setUp() {
        context = ApplicationProvider.getApplicationContext();
        receiver = new NotificationActionReceiver();
    }

    @Test
    public void testAcceptAction() {
        // Prepare the intent for the "Accept" action
        Intent intent = new Intent("ACTION_ACCEPT");
        intent.putExtra("eventId", "mockEventId");
        intent.putExtra("userId", "mockUserId");
        intent.putExtra("notificationId", "mockNotificationId");

        // Insert mock event data
        Map<String, Object> mockEventData = new HashMap<>();
        mockEventData.put("title", "Mock Event");

        db.collection("events").document("mockEventId").set(mockEventData).addOnSuccessListener(aVoid -> {
            receiver.onReceive(context, intent);

            // Verify the user was added to the final list in Firebase Emulator
            db.collection("events").document("mockEventId")
                    .collection("finalList").document("mockUserId")
                    .get().addOnSuccessListener(snapshot -> {
                        assertNotNull("User should be added to the final list.", snapshot);
                    });
        });
    }

    @Test
    public void testDeclineAction() {
        // Prepare the intent for the "Decline" action
        Intent intent = new Intent("ACTION_DECLINE");
        intent.putExtra("eventId", "mockEventId");
        intent.putExtra("userId", "mockUserId");
        intent.putExtra("notificationId", "mockNotificationId");

        // Insert mock event data
        Map<String, Object> mockEventData = new HashMap<>();
        mockEventData.put("title", "Mock Event");

        db.collection("events").document("mockEventId").set(mockEventData).addOnSuccessListener(aVoid -> {
            receiver.onReceive(context, intent);

            // Verify the user was added to the cancelled list in Firebase Emulator
            db.collection("events").document("mockEventId")
                    .collection("cancelledList").document("mockUserId")
                    .get().addOnSuccessListener(snapshot -> {
                        assertNotNull("User should be added to the cancelled list.", snapshot);
                    });
        });
    }

    @Test
    public void testReRegisterAction() {
        // Prepare the intent for the "Re-register" action
        Intent intent = new Intent("ACTION_REREGISTER");
        intent.putExtra("eventId", "mockEventId");
        intent.putExtra("userId", "mockUserId");
        intent.putExtra("notificationId", "mockNotificationId");

        // Insert mock waiting list data
        Map<String, Object> mockWaitingListData = new HashMap<>();
        mockWaitingListData.put("title", "Mock Waiting List");

        db.collection("waitingList").document("mockWaitingListId").set(mockWaitingListData).addOnSuccessListener(aVoid -> {
            receiver.onReceive(context, intent);

            // Verify the user was re-added to the waiting list in Firebase Emulator
            db.collection("waitingList").document("mockWaitingListId")
                    .collection("users").document("mockUserId")
                    .get().addOnSuccessListener(snapshot -> {
                        assertNotNull("User should be re-added to the waiting list.", snapshot);
                    });
        });
    }
}
