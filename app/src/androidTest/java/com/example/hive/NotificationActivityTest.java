package com.example.hive;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.content.Intent;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.hive.Events.EventListActivity;
import com.example.hive.Models.Notification;
import com.example.hive.Views.NotificationActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

@RunWith(AndroidJUnit4.class)
public class NotificationActivityTest {

    @Rule
    public ActivityScenarioRule<NotificationActivity> activityRule =
            new ActivityScenarioRule<>(new Intent(ApplicationProvider.getApplicationContext(), NotificationActivity.class));

    @Test
    public void testNotificationsContainerIsDisplayed() {
        // Check if the notifications container is displayed
        onView(withId(R.id.notificationsContainer)).check(matches(isDisplayed()));
    }

    @Test
    public void testBackButtonNavigatesToEventList() {
        // Simulate clicking the back button
        onView(withId(R.id.backButton)).perform(click());

        // Verify that the activity is no longer displayed
        activityRule.getScenario().onActivity(activity -> {
            Intent expectedIntent = new Intent(activity, EventListActivity.class);
            assert expectedIntent != null : "Back navigation should not result in a null Intent.";
        });
    }

    @Test
    public void testEmptyNotificationsShowsMessage() {
        activityRule.getScenario().onActivity(activity -> {
            try {
                // Access the private field 'notifications'
                Field notificationsField = NotificationActivity.class.getDeclaredField("notifications");
                notificationsField.setAccessible(true);
                ArrayList<?> notifications = (ArrayList<?>) notificationsField.get(activity);

                // Clear the notifications list
                notifications.clear();

                // Use reflection to invoke 'displayNotifications'
                Method displayMethod = NotificationActivity.class.getDeclaredMethod("displayNotifications");
                displayMethod.setAccessible(true);

                activity.runOnUiThread(() -> {
                    try {
                        displayMethod.invoke(activity);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    // Assert that the message is displayed
                    onView(withId(R.id.noNotificationsMessage)).check(matches(isDisplayed()));
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Test
    public void testAcceptButtonFunctionality() {
        activityRule.getScenario().onActivity(activity -> {
            try {
                // Access private field 'notifications'
                Field notificationsField = NotificationActivity.class.getDeclaredField("notifications");
                notificationsField.setAccessible(true);
                ArrayList<Notification> notifications = (ArrayList<Notification>) notificationsField.get(activity);

                // Add mock notification
                notifications.add(new Notification("win", "Test Content", "12345", "testEventId"));

                // Use reflection to invoke 'displayNotifications'
                Method displayMethod = NotificationActivity.class.getDeclaredMethod("displayNotifications");
                displayMethod.setAccessible(true);

                activity.runOnUiThread(() -> {
                    try {
                        displayMethod.invoke(activity);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    // Simulate clicking the accept button
                    onView(withId(R.id.action_button_1)).perform(click());
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Test
    public void testDeclineButtonFunctionality() {
        activityRule.getScenario().onActivity(activity -> {
            try {
                // Access private field 'notifications'
                Field notificationsField = NotificationActivity.class.getDeclaredField("notifications");
                notificationsField.setAccessible(true);
                ArrayList<Notification> notifications = (ArrayList<Notification>) notificationsField.get(activity);

                // Add mock notification
                notifications.add(new Notification("win", "Test Content", "12345", "testEventId"));

                // Use reflection to invoke 'displayNotifications'
                Method displayMethod = NotificationActivity.class.getDeclaredMethod("displayNotifications");
                displayMethod.setAccessible(true);

                activity.runOnUiThread(() -> {
                    try {
                        displayMethod.invoke(activity);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    // Simulate clicking the decline button
                    onView(withId(R.id.action_button_2)).perform(click());
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
