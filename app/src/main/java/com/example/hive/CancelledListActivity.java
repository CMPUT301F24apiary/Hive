package com.example.hive;


import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * For the organizer to see the cancelled entrants
 *
 * @author HRITTIJA
 */
public class CancelledListActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private List<String> entrantsList;
    private ArrayAdapter<String> adapter;
    private String eventId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cancelledlist);

        db = FirebaseFirestore.getInstance();

        eventId = getIntent().getStringExtra("eventId");
        if (eventId == null) {
            Toast.makeText(this, "Event ID not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize ListView
        ListView listView = findViewById(R.id.cancelled_list_view);
        entrantsList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, R.layout.list_item, entrantsList);
        listView.setAdapter(adapter);

        // Add real-time filtering while typing in the search bar
        EditText searchBar = findViewById(R.id.search_bar);
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s); // Filters the ListView dynamically as the user types
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // Back button logic
        findViewById(R.id.back_button).setOnClickListener(v -> {
            Intent intent = new Intent(CancelledListActivity.this, OptionsPageActivity.class);
            intent.putExtra("eventId", eventId); // Pass the eventId back to OptionsPageActivity
            startActivity(intent);
            finish();
        });

        // Fetch waiting list
        fetchCancelledList();
    }

    private void fetchCancelledList() {
        Log.d("CancelledListActivity", "Fetching cancelled list for eventId: " + eventId);

        db.collection("events").document(eventId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        DocumentSnapshot eventDocument = task.getResult();
                        String cancelledListId = eventDocument.getString("cancelledlistID");

                        if (cancelledListId != null) {
                            Log.d("CancelledListActivity", "CancelledListID retrieved: " + cancelledListId);

                            db.collection("cancelled-list").document(cancelledListId).get()
                                    .addOnCompleteListener(cancelledTask -> {
                                        if (cancelledTask.isSuccessful() && cancelledTask.getResult() != null) {
                                            DocumentSnapshot cancelledDocument = cancelledTask.getResult();
                                            List<String> deviceIds = (List<String>) cancelledDocument.get("userIds");

                                            if (deviceIds != null) {
                                                Log.d("CancelledListActivity", "Device IDs retrieved: " + deviceIds.toString());

                                                // List to hold usernames
                                                List<String> usernames = new ArrayList<>();

                                                // Fetch usernames corresponding to each deviceId
                                                for (String deviceId : deviceIds) {
                                                    Log.d("CancelledListActivity", "Fetching username for deviceId: " + deviceId);

                                                    db.collection("users").document(deviceId).get()
                                                            .addOnCompleteListener(userTask -> {
                                                                if (userTask.isSuccessful() && userTask.getResult() != null) {
                                                                    DocumentSnapshot userDocument = userTask.getResult();
                                                                    String username = userDocument.getString("username");
                                                                    if (username != null) {
                                                                        usernames.add(username);
                                                                        Log.d("CancelledListActivity", "Username retrieved: " + username);
                                                                    } else {
                                                                        Log.e("CancelledListActivity", "No username found for deviceId " + deviceId);
                                                                    }
                                                                } else {
                                                                    Log.e("CancelledListActivity", "Error fetching user for deviceId " + deviceId, userTask.getException());
                                                                }

                                                                // Check if all usernames have been fetched
                                                                if (usernames.size() == deviceIds.size()) {
                                                                    entrantsList.clear();
                                                                    entrantsList.addAll(usernames);
                                                                    adapter.notifyDataSetChanged();
                                                                    Log.d("CancelledListActivity", "UI updated with usernames: " + usernames.toString());
                                                                }
                                                            });
                                                }
                                            } else {
                                                Log.e("CancelledListActivity", "No userIds found in cancelled list document");
                                            }
                                        } else {
                                            Log.e("CancelledListActivity", "Error fetching cancelled list document: ", cancelledTask.getException());
                                            Toast.makeText(this, "Failed to load cancelled list", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            Log.e("CancelledListActivity", "No cancelledListId found in event document");
                            Toast.makeText(this, "Cancelled list ID not found", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e("CancelledListActivity", "Error fetching event document: ", task.getException());
                        Toast.makeText(this, "Failed to load event", Toast.LENGTH_SHORT).show();
                    }
                });
    }









}

