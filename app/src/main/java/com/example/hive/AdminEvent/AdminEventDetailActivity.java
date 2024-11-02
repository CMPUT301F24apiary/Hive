package com.example.hive.AdminEvent;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentResultListener;

import com.example.hive.Controllers.AdminEventListController;
import com.example.hive.R;
import com.example.hive.TestEvent;

/**
 * Display event information and delete button for admin.
 * NOTE - This is mostly just a dev activity. Once actual event page is created, that will replace
 * this.
 *
 * @author Zach
 */
public class AdminEventDetailActivity extends AppCompatActivity implements DeleteEventListener {

    /**
     * Controller that communicates with firebase
     */
    private AdminEventListController controller;

    /**
     * Button to initiate event deletion
     */
    private Button deleteEvent;

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
    private TestEvent event;

    /**
     * Calls on the controller to delete this event from firebase
     */
    public void deleteEvent() {
        controller.deleteSingleEventFromDB("", this::onDelete);
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
        setContentView(R.layout.activity_admin_event_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        deleteEvent = findViewById(R.id.delete_event_button);
        controller = new AdminEventListController();

        event = (TestEvent) getIntent().getParcelableExtra("event");

        id = event.getFirebaseID();

        position = getIntent().getStringExtra("position");

        getSupportFragmentManager().setFragmentResultListener("confirmation", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                boolean isConfirmed = result.getBoolean("confirmed");
                if (isConfirmed) {
                    controller.deleteSingleEventFromDB(event.getFirebaseID(), (res) -> onDelete(res));
                }
            }
        });

        deleteEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ConfirmEventDelete().show(getSupportFragmentManager(), "Confirm Delete");
            }
        });

    }
}