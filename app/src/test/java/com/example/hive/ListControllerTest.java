package com.example.hive;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import com.example.hive.Controllers.EventController;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.HashSet;


public class ListControllerTest {

    @Mock
    private EventController mockEventController;

    private TestListController listController;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        listController = new TestListController(mockEventController);
    }

    @Test
    public void testGenerateInvitedList_whenEntrantsLessThanRequired() {
        // Setup
        ArrayList<String> entrants = new ArrayList<>();
        entrants.add("user1");
        entrants.add("user2");
        String eventId = "test-event";
        int numParticipants = 3;

        // Execute
        ArrayList<String> result = listController.generateInvitedList(eventId, entrants, numParticipants);

        // Verify
        assertEquals(2, result.size());
        assertTrue(result.contains("user1"));
        assertTrue(result.contains("user2"));
        verify(mockEventController).addInvitedList(eventId, result);
        verify(mockEventController).updateWaitingList(eventId, new ArrayList<>());
    }

    @Test
    public void testGenerateInvitedList_whenEnoughEntrants() {
        // Setup
        ArrayList<String> entrants = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            entrants.add("user" + i);
        }
        String eventId = "test-event";
        int numParticipants = 3;

        // Execute
        ArrayList<String> result = listController.generateInvitedList(eventId, entrants, numParticipants);

        // Verify
        assertEquals(numParticipants, result.size());
        // Verify all selected users are unique
        assertEquals(new HashSet<>(result).size(), result.size());
        // Verify selected users were from original list
        for (String userId : result) {
            assertTrue(userId.startsWith("user"));
            assertTrue(Integer.parseInt(userId.substring(4)) < 5);
        }
        verify(mockEventController).addInvitedList(eventId, result);

        // Verify the remaining users are in the waiting list
        ArrayList<String> remainingEntrants = new ArrayList<>(entrants);
        remainingEntrants.removeAll(result);
        verify(mockEventController).updateWaitingList(eventId, remainingEntrants);
    }

    @Test
    public void testGenerateInvitedList_withEmptyEntrants() {
        // Setup
        ArrayList<String> entrants = new ArrayList<>();
        String eventId = "test-event";
        int numParticipants = 3;

        // Execute
        ArrayList<String> result = listController.generateInvitedList(eventId, entrants, numParticipants);

        // Verify
        assertTrue(result.isEmpty());
        verify(mockEventController).addInvitedList(eventId, result);
        verify(mockEventController).updateWaitingList(eventId, entrants);
    }

    @Test
    public void testGenerateInvitedList_exactNumberOfEntrants() {
        // Setup
        ArrayList<String> entrants = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            entrants.add("user" + i);
        }
        String eventId = "test-event";
        int numParticipants = 3;

        HashSet<String> entrantsHash = new HashSet<>(entrants);

        // Execute
        ArrayList<String> result = listController.generateInvitedList(eventId, entrants, numParticipants);

        // Verify
        assertEquals(numParticipants, result.size());
        assertEquals(entrantsHash, new HashSet<>(result));
        verify(mockEventController).addInvitedList(eventId, result);
        verify(mockEventController).updateWaitingList(eventId, new ArrayList<>());
    }

    private static class TestListController {
        private final EventController eventController;

        public TestListController(EventController eventController) {
            this.eventController = eventController;
        }

        public ArrayList<String> generateInvitedList(String eventID, ArrayList<String> entrants,
                                                     int numParticipants) {
            if (entrants.size() < numParticipants) {
                eventController.addInvitedList(eventID, entrants);
                eventController.updateWaitingList(eventID, new ArrayList<>());
                return entrants;
            }

            ArrayList<String> invited = new ArrayList<>();
            while (invited.size() < numParticipants) {
                double randomNum = Math.random() % entrants.size();
                int pos = (int) Math.floor(randomNum);
                invited.add(entrants.remove(pos));
            }

            eventController.addInvitedList(eventID, invited);
            eventController.updateWaitingList(eventID, entrants);

            return invited;
        }
    }
}

