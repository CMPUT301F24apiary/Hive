package com.example.hive.Views;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hive.Controllers.FirebaseController;
import com.example.hive.Controllers.ProfileAdapter;
import com.example.hive.Models.User;
import com.example.hive.R;

import java.util.ArrayList;
import java.util.List;

/**
 * this activity displays the profile list that admin can browse through.
 * it uses the profile adapter
 */
public class AdminProfileListActivity extends AppCompatActivity {
    public RecyclerView recyclerView;
    private ProfileAdapter profileAdapter;
    private List<User> userList, users;
    public static final int REQUEST_CODE_PROFILE_VIEW = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_profile_list);
        userList = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerViewAdminProfileList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FirebaseController firebaseController = new FirebaseController();
        firebaseController.fetchAllUsers().thenAccept(userList -> {
            Log.d(TAG, "User list size: " + userList.size());
            profileAdapter = new ProfileAdapter(this, userList);
            recyclerView.setAdapter(profileAdapter);
        }).exceptionally(e->{
            Log.e(TAG, "Error with fetchAllUsers in firebase controller", e);
            return null;
        });
    }
    /*
    private void refreshUserList() {
        FirebaseController firebaseController = new FirebaseController();
        firebaseController.fetchAllUsers().thenAccept(userList-> {
            Log.d(TAG, "User list size: " + userList.size());
            users.clear();
            users.addAll(userList);
            if (profileAdapter == null) {
                profileAdapter = new ProfileAdapter(this, userList);
                recyclerView.setAdapter(profileAdapter);
            } else {
                profileAdapter.notifyDataSetChanged();
            }
        }).exceptionally(e-> {
            Log.e(TAG, "Error with fetchAllUsers in firebase controller when refreshUserList", e);
            return null;
        });
    }

     */

}
