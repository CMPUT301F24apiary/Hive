package com.example.hive.Controllers;

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
import com.example.hive.ProfileActivity;
import com.example.hive.R;
import com.example.hive.Views.AdminProfileListActivity;
import com.example.hive.Views.AdminProfileViewActivity;

import java.util.List;

/**
 * display each item in users list. when user clicks on details button, they are led to
 * admin profile page where admin can delete the profile.
 * known bug: crashes if there are no users in the database
 */
public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ProfileViewHolder> {
    private Context context;
    private List<User> userList;

    public ProfileAdapter(Context context, List<User> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public ProfileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.profile_item, parent, false);
        return new ProfileViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProfileAdapter.ProfileViewHolder holder, int position) {
        User user = userList.get(position);

        // using Glide for image loading
        Glide.with(context).load(user.getProfileImageUrl())
                .error(R.drawable.ic_profile)  // default image if there is error/no profile pic set
                .into(holder.imageViewProfile);

        holder.textViewName.setText(user.getUserName());
        holder.buttonViewDetails.setOnClickListener(v-> {
            Intent intent = new Intent(context, AdminProfileViewActivity.class);
            intent.putExtra("deviceId", user.getDeviceId());
            context.startActivity(intent);
            Log.d(TAG, "Device ID for user " + user.getUserName() + ": " + user.getDeviceId());

        });
        holder.itemView.setOnClickListener(v-> {
            Intent intent = new Intent(context, AdminProfileViewActivity.class);
            intent.putExtra("deviceId", user.getDeviceId());
            ((Activity) context).startActivityForResult(intent, AdminProfileListActivity.REQUEST_CODE_PROFILE_VIEW);
        });
        
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class ProfileViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewProfile;
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

    public void refresh(List<User> userList) {
        this.userList = userList;
        notifyDataSetChanged();
    }

}
