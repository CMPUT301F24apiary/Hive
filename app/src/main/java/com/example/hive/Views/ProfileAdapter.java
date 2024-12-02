package com.example.hive.Views;

import static android.content.ContentValues.TAG;

import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.example.hive.Models.User; // Assuming you have a User model class
import com.example.hive.R;

import java.util.ArrayList;
import java.util.List;

/**
 * display each item in users list. when (admin) user clicks on details button, they are led to
 * admin version of the profile page where admin can delete the profile.
 * known bug: crashes if there are no users in the database
 */
public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ProfileViewHolder> {
    private Context context;
    private List<User> userList;
    private List<User> userListOriginal;

    public ProfileAdapter(Context context, List<User> userList) {
        this.context = context;
        this.userList = userList != null ? userList : new ArrayList<>();
        this.userListOriginal = new ArrayList<>(this.userList);
        //this.userListFiltered = new ArrayList<>(this.userList);
    }

    @NonNull
    @Override
    public ProfileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.profile_item, parent, false);
        return new ProfileViewHolder(view);
    }

    /**
     * Launch profile view intent if view details button is clicked
     * @param holder The ViewHolder which should be updated to represent the contents of the
     *        item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull ProfileAdapter.ProfileViewHolder holder, int position) {
        if (position >= userList.size()) {
            return;  // position must be within userList, not out of bounds
        }
        User user = userList.get(position);

        // using Glide for image loading
        if (user.getProfileImageUrl() != null && !user.getProfileImageUrl().isEmpty()) {
            Glide.with(context).load(user.getProfileImageUrl())
                    .error(R.drawable.ic_profile)
                    .circleCrop()
                    .into(holder.imageViewProfile);
        } else {
            holder.imageViewProfile.setImageDrawable(user.getDisplayDrawable());
        }
        //Glide.with(context).load(user.getProfileImageUrl())
              //  .error(R.drawable.ic_profile)  // default image if there is error/no profile pic set
              //  .into(holder.imageViewProfile);

        holder.textViewName.setText(user.getUserName());
        holder.buttonViewDetails.setOnClickListener(v-> {
            Intent intent = new Intent(context, AdminProfileViewActivity.class);
            intent.putExtra("deviceId", user.getDeviceId());
            intent.putExtra("profileImageUrl", user.getProfileImageUrl());
            context.startActivity(intent);
            Log.d(TAG, "Device ID for user " + user.getUserName() + ": " + user.getDeviceId());

        });

    }

    /**
     * Get number of users in userList (should encompass all users in db)
     * @return
     */
    @Override
    public int getItemCount() {
        return userList != null ? userList.size() : 0;
    }

    public static class ProfileViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageViewProfile;
        public TextView textViewName;
        Button buttonViewDetails, deleteProfileButton;

        public ProfileViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewProfile = itemView.findViewById(R.id.imageViewProfileImage);
            textViewName = itemView.findViewById(R.id.textViewProfileItemName);
            buttonViewDetails = itemView.findViewById(R.id.ButtonProfileItemViewDetails);
            deleteProfileButton = itemView.findViewById(R.id.deleteProfileButton);
        }
    }

    /**
     * filter profiles view for admin
     * @param query
     */
    public void filter(String query) {
        userList.clear();
        if (query.isEmpty()) {
            userList.addAll(userListOriginal);
        } else {
            String queryLower = query.toLowerCase();
            for (User user : userListOriginal) {
                if (user.getUserName().toLowerCase().contains(queryLower)) {
                    userList.add(user);
                }
            }
        }
        notifyDataSetChanged();
    }

    /**
     * Refresh user list e.g. when a user is deleted by admin
     * @param newList
     */
    public void refresh(List<User> newList) {
        userList.clear();
        userList.addAll(newList);
        notifyDataSetChanged();
    }
}
