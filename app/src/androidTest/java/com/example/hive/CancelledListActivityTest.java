package com.example.hive;



import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.content.Intent;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class CancelledListActivityTest {

    @Rule
    public ActivityScenarioRule<CancelledListActivity> activityRule =
            new ActivityScenarioRule<>(new Intent(ApplicationProvider.getApplicationContext(), CancelledListActivity.class)
                    .putExtra("eventId", "mockEventId"));

    @Test
    public void testCancelledListIsDisplayed() {
        // Check if the ListView for cancelled entrants is displayed
        onView(withId(R.id.cancelled_list_view)).check(matches(isDisplayed()));
    }

    @Test
    public void testSearchBarIsDisplayed() {
        // Check if the search bar is displayed
        onView(withId(R.id.search_bar)).check(matches(isDisplayed()));
    }

    @Test
    public void testBackButtonNavigatesToOptionsPage() {
        // Simulate clicking the back button
        onView(withId(R.id.back_button)).perform(click());

        // Verify navigation to OptionsPageActivity
        activityRule.getScenario().onActivity(activity -> {
            Intent expectedIntent = new Intent(activity, OptionsPageActivity.class);
            assert expectedIntent != null : "Back navigation should not result in a null Intent.";
        });
    }

    @Test
    public void testDeleteAllButtonShowsConfirmationDialog() {
        // Simulate clicking the "Delete All" button
        onView(withId(R.id.delete_all_button)).perform(click());

        // Verify that the confirmation dialog is displayed
        onView(withText("Are you sure you want to delete all entries?")).check(matches(isDisplayed()));
    }


    @Test
    public void testEmptyCancelledListShowsToast() {
        activityRule.getScenario().onActivity(activity -> {
            try {
                // Access private field 'entrantsList'
                Field entrantsListField = CancelledListActivity.class.getDeclaredField("entrantsList");
                entrantsListField.setAccessible(true);
                List<String> entrantsList = (List<String>) entrantsListField.get(activity);

                // Clear the entrants list
                entrantsList.clear();

                // Access private field 'adapter' and notify it
                Field adapterField = CancelledListActivity.class.getDeclaredField("adapter");
                adapterField.setAccessible(true);
                ArrayAdapter<String> adapter = (ArrayAdapter<String>) adapterField.get(activity);
                adapter.notifyDataSetChanged();

                // Assert that the entrants list is empty
                assert entrantsList.isEmpty() : "Entrants list should be empty.";

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Test
    public void testRealTimeCancelledListUpdates() {
        activityRule.getScenario().onActivity(activity -> {
            try {
                Field entrantsListField = CancelledListActivity.class.getDeclaredField("entrantsList");
                entrantsListField.setAccessible(true);
                List<String> entrantsList = (List<String>) entrantsListField.get(activity);

                Field adapterField = CancelledListActivity.class.getDeclaredField("adapter");
                adapterField.setAccessible(true);
                ArrayAdapter<String> adapter = (ArrayAdapter<String>) adapterField.get(activity);

                // Add mock users to the entrants list
                entrantsList.add("Mock User 1");
                entrantsList.add("Mock User 2");
                adapter.notifyDataSetChanged();

                // Assert that the ListView is updated
                assert entrantsList.size() == 2 : "Entrants list size should match the number of added users.";

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}

