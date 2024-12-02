package com.example.hive.Views;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.hive.AdminEvent.AdminEventListAdapter;
import com.example.hive.Events.Event;
import com.example.hive.Events.EventDetailActivity;
import com.example.hive.Models.User;
import com.example.hive.ProfileActivity;
import com.example.hive.R;

import java.util.ArrayList;

/**
 * Adapter for displaying and filtering invited users in a ListView.
 * Implements Filterable to enable searching through the list of users.
 *
 * @author Zach
 */
public class InvitedEntrantsAdapter extends ArrayAdapter<User>
        implements Filterable {

    private ArrayList<User> ogList;    // Original unfiltered list
    private ArrayList<User> filtered;  // Current filtered list
    private Filter filter;

    /**
     * Constructor for InvitedEntrantsAdapter.
     *
     * @param context The current context
     * @param users ArrayList of users to display in the list
     */
    public InvitedEntrantsAdapter(Context context,
                                  ArrayList<User> users) {
        super(context, 0, users);
        this.ogList = new ArrayList<User>(users);
        this.filtered = new ArrayList<User>(users);
    }

    /**
     * Updates the two arrays needed for searching. Should be called before
     * <code>notifyDataSetChanged</code> whenever it is used.
     *
     * @param newUsers The array of users to update this adapter's arrays with
     */
    public void updateData(ArrayList<User> newUsers) {
        ogList.clear();
        ogList.addAll(newUsers);
        filtered.clear();
        filtered.addAll(newUsers);
    }

    /**
     * Return the size of the filtered list.
     *
     * @return int: Size of the filtered list
     */
    @Override
    public int getCount() {
        return filtered.size();
    }

    /**
     * Get item of filtered list at specified position.
     *
     * @param position Position of the item whose data we want within the adapter's data set
     * @return The User object at provided position
     */
    @Nullable
    @Override
    public User getItem(int position) {
        return filtered.get(position);
    }

    /**
     * Get the row id associated with the specified position in the list.
     *
     * @param position The position of the item within the adapter's data set whose row id we want
     * @return The id of the item at the specified position
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * Gets the view for the list items.
     *
     * @param position The position of the item within the adapter's data set of the item whose view we want
     * @param convertView The old view to reuse, if possible
     * @param parent The parent that this view will eventually be attached to
     * @return The view that displays a list item
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;

        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.invited_entrant_content,
                    parent, false);
        } else {
            view = convertView;
        }

        User user = getItem(position);

        ImageView pfp = view.findViewById(R.id.invited_user_image);
        TextView username = view.findViewById(R.id.invited_user_name);

        if (user == null) {
            Log.e("InvitedEntrantsAdapter getView",
                    "Pair object was null. Return default view");
            return view;
        }

        String pfpURL = user.getProfileImageUrl();
        username.setText(user.getUserName());

        if (!pfpURL.isEmpty()) {
            Glide.with(view).load(pfpURL).circleCrop().into(pfp);
        } else {
            pfp.setImageDrawable(user.getDisplayDrawable());
        }

        return view;
    }

    /**
     * Returns the filter used for searching users.
     *
     * @return Filter instance for this adapter
     */
    @NonNull
    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new InvitedEntrantsAdapter.InvitedUserFilter();
        }
        return filter;
    }

    /**
     * Custom Filter class to handle searching of users by username.
     * Filters the list based on the search query, matching usernames
     * that contain the search text (case-insensitive).
     */
    private class InvitedUserFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();
            ArrayList<User> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                // No search query, return the original list
                filteredList.addAll(ogList);
            } else {
                String query = constraint.toString().toLowerCase().trim();
                for (User user : ogList) {
                    if (user.getUserName().toLowerCase().contains(query)) {
                        filteredList.add(user);
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
            filtered.addAll((ArrayList<User>) results.values);
            notifyDataSetChanged();
        }
    }
}
