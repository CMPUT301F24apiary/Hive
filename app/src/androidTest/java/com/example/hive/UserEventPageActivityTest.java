package com.example.hive;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;

import android.content.Intent;
import android.view.View;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.hive.Views.UserEventPageActivity;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Test class for UserEventPageActivity to validate the behavior of the activity's UI and interactions.
 *
 * @author Dina
 */
@RunWith(AndroidJUnit4.class)
public class UserEventPageActivityTest {

    @Rule
    public ActivityScenarioRule<UserEventPageActivity> activityRule =
            new ActivityScenarioRule<>(UserEventPageActivity.class);

    /**
     * Sets up the test environment by injecting mock data or initializing dependencies if required.
     */
    @Before
    public void setUp() {
        // If necessary, inject dependencies or configure mock data here
    }

    /**
     * Cleans up resources or resets states after each test case is executed.
     */
    @After
    public void tearDown() {
        // Cleanup logic if necessary
    }

    /**
     * Tests if the event details are displayed correctly in the activity.
     */
    @Test
    public void testDisplayEventDetails() {
        // Launch the activity with mock data
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), UserEventPageActivity.class);
        intent.putExtra("SCAN_RESULT", "mockEventId"); // Replace with actual key
        activityRule.getScenario().onActivity(activity -> activity.startActivity(intent));

        // Wait for async tasks (if needed)
        onView(isRoot()).perform(waitFor(2000)); // Wait for 2 seconds

        // Check if event title is displayed
        onView(withId(R.id.eventTitle)).check(matches(withText("Mock Event")));

        // Check other UI elements
        onView(withId(R.id.eventDescription)).check(matches(withText("Event Description: Mock Description")));
        onView(withId(R.id.locationTextView)).check(matches(withText("Location: Mock Location")));
        onView(withId(R.id.costTextView)).check(matches(withText("$10.00")));
    }

    /**
     * Tests if the register button works as expected by simulating a button click.
     */
    @Test
    public void testRegisterButton() {
        // Launch the activity with mock data
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), UserEventPageActivity.class);
        intent.putExtra("SCAN_RESULT", "mockEventId"); // Replace with actual key
        activityRule.getScenario().onActivity(activity -> activity.startActivity(intent));

        // Simulate register button click
        onView(withId(R.id.registerButton)).perform(click());

        // Verify the success Toast is displayed
        onView(withText("Successfully registered for the event!"))

                .check(matches(isDisplayed()));
    }

    /**
     * Tests if the unregister button works as expected by simulating a button click.
     */
    @Test
    public void testUnregisterButton() {
        // Launch the activity with mock data
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), UserEventPageActivity.class);
        intent.putExtra("SCAN_RESULT", "mockEventId"); // Replace with actual key
        activityRule.getScenario().onActivity(activity -> activity.startActivity(intent));

        // Simulate unregister button click
        onView(withId(R.id.unregisterButton)).perform(click());

        // Verify the success Toast is displayed
        onView(withText("Successfully unregistered from the event!"))

                .check(matches(isDisplayed()));
    }

    /**
     * Wait utility for delaying the test to wait for async tasks or animations.
     */
    private static ViewAction waitFor(long millis) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isRoot();
            }

            @Override
            public String getDescription() {
                return "Wait for " + millis + " milliseconds.";
            }

            @Override
            public void perform(UiController uiController, View view) {
                uiController.loopMainThreadForAtLeast(millis);
            }
        };
    }
}


