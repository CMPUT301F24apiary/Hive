package com.example.hive;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

/**
 * Custom adapter for displaying Events
 *
 * @author Zach
 */
public class AdminEventListAdapter extends ArrayAdapter<TestEvent> {

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

}
