package com.example.hive;


import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isClickable;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.is;


@RunWith(AndroidJUnit4.class)
public class AddEventActivityTest {

    @Rule
    public ActivityScenarioRule<AddEventActivity> activityScenarioRule =
            new ActivityScenarioRule<>(AddEventActivity.class);

    @Before
    public void setUp() {
        // Initialize Intents for intent verification (if needed in your tests)
        Intents.init();
    }
    @Test
    public void testEventNameIsDisplayed() {
        onView(withId(R.id.eventName)).check(matches(isDisplayed()));
        onView(withId(R.id.eventDuration)).check(matches(isDisplayed()));
        onView(withId(R.id.eventCost)).check(matches(isDisplayed()));
        onView(withId(R.id.numParticipants)).check(matches(isDisplayed()));
        onView(withId(R.id.eventDescription)).check(matches(isDisplayed()));
        onView(withId(R.id.addPosterImage)).check(matches(isDisplayed()));
    }


    @Test
    public void testBackButtonNavigation() {
        onView(withId(R.id.backArrow)).perform(click());
        onView(withId(R.id.organizer_event_list_view)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    @Test
    public void testSaveButtonWithoutFillingFields() {
        onView(withId(R.id.saveButton))
                .perform(scrollTo())  // Scroll to the Save button
                .perform(click());    // Click the Save button
        onView(withId(R.id.eventName)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

    }



    @Test
    public void testDatePicker() {
        onView(withId(R.id.pick_start_date)).perform(click());
        onView(withText("OK")).check(matches(isDisplayed()));
    }
    @Test
    public void testEditTextIsClickable() {
        onView(withId(R.id.eventName)).check(matches(isClickable()));
        onView(withId(R.id.eventCost)).check(matches(isClickable()));
        onView(withId(R.id.eventDuration)).check(matches(isClickable()));
        onView(withId(R.id.eventDescription)).check(matches(isClickable()));
        onView(withId(R.id.numParticipants)).check(matches(isClickable()));
        onView(withId(R.id.eventDuration)).check(matches(isClickable()));


    }




    @After
    public void tearDown() {
        Intents.release(); // Release Intents after each test if used
    }


}

