package com.example.hive;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.example.hive.Models.Notification;

import org.junit.Before;
import org.junit.Test;

import java.util.Map;

public class ModelNotificationTests {

    private Notification notification;

    @Before
    public void setUp() {
        // Create a new Notification instance before each test
        notification = new Notification("user123", "event456", "You have won!", "win");
    }

    @Test
    public void testConstructorAndGetters() {
        // Verify constructor sets the fields correctly
        assertEquals("user123", notification.getUserId());
        assertEquals("event456", notification.getEventId());
        assertEquals("You have won!", notification.getContent());
        assertEquals("win", notification.getType());
    }

    @Test
    public void testSetters() {
        // Test setting and getting userId
        notification.setUserId("user789");
        assertEquals("user789", notification.getUserId());

        // Test setting and getting eventId
        notification.setEventId("event789");
        assertEquals("event789", notification.getEventId());

        // Test setting and getting content
        notification.setContent("Better luck next time.");
        assertEquals("Better luck next time.", notification.getContent());

        // Test setting and getting type
        notification.setType("lose");
        assertEquals("lose", notification.getType());

        // Test setting and getting Firebase ID
        notification.setFirebaseId("firebase123");
        assertEquals("firebase123", notification.getFirebaseId());
    }

    @Test
    public void testToMap() {
        // Convert Notification object to a Map
        Map<String, Object> data = notification.toMap();

        // Verify the Map contains correct data
        assertEquals("user123", data.get("userId"));
        assertEquals("event456", data.get("eventId"));
        assertEquals("You have won!", data.get("content"));
        assertEquals("win", data.get("type"));
    }

    @Test
    public void testDefaultConstructorAndSetters() {
        // Create a notification using the default constructor
        Notification defaultNotification = new Notification();

        // Verify fields are initially null
        assertEquals(null, defaultNotification.getUserId());
        assertEquals(null, defaultNotification.getEventId());
        assertEquals(null, defaultNotification.getContent());
        assertEquals(null, defaultNotification.getType());
        assertEquals(null, defaultNotification.getFirebaseId());

        // Use setters to set values
        defaultNotification.setUserId("user456");
        defaultNotification.setEventId("event123");
        defaultNotification.setContent("Event update");
        defaultNotification.setType("update");
        defaultNotification.setFirebaseId("firebase789");

        // Verify fields are updated
        assertEquals("user456", defaultNotification.getUserId());
        assertEquals("event123", defaultNotification.getEventId());
        assertEquals("Event update", defaultNotification.getContent());
        assertEquals("update", defaultNotification.getType());
        assertEquals("firebase789", defaultNotification.getFirebaseId());
    }

    @Test
    public void testExcludeAnnotationForFirebaseId() {
        // Test that the Firebase ID is excluded from the Firestore properties
        notification.setFirebaseId("excludedFirebaseId");

        // Simulate Firebase serialization
        Map<String, Object> serializedData = notification.toMap();

        // Ensure firebaseId is not part of the serialized data
        assertEquals(null, serializedData.get("firebaseId"));
    }

    @Test
    public void testEmptyNotification() {
        // Test creating an empty notification
        Notification emptyNotification = new Notification();

        // Ensure all fields are null
        assertEquals(null, emptyNotification.getUserId());
        assertEquals(null, emptyNotification.getEventId());
        assertEquals(null, emptyNotification.getContent());
        assertEquals(null, emptyNotification.getType());
    }

    @Test
    public void testNotificationFields() {
        // Test setting and getting Firebase ID
        notification.setFirebaseId("testFirebaseId");
        assertEquals("testFirebaseId", notification.getFirebaseId());
    }
}
