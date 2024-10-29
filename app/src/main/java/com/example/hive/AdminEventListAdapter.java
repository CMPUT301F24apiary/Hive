package com.example.hive;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

/**
 * Custom adapter for displaying Events
 *
 * @author Zach
 */
public class AdminEventListAdapter extends ArrayAdapter<TestEvent> implements Filterable {

    /**
     * Original list of events
     */
    private ArrayList<TestEvent> og;
    /**
     * Filtered list of events
     */
    private ArrayList<TestEvent> filtered;
    /**
     * Filter
     */
    private Filter filter;

    /**
     * Constructor for the adapter. Calls ArrayAdapter's instructor with given data
     *
     * @param context
     * The context from which this adapter is being created.
     * @param events
     * The list of events to display.
     */
    public AdminEventListAdapter(Context context, ArrayList<TestEvent> events) {
        super(context, 0, events);
        this.og = new ArrayList<TestEvent>(events);
        this.filtered = new ArrayList<TestEvent>(events);
    }

    public void updateData(ArrayList<TestEvent> newEvents) {
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
     * The TestEvent object at provided position
     */
    @Nullable
    @Override
    public TestEvent getItem(int position) {
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
        Log.d("Adapter", "getView called for position: " + position + " with title " + getItem(position).getTitle());
        View view;

        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.admin_event_list_item,
                    parent, false);
        } else {
            view = convertView;
        }

        TestEvent event = getItem(position);

        TextView eventTitle = view.findViewById(R.id.event_title);
        TextView eventDate = view.findViewById(R.id.event_date);
        TextView eventTime = view.findViewById(R.id.event_time);
        TextView eventCost = view.findViewById(R.id.event_cost);

        eventTitle.setText(event.getTitle());
        eventDate.setText(event.getDate());
        eventTime.setText(event.getTime());
        eventCost.setText(String.format("$%s", event.getCost()));

        return view;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new EventFilter();
        }
        return filter;
    }

    private class EventFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();
            ArrayList<TestEvent> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                // No search query, return the original list
                filteredList.addAll(og);
            } else {
                String query = constraint.toString().toLowerCase().trim();
                for (TestEvent event : og) {
                    if (event.getTitle().toLowerCase().contains(query)) {
                        filteredList.add(event);
                    }
                }
            }

            filterResults.values = filteredList;
            filterResults.count = filteredList.size();
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            // Update filtered data and refresh the list
            filtered.clear();
            filtered.addAll((ArrayList<TestEvent>) results.values);
            notifyDataSetChanged();
        }
    }

}
