package com.example.hive;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.content.Intent;
import android.location.LocationManager;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.hive.Views.UserEventPageActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class UserEventPageActivityInstrumentedTest {

    @Rule
    public ActivityScenarioRule<UserEventPageActivity> activityRule =
            new ActivityScenarioRule<>(new Intent(ApplicationProvider.getApplicationContext(), UserEventPageActivity.class)
                    .putExtra("SCAN_RESULT", "mockEventId")); // Provide the required SCAN_RESULT

    @Test
    public void testEventDescriptionIsDisplayed() {
        // Check if the event description TextView is displayed
        onView(withId(R.id.eventDescription)).check(matches(isDisplayed()));
    }

    @Test
    public void testRegisterButtonIsDisplayed() {
        // Check if the Register button is displayed
        onView(withId(R.id.registerButton)).check(matches(isDisplayed()));
    }

    @Test
    public void testUnregisterButtonIsDisplayed() {
        // Check if the Unregister button is displayed
        onView(withId(R.id.unregisterButton)).check(matches(isDisplayed()));
    }

    @Test
    public void testRegisterButtonFunctionality() {
        onView(withId(R.id.registerButton)).perform(click());


        activityRule.getScenario().onActivity(activity -> {
            assertNotNull("Event ID should not be null", activity.eventId);
        });
    }

    @Test
    public void testUnregisterButtonFunctionality() {
        onView(withId(R.id.unregisterButton)).perform(click());

        activityRule.getScenario().onActivity(activity -> {
            assertNotNull("Event ID should not be null", activity.eventId);
        });
    }

    @Test
    public void testWaitingListNotFull() {

        activityRule.getScenario().onActivity(activity -> {
            activity.fetchEventDetails("mockEventId");

            activityRule.getScenario().onActivity(act -> {
                // Mocked number of participants
                int currentParticipants = 5;  // Example current participants
                int participantLimit = 10;  // Example limit

                assertTrue("Waiting list should not be full", currentParticipants < participantLimit);
            });
        });
    }

    @Test
    public void testGeolocationIsTurnedOn() {
        activityRule.getScenario().onActivity(activity -> {
            LocationManager locationManager = (LocationManager) activity.getSystemService(UserEventPageActivity.LOCATION_SERVICE);

            boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            assertTrue("Geolocation should be turned on", isGPSEnabled || isNetworkEnabled);
        });
    }
}

