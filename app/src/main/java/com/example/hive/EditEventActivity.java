package com.example.hive;


import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.hive.Controllers.EventController;
import com.example.hive.Controllers.ImageController;
import com.example.hive.DateAndTimePickers.DatePickerFragment;
import com.example.hive.DateAndTimePickers.TimePickerFragment;
import com.example.hive.Events.Event;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.util.Pair;
import android.widget.ToggleButton;

/**
 * Activity to edit and event or event poster/details
 */
public class EditEventActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {

    private static final int GALLERY_REQUEST_CODE = 100;
    private EditText eventName, eventDuration, eventCost, numParticipants, entrantLimit, eventLocation, eventDescription;
    private ImageView addPosterImage;
    private ToggleButton toggleReplacementDraw, toggleGeolocation;
    private Uri posterImageUri;
    private Event currentEvent;  // The event being edited
    private String startTime, startDate, selectionDate;
    private Button pickStartTime, pickStartDate, pickSelectionDate, removePoster;
    private String activeDateButton;
    private String firebaseID;
    private boolean removePosterSelected = false;
    private String posterURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editevent);

        eventName = findViewById(R.id.eventName);
        eventDuration = findViewById(R.id.eventDuration);
        eventLocation = findViewById(R.id.eventLocation);
        eventCost = findViewById(R.id.eventCost);
        numParticipants = findViewById(R.id.numParticipants);
        entrantLimit = findViewById(R.id.entrantLimit);
        toggleReplacementDraw = findViewById(R.id.toggleReplacementDraw);
        toggleGeolocation = findViewById(R.id.toggleGeolocation);
        eventDescription = findViewById(R.id.eventDescription);
        addPosterImage = findViewById(R.id.addPosterImage);
        pickStartTime = findViewById(R.id.pick_start_time);
        pickStartDate = findViewById(R.id.pick_start_date);
        pickSelectionDate = findViewById(R.id.pick_selection_date);
        removePoster = findViewById(R.id.remove_poster);

        currentEvent = getIntent().getParcelableExtra("event");
        if (currentEvent != null) {
            Log.d("EditEventActivity", "Event Title: " + currentEvent.getTitle());
            // Log other fields similarly
        } else {
            Log.e("EditEventActivity", "No event data passed");
        }

        if (currentEvent != null) {
            eventName.setText(currentEvent.getTitle());
            eventDuration.setText(currentEvent.getDuration());
            pickStartTime.setText(currentEvent.getStartTime());
            startTime = currentEvent.getStartTime();
            pickStartDate.setText(currentEvent.getDateInDashFormat("start"));
            startDate = currentEvent.getDateInDashFormat("start");
            pickSelectionDate.setText(currentEvent.getDateInDashFormat("selection"));
            selectionDate = currentEvent.getDateInDashFormat("selection");
            eventLocation.setText(currentEvent.getLocation());
            eventCost.setText(currentEvent.getCost());
            numParticipants.setText(String.valueOf(currentEvent.getNumParticipants()));
            eventDescription.setText(currentEvent.getDescription());
            entrantLimit.setText(String.valueOf(currentEvent.getEntrantLimit()));
            toggleGeolocation.setChecked(currentEvent.isGeolocationOn());
            toggleReplacementDraw.setChecked(currentEvent.isReplacementDrawOn());
            firebaseID = currentEvent.getFirebaseID();
            posterURL = currentEvent.getPosterURL();
            if (posterURL != null && !posterURL.isEmpty()) {
                Glide.with(this).load(posterURL).into(addPosterImage);
                removePoster.setVisibility(View.VISIBLE);
            }
        } else {
            Toast.makeText(this, "Event details are not available", Toast.LENGTH_SHORT).show();
        }

        pickStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                int startHr, startMin;
                startHr = Integer.parseInt(currentEvent.getStartTime().split(":")[0]);
                startMin = Integer.parseInt(currentEvent.getStartTime().split(":")[1]);
                b.putInt("hr", startHr);
                b.putInt("min", startMin);
                TimePickerFragment timeFrag = new TimePickerFragment();
                timeFrag.setArguments(b);
                timeFrag.show(getSupportFragmentManager(), "Pick Start Time");
            }
        });

        pickStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activeDateButton = "start";
                Bundle b = new Bundle();
                String[] date = currentEvent.getDateInDashFormat("start").split("-");
                b.putInt("day", Integer.parseInt(date[0]));
                b.putInt("month", Integer.parseInt(date[1])-1);
                b.putInt("year", Integer.parseInt(date[2]));
                DatePickerFragment dateFrag = new DatePickerFragment();
                dateFrag.setArguments(b);
                dateFrag.show(getSupportFragmentManager(), "Pick Start Date");
            }
        });

        pickSelectionDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activeDateButton = "select";
                Bundle b = new Bundle();
                String[] date = currentEvent.getDateInDashFormat("selection").split("-");
                b.putInt("day", Integer.parseInt(date[0]));
                b.putInt("month", Integer.parseInt(date[1])-1);
                b.putInt("year", Integer.parseInt(date[2]));
                DatePickerFragment dateFrag = new DatePickerFragment();
                dateFrag.setArguments(b);
                dateFrag.show(getSupportFragmentManager(), "Pick Selection Date");
            }
        });

        removePoster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPosterImage.setImageResource(R.drawable.ic_add);
                removePosterSelected = true;
            }
        });

        // Back button to go to the event detail
        ImageView backArrow = findViewById(R.id.backArrow);
        backArrow.setOnClickListener(v -> {
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
        String location = eventLocation.getText().toString().trim();
        String duration = eventDuration.getText().toString().trim();
        String cost = eventCost.getText().toString().trim();
        String participantsStr = numParticipants.getText().toString().trim();
        String entrantString = entrantLimit.getText().toString().trim();
        Integer entrant;
        if (!entrantString.equals("null")) {
            entrant = Integer.parseInt(entrantLimit.getText().toString().trim());
        } else {
            entrant = null;
        }

        String description = eventDescription.getText().toString().trim();

        if (title.isEmpty() || startDate.isEmpty() || location.isEmpty() || startTime.isEmpty() ||
                cost.isEmpty() || participantsStr.isEmpty() || description.isEmpty() ||
                duration.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        Pattern durationPattern = Pattern.compile("^\\d{1,2}:\\d{2}");
        Matcher durationMatcher = durationPattern.matcher(duration);
        boolean durationMatch = durationMatcher.find();

        if (!durationMatch) {
            Toast.makeText(this, "Invalid duration - should be hh:mm",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        boolean geolocationOn = toggleGeolocation.isChecked();
        boolean replacementDrawOn = toggleReplacementDraw.isChecked();

        int numParticipantsCount = Integer.parseInt(participantsStr);
        long startDateTime = convertDateToMS(startDate, startTime);

        String endDateAndTime = getEndDateTimeFromDuration(startDate, startTime, duration);
        String[] endDateSplit = endDateAndTime.split(" ");
        long endDateTime = convertDateToMS(endDateSplit[0], endDateSplit.length > 1 ? endDateSplit[1] : "");

        long selectionDateLong = convertDateToMS(selectionDate, "0:00");

        if (removePosterSelected) {
            new ImageController().deleteImageAndUpdateRelatedDoc(posterURL, null, firebaseID, success -> {
                Log.d("EditEvent", "Image Removed");
            });
        }

        if (posterImageUri == null) {
            updateEvent(firebaseID, title, cost, startDateTime, endDateTime, description,
                    numParticipantsCount, location, null, selectionDateLong, entrant,
                    duration, geolocationOn, replacementDrawOn);
        } else {
            ImageController imgControl = new ImageController();
            try {
                imgControl.saveImage(this, posterImageUri, "event poster")
                        .addOnSuccessListener(urlAndID -> {
                            updateEvent(firebaseID, title, cost, startDateTime, endDateTime, description,
                                    numParticipantsCount, location, urlAndID, selectionDateLong,
                                    entrant, duration, geolocationOn, replacementDrawOn);
                        }).addOnFailureListener(e -> {
                            Toast.makeText(this, "Failed to upload image: " + e.getMessage() +
                                            "\nEvent will still be created - navigate to the event page and edit it to try uploading the poster again.",
                                    Toast.LENGTH_SHORT).show();
                            updateEvent(firebaseID, title, cost, startDateTime, endDateTime, description,
                                    numParticipantsCount, location, null, selectionDateLong,
                                    entrant, duration, geolocationOn, replacementDrawOn);
                        });
            } catch (Exception e) {
                Toast.makeText(this, "Image upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void updateEvent(String firebaseID, String title, String cost, long startDateTime,
                             long endDateTime, String description, int numParticipantsCount,
                             String location, @Nullable Pair<String, String> urlAndID,
                             long selectionDate, @Nullable Integer entrantLimit, String duration,
                             boolean geolocationOn, boolean replacementDrawOn) {
        Event event;
        if (urlAndID != null) {
            event = new Event(title, cost, startDateTime, endDateTime, firebaseID, description,
                    numParticipantsCount, location, urlAndID.first, selectionDate,entrantLimit,duration, geolocationOn, replacementDrawOn);  // Use the ID for the event if it's being updated
        } else {
            event = new Event(title, cost, startDateTime, endDateTime, firebaseID, description,
                    numParticipantsCount, location,  null, selectionDate,entrantLimit,duration, geolocationOn, replacementDrawOn);
        }

        EventController controller = new EventController();
        controller.updateEvent(event, id -> {
            if (urlAndID != null && urlAndID.second != null) {
                new ImageController().updateImageRef(urlAndID.second, id, false);
            }

            // Display a toast message confirming the update
            Toast.makeText(this, "Event updated: '" + event.getTitle() + "'", Toast.LENGTH_SHORT).show();

            // Finish the activity (or navigate as needed)
            finish();
        });
    }


    /**
     * This method is used to convert the date in milliseconds
     * @param date in form DD-MM-77
     * @param time in form HH:MM
     * @return the date in ms
     */
    private long convertDateToMS(String date, String time) {
        String pattern = "dd-MM-yyyy HH:mm";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.getDefault());
        try {
            String dateTimeCombined = date + " " + time;

            Date dateTime = sdf.parse(dateTimeCombined);

            return dateTime != null ? dateTime.getTime() : 0;
        } catch (ParseException e) {
            Log.e("Add Event Parse Error", e.toString());
            return 0;
        }
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

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        startTime = String.valueOf(hourOfDay) + ":" + String.valueOf(minute);
        pickStartTime.setText(startTime);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        String date = String.valueOf(dayOfMonth) + "-" + String.valueOf(month+1) + "-" + String.valueOf(year);
        if (activeDateButton.equals("start")) {
            startDate = date;
            pickStartDate.setText(startDate);
        } else if (activeDateButton.equals("select")) {
            selectionDate = date;
            pickSelectionDate.setText(selectionDate);
        }
    }
}

