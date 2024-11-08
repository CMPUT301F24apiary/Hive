package com.example.hive;

import static org.mockito.Mockito.*;
import static org.junit.Assert.assertEquals;

import android.content.Intent;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

public class NotificationTests {

    private NotificationActivity notificationActivity;

    @Before
    public void setUp() {
        // Create a mock instance of NotificationActivity
        notificationActivity = mock(NotificationActivity.class);
    }

    @Test
    public void testAcceptEvent() {
        // Simulate accepting an event
        String eventName = "Test Event";

        // Use Mockito to stub the acceptEvent method
        doNothing().when(notificationActivity).acceptEvent(eventName);

        // Invoke the acceptEvent method
        notificationActivity.acceptEvent(eventName);

        // Verify that the acceptEvent method was called with the correct parameter
        verify(notificationActivity).acceptEvent(eventName);
    }

    @Test
    public void testDeclineEvent() {
        // Simulate declining an event
        String eventName = "Test Event Decline";

        // Use Mockito to stub the declineEvent method
        doNothing().when(notificationActivity).declineEvent(eventName);

        // Invoke the declineEvent method
        notificationActivity.declineEvent(eventName);

        // Verify that the declineEvent method was called with the correct parameter
        verify(notificationActivity).declineEvent(eventName);
    }

    @Test
    public void testReRegisterEvent() {
        // Simulate re-registering for an event
        String eventName = "Test Re-register Event";

        // Use Mockito to stub the reRegisterEvent method
        doNothing().when(notificationActivity).reRegisterEvent(eventName);

        // Invoke the reRegisterEvent method
        notificationActivity.reRegisterEvent(eventName);

        // Verify that the reRegisterEvent method was called with the correct parameter
        verify(notificationActivity).reRegisterEvent(eventName);
    }
}
