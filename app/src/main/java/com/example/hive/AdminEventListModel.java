package com.example.hive;

import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Model for the event list view.
 *
 * @author Zach
 */
public class AdminEventListModel extends AbstractModel<TestEvent> {

    /**
     * Instance of <code>FirebaseManager</code>
     */
    private FirebaseManager dbManager;
    /**
     * The reference to Firestore database.
     */
    FirebaseFirestore db;

    /**
     * Constructor. Call <code>AbstractModel</code>'s constructor, and get the
     * <code>FirebaseManager</code> and firestore reference.
     */
    public AdminEventListModel() {
        super();
        this.dbManager = super.getManager();
        this.db = dbManager.getDB();
    }

    /**
     * Gets all event information from events collection of database. Once all data is retrieved,
     * callback function is called.
     *
     * @param callback
     * The function to run after all data has been retrieved.
     */
    @Override
    public void getAllFromDB(OnSuccessListener<ArrayList<TestEvent>> callback) {
        ArrayList<TestEvent> data = new ArrayList<>();
        CollectionReference eventsCollection = db.collection("events");

        eventsCollection.get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                String title = (String) doc.get("title");
                long dateAsLong = (long) doc.get("date");
                Date dateAsDate = new Date(dateAsLong);
                SimpleDateFormat sdf = new SimpleDateFormat("MMM dd-HH:mm", Locale.ENGLISH);
                String formatted = sdf.format(dateAsDate);
                String date = formatted.split("-")[0];
                String time = formatted.split("-")[1];
                String cost = (String) doc.get("cost");

                TestEvent newEvent = new TestEvent(title, date, time, cost);
                data.add(newEvent);
            }
            // Notify the callback with the fetched data
            callback.onSuccess(data);
        }).addOnFailureListener(e -> Log.e("ModelGetAll", "Error fetching data", e));
    }

}
