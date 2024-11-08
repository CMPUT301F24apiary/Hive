package com.example.hive;




import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.espresso.action.ViewActions;
import androidx.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;



import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;


@RunWith(AndroidJUnit4.class)
public class AddEventActivityTest {

    @Rule
    public ActivityScenarioRule<AddEventActivity> activityScenarioRule =
            new ActivityScenarioRule<>(AddEventActivity.class);

    @Test
    public void testSaveEventDetails() {
        onView(withId(R.id.eventName)).perform(ViewActions.typeText("Test Event"));
        onView(withId(R.id.eventDate)).perform(ViewActions.typeText("05-11-2024"));
        onView(withId(R.id.eventTime)).perform(ViewActions.typeText("10:00"));
        onView(withId(R.id.eventDuration)).perform(ViewActions.typeText("02:00"));
        onView(withId(R.id.eventLocation)).perform(ViewActions.typeText("Community Center"));
        onView(withId(R.id.eventCost)).perform(ViewActions.typeText("50"));
        onView(withId(R.id.numParticipants)).perform(ViewActions.typeText("100"));
        onView(withId(R.id.eventDescription)).perform(ViewActions.typeText("A fun community event"));


        onView(withId(R.id.saveButton)).perform(scrollTo(), click());

    }


    @Test
    public void testBackButtonNavigation() {
        onView(withId(R.id.backArrow)).perform(click());

        onView(withId(R.id.organizer_event_list_view)).check(matches(isDisplayed()));
    }
}
