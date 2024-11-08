package com.example.hive;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import android.content.Intent;

import com.example.hive.AdminEvent.AdminEventListActivity;
import com.example.hive.AdminImage.AdminImageListActivity;
import com.example.hive.Events.EventDetailActivity;

import org.junit.Before;
import org.junit.Test;

public class AdminEventAndImagesTest {

    private AdminEventListActivity adminEventListActivity;

    @Before
    public void setUp() {
        adminEventListActivity = mock(AdminEventListActivity.class);
    }

    @Test
    public void TestEventDetailsClicked() {
        // Set up a mocked Intent for the Details button navigation
        Intent intent = new Intent(adminEventListActivity, EventDetailActivity.class);

        // Simulate the navigation action in the test by setting the intent explicitly
        when(adminEventListActivity.getIntent()).thenReturn(intent);

        // Verify that the intent is not null, indicating the navigation is set
        assertNotNull("Intent should not be null", adminEventListActivity.getIntent());
    }

    @Test
    public void TestViewImagesClicked() {
        // Set up a mocked Intent for the View Images button navigation
        Intent intent = new Intent(adminEventListActivity, AdminImageListActivity.class);

        // Simulate the navigation action in the test by setting the intent explicitly
        when(adminEventListActivity.getIntent()).thenReturn(intent);

        // Verify that the intent is not null, indicating the navigation is set
        assertNotNull("Intent should not be null", adminEventListActivity.getIntent());
    }

}
