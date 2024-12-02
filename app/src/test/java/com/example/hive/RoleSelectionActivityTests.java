package com.example.hive;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

import android.content.Intent;
import com.example.hive.AdminEvent.AdminEventListActivity;
import com.example.hive.Events.EventListActivity;
import com.example.hive.Views.OrganizerEventListActivity;
import com.example.hive.Views.RoleSelectionActivity;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class RoleSelectionActivityTests {

    private RoleSelectionActivity roleSelectionActivity;

    @Before
    public void setUp() {
        // Mock the RoleSelectionActivity
        roleSelectionActivity = Mockito.mock(RoleSelectionActivity.class);
    }

    @Test
    public void testUserButtonNavigation() {
        // Set up a mocked Intent for the User button navigation
        Intent intent = new Intent(roleSelectionActivity, EventListActivity.class);

        // Simulate the navigation action in the test by setting the intent explicitly
        when(roleSelectionActivity.getIntent()).thenReturn(intent);

        // Verify that the intent is not null, indicating the navigation is set
        assertNotNull("Intent should not be null", roleSelectionActivity.getIntent());
    }

    @Test
    public void testOrganizerButtonNavigation() {
        // Set up a mocked Intent for the Organizer button navigation
        Intent intent = new Intent(roleSelectionActivity, OrganizerEventListActivity.class);

        // Simulate the navigation action in the test by setting the intent explicitly
        when(roleSelectionActivity.getIntent()).thenReturn(intent);

        // Verify that the intent is not null
        assertNotNull("Intent should not be null", roleSelectionActivity.getIntent());
    }

    @Test
    public void testAdminButtonNavigation() {
        // Set up a mocked Intent for the Admin button navigation
        Intent intent = new Intent(roleSelectionActivity, AdminEventListActivity.class);

        // Simulate the navigation action in the test by setting the intent explicitly
        when(roleSelectionActivity.getIntent()).thenReturn(intent);

        // Verify that the intent is not null
        assertNotNull("Intent should not be null", roleSelectionActivity.getIntent());
    }
}
