package com.example.hive.Events;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.hive.AdminEvent.ConfirmEventDelete;
import com.example.hive.AdminEvent.DeleteEventListener;
import com.example.hive.Controllers.EventController;
import com.example.hive.R;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.net.URI;

/**
 * Display event information and delete button for admin.
 *
 * TODO Display different options for different roles
 *
 * @author Zach
 */
public class EventDetailActivity extends AppCompatActivity implements DeleteEventListener {

    /**
     * Controller that communicates with firebase
     */
    private EventController controller;

    /**
     * Firebase ID of the event displayed in this activity
     */
    private String id;

    /**
     * Position of this activity in the array in <code>AdminEventListActivity</code>
     */
    private String position;

    /**
     * The event object that is displayed in this activity
     */
    private Event event;

    /**
     * Calls on the controller to delete this event from firebase
     */
    public void deleteEvent() {
        controller.deleteSingleEventFromDB(event.getFirebaseID(), this::onDelete);
    }

    /**
     * The callback function for after the event is deleted.
     * <br/><br/>
     * If the event was deleted successfully, this function puts the proper information in the
     * result intent, so it can be read and handled properly in <code>AdminEventListActivity</code>,
     * and then finishes the activity.
     * <br/><br/>
     * If the event was not deleted, then this logs that to error output, displays a toast to the
     * user, and keeps this activity alive.
     *
     * @param isDeleted True if the event was deleted, false otherwise. Passed by the controller's
     *                  function to delete the event.
     */
    public void onDelete(Boolean isDeleted) {
        if (isDeleted) {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("id", id);
            resultIntent.putExtra("position", position);
            setResult(1, resultIntent);
            finish();
        } else {
            Log.e("DeleteEvent", "Event not deleted");
            Toast.makeText(this, "Event could not be deleted.", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_event_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Get reference to deleteEvent button
        Button deleteEvent = findViewById(R.id.delete_event_button);

        // Get reference to delete QR button
        Button deleteQR = findViewById(R.id.delete_qr_button);

        // Get reference to back button
        ImageButton backBtn = findViewById(R.id.event_back_button);

        // Get reference to event poster image view
        ImageView eventPosterView = findViewById(R.id.event_poster);

        // Get references to TextViews
        TextView eventTitleView = findViewById(R.id.event_detail_title);
        TextView eventDateTimesView = findViewById(R.id.event_detail_date_time);
        TextView eventLocationView = findViewById(R.id.event_detail_location);
        TextView eventCostView = findViewById(R.id.event_detail_cost);
        TextView eventDescriptionView = findViewById(R.id.event_detail_description);
        TextView eventNumParticipantsView = findViewById(R.id.event_detail_number_participants);

        // Create new instance of EventController to communicate with firebase
        controller = new EventController();

        // Get event object from the intent
        event = (Event) getIntent().getParcelableExtra("event");

        // Get firebase ID of event - if event is null, then there was an issue getting the event
        // from intent, so report to error log and finish activity
        if (event != null) {
            id = event.getFirebaseID();
            String title = event.getTitle();
            eventTitleView.setText(title);
            String cost = event.getCost();
            eventCostView.setText(String.format("$%s", cost));
            String startDate = event.getStartDate();
            String startTime = event.getStartTime();
            String endDate = event.getEndDate();
            String endTime = event.getEndTime();
            long startInMS = event.getStartDateInMS();
            long endInMS = event.getEndDateInMS();
            boolean useEndDate = (startInMS / 86400000) == (endInMS / 86400000);
            String finalDatesAndTimes;
            if (useEndDate) {
                finalDatesAndTimes = String.format("%s, %s - %s, %s", startDate, startTime, endDate,
                        endTime);
            } else {
                finalDatesAndTimes = String.format("%s, %s - %s", startDate, startTime, endTime);
            }
            eventDateTimesView.setText(finalDatesAndTimes);
            String location = event.getLocation();
            eventLocationView.setText(location);
            String description = event.getDescription();
            eventDescriptionView.setText(description);
            String posterURL = event.getPosterURL();
            if (posterURL != null) {
                Glide.with(this).load(posterURL).into(eventPosterView);
            } else {
                eventPosterView.setVisibility(View.GONE);
            }
        } else {
            Log.e("Event Detail Activity", "Error getting event from intent");
            finish();
        }

        // Get the position of the event in the list activity's array from the intent
        position = getIntent().getStringExtra("position");

        // Logic for when back button is clicked - go back to previous activity
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Logic for when delete button is clicked - show confirmation fragment
        deleteEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ConfirmEventDelete().show(getSupportFragmentManager(), "Confirm Delete");
            }
        });

    }
}