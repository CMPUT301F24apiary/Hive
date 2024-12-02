package com.example.hive.Views;



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

import com.example.hive.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

/**
 * For the organizer to see the final entrants who were invited and they accepted
 *
 * @author HRITTIJA
 */
public class FinalListActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private List<String> entrantsList;
    private ArrayAdapter<String> adapter;
    private String eventId;
    /**
     * Called when the activity is created. It initializes the UI, fetches the final entrants list from Firebase,
     * and sets up search functionality.
     *
     * @param savedInstanceState The state of the activity during its previous run.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finallist);

        db = FirebaseFirestore.getInstance();

        eventId = getIntent().getStringExtra("eventId");
        if (eventId == null) {
            Toast.makeText(this, "Event ID not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize ListView
        ListView listView = findViewById(R.id.final_list_view);
        entrantsList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, R.layout.list_item, entrantsList);
        listView.setAdapter(adapter);
        EditText searchBar = findViewById(R.id.search_bar);
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
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
//            Intent intent = new Intent(FinalListActivity.this, OptionsPageActivity.class);
//            intent.putExtra("eventId", eventId); // Pass the eventId back to OptionsPageActivity
//            startActivity(intent);
            finish();
        });

        fetchFinalList();
    }
    /**
     * Fetches the final list of entrants for the event from Firestore.
     * The event's final list ID is retrieved, and the corresponding device IDs are fetched.
     * Usernames corresponding to those device IDs are then displayed in the ListView.
     */
    private void fetchFinalList() {
        Log.d("FinalListActivity", "Fetching finallist for eventId: " + eventId);

        db.collection("events").document(eventId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        DocumentSnapshot eventDocument = task.getResult();
                        String finalListId = eventDocument.getString("finallistID");

                        if (finalListId != null) {
                            Log.d("FinalListActivity", "FinalListID retrieved: " + finalListId);

                            db.collection("final-list").document(finalListId).get()
                                    .addOnCompleteListener(finalTask -> {
                                        if (finalTask.isSuccessful() && finalTask.getResult() != null) {
                                            DocumentSnapshot finalDocument = finalTask.getResult();
                                            List<String> deviceIds = (List<String>) finalDocument.get("deviceIds");

                                            if (deviceIds != null) {
                                                Log.d("FinalListActivity", "Device IDs retrieved: " + deviceIds.toString());

                                                // List to hold usernames
                                                List<String> usernames = new ArrayList<>();

                                                // Fetch usernames corresponding to each deviceId
                                                for (String deviceId : deviceIds) {
                                                    Log.d("FinalListActivity", "Fetching username for deviceId: " + deviceId);

                                                    db.collection("users").document(deviceId).get()
                                                            .addOnCompleteListener(userTask -> {
                                                                if (userTask.isSuccessful() && userTask.getResult() != null) {
                                                                    DocumentSnapshot userDocument = userTask.getResult();
                                                                    String username = userDocument.getString("username");
                                                                    if (username != null) {
                                                                        usernames.add(username);
                                                                        Log.d("FinalListActivity", "Username retrieved: " + username);
                                                                    } else {
                                                                        Log.e("FinalListActivity", "No username found for deviceId " + deviceId);
                                                                    }
                                                                } else {
                                                                    Log.e("FinalListActivity", "Error fetching user for deviceId " + deviceId, userTask.getException());
                                                                }

                                                                // Check if all usernames have been fetched
                                                                if (usernames.size() == deviceIds.size()) {
                                                                    entrantsList.clear();
                                                                    entrantsList.addAll(usernames);
                                                                    adapter.notifyDataSetChanged();
                                                                }
                                                            });
                                                }
                                            } else {
                                                Log.e("FinalListActivity", "No userIds found in final list document");
                                            }
                                        } else {
                                            Toast.makeText(this, "Failed to load final list", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            Toast.makeText(this, "Final list ID not found", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Failed to load event", Toast.LENGTH_SHORT).show();
                    }
                });
    }









}

