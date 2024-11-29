package com.example.hive.Controllers;

import android.util.Log;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;

import com.example.hive.Events.Event;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    public void addEvent(Event event, String deviceID, OnSuccessListener<String> listener) {
        HashMap<String, Object> data = event.getAll();
        data.put("isLotteryDrawn", Boolean.FALSE);

        DocumentReference waitingListDoc = db.collection("waiting-list").document();
        String waitingListDocID = waitingListDoc.getId();
        waitingListDoc.set(new HashMap<>()).addOnSuccessListener(v -> {

            data.put("waiting-list-id", waitingListDocID);
            db.collection("events").add(data).addOnSuccessListener(documentReference -> {
                event.setFirebaseID(documentReference.getId());
                HashMap<String, Object> userData = new HashMap<>();
                userData.put("events", documentReference.getId());
                updateUserByDeviceId(deviceID, userData, success -> {
                    if (success) {
                        listener.onSuccess(documentReference.getId());
                        lastSavedEvent = event;  // Store event for testing purposes
                    }
                });

            }).addOnFailureListener(e -> {
                Log.w("EventController", "Error adding event", e);
            });
        });

    }


    public void updateEvent(Event event, OnSuccessListener<String> listener) {
        if (event.getFirebaseID() == null) {
            Log.w("EventController", "Event ID is null. Fetching event ID...");

            // If the event doesn't have a firebaseID, attempt to fetch it by other identifiers
            db.collection("events")
                    .whereEqualTo("title", event.getTitle())
                    .whereEqualTo("startDateInMS", event.getStartDate())  // Ensure the comparison uses the right field
                    .get()
                    .addOnSuccessListener(querySnapshot -> {
                        if (!querySnapshot.isEmpty()) {
                            DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                            // Set the firebaseID once we find the event
                            event.setFirebaseID(document.getId());

                            // Now retry the update with the correct firebaseID
                            updateEvent(event, listener);  // Recursive call to retry the update with the new ID
                        } else {
                            Log.e("EventController", "No matching event found for update.");
                        }
                    })
                    .addOnFailureListener(e -> Log.e("EventController", "Error fetching event ID", e));
            return;  // Exit the current method to prevent updating with a null ID
        }

        // Proceed with updating the event if the firebaseID is set
        HashMap<String, Object> data = event.getAll();
        db.collection("events").document(event.getFirebaseID()).update(data)
                .addOnSuccessListener(unused -> {
                    Log.d("EventController", "Event updated successfully.");
                    listener.onSuccess(event.getFirebaseID());  // Return the event ID on success
                })
                .addOnFailureListener(e -> Log.w("EventController", "Error updating event", e));
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
                Long entrantLimitLong = (Long) doc.get("entrantLimit");
                Integer entrantLimit = null;  // Default to null in case there's no value
                if (entrantLimitLong != null) {
                    entrantLimit = entrantLimitLong.intValue();  // Convert to Integer if not null
                } else {
                    Log.d("EventController", "entrantLimit is null for event: " + doc.getId());
                }

                String duration = (String) doc.get("duration");


                String description = (String) doc.get("description");
                String location = (String) doc.get("location");
                String posterTemp = (String) doc.get("poster");
                long numParticipants = (long) doc.get("numParticipants");
                long selectionDate = (long) doc.get("selectionDate");
                boolean geolocationOn = (boolean) doc.get("geolocation");
                boolean replacementDrawOn = (boolean) doc.get("replacementDrawAllowed");
                String posterURL = Objects.equals(posterTemp, "") ? null : posterTemp;
                boolean isLotteryDrawn = (boolean) doc.get("isLotteryDrawn");
                Event newEvent = new Event(title, cost, startDate, endDate, id, description,
                        (int) numParticipants, location, posterURL, selectionDate, entrantLimit,
                        duration, geolocationOn, replacementDrawOn, isLotteryDrawn);
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
                Long startDateLong = (Long) doc.get("startDateInMS");
                Long endDateLong = (Long) doc.get("endDateInMS");
                String cost = (String) doc.get("cost");
                Long entrantLimitLong = (Long) doc.get("entrantLimit");
                Integer entrantLimit = null;
                if (entrantLimitLong != null) {
                    entrantLimit = entrantLimitLong.intValue(); // Convert to Integer if not null
                }
                String duration = (String) doc.get("duration");
                String description = (String) doc.get("description");
                String location = (String) doc.get("location");
                String posterTemp = (String) doc.get("poster");
                long numParticipantsLong = (long) doc.get("numParticipants");
                Long selectionDateLong = (Long) doc.get("selectionDate");
                boolean geolocationOn = (boolean) doc.get("geolocation");
                boolean replacementDrawOn = (boolean) doc.get("replacementDrawAllowed");
                String posterURL = Objects.equals(posterTemp, "") ? null : posterTemp;
                boolean isLotteryDrawn = (boolean) doc.get("isLotteryDrawn");


                // Check if the required fields are null before creating the event object
                if (startDateLong != null && endDateLong != null && selectionDateLong != null) {
                    Event newEvent = new Event(title, cost, startDateLong, endDateLong, id,
                            description, (int) numParticipantsLong, location, posterURL,
                            selectionDateLong, entrantLimit, duration, geolocationOn,
                            replacementDrawOn, isLotteryDrawn);
                    data.add(newEvent);
                } else {
                    Log.d("EventController", "One of the required fields is null for event: " + id);
                }
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

    public void getSingleEvent(String id, OnSuccessListener<Event> listener) {
        CollectionReference eventsCollection = db.collection("events");

        eventsCollection.document(id).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Event event = documentSnapshot.toObject(Event.class);
                        listener.onSuccess(event);
                    }
                });
    }

    public void getField(String id, String whichField, OnSuccessListener<Object> listener) {
        CollectionReference eventsCollection = db.collection("events");

        eventsCollection.document(id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Object res = documentSnapshot.get(whichField);
                if (res != null) {
                    listener.onSuccess(res);
                } else {
                    Log.e("EventController - getField", whichField + " is not a field");
                }
            }
        });

    }

    public void getInvitedList(String eventID,
                               OnSuccessListener<Pair<Boolean, ArrayList<String>>> listener) {
        CollectionReference eventsCollection = db.collection("events");

        eventsCollection.document(eventID).collection("invited-list").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> docs = queryDocumentSnapshots.getDocuments();
                        if (docs.isEmpty()) {
                            listener.onSuccess(new Pair<>(Boolean.FALSE, null));
                        } else {
                            ArrayList<String> userIDs = new ArrayList<>();
                            for (DocumentSnapshot doc:docs) {
                                userIDs.add(doc.getId());
                            }
                            listener.onSuccess(new Pair<>(Boolean.TRUE, userIDs));
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        listener.onSuccess(new Pair<>(Boolean.FALSE, null));
                    }
                });

    }

    public void addInvitedList(String eventID, ArrayList<String> invited) {
        CollectionReference eventsCollection = db.collection("events");

        for (String uid:invited) {
            Log.d("AddInvitedList", uid);
            eventsCollection.document(eventID).collection("invited-list").document(uid)
                    .set(new HashMap<>());
        }
        HashMap<String, Object> data = new HashMap<>();
        data.put("isLotteryDrawn", Boolean.TRUE);
        eventsCollection.document(eventID).update(data);

    }

}