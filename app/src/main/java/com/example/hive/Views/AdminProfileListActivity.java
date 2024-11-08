package com.example.hive.Views;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hive.AdminEvent.AdminEventListActivity;
import com.example.hive.Controllers.FirebaseController;
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
    private List<User> userList = new ArrayList<>();
    public static final int REQUEST_CODE_PROFILE_VIEW = 1;
    private SearchView searchView;
    private ImageButton backArrow;
    private FirebaseController firebaseController;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_profile_list);
        recyclerView = findViewById(R.id.recyclerViewAdminProfileList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        firebaseController = new FirebaseController();
        profileAdapter = new ProfileAdapter(this, userList);
        recyclerView.setAdapter(profileAdapter);
        fetchAllUsers();

        searchView = findViewById(R.id.adminProfileSearchView);
        backArrow = findViewById(R.id.backButton);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                profileAdapter.filter(query);
                return true;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                profileAdapter.filter(newText);
                return true;
            }
        });

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Notify the user when the back arrow is clicked
                Intent i = new Intent(AdminProfileListActivity.this, AdminEventListActivity.class);
                Toast.makeText(AdminProfileListActivity.this, "Back arrow clicked", Toast.LENGTH_SHORT).show();
                finish();
                startActivity(i);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchAllUsers();
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_PROFILE_VIEW && resultCode == RESULT_OK && data != null) {
            String deletedDeviceId = data.getStringExtra("deviceId");
            if (deletedDeviceId != null) {
                Log.d("AdminProfileListActivity", "Device ID received: " + deletedDeviceId);
                //removeUserFromList(deletedDeviceId);  // made into a reusable function to refresh users list
                fetchAllUsers();
            }
        }
    }

    private void removeUserFromList(String deviceId) {
        for (int i = 0; i < userList.size(); i++) {  // constraint
            if (userList.get(i).getDeviceId().equals(deviceId)) {
                userList.remove(i);
                profileAdapter.notifyItemRemoved(i);
                break;
            }
        }
    }


    private void fetchAllUsers() {
        firebaseController.fetchAllUsers().thenAccept(users -> {
            userList.clear();
            userList.addAll(users);
            profileAdapter.notifyDataSetChanged();
        }).exceptionally(e-> {
            Log.e(TAG, "Error with fetchAllUsers method in firebase and AdminProfileListActivity.java", e);
            return null;
        });
    }
}
