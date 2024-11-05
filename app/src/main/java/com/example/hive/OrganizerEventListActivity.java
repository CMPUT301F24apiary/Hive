package com.example.hive;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hive.Controllers.EventController;

import java.util.ArrayList;

/**
 * This shows the event list to the organizer
 */
public class OrganizerEventListActivity extends AppCompatActivity {

    private ImageButton facilityprofileButton;
    private Button addEventButton;
    /**
     * Lays out search view and list view
     */
    private LinearLayout eventLinearContainer;
    /**
     * This activity's adapter
     */
    private OrganizerEventAdapter eventAdapter;
    /**
     * Reference to icon indicating sort direction for event date
     */
    private TextView sortByDateIcon;
    /**
     * Boolean value to represent direction in which we are sorting for event date
     */
    private boolean sortByDateAsc;
    /**
     * Reference to icon indicating sort direction for event title
     */
    private TextView sortByTitleIcon;
    /**
     * Boolean value to represent direction in which we are sorting for event title
     */
    private boolean sortByTitleAsc;
    /**
     * Reference to icon indicating sort direction for event cost
     */
    private TextView sortByCostIcon;
    /**
     * Boolean value to represent direction in which we are sorting for event cost
     */
    private boolean sortByCostAsc;
    /**
     * This activity's data list - holds all events
     */
    private ArrayList<Event> eventDataList;

    /**
     * Updates the ListView by removing loading screen, clearing current list, adding the new items
     * and notifying the adapter.
     *
     * @param data
     * The list of events to display
     */
    public void updateList(ArrayList<Event> data) {
        // Get reference to TextView that displays loading text and hide it
        TextView loading = findViewById(R.id.event_list_loading_text);
        loading.setVisibility(View.GONE);
        // Un-hide the container that displays the list, search, and sort
        eventLinearContainer.setVisibility(View.VISIBLE);
        // Update the array in this activity and call the adapter's update method
        eventDataList.clear();
        eventDataList.addAll(data);
        eventAdapter.updateData(eventDataList);
        // Notify the adapter that the dataset has changed, so it can update the ListView
        eventAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizer_eventlist);

        facilityprofileButton = findViewById(R.id.facilityprofileButton);
        addEventButton= findViewById(R.id.addEventButton);


        // Create activity result launcher for item addition - adds ability to get result from the
        // detail activity. If the result indicates addition was performed, add the event to arrays
        // and notify the adapter
        ActivityResultLauncher<Intent> addItemLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == 1) {
                        Intent data = result.getData();
                        if (data != null) {
                            Event event = data.getParcelableExtra("event");
                            eventDataList.add(event);
                            eventAdapter.updateData(eventDataList);
                            eventAdapter.notifyDataSetChanged();
                        }
                    }
                }
        );

        // Create activity result launcher for item deletion - adds ability to get result from the
        // detail activity. If the result indicates deletion was performed, remove the event from
        // arrays and notify the adapter
        ActivityResultLauncher<Intent> deleteItemLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == 1) {
                        Intent data = result.getData();
                        if (data != null) {
                            String deletedItemId = data.getStringExtra("item_id");
                            int deletedPos = Integer
                                    .parseInt(data.getStringExtra("position"));
                            eventDataList.remove(deletedPos);
                            eventAdapter.updateData(eventDataList);
                            eventAdapter.notifyDataSetChanged();
                        }
                    }
                }
        );

        // Create new list to hold all events, and provide it to the adapter
        eventDataList = new ArrayList<Event>();
        eventAdapter = new OrganizerEventAdapter(this, eventDataList, deleteItemLauncher);

        // Container that holds the list, search bar, and sort options
        eventLinearContainer = findViewById(R.id.organizer_event_list_linear_layout);
        ListView eventList = findViewById(R.id.organizer_event_list_view);
        SearchView eventSearchView = findViewById(R.id.organizer_event_list_search_view);
        // Set the adapter for the event list view
        eventList.setAdapter(eventAdapter);

        // Get references to all sort texts and arrows
        TextView sortByDate = findViewById(R.id.date_sort);
        sortByDateIcon = findViewById(R.id.date_sort_icon);
        TextView sortByCost = findViewById(R.id.cost_sort);
        sortByCostIcon = findViewById(R.id.cost_sort_icon);
        TextView sortByTitle = findViewById(R.id.title_sort);
        sortByTitleIcon = findViewById(R.id.title_sort_icon);

        // Define controller that communicates with firebase
        EventController controller = new EventController();

        // Use the getOrganizersEventsFromDB method from the controller to get all the organizer's
        // events in the database. Use this activity's updateList method to display all the events
        // in the app
        controller.getOrganizersEventsFromDB(this::updateList);

        facilityprofileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(OrganizerEventListActivity.this, FacilityActivity.class);
                startActivity(intent);
            }
        });
        addEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OrganizerEventListActivity.this, AddEventActivity.class);
                addItemLauncher.launch(intent);
            }
        });
        sortByDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventDataList.sort((o1, o2) -> {
                    int res = Long.compare(o1.getStartDateInMS(), o2.getStartDateInMS());
                    return sortByDateAsc ? res : -res;
                });

                // Toggle the sort direction and update the icon
                sortByDateAsc = !sortByDateAsc;
                sortByDateIcon.setText(sortByDateAsc ? "⌄" : "⌃");

                eventAdapter.updateData(eventDataList);
                eventAdapter.notifyDataSetChanged();
            }
        });

        // Logic to sort list by title
        sortByTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventDataList.sort((o1, o2) -> {
                    int res = o1.getTitle().compareTo(o2.getTitle());
                    return sortByTitleAsc ? res : -res;
                });

                sortByTitleAsc = !sortByTitleAsc;
                sortByTitleIcon.setText(sortByTitleAsc ? "⌄" : "⌃");

                eventAdapter.updateData(eventDataList);
                eventAdapter.notifyDataSetChanged();
            }
        });

        // Logic to sort list by cost
        sortByCost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventDataList.sort((o1, o2) -> {
                    float cost1 = Float.parseFloat(o1.getCost());
                    float cost2 = Float.parseFloat(o2.getCost());
                    int res = Float.compare(cost1, cost2);
                    return sortByCostAsc ? res : -res;
                });

                sortByCostAsc = !sortByCostAsc;
                sortByCostIcon.setText(sortByCostAsc ? "⌄" : "⌃");

                eventAdapter.updateData(eventDataList);
                eventAdapter.notifyDataSetChanged();
            }
        });

        // Logic to search for an event - currently supports only searching event title
        eventSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                eventAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                eventAdapter.getFilter().filter(newText);
                return false;
            }
        });
    }
}


