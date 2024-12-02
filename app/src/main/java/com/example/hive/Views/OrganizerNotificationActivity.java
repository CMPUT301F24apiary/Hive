package com.example.hive.Views;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.hive.Controllers.EventController;
import com.example.hive.Controllers.ListController;
import com.example.hive.R;

/**
 * Activity for sending notifications to different groups of event participants.
 * Allows organizers to send customized notifications to selected, cancelled,
 * and waiting list participants.
 *
 * @author Zach
 */
public class OrganizerNotificationActivity extends AppCompatActivity {

    /** EditText for notification message */
    private EditText message;
    /** Controller for managing lists */
    private ListController listController;
    /** Controller for managing events */
    private EventController eventController;
    /** Flags to track which groups to send notifications to */
    private boolean sendToSelected, sendToCancelled, sendToWaiting;

    /**
     * Initializes the activity, sets up UI components and event handlers.
     * Configures switches for selecting recipient groups and handles sending
     * notifications with confirmation dialogs.
     *
     * @param savedInstanceState Bundle: If the activity is being re-initialized after previously
     *                          being shut down, this contains the data it most recently supplied
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_organizer_notification);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        listController = new ListController();
        eventController = new EventController();

        SwitchCompat selected = findViewById(R.id.selected_entrants_switch);

        selected.setOnCheckedChangeListener((buttonView, isChecked) -> sendToSelected = isChecked);

        SwitchCompat cancelled = findViewById(R.id.cancelled_entrants_switch);

        cancelled.setOnCheckedChangeListener((buttonView, isChecked) -> sendToCancelled = isChecked);

        SwitchCompat waiting = findViewById(R.id.waiting_list_switch);

        waiting.setOnCheckedChangeListener((buttonView, isChecked) -> sendToWaiting = isChecked);

        message = findViewById(R.id.message_edit_text);

        Button sendNotif = findViewById(R.id.send_notif_button);

        ImageButton back = findViewById(R.id.back_arrow);

        back.setOnClickListener(v -> finish());

        // get event id from prev activity
        String eventID = getIntent().getStringExtra("eventID");
        Log.d("OrganizerNotif", eventID == null ? "null" : eventID);
        if (eventID == null) {
            finish();
            return;
        }

        // Skeleton - add logic to send notifications here
        sendNotif.setOnClickListener(v -> {
            String notificationMessage = message.getText().toString().trim();

            if (notificationMessage.isEmpty()) {
                Toast.makeText(OrganizerNotificationActivity.this,
                        "You must enter a message.", Toast.LENGTH_LONG).show();
                return;
            }

            if (!sendToCancelled && !sendToSelected && !sendToWaiting) {
                Toast.makeText(OrganizerNotificationActivity.this,
                        "You must select at least one group to notify.",
                        Toast.LENGTH_LONG).show();
                return;
            }

            if (sendToSelected) {
                new AlertDialog.Builder(this)
                        .setTitle("Confirm Notification")
                        .setMessage("Are you sure you want to notify all entrants on the selected list?")
                        .setPositiveButton("Yes", (dialog, which) -> eventController.getInvitedList(eventID, successAndList -> {
                            if (successAndList.first) {
                                for (String uid : successAndList.second) {
                                    listController.addNotification(uid, eventID, "selected", notificationMessage);
                                }
                                Toast.makeText(this,
                                        "Successfully sent notification to selected users", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(this,
                                        "No invited list for this event", Toast.LENGTH_SHORT).show();
                            }
                        }))
                        .setNegativeButton("Cancel", null)
                        .show();
            }


            if (sendToCancelled) {
                new AlertDialog.Builder(this)
                        .setTitle("Confirm Notification")
                        .setMessage("Are you sure you want to notify all entrants on the cancelled list?")
                        .setPositiveButton("Yes", (dialog, which) -> listController.fetchCancelledList(eventID, uids -> {
                            if (uids != null) {
                                for (String uid : uids) {
                                    listController.addNotification(uid, eventID, "cancelled", notificationMessage);
                                }
                                Toast.makeText(this,
                                        "Successfully sent notification to cancelled users", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(this,
                                        "No cancelled list for this event", Toast.LENGTH_SHORT).show();
                            }
                        }))
                        .setNegativeButton("Cancel", null)
                        .show();
            }


            if (sendToWaiting) {
                // adding confirmation dialog
                new AlertDialog.Builder(this)
                        .setTitle("Confirm Notification")
                        .setMessage("Are you sure you want to notify all entrants on the waiting list?")
                        .setPositiveButton("Yes", (dialog, which) -> listController.getWaitingListUIDs(eventID, uids -> {
                            if (uids != null) {
                                for (String uid : uids) {
                                    listController.addNotification(uid, eventID, "waiting", notificationMessage);
                                }
                                Toast.makeText(this,
                                        "Successfully sent notification to waiting users", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(this,
                                        "No waiting list for this event", Toast.LENGTH_SHORT).show();
                            }
                        }))
                        .setNegativeButton("Cancel", null)
                        .show();
            }
        });
    }
}