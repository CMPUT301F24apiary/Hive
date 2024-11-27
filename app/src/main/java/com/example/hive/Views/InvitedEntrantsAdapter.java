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

public class InvitedEntrantsAdapter extends ArrayAdapter<User>
        implements Filterable {

    private ArrayList<User> ogList;
    private ArrayList<User> filtered;
    private Filter filter;

    public InvitedEntrantsAdapter(Context context,
                                  ArrayList<User> users) {
        super(context, 0, users);
        this.ogList = new ArrayList<User>(users);
        this.filtered = new ArrayList<User>(users);
    }

    /**
     * Updates the two arrays needed for searching. Should be called before
     * <code>notifyDataSetChanged</code> whenever it is used
     *
     * @param newUsers
     * The array of pairs to update this adapter's arrays with
     */
    public void updateData(ArrayList<User> newUsers) {
        ogList.clear();
        ogList.addAll(newUsers);
        filtered.clear();
        filtered.addAll(newUsers);
    }/**
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
     * The pair object at provided position
     */
    @Nullable
    @Override
    public User getItem(int position) {
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

    @NonNull
    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new InvitedEntrantsAdapter.InvitedUserFilter();
        }
        return filter;
    }

    /**
     * Custom Filter class to handle searching of events
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
