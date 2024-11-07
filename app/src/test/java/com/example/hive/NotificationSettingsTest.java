package com.example.hive;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import android.content.Intent;
import android.widget.ImageButton;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

public class NotificationSettingsTest {

    private ProfileEditActivity profileEditActivity;
    private ImageButton notificationBellButton;
    private ImageButton notificationChosenBellButton;
    private ImageButton notificationNotChosenBellButton;
    private ImageButton notificationOrganizerBellButton;

    @BeforeEach
    public void setUp() {
        // Create an instance of ProfileEditActivity
        profileEditActivity = new ProfileEditActivity();

        // Mock notification buttons
        notificationBellButton = mock(ImageButton.class);
        notificationChosenBellButton = mock(ImageButton.class);
        notificationNotChosenBellButton = mock(ImageButton.class);
        notificationOrganizerBellButton = mock(ImageButton.class);

        // Assign mocked buttons to profileEditActivity fields using reflection
        setPrivateField(profileEditActivity, "notificationBellButton", notificationBellButton);
        setPrivateField(profileEditActivity, "notificationChosenBellButton", notificationChosenBellButton);
        setPrivateField(profileEditActivity, "notificationNotChosenBellButton", notificationNotChosenBellButton);
        setPrivateField(profileEditActivity, "notificationOrganizerBellButton", notificationOrganizerBellButton);

        // Set up click listeners for notification bell buttons
        profileEditActivity.setupNotificationButtons();
    }

    @Test
    public void testNotificationBellButtonClick() {
        // Simulate clicking the notification bell button
        notificationBellButton.performClick();

        // Capture the Intent that is being started
        ArgumentCaptor<Intent> intentCaptor = ArgumentCaptor.forClass(Intent.class);
        verify(profileEditActivity).startActivity(intentCaptor.capture());

        // Verify the Intent is for NotificationActivity
        Intent capturedIntent = intentCaptor.getValue();
        assertEquals(NotificationActivity.class.getName(), capturedIntent.getComponent().getClassName());
    }

    @Test
    public void testNotificationChosenBellButtonClick() {
        // Simulate clicking the notification chosen bell button
        notificationChosenBellButton.performClick();

        // Capture the Intent that is being started
        ArgumentCaptor<Intent> intentCaptor = ArgumentCaptor.forClass(Intent.class);
        verify(profileEditActivity).startActivity(intentCaptor.capture());

        // Verify the Intent is for NotificationActivity
        Intent capturedIntent = intentCaptor.getValue();
        assertEquals(NotificationActivity.class.getName(), capturedIntent.getComponent().getClassName());
    }

    @Test
    public void testNotificationNotChosenBellButtonClick() {
        // Simulate clicking the notification not chosen bell button
        notificationNotChosenBellButton.performClick();

        // Capture the Intent that is being started
        ArgumentCaptor<Intent> intentCaptor = ArgumentCaptor.forClass(Intent.class);
        verify(profileEditActivity).startActivity(intentCaptor.capture());

        // Verify the Intent is for NotificationActivity
        Intent capturedIntent = intentCaptor.getValue();
        assertEquals(NotificationActivity.class.getName(), capturedIntent.getComponent().getClassName());
    }

    @Test
    public void testNotificationOrganizerBellButtonClick() {
        // Simulate clicking the notification organizer bell button
        notificationOrganizerBellButton.performClick();

        // Capture the Intent that is being started
        ArgumentCaptor<Intent> intentCaptor = ArgumentCaptor.forClass(Intent.class);
        verify(profileEditActivity).startActivity(intentCaptor.capture());

        // Verify the Intent is for NotificationActivity
        Intent capturedIntent = intentCaptor.getValue();
        assertEquals(NotificationActivity.class.getName(), capturedIntent.getComponent().getClassName());
    }

    // Helper method to set private fields using reflection
    private void setPrivateField(Object target, String fieldName, Object value) {
        try {
            java.lang.reflect.Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
