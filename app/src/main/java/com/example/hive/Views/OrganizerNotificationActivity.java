package com.example.hive.Views;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
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

public class OrganizerNotificationActivity extends AppCompatActivity {

    private SwitchCompat selected, cancelled, waiting;
    private EditText message;
    private Button sendNotif;
    private ImageButton back;
    private ListController listController;
    private EventController eventController;
    private boolean sendToSelected, sendToCancelled, sendToWaiting;

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

        selected = findViewById(R.id.selected_entrants_switch);

        selected.setOnCheckedChangeListener((buttonView, isChecked) -> sendToSelected = isChecked);

        cancelled = findViewById(R.id.cancelled_entrants_switch);

        cancelled.setOnCheckedChangeListener((buttonView, isChecked) -> sendToCancelled = isChecked);

        waiting = findViewById(R.id.waiting_list_switch);

        waiting.setOnCheckedChangeListener((buttonView, isChecked) -> sendToWaiting = isChecked);

        message = findViewById(R.id.message_edit_text);
        sendNotif = findViewById(R.id.send_notif_button);
        back = findViewById(R.id.back_arrow);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        String eventID = getIntent().getStringExtra("eventID");
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
                eventController.getInvitedList(eventID, successAndList -> {
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
                });
            }

            if (sendToCancelled) {
                listController.fetchCancelledList(eventID, uids -> {
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
                });
            }

            if (sendToWaiting) {
                listController.getWaitingListUIDs(eventID, uids -> {
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
                });
            }

        });

    }
}