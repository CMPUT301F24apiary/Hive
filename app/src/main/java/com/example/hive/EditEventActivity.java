package com.example.hive;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hive.Controllers.EventController;
import com.example.hive.Events.Event;
import com.google.android.gms.tasks.OnSuccessListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class EditEventActivity extends AppCompatActivity {

    private static final int GALLERY_REQUEST_CODE = 100;
    private EditText eventName, eventDate, eventTime, eventDuration, eventCost, numParticipants, entrantLimit, selectionDate, eventLocation, eventDescription;
    private ToggleButton toggleReplacementDraw, toggleGeolocation;
    private ImageView addPosterImage;
    private Uri posterImageUri;
    private Event event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editevent);

        EventController controller = new EventController();

        ImageView backArrow = findViewById(R.id.backArrow);
        eventName = findViewById(R.id.eventName);
        eventDate = findViewById(R.id.eventDate);
        eventTime = findViewById(R.id.eventTime);
        eventDuration = findViewById(R.id.eventDuration);
        eventLocation = findViewById(R.id.eventLocation);
        eventCost = findViewById(R.id.eventCost);
        numParticipants = findViewById(R.id.numParticipants);
        entrantLimit = findViewById(R.id.entrantLimit);
        selectionDate = findViewById(R.id.SelectionDate);
        toggleReplacementDraw = findViewById(R.id.toggleReplacementDraw);
        toggleGeolocation = findViewById(R.id.toggleGeolocation);
        eventDescription = findViewById(R.id.eventDescription);
        addPosterImage = findViewById(R.id.addPosterImage);

        Intent intent = getIntent();
        event = intent.getParcelableExtra("event");
        if (event == null) {
            Toast.makeText(this, "Error loading event data", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Populate the fields with event details
        loadEventDetails();

        backArrow.setOnClickListener(v -> {
            finish();
        });

        addPosterImage.setOnClickListener(this::onAddPosterClick);

        TextView updateButton = findViewById(R.id.updateButton);
        updateButton.setOnClickListener(v -> saveEventDetails());
    }

    private void loadEventDetails() {
        eventName.setText(event.getTitle());
        eventDate.setText(new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date(event.getStartDate())));
        eventTime.setText(new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date(event.getStartDate())));
        eventDuration.setText(getDurationString(event.getStartDateInMS(), event.getEndDateInMS()));
        eventLocation.setText(event.getLocation());
        eventCost.setText(event.getCost());
        numParticipants.setText(String.valueOf(event.getNumParticipants()));
        eventDescription.setText(event.getDescription());

//        if (event.getPosterUri() != null) {
//            posterImageUri = Uri.parse(event.getPosterUri());
//            addPosterImage.setImageURI(posterImageUri);
//        }
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
        String duration = eventDuration.getText().toString().trim();
        String cost = eventCost.getText().toString().trim();
        String participantsStr = numParticipants.getText().toString().trim();
        String description = eventDescription.getText().toString().trim();

        if (title.isEmpty() || date.isEmpty() || location.isEmpty() || time.isEmpty() ||
                cost.isEmpty() || participantsStr.isEmpty() || description.isEmpty() ||
                duration.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        int numParticipantsCount = Integer.parseInt(participantsStr);

        long startDateTime = convertDateToMS(date, time);

        String endDateAndTime = getEndDateTimeFromDuration(date, time, duration);

        String[] endDateSplit = endDateAndTime.split(" ");

        long endDateTime = convertDateToMS(endDateSplit[0], endDateSplit[1]);

        event.setTitle(title);
        event.setStartDate(startDateTime);
        event.setEndDate(endDateTime);
        event.setLocation(location);
        event.setDescription(description);
        event.setCost(cost);
        event.setNumParticipants(numParticipantsCount);
//        if (posterImageUri != null) {
//            event.setPosterUri(posterImageUri.toString());
//        }

        EventController controller = new EventController();
        controller.updateEvent(event, new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(EditEventActivity.this, "Event updated successfully", Toast.LENGTH_SHORT).show();

                Intent resultIntent = new Intent();
                resultIntent.putExtra("event", event);
                setResult(RESULT_OK, resultIntent);
                finish();  // Finish the activity and go back
            }
        });

    }

    private long convertDateToMS(String date, String time) {
        String pattern = "dd-MM-yyyy HH:mm";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.getDefault());
        try {
            String dateTimeCombined = date + " " + time;

            Date dateTime = sdf.parse(dateTimeCombined);

            return dateTime != null ? dateTime.getTime() : 0;
        } catch (ParseException e) {
            Log.e("Edit Event Parse Error", e.toString());
            return 0;
        }
    }

    private String getEndDateTimeFromDuration(String startDate, String startTime, String duration) {
        return new AddEventActivity().getEndDateTimeFromDuration(startDate, startTime, duration);
    }

    private String getDurationString(long startTime, long endTime) {
        long durationMillis = endTime - startTime;
        long hours = (durationMillis / (1000 * 60 * 60)) % 24;
        long minutes = (durationMillis / (1000 * 60)) % 60;
        return String.format(Locale.getDefault(), "%02d:%02d", hours, minutes);
    }
}
