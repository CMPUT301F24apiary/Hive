package com.example.hive;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

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
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, entrantsList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                // Customize text color to white
                View view = super.getView(position, convertView, parent);
                TextView text = view.findViewById(android.R.id.text1);
                text.setTextColor(getResources().getColor(R.color.white)); // Change text color to white
                return view;
            }
        };
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

        // Back button logic
        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(WaitingListActivity.this, OptionsPageActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
