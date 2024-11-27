package com.example.hive.Views;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.example.hive.Controllers.InvitedController;
import com.example.hive.Models.User;
import com.example.hive.R;

import java.util.ArrayList;

public class InvitedEntrantsActivity extends AppCompatActivity {

    private ArrayList<String> invitedUIDs;
    private InvitedEntrantsAdapter adapter;
    private ListView invitedUsersList;
    private ArrayList<User> users;
    private TextView message;
    private Button drawLotteryButton;
    private EventController eventControl;
    private String eventID, title;

    public void update(ArrayList<User> newUsers) {
        message.setVisibility(View.GONE);
        findViewById(R.id.search_container).setVisibility(View.VISIBLE);
        invitedUsersList.setVisibility(View.VISIBLE);
        users.clear();
        users.addAll(newUsers);
        adapter.updateData(users);
        adapter.notifyDataSetChanged();
    }

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
        title = getIntent().getStringExtra("eventTitle");
        if (eventID == null || title == null) {
            finish();
            return;
        }

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
                            getInvitedUsers();
                            drawLotteryButton.setVisibility(View.GONE);
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

    private void getInvitedUsers() {
        users = new ArrayList<>();

        invitedUsersList = findViewById(R.id.invited_list_view);
        adapter = new InvitedEntrantsAdapter(
                InvitedEntrantsActivity.this, users);
        invitedUsersList.setAdapter(adapter);

        InvitedController inviteControl = new InvitedController();

        eventControl.getInvitedList(eventID, successAndList -> {
            if (successAndList.first) {
                invitedUIDs = successAndList.second;
                inviteControl.createInvitedUserList(InvitedEntrantsActivity.this,
                        invitedUIDs, title, InvitedEntrantsActivity.this::update);
            } else {
                inviteControl.getWaitingListUIDs(eventID, entrants -> {
                    eventControl.getField(eventID, "numParticipants",
                            res -> {
                                Long numParticipants = (Long) res;
                                invitedUIDs = inviteControl.generateInvitedList(
                                        entrants, numParticipants.intValue());
                                eventControl.addInvitedList(eventID, invitedUIDs);
                                inviteControl.createInvitedUserList(InvitedEntrantsActivity.this,
                                        invitedUIDs, title, InvitedEntrantsActivity.this::update);
                            });
                });
            }
        });

    }
}