package com.example.hive;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.hive.Events.Event;
import com.example.hive.Events.EventDetailActivity;

import java.util.ArrayList;

/**
 * Custom adapter to display organizer events in a ListView. Currently identical to
 * <code>AdminEventListAdapter</code> - changes may be made once users can be distinguished.
 *
 * @author Zach
 */
public class OrganizerEventAdapter extends ArrayAdapter<Event> implements Filterable {

    /**
     * Original list of events
     */
    private final ArrayList<Event> og;

    /**
     * Filtered list of events
     */
    private final ArrayList<Event> filtered;

    /**
     * Filter object for searching
     */
    private Filter filter;

    /**
     * The delete item launcher that is defined in the list activity
     */
    private final ActivityResultLauncher<Intent> deleteItemLauncher;

    /**
     * Constructor for the adapter. Calls ArrayAdapter's instructor with given data
     *
     * @param context
     * The context from which this adapter is being created.
     * @param events
     * The list of events to display.
     * @param deleteItemLauncher
     * The launcher for the event view activity, as defined in list activity
     */
    public OrganizerEventAdapter(Context context,
                                 ArrayList<Event> events,
                                 ActivityResultLauncher<Intent> deleteItemLauncher) {
        super(context, 0, events);
        this.og = new ArrayList<Event>(events);
        this.filtered = new ArrayList<Event>(events);
        this.deleteItemLauncher = deleteItemLauncher;
    }

    /**
     * Updates the two arrays needed for searching. Should be called before
     * <code>notifyDataSetChanged</code> whenever it is used
     *
     * @param newEvents
     * The array of event objects to update this adapter's arrays with
     */
    public void updateData(ArrayList<Event> newEvents) {
        og.clear();
        og.addAll(newEvents);
        filtered.clear();
        filtered.addAll(newEvents);
    }

    /**
     * Return the size of the filtered list
     *
     * @return
     * int, size of the filtered list
     */
    @Override
    public int getCount() {
        return filtered.size();
    }

    /**
     * Get item of filtered list at specified position
     *
     * @param position Position of the item whose data we want within the adapter's
     * data set.
     * @return
     * The Event object at provided position
     */
    @Nullable
    @Override
    public Event getItem(int position) {
        return filtered.get(position);
    }

    /**
     * @param position The position of the item within the adapter's data set whose row id we want.
     * @return
     * The requested item's row id
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * Gets the view for the list items.
     *
     * @param position The position of the item within the adapter's data set of the item whose view
     *        we want.
     * @param convertView The old view to reuse, if possible. Note: You should check that this view
     *        is non-null and of an appropriate type before using. If it is not possible to convert
     *        this view to display the correct data, this method can create a new view.
     *        Heterogeneous lists can specify their number of view types, so that this View is
     *        always of the right type (see {@link #getViewTypeCount()} and
     *        {@link #getItemViewType(int)}).
     * @param parent The parent that this view will eventually be attached to
     * @return
     * The view that displays a list item.
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;

        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.event_list_item,
                    parent, false);
        } else {
            view = convertView;
        }

        Event event = getItem(position);

        TextView eventTitle = view.findViewById(R.id.event_title);
        TextView eventDate = view.findViewById(R.id.event_date);
        TextView eventTime = view.findViewById(R.id.event_time);
        TextView eventCost = view.findViewById(R.id.event_cost);

        if (event == null) {
            Log.e("EventListAdapter getView",
                    "Event object was null. Return default view");
            return view;
        }

        eventTitle.setText(event.getTitle());
        eventDate.setText(event.getStartDate());
        eventTime.setText(event.getStartTime());
        eventCost.setText(String.format("$%s", event.getCost()));

        Button detailsBtn = view.findViewById(R.id.event_details_button);

        detailsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), EventDetailActivity.class);
                i.putExtra("event", event);
                i.putExtra("position", String.valueOf(position));
                deleteItemLauncher.launch(i);
            }
        });

        return view;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new OrganizerEventAdapter.EventFilter();
        }
        return filter;
    }

    /**
     * Custom Filter class to handle searching of events
     */
    private class EventFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();
            ArrayList<Event> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                // No search query, return the original list
                filteredList.addAll(og);
            } else {
                String query = constraint.toString().toLowerCase().trim();
                for (Event event : og) {
                    if (event.getTitle().toLowerCase().contains(query)) {
                        filteredList.add(event);
                    }
                }
            }

            filterResults.values = filteredList;
            filterResults.count = filteredList.size();
            return filterResults;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            // Update filtered data and refresh the list
            filtered.clear();
            filtered.addAll((ArrayList<Event>) results.values);
            notifyDataSetChanged();
        }
    }

}
