package com.example.hive.Controllers;

import android.util.Log;

import androidx.annotation.VisibleForTesting;

import com.example.hive.Events.Event;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

/**
 * Controller to handle retrieving all events from the firestore database. Extends
 * <code>FirebaseController</code> and implements additional methods relating to events.
 *
 * @author Zach
 */
public class EventController extends FirebaseController {

    // Database reference
    private final FirebaseFirestore db;
    private Event lastSavedEvent;

    /**
     * Constructor - call <code>FirebaseController</code>'s constructor and initialize the database
     * reference
     */
    public EventController() {
        super();
        this.db = super.getDb();
    }


    @VisibleForTesting
    public Event getLastSavedEvent() {
        return lastSavedEvent;
    }

    public void addEvent(Event event, OnSuccessListener<String> listener) {
        HashMap<String, Object> data = event.getAll();

        db.collection("events").add(data).addOnSuccessListener(documentReference -> {
            listener.onSuccess(documentReference.getId());
            lastSavedEvent = event;  // Store event for testing purposes
        }).addOnFailureListener(e -> {
            Log.w("EventController", "Error adding event", e);
        });
    }

    public void updateEvent(Event event, OnSuccessListener<Void> listener) {
        if (event.getFirebaseID() == null || event.getFirebaseID().isEmpty()) {
            Log.w("EventController", "Event does not have a valid Firebase ID.");
            return;
        }

        DocumentReference eventRef = db.collection("events").document(event.getFirebaseID());

        eventRef.update(
                "title", event.getTitle(),
                "cost", event.getCost(),
                "startDateTime", event.getStartDateInMS(),
                "endDateTime", event.getEndDateInMS(),
                "location", event.getLocation(),
                "description", event.getDescription(),
                "numParticipants", event.getNumParticipants()
        ).addOnSuccessListener(aVoid -> {
            listener.onSuccess(null);  // Call the listener when update is successful
            Log.d("EventController", "Event successfully updated!");
        }).addOnFailureListener(e -> {
            Log.w("EventController", "Error updating event", e);
            // You could handle the failure here, but we won't pass failure to the listener.
        });
    }








    /**
     * Gets all event information from events collection of database. Once all data is retrieved,
     * callback function is called.
     *
     * @param callback
     * The function to run after all data has been retrieved.
     */
    public void getAllEventsFromDB(OnSuccessListener<ArrayList<Event>> callback) {
        ArrayList<Event> data = new ArrayList<>();
        CollectionReference eventsCollection = db.collection("events");

        eventsCollection.get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                String id = doc.getId();
                String title = (String) doc.get("title");
                long startDate = (long) doc.get("startDateInMS");
                long endDate = (long) doc.get("endDateInMS");
                String cost = (String) doc.get("cost");
                String description = (String) doc.get("description");
                String location = (String) doc.get("location");
                String posterTemp = (String) doc.get("poster");
                Long numParticipantsLong = (Long) doc.get("numParticipants");
                int numParticipants = numParticipantsLong.intValue();
                String posterURL = Objects.equals(posterTemp, "") ? null : posterTemp;
                Event newEvent = new Event(title, cost, startDate, endDate, id, description,
                        numParticipants, location, posterURL);
                data.add(newEvent);
            }
            // Notify the callback with the fetched data
            callback.onSuccess(data);
        }).addOnFailureListener(e -> Log.e("ModelGetAll", "Error fetching data", e));
    }

    /**
     * Gets all event information from events collection of database that were made by given user.
     * Once all data is retrieved, callback function is called.
     *
     * TODO Add checks for user. Currently this function just returns all events.
     *
     * @param callback
     * The function to run after all data has been retrieved.
     */
    public void getOrganizersEventsFromDB(OnSuccessListener<ArrayList<Event>> callback) {
        ArrayList<Event> data = new ArrayList<>();
        CollectionReference eventsCollection = db.collection("events");

        eventsCollection.get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                String id = doc.getId();
                String title = (String) doc.get("title");
                long startDate = (long) doc.get("startDateInMS");
                long endDate = (long) doc.get("endDateInMS");
                String cost = (String) doc.get("cost");
                String description = (String) doc.get("description");
                String location = (String) doc.get("location");
                String posterTemp = (String) doc.get("poster");
                long numParticipants = (long) doc.get("numParticipants");
                String posterURL = Objects.equals(posterTemp, "") ? null : posterTemp;
                Event newEvent = new Event(title, cost, startDate, endDate, id, description,
                        (int) numParticipants, location, posterURL);
                data.add(newEvent);
            }
            // Notify the callback with the fetched data
            callback.onSuccess(data);
        }).addOnFailureListener(e -> Log.e("ModelGetAll", "Error fetching data", e));
    }

    /**
     * Deletes a single event from the database with provided id. Handles the case in which the
     * document does not exist, or the deletion fails.
     *
     * @param id
     * The id number created in firebase that refers to the event that is to be deleted.
     * @param callback
     * The callback function to call on either success or failure. Must have a Boolean parameter.
     */
    public void deleteSingleEventFromDB(String id, OnSuccessListener<Boolean> callback) {
        CollectionReference eventsCollection = db.collection("events");

        // First, check if the document exists
        eventsCollection.document(id).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                // Document exists, proceed to delete
                eventsCollection.document(id).delete().addOnSuccessListener(unused -> {
                    callback.onSuccess(Boolean.TRUE);
                }).addOnFailureListener(e -> {
                    Log.e("ControllerDeleteEvent", "Error deleting document", e);
                    callback.onSuccess(Boolean.FALSE);
                });
            } else {
                Log.d("ControllerDeleteEvent", "Doc does not exist");
                // Document does not exist, notify callback with FALSE
                callback.onSuccess(Boolean.FALSE);
            }
        }).addOnFailureListener(e -> {
            Log.e("ControllerDeleteEvent", "Error fetching document", e);
            callback.onSuccess(Boolean.FALSE);
        });
    }

}
