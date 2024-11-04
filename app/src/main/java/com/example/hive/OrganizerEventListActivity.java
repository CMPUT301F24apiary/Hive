package com.example.hive;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;

/**
 * This shows the eventlist to the organizer
 */
public class OrganizerEventListActivity extends AppCompatActivity {

    private ImageButton facilityprofileButton;
    private Button addEventButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizer_eventlist);

        facilityprofileButton = findViewById(R.id.facilityprofileButton);
        addEventButton= findViewById(R.id.addEventButton);
        facilityprofileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(OrganizerEventListActivity.this, FacilityActivity.class);
                startActivity(intent);
            }
        });
        addEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OrganizerEventListActivity.this, AddEventActivity.class);
                startActivity(intent);
            }
        });
    }
}


