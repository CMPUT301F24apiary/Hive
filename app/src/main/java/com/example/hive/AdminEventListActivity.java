package com.example.hive;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.SearchView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.Comparator;

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
     * Lays out search view and list view
     */
    private LinearLayout eventLinearContainer;
    /**
     * This activity's ListView that displays the events in list form
     */
    private ListView eventList;
    /**
     * This activity's SearchView that displays the events in list form
     */
    private SearchView eventSearchView;
    /**
     * This activity's adapter
     */
    private AdminEventListAdapter eventAdapter;
    /**
     * Sort
     */
    private TextView sortByDate;
    private TextView sortByDateIcon;
    private boolean sortByDateAsc;
    private TextView sortByTitle;
    private TextView sortByTitleIcon;
    private boolean sortByTitleAsc;
    private TextView sortByCost;
    private TextView sortByCostIcon;
    private boolean sortByCostAsc;
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
        Log.d("AdminEventListActivity", "updateList called with " + data.size() + " items");
        TextView loading = findViewById(R.id.event_list_loading_text);
        loading.setVisibility(View.GONE);
        eventLinearContainer.setVisibility(View.VISIBLE);
        eventDataList.clear();
        eventDataList.addAll(data);
        Log.d("AdminEventListActivity", "eventDataList size after update: " + eventDataList.size());
        eventAdapter.updateData(eventDataList);
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
        eventLinearContainer = findViewById(R.id.admin_event_list_linear_layout);
        eventList = findViewById(R.id.admin_event_list_view);
        eventSearchView = findViewById(R.id.admin_event_list_search_view);
        eventList.setAdapter(eventAdapter);

        model = new AdminEventListModel();
        view = new AdminEventListView(model, this);
        model.addView(view);

        sortByDate = findViewById(R.id.date_sort);
        sortByDateIcon = findViewById(R.id.date_sort_icon);
        sortByCost = findViewById(R.id.cost_sort);
        sortByCostIcon = findViewById(R.id.cost_sort_icon);
        sortByTitle = findViewById(R.id.title_sort);
        sortByTitleIcon = findViewById(R.id.title_sort_icon);

        sortByDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventDataList.sort(new Comparator<TestEvent>() {
                    @Override
                    public int compare(TestEvent o1, TestEvent o2) {
                        long dateDiff = o1.getDateInMS() - o2.getDateInMS();
                        int res = 0;
                        if (dateDiff < 0) {
                            res = -1;
                        } else if (dateDiff > 0) {
                            res = 1;
                        }
                        if (sortByDateAsc) {
                            sortByDateAsc = false;
                            sortByDateIcon.setText("⌃");
                        } else {
                            sortByDateAsc = true;
                            sortByDateIcon.setText("⌄");
                            res = -res;
                        }
                        return res;
                    }
                });
                eventAdapter.updateData(eventDataList);
                eventAdapter.notifyDataSetChanged();
            }
        });

        sortByTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventDataList.sort(new Comparator<TestEvent>() {
                    @Override
                    public int compare(TestEvent o1, TestEvent o2) {
                        int res = o1.getTitle().compareTo(o2.getTitle());
                        if (sortByTitleAsc) {
                            sortByTitleAsc = false;
                            sortByTitleIcon.setText("⌃");
                        } else {
                            sortByTitleAsc = true;
                            sortByTitleIcon.setText("⌄");
                            res = -res;
                        }
                        return res;
                    }
                });
                eventAdapter.updateData(eventDataList);
                eventAdapter.notifyDataSetChanged();
            }
        });

        sortByCost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventDataList.sort(new Comparator<TestEvent>() {
                    @Override
                    public int compare(TestEvent o1, TestEvent o2) {
                        float cost1 = Float.parseFloat(o1.getCost());
                        float cost2 = Float.parseFloat(o2.getCost());
                        float diff = cost1 - cost2;
                        int res = 0;
                        if (diff < 0) {
                            res = -1;
                        } else if (diff > 0) {
                            res = 1;
                        }
                        if (sortByCostAsc) {
                            sortByCostAsc = false;
                            sortByCostIcon.setText("⌃");
                        } else {
                            sortByCostAsc = true;
                            sortByCostIcon.setText("⌄");
                            res = -res;
                        }
                        return res;
                    }
                });
                eventAdapter.updateData(eventDataList);
                eventAdapter.notifyDataSetChanged();
            }
        });

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