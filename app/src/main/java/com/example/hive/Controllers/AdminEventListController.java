package com.example.hive.Controllers;

import static com.google.android.gms.common.internal.safeparcel.SafeParcelable.NULL;

import android.util.Log;

import com.example.hive.Events.TestEvent;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

/**
 * Controller to handle retrieving all events from the firestore database. Extends
 * <code>FirebaseController</code> and implements additional methods relating to events.
 *
 * @author Zach
 */
public class AdminEventListController extends FirebaseController {

    // Database reference
    private final FirebaseFirestore db;

    /**
     * Constructor - call <code>FirebaseController</code>'s constructor and initialize the database
     * reference
     */
    public AdminEventListController() {
        super();
        this.db = super.getDb();
    }

    /**
     * Gets all event information from events collection of database. Once all data is retrieved,
     * callback function is called.
     *
     * @param callback
     * The function to run after all data has been retrieved.
     */
    public void getAllEventsFromDB(OnSuccessListener<ArrayList<TestEvent>> callback) {
        ArrayList<TestEvent> data = new ArrayList<>();
        CollectionReference eventsCollection = db.collection("events");

        eventsCollection.get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                String id = doc.getId();
                String title = (String) doc.get("title");
                long startDate = (long) doc.get("start_date_time");
                long endDate = (long) doc.get("end_date_time");
                String cost = (String) doc.get("cost");
                String description = (String) doc.get("description");
                String location = (String) doc.get("location");
                String posterTemp = (String) doc.get("poster");
                String posterURL = Objects.equals(posterTemp, "") ? null : posterTemp;
                TestEvent newEvent = new TestEvent(title, cost, startDate, endDate, id, description,
                        location, posterURL);
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
