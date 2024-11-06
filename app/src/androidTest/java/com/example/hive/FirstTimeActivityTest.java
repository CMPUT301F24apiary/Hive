package com.example.hive;

import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.intent.Intents;

import com.example.hive.Views.FirstTimeActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * Ensure that firstTimeActivity completes and user proceeds to next activity
 * Here, the next activity is the RoleSelectionActivity, as defined in
 * FirstTimeActivity.java and in MainActivity, where if a user exists in the db,
 * they are not directed to FirstTimeActivity.
 */
@RunWith(JUnit4.class)
public class FirstTimeActivityTest {
    @Before
    public void before() {
        Intents.init();
    }

    @After
    public void tearDown() {
        Intents.release();
    }

    @Test
    public void testFirstTimeActivity_LaunchRoleSelectionActivity() {
        try (ActivityScenario<FirstTimeActivity> scenario = ActivityScenario.launch(FirstTimeActivity.class)) {
            scenario.onActivity(activity -> activity.completeFirstTimeActivity());
            intended(hasComponent(RoleSelectionActivity.class.getName()));
        }
    }
}
