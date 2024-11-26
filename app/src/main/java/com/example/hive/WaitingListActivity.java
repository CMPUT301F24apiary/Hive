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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class WaitingListActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private List<String> entrantsList;
    private ArrayAdapter<String> adapter;
    private String eventId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waitinglist);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Get eventId from intent
        eventId = getIntent().getStringExtra("eventId");
        if (eventId == null) {
            Toast.makeText(this, "Event ID not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize ListView
        ListView listView = findViewById(R.id.waiting_list_view);
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
                // Not needed
            }
        });

        // Back button logic
        findViewById(R.id.back_button).setOnClickListener(v -> {
            Intent intent = new Intent(WaitingListActivity.this, OptionsPageActivity.class);
            intent.putExtra("eventId", eventId); // Pass the eventId back to OptionsPageActivity
            startActivity(intent);
            finish();
        });

        // Fetch waiting list
        fetchWaitingList();
    }

    private void fetchWaitingList() {
        Log.d("WaitingListActivity", "Fetching waiting list for eventId: " + eventId);

        CollectionReference waitingListRef = db.collection("events").document(eventId).collection("waiting-list");
        waitingListRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                entrantsList.clear(); // Clear the list to avoid duplicates
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String username = document.getString("username"); // Get the "username" field
                    Log.d("WaitingListActivity", "Fetched username: " + username);
                    if (username != null) {
                        entrantsList.add(username); // Add each username to the list
                    }
                }
                adapter.notifyDataSetChanged(); // Update the ListView with new data
                Log.d("WaitingListActivity", "Entrants list updated: " + entrantsList);
            } else {
                Log.e("WaitingListActivity", "Error fetching waiting list: ", task.getException());
                Toast.makeText(this, "Failed to load waiting list", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
