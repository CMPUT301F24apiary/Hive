package com.example.hive.Views;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.hive.Controllers.EventController;
import com.example.hive.Controllers.ListController;
import com.example.hive.Models.User;
import com.example.hive.R;

import java.util.ArrayList;

/**
 * Activity for displaying and managing invited entrants for an event.
 * Handles lottery draws and displays the list of invited users.
 *
 * @author Zach 
 */
public class InvitedEntrantsActivity extends AppCompatActivity {

    private ArrayList<String> invitedUIDs;
    private InvitedEntrantsAdapter adapter;
    private ListView invitedUsersList;
    private ArrayList<User> users;
    private TextView message;
    private Button drawLotteryButton;
    private EventController eventControl;
    private String eventID;

    /**
     * Updates the UI with a new list of users.
     * Hides the message, shows the search container and list view,
     * and updates the adapter with new user data.
     *
     * @param newUsers ArrayList<User>: The new list of users to display
     */
    public void update(ArrayList<User> newUsers) {
        message.setVisibility(View.GONE);
        findViewById(R.id.search_container).setVisibility(View.VISIBLE);
        invitedUsersList.setVisibility(View.VISIBLE);
        drawLotteryButton.setVisibility(View.GONE);
        users.clear();
        users.addAll(newUsers);
        adapter.updateData(users);
        adapter.notifyDataSetChanged();
    }

    /**
     * Initializes the activity, sets up UI components and event handlers.
     * Handles lottery draw functionality based on event selection date.
     *
     * @param savedInstanceState Bundle: If the activity is being re-initialized after previously
     *                          being shut down, this contains the data it most recently supplied
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_invited_entrants);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        eventID = getIntent().getStringExtra("eventId");
        if (eventID == null) {
            finish();
            return;
        }

        invitedUsersList = findViewById(R.id.invited_list_view);
        users = new ArrayList<>();
        adapter = new InvitedEntrantsAdapter(
                InvitedEntrantsActivity.this, users);
        invitedUsersList.setAdapter(adapter);

        ImageButton back = findViewById(R.id.back_button);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        message = findViewById(R.id.lottery_draw_message);
        drawLotteryButton = findViewById(R.id.draw_lottery_button);

        eventControl = new EventController();

        eventControl.getSingleEvent(eventID, event -> {
            long selectionDate = event.getSelectionDateInMS();
            long currentDate = System.currentTimeMillis();
            if (currentDate < selectionDate) {
                message.setText("Lottery can not be drawn until " + event.getSelectionDate());
            } else {
                if (event.isLotteryDrawn()) {
                    getInvitedUsers();
                } else {
                    message.setText("Lottery can be drawn!");
                    drawLotteryButton.setVisibility(View.VISIBLE);
                    drawLotteryButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.d("InvitedEntrantsActivity", "Lottery draw button clicked.");

                            // Create an instance of ListController
                            ListController listController = new ListController();

                            // Run the lottery with EventController
                            listController.runLottery(InvitedEntrantsActivity.this, eventID,
                                    event.getNumParticipants(), InvitedEntrantsActivity.this::update);
                        }
                    });
                }
            }
        });

        // Logic to search for a user
        SearchView search = findViewById(R.id.search_bar);
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });
    }

    /**
     * Retrieves and processes the list of invited users for the event.
     * If an invited list exists, displays it. Otherwise, generates a new list
     * from the waiting list based on the number of participants.
     */
    private void getInvitedUsers() {
        ListController inviteControl = new ListController();

        eventControl.getInvitedList(eventID, successAndList -> {
            if (successAndList.first) {
                invitedUIDs = successAndList.second;
                inviteControl.createInvitedUserList(invitedUIDs,
                        InvitedEntrantsActivity.this::update);
            } else {
                inviteControl.getWaitingListUIDs(eventID, entrants -> {
                    eventControl.getField(eventID, "numParticipants",
                            res -> {
                                Long numParticipants = (Long) res;
                                invitedUIDs = inviteControl.generateInvitedList(eventID, entrants,
                                        numParticipants.intValue());
                                eventControl.addInvitedList(eventID, invitedUIDs);
                                inviteControl.createInvitedUserList(invitedUIDs,
                                        InvitedEntrantsActivity.this::update);
                            });
                });
            }
        });
    }
}