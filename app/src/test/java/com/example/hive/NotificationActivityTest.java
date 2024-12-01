package com.example.hive;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.example.hive.Models.Notification;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static androidx.test.espresso.action.ViewActions.*;

@RunWith(AndroidJUnit4.class)
public class NotificationActivityTest {

    @Rule
    public ActivityTestRule<NotificationActivity> activityRule =
            new ActivityTestRule<>(NotificationActivity.class, true, false);

    /**
     * Test to verify that the activity launches without errors.
     */
    @Test
    public void testActivityLaunch() {
        activityRule.launchActivity(new Intent());
        onView(withId(R.id.notificationsContainer)).check(matches(isDisplayed()));
    }

    /**
     * Test to verify that the "No Notifications" message is shown when the list is empty.
     */
    @Test
    public void testNoNotificationsMessageDisplayed() {
        activityRule.launchActivity(new Intent());

        // Simulate an empty notifications list
        activityRule.getActivity().runOnUiThread(() -> {
            ArrayList<Notification> emptyList = new ArrayList<>();
            activityRule.getActivity().notifications = emptyList;
            activityRule.getActivity().displayNotifications();
        });

        // Verify "No Notifications" message is visible
        onView(withId(R.id.noNotificationsMessage)).check(matches(isDisplayed()));
    }

    /**
     * Test to verify that notifications are displayed in the UI.
     */
    @Test
    public void testNotificationsDisplayed() {
        activityRule.launchActivity(new Intent());

        // Simulate adding notifications to the list
        activityRule.getActivity().runOnUiThread(() -> {
            ArrayList<Notification> mockNotifications = new ArrayList<>();
            Notification notification = new Notification();
            notification.setContent("Test Notification");
            notification.setType("win");
            mockNotifications.add(notification);

            activityRule.getActivity().notifications = mockNotifications;
            activityRule.getActivity().displayNotifications();
        });

        // Verify the notification content is displayed in the container
        onView(withText("Test Notification")).check(matches(isDisplayed()));
    }

    /**
     * Test the back button functionality to navigate to the EventListActivity.
     */
    @Test
    public void testBackButtonFunctionality() {
        activityRule.launchActivity(new Intent());

        // Perform a click on the back button
        onView(withId(R.id.backButton)).perform(click());

        // Verify that the intended activity is launched (you'll need a mock or intent validation here)
        // For now, we verify no crash and activity finishes successfully
        assertTrue(activityRule.getActivity().isFinishing());
    }

    /**
     * Test that buttons for "Accept" and "Decline" work for a "win" notification.
     */
    @Test
    public void testNotificationButtonsForWin() {
        activityRule.launchActivity(new Intent());

        // Simulate adding a "win" notification to the list
        activityRule.getActivity().runOnUiThread(() -> {
            ArrayList<Notification> mockNotifications = new ArrayList<>();
            Notification notification = new Notification();
            notification.setContent("You've won!");
            notification.setType("win");
            mockNotifications.add(notification);

            activityRule.getActivity().notifications = mockNotifications;
            activityRule.getActivity().displayNotifications();
        });

        // Verify that "Accept" and "Decline" buttons are visible
        onView(withText("Accept")).check(matches(isDisplayed()));
        onView(withText("Decline")).check(matches(isDisplayed()));

        // Click "Accept" and ensure no crash
        onView(withText("Accept")).perform(click());
    }

    /**
     * Test that "Re-register" button works for a "lose" notification.
     */
    @Test
    public void testNotificationButtonsForLose() {
        activityRule.launchActivity(new Intent());

        // Simulate adding a "lose" notification to the list
        activityRule.getActivity().runOnUiThread(() -> {
            ArrayList<Notification> mockNotifications = new ArrayList<>();
            Notification notification = new Notification();
            notification.setContent("Better luck next time.");
            notification.setType("lose");
            mockNotifications.add(notification);

            activityRule.getActivity().notifications = mockNotifications;
            activityRule.getActivity().displayNotifications();
        });

        // Verify that "Re-register" button is visible and clickable
        onView(withText("Re-register")).check(matches(isDisplayed()));
        onView(withText("Re-register")).perform(click());
    }
}
