package com.example.hive;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import static org.junit.Assert.assertNotNull;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.hive.Views.EntrantMapActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class EntrantMapActivityInstrumentedTest {

    @Rule
    public ActivityScenarioRule<EntrantMapActivity> activityRule =
            new ActivityScenarioRule<>(new Intent(ApplicationProvider.getApplicationContext(), EntrantMapActivity.class)
                    .putExtra("eventId", "mockEventId")); // Provide the required eventId

    @Test
    public void testMapViewIsDisplayed() {
        // Ensure the map view is displayed
        onView(withId(R.id.equirectangular_map)).check(matches(isDisplayed()));
    }

    @Test
    public void testPlotLocationUpdatesMap() {
        // Use the activity instance to call plotLocationOnMap and validate its behavior
        activityRule.getScenario().onActivity(activity -> {
            // Fake latitude and longitude
            double fakeLatitude = 45.0;
            double fakeLongitude = 90.0;

            // Ensure mapView is initialized
            assertNotNull("Map view should not be null", activity.mapView);

            // Call the method to test
            activity.plotLocationOnMap(fakeLatitude, fakeLongitude);

            // Verify that the ImageView contains an updated bitmap
            BitmapDrawable drawable = (BitmapDrawable) activity.mapView.getDrawable();
            assertNotNull("Drawable should not be null after plotting location", drawable);

            Bitmap bitmap = drawable.getBitmap();
            assertNotNull("Bitmap should not be null after plotting location", bitmap);
        });
    }
}
