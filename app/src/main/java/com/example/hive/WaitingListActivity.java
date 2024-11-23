package com.example.hive;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class WaitingListActivity extends AppCompatActivity {

    private ArrayAdapter<String> adapter;
    private List<String> entrantsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waitinglist);

        // Sample data for the waiting list
        entrantsList = new ArrayList<>();
        entrantsList.add("John Doe");
        entrantsList.add("Jane Smith");
        entrantsList.add("Michael Brown");
        entrantsList.add("Emily Davis");
        entrantsList.add("Chris Johnson");
        entrantsList.add("Sarah Wilson");

        // ListView and adapter
        ListView listView = findViewById(R.id.waiting_list_view);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, entrantsList);
        listView.setAdapter(adapter);

        // Search functionality
        EditText searchBar = findViewById(R.id.search_bar);
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No action needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {
                // No action needed
            }
        });
    }
}
