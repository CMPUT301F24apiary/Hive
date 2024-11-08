package com.example.hive;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertEquals;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;

import androidx.test.annotation.UiThreadTest;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.hive.Controllers.ProfileAdapter;
import com.example.hive.Models.User;
import com.example.hive.Views.AdminProfileViewActivity;
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

    /*
    Test that items are added to userList and profileAdapter method works
     */
    @Test
    public void testItemCount() {
        context = ApplicationProvider.getApplicationContext();
        userList = new ArrayList<>();
        User user = User.getInstance();
        userList.add(user);
        profileAdapter = new ProfileAdapter(context, userList);
        assertEquals("item count matches userList test", userList.size(), profileAdapter.getItemCount());
        userList.remove(user);
        assertEquals("item count matches userList test", userList.size(), profileAdapter.getItemCount());
    }

    /*
    Test that name of user and other info is in profile adapter so that user profile is properly fetched
     */
    @UiThreadTest // must run Glide on main thread
    @Test
    public void testBindViewHolder() {
        context = ApplicationProvider.getApplicationContext();
        userList = new ArrayList<>();
        User user = User.getInstance();
        user.setDeviceId("123");
        user.setUserName("Clare");
        user.setEmail("test@test");
        user.setPhoneNumber("780");
        user.setRole("Admin");
        //user.setRoleList(["Admin", "Entrant"]);
        user.setProfileImageUrl("noUrlForNow");
        userList.add(user);
        profileAdapter = new ProfileAdapter(context, userList);
        ProfileAdapter.ProfileViewHolder viewHolder = profileAdapter.onCreateViewHolder(new FrameLayout(context), 0);
        profileAdapter.onBindViewHolder(viewHolder, 0);
        assertEquals("Clare", viewHolder.textViewName.getText().toString());  // test name is the (default) name of the user
        assertEquals(View.VISIBLE, viewHolder.imageViewProfile.getVisibility());
    }
}
