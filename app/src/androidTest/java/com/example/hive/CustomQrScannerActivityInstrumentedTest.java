package com.example.hive;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import static org.junit.Assert.assertTrue;

import android.content.Intent;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.hive.Views.CustomQrScannerActivity;
import com.example.hive.R;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class CustomQrScannerActivityInstrumentedTest {

    @Rule
    public ActivityScenarioRule<CustomQrScannerActivity> activityRule =
            new ActivityScenarioRule<>(CustomQrScannerActivity.class);

    @Test
    public void testCameraPreviewIsDisplayed() {
        // Ensure the camera preview is displayed
        onView(withId(R.id.barcode_scanner)).check(matches(isDisplayed()));
    }

    @Test
    public void testCancelButtonIsDisplayed() {
        // Ensure the cancel button is displayed
        onView(withId(R.id.cancelButton)).check(matches(isDisplayed()));
    }

    @Test
    public void testCancelButtonFunctionality() {
        // Simulate clicking the cancel button
        onView(withId(R.id.cancelButton)).perform(click());

        // Verify the activity is finished
        activityRule.getScenario().onActivity(activity -> {
            assertTrue(activity.isFinishing());
        });
    }
}
