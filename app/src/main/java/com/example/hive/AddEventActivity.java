package com.example.hive;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hive.Controllers.AdminEventListController;

/**
 * Author:Hrittija
 */
public class AddEventActivity extends AppCompatActivity {

    private static final int GALLERY_REQUEST_CODE = 100;
    private EditText eventName, eventDate,eventTime, eventCost, numParticipants, entrantLimit, selectionDate,eventLocation, eventDescription;
    private ToggleButton toggleReplacementDraw, toggleGeolocation;
    private ImageView addPosterImage;
    private Uri posterImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addevents);
        AdminEventListController controller = new AdminEventListController();

        ImageView backArrow = findViewById(R.id.backArrow);
        eventName = findViewById(R.id.eventName);
        eventDate = findViewById(R.id.eventDate);
        eventTime = findViewById(R.id.eventTime);
        eventLocation = findViewById(R.id.eventLocation);
        eventCost = findViewById(R.id.eventCost);
        numParticipants = findViewById(R.id.numParticipants);
        entrantLimit = findViewById(R.id.entrantLimit);
        selectionDate = findViewById(R.id.SelectionDate);
        toggleReplacementDraw = findViewById(R.id.toggleReplacementDraw);
        toggleGeolocation = findViewById(R.id.toggleGeolocation);
        eventDescription = findViewById(R.id.eventDescription);
        addPosterImage = findViewById(R.id.addPosterImage);

        backArrow.setOnClickListener(v -> {
            Intent intent = new Intent(AddEventActivity.this, OrganizerEventListActivity.class);
            startActivity(intent);
            finish();
        });

        addPosterImage.setOnClickListener(this::onAddPosterClick);

        TextView saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(v -> saveEventDetails());
    }



    public void onAddPosterClick(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, GALLERY_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            posterImageUri = data.getData();
            addPosterImage.setImageURI(posterImageUri);
        }
    }


    private void saveEventDetails() {
        String title = eventName.getText().toString().trim();
        String date = eventDate.getText().toString().trim();
        String location = eventLocation.getText().toString().trim();
        String time = eventTime.getText().toString().trim();
        String cost = eventCost.getText().toString().trim();
        String participantsStr = numParticipants.getText().toString().trim();
        String description = eventDescription.getText().toString().trim();

        if (title.isEmpty() || date.isEmpty() || location.isEmpty() || time.isEmpty() ||
                cost.isEmpty() || participantsStr.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        int numParticipantsCount = Integer.parseInt(participantsStr);
        long startDateTime = System.currentTimeMillis();
        long endDateTime = startDateTime + 3600000;

        TestEvent event = new TestEvent(title, date, time, cost, description, location,
                numParticipantsCount, startDateTime, endDateTime, null);

        AdminEventListController controller = new AdminEventListController();
        controller.addEvent(event, aVoid -> {
            Toast.makeText(this, "Event created: " + event.toString(), Toast.LENGTH_SHORT).show();
            finish();
        });
    }





}
