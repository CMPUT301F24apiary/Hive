package com.example.hive;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

/**
 * Activity to display the list of all events.
 *
 * @author Zach
 */
public class AdminEventListActivity extends AppCompatActivity {

    /**
     * This activity's view
     */
    private AdminEventListView view;
    /**
     * This activity's model
     */
    private AdminEventListModel model;
    /**
     * This activity's ListView that displays the events in list form
     */
    private ListView eventList;
    /**
     * This activity's adapter
     */
    private AdminEventListAdapter eventAdapter;
    /**
     * This activity's data list - holds all events
     */
    private ArrayList<TestEvent> eventDataList;

    /**
     * Updates the ListView by removing loading screen, clearing current list, adding the new items
     * and notifying the adapter.
     *
     * @param data
     * The list of events to display
     */
    public void updateList(ArrayList<TestEvent> data) {
        TextView loading = findViewById(R.id.event_list_loading_text);
        loading.setVisibility(View.GONE);
        eventList.setVisibility(View.VISIBLE);
        eventDataList.clear();
        eventDataList.addAll(data);
        eventAdapter.notifyDataSetChanged();
    }

    /**
     * onCreate method for EventList activity.
     *
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_event_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        eventDataList = new ArrayList<TestEvent>();
        eventAdapter = new AdminEventListAdapter(this, eventDataList);
        eventList = findViewById(R.id.event_list_view);
        eventList.setAdapter(eventAdapter);

        model = new AdminEventListModel();
        view = new AdminEventListView(model, this);
        model.addView(view);

    }
}