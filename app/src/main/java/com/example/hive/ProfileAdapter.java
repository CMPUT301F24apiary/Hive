package com.example.hive;

import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/*
Use RecyclerView to display list of profiles in admin view
This is an adapter for the view to display profiles.
 */
public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ProfileViewHolder> {
    private List<UserProfile> profileList;

    public ProfileAdapter(List<UserProfile> profileList) {
        this.profileList = profileList;
    }

    @NonNull
    public ProfileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_profile, parent, false);
        return new ProfileViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProfileAdapter.ProfileViewHolder holder, int position) {
        UserProfile profile = profileList.get(position);
        holder.nameTextView.setText(profile.getName());
        holder.emailTextView.setText(profile.getEmail());
    }

    public int getItemCount() {
        return profileList.size();
    }

    public static class ProfileViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, emailTextView;
        ImageView profileImageView;

        public ProfileViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.profileName);
            emailTextView = itemView.findViewById(R.id.profileEmail);
            profileImageView = itemView.findViewById(R.id.profileImage);
        }
    }
}
