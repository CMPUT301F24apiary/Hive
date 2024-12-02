package com.example.hive;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import com.example.hive.Models.Event;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

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

        testEventWithNullID = new Event(
                "Test Title Null ID",
                "40.00",
                1731168706000L,
                1731177706000L,
                null,
                "Test Description Null ID",
                30,
                "Test Location Null ID",
                "Test Image URL Null ID",
                1731168706000L,
                40,
                "3 hours",
                false,
                true,
                false
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
                null,
                1731168706000L,
                30,
                "1 hour",
                true,
                false,
                false
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
        assertEquals(true, testEvent.getGeolocation());
        assertEquals(50, testEvent.getEntrantLimit().intValue());
        assertEquals("2 hours", testEvent.getDuration());
        assertEquals(true, testEvent.isReplacementDrawAllowed());
        assertEquals(false, testEvent.isLotteryDrawn());
        assertEquals(1731168706000L, testEvent.getSelectionDateInMS());

        // Test null cases
        assertNull(testEventWithNullID.getFirebaseID());
        assertNull(testEventWithNullURL.getPosterURL());
        assertEquals(false, testEventWithNullID.getGeolocation());
    }

    @Test
    public void testEventSetters() {
        testEventWithNullID.setFirebaseID("NewFirebaseID");
        assertEquals("NewFirebaseID", testEventWithNullID.getFirebaseID());

        testEventWithNullURL.setPosterURL("NewPosterURL");
        assertEquals("NewPosterURL", testEventWithNullURL.getPosterURL());

        testEventWithNullID.setGeolocation(true);
        assertEquals(true, testEventWithNullID.getGeolocation());

        testEvent.setLotteryDrawn(true);
        assertEquals(true, testEvent.isLotteryDrawn());

        testEvent.setReplacementDrawAllowed(false);
        assertEquals(false, testEvent.isReplacementDrawAllowed());
    }

    @Test
    public void testEventDateConversion() {
        assertEquals("Nov 09", testEvent.getStartDate());
        assertEquals("09:11", testEvent.getStartTime());
        assertEquals("Nov 09", testEvent.getEndDate());
        assertEquals("11:41", testEvent.getEndTime());
        assertEquals("Nov 09", testEvent.getSelectionDate());
    }

    @Test
    public void testWaitingListOperations() {
        String testUserId = "user123";
        String testUserName = "John Doe";

        testEvent.addToWaitingList(testUserId, testUserName);
        assertEquals(testUserName, testEvent.getWaitingList().get(testUserId));

        testEvent.removeFromWaitingList(testUserId);
        assertNull(testEvent.getWaitingList().get(testUserId));
    }

    @Test
    public void testDateInDashFormat() {
        assertEquals("09-11-2024", testEvent.getDateInDashFormat("start"));
        assertEquals("09-11-2024", testEvent.getDateInDashFormat("end"));
        assertEquals("09-11-2024", testEvent.getDateInDashFormat("selection"));
    }

    @Test
    public void testGetAll() {
        HashMap<String, Object> data = testEvent.getAll();
        assertEquals(testEvent.getTitle(), data.get("title"));
        assertEquals(testEvent.getCost(), data.get("cost"));
        assertEquals(testEvent.getDescription(), data.get("description"));
        assertEquals(testEvent.getLocation(), data.get("location"));
        assertEquals(testEvent.getEndDateInMS(), data.get("endDateInMS"));
        assertEquals(testEvent.getStartDateInMS(), data.get("startDateInMS"));
        assertEquals(testEvent.getNumParticipants(), data.get("numParticipants"));
        assertEquals(testEvent.getDuration(), data.get("duration"));
        assertEquals(testEvent.getSelectionDateInMS(), data.get("selectionDate"));
        assertEquals(testEvent.getGeolocation(), data.get("geolocation"));
        assertEquals(testEvent.isReplacementDrawAllowed(), data.get("replacementDrawAllowed"));
        assertEquals(testEvent.getEntrantLimit(), data.get("entrantLimit"));
        assertEquals(testEvent.getPosterURL(), data.get("poster"));
    }
}