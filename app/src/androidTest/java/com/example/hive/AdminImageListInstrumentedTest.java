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

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.example.hive.AdminImage.AdminImageListActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.HashMap;

@RunWith(JUnit4.class)
public class AdminImageListInstrumentedTest {

    @Rule
    public ActivityScenarioRule<AdminImageListActivity> activityRule =
            new ActivityScenarioRule<>(AdminImageListActivity.class);

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
        onView(withId(R.id.image_list_loading_text)).check(matches(isDisplayed()));
    }

    @Test
    public void TestTopBarAndListDisplayed() {
        ActivityScenario<AdminImageListActivity> scenario = activityRule.getScenario();
        scenario.onActivity(activity -> {
            // Call the updateList() method to simulate the data being loaded
            activity.updateList(new ArrayList<>());
        });

        // Check that the loading text is no longer displayed
        onView(withId(R.id.image_list_loading_text))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
        // Check that the list view is now displayed
        onView(withId(R.id.admin_image_recycler)).check(matches(isDisplayed()));
        onView(withId(R.id.view_profiles_btn)).check(matches(isDisplayed()));
        onView(withId(R.id.view_events_btn)).check(matches(isDisplayed()));
    }

    @Test
    public void TestDataDisplays() {
        ActivityScenario<AdminImageListActivity> scenario = activityRule.getScenario();
        scenario.onActivity(activity -> {
            ArrayList<HashMap<String, String>> data = new ArrayList<>();
            HashMap<String, String> testImage = new HashMap<>();
            testImage.put("url", "");
            testImage.put("info", "test info");
            data.add(testImage);
            // Call the updateList() method to simulate the data being loaded
            activity.updateList(data);
        });

        onView(withId(R.id.admin_image_item_image_detail)).check(matches(isDisplayed()));
        onView(withId(R.id.admin_image_item_image_detail)).check(matches(withText("test info")));

    }

}
