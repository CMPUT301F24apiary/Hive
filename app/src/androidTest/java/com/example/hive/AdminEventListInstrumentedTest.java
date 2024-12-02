package com.example.hive;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.mockito.AdditionalMatchers.not;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.example.hive.AdminEvent.AdminEventListActivity;
import com.example.hive.Models.Event;
import com.example.hive.Events.EventDetailActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;

@RunWith(JUnit4.class)
public class AdminEventListInstrumentedTest {

    @Rule
    public ActivityScenarioRule<AdminEventListActivity> activityRule =
            new ActivityScenarioRule<>(AdminEventListActivity.class);

    @Before
    public void setUp() {
        Intents.init();
    }

    @After
    public void tearDown() {
        Intents.release();
    }


    @Test
    public void TestLoadingDisplayed() {
        onView(withId(R.id.event_list_loading_text)).check(matches(isDisplayed()));
    }

    @Test
    public void TestTopBarAndListDisplayed() {
        ActivityScenario<AdminEventListActivity> scenario = activityRule.getScenario();
        scenario.onActivity(activity -> {
            // Call the updateList() method to simulate the data being loaded
            activity.updateList(new ArrayList<>());
        });

        // Check that the loading text is no longer displayed
        onView(withId(R.id.event_list_loading_text))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
        // Check that the list view is now displayed
        onView(withId(R.id.admin_event_list_view)).check(matches(isDisplayed()));
        onView(withId(R.id.view_profiles_btn)).check(matches(isDisplayed()));
        onView(withId(R.id.view_images_btn)).check(matches(isDisplayed()));
    }

    @Test
    public void TestDataDisplays() {
        ActivityScenario<AdminEventListActivity> scenario = activityRule.getScenario();
        scenario.onActivity(activity -> {
            ArrayList<Event> data = new ArrayList<>();
            Event testEvent = new Event(
                    "Test Title",
                    "60.00",
                    1731168706000L,    // startDate
                    1731177706000L,    // endDate
                    "testFirebaseID",
                    "Test Description",
                    20,               // numParticipants
                    "Test Location",
                    "Test Image URL", // poster
                    1731168706000L,   // selectionDate
                    50,              // entrantLimit
                    "2 hours",       // duration
                    true,           // geolocation
                    true,           // replacementDrawAllowed
                    false           // isLotteryDrawn
            );
            data.add(testEvent);
            // Call the updateList() method to simulate the data being loaded
            activity.updateList(data);
        });

        onView(withId(R.id.event_title)).check(matches(isDisplayed()));
        onView(withId(R.id.event_title)).check(matches(withText("Test Title")));

    }

    @Test
    public void testDetailsButtonClicked() {
        // Get a reference to the activity instance
        ActivityScenario<AdminEventListActivity> scenario = activityRule.getScenario();
        scenario.onActivity(activity -> {
            // Set up the necessary data
            ArrayList<Event> events = new ArrayList<>();
            events.add(new Event("Test Event", "10.0", 1620000000, 1620086400, "1", "Description 1", 100, "Location 1", "poster_url_1", 1620086400, 150, "1h 30m", true, true, false));
            activity.updateList(events);
        });

        // Click the details button for the first event
        onView(withId(R.id.event_details_button)).perform(click());


        // Verify that the EventDetailActivity is started
        intended(hasComponent(EventDetailActivity.class.getName()));
    }

}
