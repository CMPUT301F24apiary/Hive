package com.example.hive;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hive.Controllers.EventController;
import com.example.hive.Controllers.FacilityController;
import com.example.hive.Controllers.FirebaseController;
import com.example.hive.Controllers.ImageController;
import com.example.hive.DateAndTimePickers.DatePickerFragment;
import com.example.hive.DateAndTimePickers.TimePickerFragment;
import com.example.hive.Events.Event;
import com.example.hive.Models.User;
import com.example.hive.OrganizerEventListActivity;
import com.example.hive.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Activity to add an event by filling in all the event details(By an organizer)
 *
 * @author Hrittija
 */
public class AddEventActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {

    private static final int GALLERY_REQUEST_CODE = 100;
    private EditText eventName, eventDuration, eventCost, numParticipants, entrantLimit, eventDescription;
    private ToggleButton toggleReplacementDraw, toggleGeolocation;
    private ImageView addPosterImage;
    private Uri posterImageUri;
    private String startTime, startDate, selectionDate;
    private Button pickStartTime, pickStartDate, pickSelectionDate;
    private String activeDateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addevents);
        EventController controller = new EventController();

        ImageView backArrow = findViewById(R.id.backArrow);
        eventName = findViewById(R.id.eventName);
        eventDuration = findViewById(R.id.eventDuration);
//        eventLocation = findViewById(R.id.eventLocation);
        eventCost = findViewById(R.id.eventCost);
        numParticipants = findViewById(R.id.numParticipants);
        entrantLimit = findViewById(R.id.entrantLimit);
        toggleReplacementDraw = findViewById(R.id.toggleReplacementDraw);
        toggleGeolocation = findViewById(R.id.toggleGeolocation);
        eventDescription = findViewById(R.id.eventDescription);
        addPosterImage = findViewById(R.id.addPosterImage);

        backArrow.setOnClickListener(v -> {
            Intent intent = new Intent(AddEventActivity.this, OrganizerEventListActivity.class);
            startActivity(intent);
            finish();
        });

        pickStartTime = findViewById(R.id.pick_start_time);
        pickStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerFragment().show(getSupportFragmentManager(), "Pick Start Time");
            }
        });

        pickStartDate = findViewById(R.id.pick_start_date);
        pickStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activeDateButton = "start";
                new DatePickerFragment().show(getSupportFragmentManager(), "Pick Start Date");
            }
        });

        pickSelectionDate = findViewById(R.id.pick_selection_date);
        pickSelectionDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activeDateButton = "select";
                new DatePickerFragment().show(getSupportFragmentManager(), "Pick Selection Date");
            }
        });

        addPosterImage.setOnClickListener(this::onAddPosterClick);

        TextView saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(v -> saveEventDetails());
    }


    /**
     * To open the gallery for facility profile picture uploading
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
     * The method to save the event details
     */
    private void saveEventDetails() {
        String title = eventName.getText().toString().trim();
        String duration = eventDuration.getText().toString().trim();
        String cost = eventCost.getText().toString().trim();
        String participantsStr = numParticipants.getText().toString().trim();
        String entrant= entrantLimit.getText().toString().trim();

        String description = eventDescription.getText().toString().trim();

        if (title.isEmpty() || startDate.isEmpty() || startTime.isEmpty() ||
                cost.isEmpty() || participantsStr.isEmpty() || description.isEmpty() ||
                duration.isEmpty() || selectionDate.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields.",
                    Toast.LENGTH_SHORT).show();
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
        int entrantInt = Integer.parseInt(entrant);
        long startDateTime = convertDateToMS(startDate, startTime);

        String endDateAndTime = getEndDateTimeFromDuration(startDate, startTime, duration);

        String[] endDateSplit = endDateAndTime.split(" ");

        long endDateTime = convertDateToMS(endDateSplit[0], endDateSplit[1]);

        long selectionDateLong = convertDateToMS(selectionDate, "0:00");

        if (posterImageUri == null) {
            saveEvent(title, cost, startDateTime, endDateTime, description,
                    numParticipantsCount, null, selectionDateLong,entrantInt,
                    duration, geolocationOn, replacementDrawOn);
        } else {
            ImageController imgControl = new ImageController();
            try {
                imgControl.saveImage(this, posterImageUri, "event poster")
                        .addOnSuccessListener(urlAndID -> {

                            saveEvent(title, cost, startDateTime, endDateTime, description,
                                    numParticipantsCount, urlAndID, selectionDateLong,
                                    entrantInt, duration, geolocationOn, replacementDrawOn);

                        }).addOnFailureListener(e -> {
                            // Handle the failure of the image upload
                            Toast.makeText(this, "Failed to upload image: " +
                                            e.getMessage() +
                                            "\nEvent will still be created - navigate to the event page " +
                                            "and edit it to try uploading the poster again.",
                                    Toast.LENGTH_SHORT).show();

                            saveEvent(title, cost, startDateTime, endDateTime, description,
                                    numParticipantsCount, null, selectionDateLong,
                                    entrantInt, duration, geolocationOn, replacementDrawOn);

                        });
            } catch(Exception e) {
                Toast.makeText(this, "Image upload failed: " + e.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void saveEvent(String title, String cost, long startDateTime, long endDateTime,
                           String description, int numParticipantsCount,
                           @Nullable Pair<String, String> urlAndID, long selectionDate, int entrant,
                           String duration, boolean geolocationOn, boolean replacementDrawOn) {

        String deviceID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        new FacilityController().getUserFacilityDetails(deviceID, facility -> {

                Event event = new Event(title, cost, startDateTime, endDateTime, null, description,
                        numParticipantsCount, facility.getName(), urlAndID == null ? null : urlAndID.first,
                        selectionDate,entrant, duration, geolocationOn, replacementDrawOn, false);

                EventController controller = new EventController();
                controller.addEvent(event, id -> {
                    event.setFirebaseID(id);
                    if (urlAndID != null) {
                        new ImageController().updateImageRef(urlAndID.second, id);
                    }

                    // Create an empty waiting-list subcollection for the event
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection("events").document(id).collection("waiting-list")
                            .add(new HashMap<>()) // Adds an empty document to initialize the collection
                            .addOnSuccessListener(documentReference -> Log.d("AddEventActivity", "Waiting list created successfully"))
                            .addOnFailureListener(e -> Log.e("AddEventActivity", "Failed to create waiting list", e));

                    Toast.makeText(this, "Event created: " + event.toString(), Toast.LENGTH_SHORT).show();
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("event", event);
                    setResult(1, resultIntent);
                    finish();
                });
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

    /**
     * This method adds the duration to the start time to find the end time and considers if
     * it goes beyond 24 hours.
     * @param startDate the event start date
     * @param startTime the time
     * @param duration the duration of event
     * @return
     */
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

