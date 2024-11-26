package com.example.hive.Views;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;

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

    public void update(ArrayList<User> newUsers) {
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

        String eventID = getIntent().getStringExtra("eventId");
        if (eventID == null) {
            finish();
            return;
        }

        users = new ArrayList<>();

        invitedUsersList = findViewById(R.id.invited_list_view);
        adapter = new InvitedEntrantsAdapter(this, users);
        invitedUsersList.setAdapter(adapter);

        InvitedController inviteControl = new InvitedController();
        EventController eventControl = new EventController();

        eventControl.getInvitedList(eventID, successAndList -> {
            if (successAndList.first) {
                invitedUIDs = successAndList.second;
                inviteControl.createInvitedUserList(invitedUIDs, this::update);
            } else {
                inviteControl.getWaitingListUIDs(eventID, entrants -> {
                    eventControl.getField(eventID, "numParticipants", res -> {
                        Long numParticipants = (Long) res;
                        invitedUIDs = inviteControl.generateInvitedList(entrants,
                                numParticipants.intValue());
                        eventControl.addInvitedList(eventID, invitedUIDs);
                        inviteControl.createInvitedUserList(invitedUIDs, this::update);
                    });
                });
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
}