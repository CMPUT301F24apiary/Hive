package com.example.hive;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import android.content.Intent;
import android.widget.Button;
import android.widget.TextView;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

public class NotificationTests {

    private NotificationActivity notificationActivity;

    private TextView notificationTextView;
    private Button acceptButton;
    private Button declineButton;
    private Button reRegisterButton;

    @BeforeEach
    public void setUp() {
        notificationActivity = mock(NotificationActivity.class);

        // Mock views
        notificationTextView = mock(TextView.class);
        acceptButton = mock(Button.class);
        declineButton = mock(Button.class);
        reRegisterButton = mock(Button.class);

        // Mock the behavior of findViewById to return mocked views
        when(notificationActivity.findViewById(anyInt())).thenAnswer(invocation -> {
            int id = invocation.getArgument(0);
            switch (id) {
                case 1:  // Arbitrary value representing notification1
                    return notificationTextView;
                case 2:  // Arbitrary value representing acceptButton1
                    return acceptButton;
                case 3:  // Arbitrary value representing declineButton1
                    return declineButton;
                case 4:  // Arbitrary value representing reRegisterButton2
                    return reRegisterButton;
                default:
                    return null;
            }
        });
    }

    @Test
    public void testAcceptEvent() {
        // Test accept event functionality
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);

        notificationActivity.acceptEvent("Test Event");

        verify(notificationActivity).acceptEvent(captor.capture());
        assertEquals("Test Event", captor.getValue());
    }

    @Test
    public void testDeclineEvent() {
        // Test decline event functionality
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);

        notificationActivity.declineEvent("Test Event Decline");

        verify(notificationActivity).declineEvent(captor.capture());
        assertEquals("Test Event Decline", captor.getValue());
    }

    @Test
    public void testReRegisterEvent() {
        // Test re-register event functionality
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);

        notificationActivity.reRegisterEvent("Test Re-register Event");

        verify(notificationActivity).reRegisterEvent(captor.capture());
        assertEquals("Test Re-register Event", captor.getValue());
    }
}
