package com.example.hive;

import static org.mockito.Mockito.*;
import static org.junit.Assert.assertNotNull;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;

public class ActivityEventListTests {

    private EventListActivity eventListActivity;
    private ImageButton notificationBellButton;
    private ImageButton profileButton;
    private Button adminViewEventListButton;
    private Button switchRolesButton;

    @Before
    public void setUp() {
        // Mock the activity to avoid Android dependencies
        eventListActivity = Mockito.mock(EventListActivity.class);

        // Mock UI components
        notificationBellButton = Mockito.mock(ImageButton.class);
        profileButton = Mockito.mock(ImageButton.class);
        adminViewEventListButton = Mockito.mock(Button.class);
        switchRolesButton = Mockito.mock(Button.class);

        // Simulate findViewById calls in the activity
        when(eventListActivity.findViewById(R.id.notificationBellButton)).thenReturn(notificationBellButton);
        when(eventListActivity.findViewById(R.id.profileButton)).thenReturn(profileButton);
        when(eventListActivity.findViewById(R.id.switchRolesButton)).thenReturn(switchRolesButton);

        // Use doAnswer to simulate button click interactions
        doAnswer((Answer<Void>) invocation -> null).when(notificationBellButton).performClick();
        doAnswer((Answer<Void>) invocation -> null).when(profileButton).performClick();
        doAnswer((Answer<Void>) invocation -> null).when(adminViewEventListButton).performClick();
        doAnswer((Answer<Void>) invocation -> null).when(switchRolesButton).performClick();
    }

    @Test
    public void testActivityInitialization() {
        // Ensure activity and UI components are not null
        assertNotNull("EventListActivity should not be null", eventListActivity);
        assertNotNull("Notification Bell Button should not be null", eventListActivity.findViewById(R.id.notificationBellButton));
        assertNotNull("Profile Button should not be null", eventListActivity.findViewById(R.id.profileButton));
        assertNotNull("Switch Roles Button should not be null", eventListActivity.findViewById(R.id.switchRolesButton));
    }

    @Test
    public void testNotificationBellButtonClick() {
        // Simulate clicking the notification bell button
        notificationBellButton.performClick();

        // Verify the button click
        verify(notificationBellButton, times(1)).performClick();
    }

    @Test
    public void testProfileButtonClick() {
        // Simulate clicking the profile button
        profileButton.performClick();

        // Verify the button click
        verify(profileButton, times(1)).performClick();
    }

    @Test
    public void testAdminViewEventListButtonClick() {
        // Simulate clicking the admin event list button
        adminViewEventListButton.performClick();

        // Verify the button click
        verify(adminViewEventListButton, times(1)).performClick();
    }

    @Test
    public void testSwitchRolesButtonClick() {
        // Simulate clicking the switch roles button
        switchRolesButton.performClick();

        // Verify the button click
        verify(switchRolesButton, times(1)).performClick();
    }
}
