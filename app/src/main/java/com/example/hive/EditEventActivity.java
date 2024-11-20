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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Activity to edit an existing event by updating event details (By an organizer)
 *
 * @author Hrittija
 */
public class EditEventActivity extends AppCompatActivity {

    private static final int GALLERY_REQUEST_CODE = 100;
    private EditText eventName, eventDate, eventTime, eventDuration, eventCost, numParticipants, entrantLimit, selectionDate, eventLocation, eventDescription;
    private ToggleButton toggleReplacementDraw, toggleGeolocation;
    private ImageView addPosterImage;
    private Uri posterImageUri;
    private Event eventToEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addevents);  // Use the same layout as AddEventActivity

        // Retrieve the event to edit from the intent
        Intent intent = getIntent();
        eventToEdit = (Event) intent.getSerializableExtra("event");  // Assuming Event implements Serializable

        // Initialize the views
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

        // Set up back button
        backArrow.setOnClickListener(v -> {
            Intent intentBack = new Intent(EditEventActivity.this, OrganizerEventListActivity.class);
            startActivity(intentBack);
            finish();
        });

        // Set up poster image
        addPosterImage.setOnClickListener(this::onAddPosterClick);

        // Populate the fields with the event details
        populateEventDetails();

        // Set up save button to save edited event details
        TextView saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(v -> saveEventDetails());
    }

    /**
     * Populate the fields with existing event details
     */
    private void populateEventDetails() {
        if (eventToEdit != null) {
            eventName.setText(eventToEdit.getTitle());
//            eventDate.setText(formatDate(eventToEdit.getStartDate()));
//            eventTime.setText(formatTime(eventToEdit.getStartTime()));
//            eventDuration.setText(getEventDuration(eventToEdit.getStartDateTime(), eventToEdit.getEndDateTime()));
            eventLocation.setText(eventToEdit.getLocation());
            eventCost.setText(eventToEdit.getCost());
            numParticipants.setText(String.valueOf(eventToEdit.getNumParticipants()));
            eventDescription.setText(eventToEdit.getDescription());
            // Set any other fields based on the event object
        }
    }

    /**
     * Format the start date to "dd-MM-yyyy" format
     */
    private String formatDate(long startDateTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        return sdf.format(new Date(startDateTime));
    }

    /**
     * Format the start time to "HH:mm" format
     */
    private String formatTime(long startDateTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return sdf.format(new Date(startDateTime));
    }

    /**
     * Get the duration of the event based on start and end time
     */
    private String getEventDuration(long startDateTime, long endDateTime) {
        long durationInMillis = endDateTime - startDateTime;
        int hours = (int) (durationInMillis / (1000 * 60 * 60));
        int minutes = (int) ((durationInMillis / (1000 * 60)) % 60);
        return String.format(Locale.getDefault(), "%02d:%02d", hours, minutes);
    }

    /**
     * To open the gallery for profile picture uploading
     * @param view
     */
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

    /**
     * The method to save the edited event details
     */
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

        eventToEdit.setTitle(title);
        eventToEdit.setCost(cost);
        eventToEdit.setStartTime(startDateTime);
//        eventToEdit.setEndTime(endDateTime);
        eventToEdit.setLocation(location);
        eventToEdit.setDescription(description);
        eventToEdit.setNumParticipants(numParticipantsCount);

        EventController controller = new EventController();
        controller.updateEvent(eventToEdit, id -> {
            eventToEdit.setFirebaseID(id);
            Toast.makeText(this, "Event updated: " + eventToEdit.toString(), Toast.LENGTH_SHORT).show();
            Intent resultIntent = new Intent();
            resultIntent.putExtra("event", eventToEdit);
            setResult(1, resultIntent);
            finish();
        });
    }

    /**
     * Convert date to milliseconds
     * @param date in form DD-MM-YY
     * @param time in form HH:MM
     * @return the date in milliseconds
     */
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

    /**
     * Get the end date and time from the start date, time, and duration
     * @param startDate the event start date
     * @param startTime the event start time
     * @param duration the event duration
     * @return the end date and time in "dd-MM-yyyy HH:mm" format
     */
    /**
     * Get the end date and time from the start date, time, and duration
     * @param startDate the event start date
     * @param startTime the event start time
     * @param duration the event duration
     * @return the end date and time in "dd-MM-yyyy HH:mm" format
     */
    private String getEndDateTimeFromDuration(String startDate, String startTime, String duration) {
        String startDateTime = startDate + " " + startTime;
        long startDateTimeMillis = convertDateToMS(startDate, startTime);

        String[] durationParts = duration.split(":");
        int hours = Integer.parseInt(durationParts[0]);
        int minutes = Integer.parseInt(durationParts[1]);

        long endDateTimeMillis = startDateTimeMillis + (hours * 60 * 60 * 1000) + (minutes * 60 * 1000);

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault());
        return sdf.format(new Date(endDateTimeMillis));
    }

}
