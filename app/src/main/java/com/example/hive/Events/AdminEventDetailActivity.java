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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.hive.Controllers.EventController;
import com.example.hive.Models.Event;
import com.example.hive.R;
import com.google.firebase.firestore.FirebaseFirestore;

public class AdminEventDetailActivity extends AppCompatActivity
implements DeleteQRCodeListener, DeleteEventListener {

    private static final String TAG = "AdminEventDetailActivity";
    private EventController controller;
    private Event event;
    private String eventId;
    private String position; // Position of this activity in the array in AdminEventListActivity

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_event_detail);

        ImageView eventPoster = findViewById(R.id.event_poster);
        ImageButton backButton = findViewById(R.id.event_back_button);
        TextView eventTitleView = findViewById(R.id.event_detail_title);
        TextView eventDateTimesView = findViewById(R.id.event_detail_date_time);
        TextView eventLocationView = findViewById(R.id.event_detail_location);
        TextView eventCostView = findViewById(R.id.event_detail_cost);
        TextView eventDescription = findViewById(R.id.event_detail_description);
        Button delQRCodeButton = findViewById(R.id.delete_qr_button);
        Button delEventButton = findViewById(R.id.delete_event_button);

        controller = new EventController();  // used to communicate with Firebase controller
        event = getIntent().getParcelableExtra("event");
        if (event != null) {
            eventId = event.getFirebaseID();
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
            eventDescription.setText(description);
            String posterURL = event.getPosterURL();
            if (posterURL != null) {
                Glide.with(this).load(posterURL).into(eventPoster);
            } else {
                eventPoster.setVisibility(View.GONE);
            }
        } else {
            Log.e("Event Detail Activity", "Error getting event from intent");
            finish();
        }

        position = getIntent().getStringExtra("position");

        // Logic for when delete button is clicked - show confirmation fragment
        delEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ConfirmEventDelete().show(getSupportFragmentManager(), "Confirm Delete");
            }
        });
        position = getIntent().getStringExtra("position");

        // Logic for when delete button is clicked - show confirmation fragment
        delQRCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ConfirmQRCodeDelete().show(getSupportFragmentManager(), "Confirm Delete");
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

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
            resultIntent.putExtra("id", eventId);
            resultIntent.putExtra("position", position);
            setResult(1, resultIntent);
            finish();
        } else {
            Log.e("DeleteEvent", "Event not deleted");
            Toast.makeText(this, "Event could not be deleted.", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    public void deleteQRCode() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("events").document(event.getFirebaseID())
                .update("qrCode", "")
                .addOnSuccessListener(v -> {
                    Toast.makeText(this, "QR Code deleted", Toast.LENGTH_LONG).show();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "FAILED QR Code Delete");
                });
    }
}
