package com.example.hive;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class CancelledListActivity extends AppCompatActivity implements ConfirmDeleteDialogFragment.ConfirmDeleteListener {

    private FirebaseFirestore db;
    private List<String> entrantsList;
    private ArrayAdapter<String> adapter;
    private String eventId;
    private String cancelledListId;

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

        // Delete All button logic
        Button deleteAllButton = findViewById(R.id.delete_all_button);
        deleteAllButton.setOnClickListener(v -> {
            ConfirmDeleteDialogFragment dialog = new ConfirmDeleteDialogFragment(this);
            dialog.show(getSupportFragmentManager(), "ConfirmDeleteDialog");
        });
    }

    private void fetchCancelledList() {
        db.collection("events").document(eventId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        DocumentSnapshot eventDocument = task.getResult();
                        cancelledListId = eventDocument.getString("cancelledlistID");

                        if (cancelledListId != null) {
                            fetchUsernames();
                        } else {
                            Toast.makeText(this, "Cancelled list ID not found", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Failed to load event", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void fetchUsernames() {
        db.collection("cancelled-list").document(cancelledListId).get()
                .addOnCompleteListener(cancelledTask -> {
                    if (cancelledTask.isSuccessful() && cancelledTask.getResult() != null) {
                        DocumentSnapshot cancelledDocument = cancelledTask.getResult();
                        List<String> deviceIds = (List<String>) cancelledDocument.get("userIds");

                        if (deviceIds != null) {
                            entrantsList.clear();
                            fetchUserDetails(deviceIds);  // Fetch usernames using the device IDs
                        } else {
                            Toast.makeText(this, "No user IDs found", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void fetchUserDetails(List<String> deviceIds) {
        entrantsList.clear();

        for (String deviceId : deviceIds) {
            db.collection("users").document(deviceId).get()  // Assuming the collection is named "users"
                    .addOnSuccessListener(userDoc -> {
                        if (userDoc.exists()) {
                            String username = userDoc.getString("username");  // Assuming there's a "username" field
                            if (username != null) {
                                entrantsList.add(username);  // Add the username to the list
                                adapter.notifyDataSetChanged();
                            } else {
                                Log.w("CancelledListActivity", "No username found for device ID: " + deviceId);
                            }
                        } else {
                            Log.w("CancelledListActivity", "User document not found for device ID: " + deviceId);
                        }
                    })
                    .addOnFailureListener(e -> Log.e("CancelledListActivity", "Error fetching user details", e));
        }
    }


    @Override
    public void onDeleteConfirmed() {
        if (cancelledListId != null) {
            DocumentReference cancelledListRef = db.collection("cancelled-list").document(cancelledListId);

            // Update Firestore by setting "userIds" to an empty list
            cancelledListRef.update("userIds", new ArrayList<>())
                    .addOnSuccessListener(aVoid -> {
                        entrantsList.clear();
                        adapter.notifyDataSetChanged();
                        Toast.makeText(CancelledListActivity.this, "All entries deleted", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(CancelledListActivity.this, "Failed to delete entries", Toast.LENGTH_SHORT).show();
                    });
        }
    }
}
