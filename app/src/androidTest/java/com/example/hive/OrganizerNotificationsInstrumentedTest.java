package com.example.hive;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import android.app.Instrumentation;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.RootMatchers;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.hive.AdminImage.AdminImageListActivity;
import com.example.hive.Views.OrganizerNotificationActivity;
import com.google.inject.matcher.Matcher;
import com.google.inject.matcher.Matchers;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.function.Predicate;

@RunWith(AndroidJUnit4.class)
public class OrganizerNotificationsInstrumentedTest {

    private View decorView;

    static Intent intent;
    static {
        intent = new Intent(ApplicationProvider.getApplicationContext(),
                OrganizerNotificationActivity.class);
        intent.putExtra("eventID", "testId");
    }

    @Rule
    public ActivityScenarioRule<OrganizerNotificationActivity> activityRule =
            new ActivityScenarioRule<>(intent);

    @Before
    public void setUp() {
        Intents.init();
        activityRule.getScenario().onActivity(new ActivityScenario.ActivityAction<OrganizerNotificationActivity>() {
            @Override
            public void perform(OrganizerNotificationActivity activity) {
                decorView = activity.getWindow().getDecorView();
            }
        });
    }

    @After
    public void tearDown() {
        Intents.release();
    }

    @Test
    public void TestDisplayedProperly() {
        onView(withId(R.id.title_send_notifs)).check(matches(isDisplayed()));
        onView(withId(R.id.title_send_notifs)).check(matches(withText("Send Notifications")));
        onView(withId(R.id.selected_entrants_switch)).check(matches(isDisplayed()));
        onView(withId(R.id.cancelled_entrants_switch)).check(matches(isDisplayed()));
        onView(withId(R.id.waiting_list_switch)).check(matches(isDisplayed()));
        onView(withId(R.id.add_message_title)).check(matches(isDisplayed()));
        onView(withId(R.id.add_message_title)).check(matches(withText("Message:")));
        onView(withId(R.id.message_edit_text)).check(matches(isDisplayed()));
        onView(withId(R.id.send_notif_button)).check(matches(isDisplayed()));
    }

}
