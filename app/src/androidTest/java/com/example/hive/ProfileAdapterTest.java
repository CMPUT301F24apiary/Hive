package com.example.hive;

import static org.junit.Assert.assertEquals;

import android.content.Context;
import android.widget.FrameLayout;

import androidx.test.annotation.UiThreadTest;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.hive.Controllers.ProfileAdapter;
import com.example.hive.Models.User;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

/*
Test profile adapter (instrumental test)
 */
@RunWith(AndroidJUnit4.class)
public class ProfileAdapterTest {
    private Context context;
    private ProfileAdapter profileAdapter;
    private List<User> userList;

    @Before
    public void before() {
        context = ApplicationProvider.getApplicationContext();
        userList = new ArrayList<>();
        User user = User.getInstance();
        userList.add(user);
        profileAdapter = new ProfileAdapter(context, userList);
    }

    @Test
    public void testItemCount() {
        assertEquals("item count matches userList test", userList.size(), profileAdapter.getItemCount());
    }

    @UiThreadTest // must run Glide on main thread
    @Test
    public void testBindViewHolder() {
        ProfileAdapter.ProfileViewHolder viewHolder = profileAdapter.onCreateViewHolder(new FrameLayout(context),0);
        profileAdapter.onBindViewHolder(viewHolder, 0);
        assertEquals("", viewHolder.textViewName.getText().toString());
    }
}
