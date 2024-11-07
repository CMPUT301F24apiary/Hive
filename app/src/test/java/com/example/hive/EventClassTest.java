package com.example.hive;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import com.example.hive.Events.Event;

import org.junit.Before;
import org.junit.Test;

/**
 * Event class tests.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 * @author Zach
 */
public class EventClassTest {
    private Event testEvent;
    private Event testEventWithNullID;
    private Event testEventWithNullURL;

    @Before
    public void setUp() {
        testEvent = new Event(
                "Test Title",
                "60.00",
                1731168706000L,
                1731177706000L,
                "testFirebaseID",
                "Test Description",
                20,
                "Test Location",
                "Test Image URL"
        );
        testEventWithNullID = new Event(
                "Test Title Null ID",
                "40.00",
                1731168706000L,
                1731177706000L,
                null,
                "Test Description Null ID",
                30,
                "Test Location Null ID",
                "Test Image URL Null ID"
        );
        testEventWithNullURL = new Event(
                "Test Title Null URL",
                "50.00",
                1731168706000L,
                1731177706000L,
                "testFirebaseID Null URL",
                "Test Description Null URL",
                40,
                "Test Location Null URL",
                null
        );
    }

    @Test
    public void testEventGetters() {
        assertEquals("Test Title", testEvent.getTitle());
        assertEquals("60.00", testEvent.getCost());
        assertEquals(1731168706000L, testEvent.getStartDateInMS());
        assertEquals(1731177706000L, testEvent.getEndDateInMS());
        assertEquals("testFirebaseID", testEvent.getFirebaseID());
        assertEquals("Test Description", testEvent.getDescription());
        assertEquals(20, testEvent.getNumParticipants());
        assertEquals("Test Location", testEvent.getLocation());
        assertEquals("Test Image URL", testEvent.getPosterURL());
        assertNull(testEventWithNullID.getFirebaseID());
        assertNull(testEventWithNullURL.getPosterURL());
    }

    @Test
    public void testEventSetters() {
        testEventWithNullID.setFirebaseID("NewFirebaseID");
        assertEquals("NewFirebaseID", testEventWithNullID.getFirebaseID());
        testEventWithNullURL.setPosterURL("NewPosterURL");
        assertEquals("NewPosterURL", testEventWithNullURL.getPosterURL());
    }

    @Test
    public void testEventDateConversion() {
        assertEquals("Nov 09", testEvent.getStartDate());
        assertEquals("09:11", testEvent.getStartTime());
        assertEquals("Nov 09", testEvent.getEndDate());
        assertEquals("11:41", testEvent.getEndTime());
    }

}
