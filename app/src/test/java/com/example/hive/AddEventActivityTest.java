package com.example.hive;


import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.hive.Controllers.EventController;
import com.example.hive.Events.Event;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(AndroidJUnit4.class)
public class AddEventActivityTest {

    private AddEventActivity activity;

    @Before
    public void setUp() {
        ActivityScenario<AddEventActivity> scenario = ActivityScenario.launch(AddEventActivity.class);
        scenario.onActivity(a -> activity = a);
    }

    @Test
    public void testConvertDateToMS() {
        // Test with a known date and time to verify milliseconds output
        String date = "10-11-2024";
        String time = "12:00";
        long expectedMillis = 1731195600000L;  // Expected milliseconds for "10-11-2024 12:00"

        long actualMillis = activity.convertDateToMS(date, time);
        assertEquals(expectedMillis, actualMillis);
    }

    @Test
    public void testGetEndDateTimeFromDuration_sameDay() {
        // Test end date and time within the same day
        String startDate = "10-11-2024";
        String startTime = "08:00";
        String duration = "02:30";

        String expectedEndDateTime = "10-11-2024 10:30";
        String actualEndDateTime = activity.getEndDateTimeFromDuration(startDate, startTime, duration);

        assertEquals(expectedEndDateTime, actualEndDateTime);
    }

    @Test
    public void testGetEndDateTimeFromDuration_nextDay() {
        // Test end date rolls over to the next day
        String startDate = "10-11-2024";
        String startTime = "23:00";
        String duration = "02:00";

        String expectedEndDateTime = "11-11-2024 01:00";
        String actualEndDateTime = activity.getEndDateTimeFromDuration(startDate, startTime, duration);

        assertEquals(expectedEndDateTime, actualEndDateTime);
    }

    @Test
    public void testSaveEventDetails() {
        activity.eventName.setText("Sample Event");
        activity.eventDate.setText("10-11-2024");
        activity.eventTime.setText("10:00");
        activity.eventDuration.setText("02:00");
        activity.eventLocation.setText("Sample Location");
        activity.eventCost.setText("20");
        activity.numParticipants.setText("50");
        activity.eventDescription.setText("Sample Description");

        activity.saveEventDetails();

        EventController controller = new EventController();
        Event savedEvent = controller.getLastSavedEvent();

        assertNotNull(savedEvent);
        assertEquals("Sample Event", savedEvent.getTitle());
        assertEquals("Sample Location", savedEvent.getLocation());
        assertEquals("Sample Description", savedEvent.getDescription());
        assertEquals(50, savedEvent.getNumParticipants());
    }
}
