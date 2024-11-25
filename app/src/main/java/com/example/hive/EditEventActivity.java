package com.example.hive;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.hive.Controllers.EventController;
import com.example.hive.Controllers.ImageController;
import com.example.hive.Events.Event;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import android.util.Pair;
import android.widget.ToggleButton;

public class EditEventActivity extends AppCompatActivity {

    private static final int GALLERY_REQUEST_CODE = 100;
    private EditText eventName, eventDate, eventTime, eventDuration, eventCost, numParticipants, entrantLimit, selectionDate, eventLocation, eventDescription;
    private ImageView addPosterImage;
    private ToggleButton toggleReplacementDraw, toggleGeolocation;
    private Uri posterImageUri;
    private Event currentEvent;  // The event being edited

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editevent);

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

        currentEvent = getIntent().getParcelableExtra("event");
        if (currentEvent != null) {
            Log.d("EditEventActivity", "Event Title: " + currentEvent.getTitle());
            // Log other fields similarly
        } else {
            Log.e("EditEventActivity", "No event data passed");
        }

        if (currentEvent != null) {
            eventName.setText(currentEvent.getTitle());
            eventDate.setText(currentEvent.getStartDate());
            eventTime.setText(currentEvent.getStartTime());
            eventDuration.setText(currentEvent.getDuration());

            eventLocation.setText(currentEvent.getLocation());
            eventCost.setText(currentEvent.getCost());
            selectionDate.setText(currentEvent.getSelectionDate());
            numParticipants.setText(String.valueOf(currentEvent.getNumParticipants()));
            eventDescription.setText(currentEvent.getDescription());
            entrantLimit.setText(String.valueOf(currentEvent.getEntrantLimit()));
        } else {
            Toast.makeText(this, "Event details are not available", Toast.LENGTH_SHORT).show();
        }


        // Back button to go to the event list
        ImageView backArrow = findViewById(R.id.backArrow);
        backArrow.setOnClickListener(v -> {
            Intent intent = new Intent(EditEventActivity.this, OrganizerEventListActivity.class);
            startActivity(intent);
            finish();
        });

        // Image selection
        addPosterImage.setOnClickListener(v -> onAddPosterClick());

        // Save the edited event details
        TextView updateButton = findViewById(R.id.updateButton);
        updateButton.setOnClickListener(v -> saveEventDetails());
    }

    public void onAddPosterClick() {
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
        String entrant = entrantLimit.getText().toString().trim();
        boolean isReplacementDraw = toggleReplacementDraw.isChecked();
        boolean isGeolocation = toggleGeolocation.isChecked();

        String description = eventDescription.getText().toString().trim();
        String selectionDateString = selectionDate.getText().toString().trim();

        if (title.isEmpty() || date.isEmpty() || location.isEmpty() || time.isEmpty() ||
                cost.isEmpty() || participantsStr.isEmpty() || description.isEmpty() ||
                duration.isEmpty() || entrant.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        int numParticipantsCount = Integer.parseInt(participantsStr);
        int entrantInt = Integer.parseInt(entrant);
        long startDateTime = convertDateToMS(date, time);

        String endDateAndTime = getEndDateTimeFromDuration(date, time, duration);
        String[] endDateSplit = endDateAndTime.split(" ");
        long endDateTime = convertDateToMS(endDateSplit[0], endDateSplit.length > 1 ? endDateSplit[1] : "");

        long selectionDate = convertDateToMS(selectionDateString, "0:00");

        if (posterImageUri == null) {
            saveEvent(title, cost, startDateTime, endDateTime, description,
                    numParticipantsCount, location, null, selectionDate, entrantInt, duration
                    );
        } else {
            ImageController imgControl = new ImageController();
            try {
                imgControl.saveImage(this, posterImageUri, "event poster")
                        .addOnSuccessListener(urlAndID -> {
                            saveEvent(title, cost, startDateTime, endDateTime, description,
                                    numParticipantsCount, location, urlAndID, selectionDate, entrantInt, duration
                                 );
                        }).addOnFailureListener(e -> {
                            Toast.makeText(this, "Failed to upload image: " + e.getMessage() +
                                            "\nEvent will still be created - navigate to the event page and edit it to try uploading the poster again.",
                                    Toast.LENGTH_SHORT).show();
                            saveEvent(title, cost, startDateTime, endDateTime, description,
                                    numParticipantsCount, location, null, selectionDate, entrantInt, duration);
                        });
            } catch (Exception e) {
                Toast.makeText(this, "Image upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void saveEvent(String title, String cost, long startDateTime, long endDateTime,
                           String description, int numParticipantsCount, String location,
                           @Nullable Pair<String, String> urlAndID, long selectionDate, int entrantLimit, String duration) {
        Event event;
        if (urlAndID != null && urlAndID.second != null) {
            event = new Event(title, cost, startDateTime, endDateTime, urlAndID.second, description,
                    numParticipantsCount, location, urlAndID.first, selectionDate,entrantLimit,duration);  // Use the ID for the event if it's being updated
        } else {
            event = new Event(title, cost, startDateTime, endDateTime, null, description,
                    numParticipantsCount, location, urlAndID == null ? null : urlAndID.first, selectionDate,entrantLimit,duration);
        }

        EventController controller = new EventController();
        controller.updateEvent(event, id -> {
            event.setFirebaseID(id);
            if (urlAndID != null && urlAndID.second != null) {
                new ImageController().updateImageRef(urlAndID.second, id, false);
            }

            // Display a toast message confirming the update
            Toast.makeText(this, "Event updated: '" + event.getTitle() + "'", Toast.LENGTH_SHORT).show();

            // Finish the activity (or navigate as needed)
            finish();
        });
    }


    private long convertDateToMS(String date, String time) {
        String[] patterns = {"MMM dd, yyyy HH:mm", "MMM dd, yyyy", "MMM dd HH:mm", "MMM dd"};
        for (String pattern : patterns) {
            SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.getDefault());
            try {
                String dateTimeCombined = date + " " + time;
                Date dateTime = sdf.parse(dateTimeCombined.trim());
                if (dateTime != null) {
                    return dateTime.getTime();
                }
            } catch (ParseException e) {
                Log.e("AddEvent Parse Error", "Unparseable date with pattern " + pattern + ": " + date + " " + time, e);
            }
        }
        // If no patterns match, return 0 or handle as needed
        return 0;
    }



    private String getEndDateTimeFromDuration(String startDate, String startTime, String duration) {
        String endDate = startDate;

        // Split up start time and duration into hours/minutes
        String[] startTimeSplit = startTime.split(":");
        String[] durationSplit = duration.split(":");

        // Convert to integers
        int startHr = Integer.parseInt(startTimeSplit[0]);
        int startMin = Integer.parseInt(startTimeSplit[1]);

        int durationHr = Integer.parseInt(durationSplit[0]);
        int durationMin = Integer.parseInt(durationSplit[1]);

        // Add the duration to the start time
        int endHr = startHr + durationHr;
        int endMin = startMin + durationMin;

        // Handle the case where the minutes of the end time are over 60 - add proper amount of
        // hours to end time, and then trim minutes to be below 60
        if (endMin >= 60) {
            endHr += endMin / 60;
            endMin = endMin % 60;
        }

        // Handle the case where the end time is past midnight
        if (endHr >= 24) {

            // Get how many days past midnight - i.e. if endHr is 49, we have 2 extra days
            int extraDays = endHr / 24;

            // Ensure end time is trimmed to be below 24
            endHr = endHr % 24;

            // Get the day, month, year from the original date and convert to ints
            String[] dateSplit = endDate.split("-");
            int dateSplitDay = Integer.parseInt(dateSplit[0]);
            int dateSplitMonth = Integer.parseInt(dateSplit[1]);
            int dateSplitYear = Integer.parseInt(dateSplit[2]);

            // Add on the extra days we calculated above
            dateSplitDay += extraDays;

            // Some boolean checks - first, if day is greater than 31 indicating we are past the end
            // of the month for those with 31 days
            boolean past31 = dateSplitDay > 31;

            // Check if day is greater than 30 for months with only 30 days
            boolean past30in30dayMonth = dateSplitDay >= 31 && (dateSplitMonth == 4 ||
                    dateSplitMonth == 6 || dateSplitMonth == 9 || dateSplitMonth == 11);

            // Check if day is greater than 28 for february, not in a leap year
            boolean past28inFeb = dateSplitDay >= 29 && dateSplitMonth == 2 &&
                    dateSplitYear % 4 != 0;

            // Finally check if day is greater than 29 in february for leap years
            // Leap years always occur in years that are divisible by 4 - there are other leap year
            // edge cases that I don't think are necessary to handle
            boolean past29leapYear = dateSplitDay >= 30 && dateSplitMonth == 2 &&
                    dateSplitYear % 4 == 0;

            // If any of the above checks are true, we need to set date to whatever date it should
            // be in the new month
            if (past29leapYear) {
                dateSplitDay = dateSplitDay - 29;
                dateSplitMonth = 3;
            } else if (past28inFeb) {
                dateSplitDay = dateSplitDay - 28;
                dateSplitMonth = 3;
            } else if (past30in30dayMonth) {
                dateSplitMonth++;
                dateSplitDay = dateSplitDay - 30;
            } else if (past31) {
                dateSplitMonth = dateSplitMonth < 12 ? dateSplitMonth + 1 : 1;
                // If we rolled over into January, increment year as well
                if (dateSplitMonth == 1) {
                    dateSplitYear++;
                }
                dateSplitDay = dateSplitDay - 31;
            }
            // Put together new end date
            endDate = String.valueOf(dateSplitDay) + "-" + String.valueOf(dateSplitMonth) + "-" +
                    String.valueOf(dateSplitYear);
        }

        // Put together end time
        String endTime = String.format(Locale.getDefault(), "%02d:%02d", endHr, endMin);

        // Return end date and end time separated by a space
        return endDate + " " + endTime;
    }

}

