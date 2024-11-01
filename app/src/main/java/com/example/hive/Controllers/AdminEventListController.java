package com.example.hive.Controllers;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.hive.TestEvent;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class AdminEventListController {

    private FirebaseController dbController;
    private FirebaseFirestore db;

    public AdminEventListController() {
        this.dbController = new FirebaseController();
        this.db = dbController.getDb();
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
                long dateAsLong = (long) doc.get("date");
                Date dateAsDate = new Date(dateAsLong);
                SimpleDateFormat sdf = new SimpleDateFormat("MMM dd-HH:mm", Locale.ENGLISH);
                String formatted = sdf.format(dateAsDate);
                String date = formatted.split("-")[0];
                String time = formatted.split("-")[1];
                String cost = (String) doc.get("cost");

                TestEvent newEvent = new TestEvent(title, date, time, cost, dateAsLong, id);
                data.add(newEvent);
            }
            // Notify the callback with the fetched data
            callback.onSuccess(data);
        }).addOnFailureListener(e -> Log.e("ModelGetAll", "Error fetching data", e));
    }

    public void getSingleEventFromDB(String id, OnSuccessListener<TestEvent> callback) {

    }

    public void deleteSingleEventFromDB(String id, OnSuccessListener<Boolean> callback) {
        CollectionReference eventsCollection = db.collection("events");
        eventsCollection.document(id).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                callback.onSuccess(Boolean.TRUE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                callback.onSuccess(Boolean.FALSE);
            }
        });
    }

}
