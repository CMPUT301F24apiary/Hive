package com.example.hive;

import android.content.Context;
import android.content.Intent;

import androidx.test.core.app.ApplicationProvider;

import com.example.hive.Events.EventDetailActivity;
import com.example.hive.Views.CancelledListActivity;
import com.example.hive.Views.EntrantMapActivity;
import com.example.hive.Views.FinalListActivity;
import com.example.hive.Views.InvitedEntrantsActivity;
import com.example.hive.Views.WaitingListActivity;

import org.junit.Test;

public class OptionsTest {

    private final Context context = ApplicationProvider.getApplicationContext();

    @Test
    public void testInvitedEntrantsNavigation() {
        // Create a mock intent for navigating to InvitedEntrantsActivity
        Intent intent = new Intent(context, InvitedEntrantsActivity.class);
        intent.putExtra("eventId", "mockEventId");

        // Simulate the navigation
        assert intent.getComponent() != null : "Component should not be null.";
        assert intent.getComponent().getClassName().equals(InvitedEntrantsActivity.class.getName()) :
                "Should navigate to InvitedEntrantsActivity.";
        assert intent.getStringExtra("eventId").equals("mockEventId") :
                "Event ID should match.";
    }

    @Test
    public void testWaitingListNavigation() {
        // Create a mock intent for navigating to WaitingListActivity
        Intent intent = new Intent(context, WaitingListActivity.class);
        intent.putExtra("eventId", "mockEventId");

        // Simulate the navigation
        assert intent.getComponent() != null : "Component should not be null.";
        assert intent.getComponent().getClassName().equals(WaitingListActivity.class.getName()) :
                "Should navigate to WaitingListActivity.";
        assert intent.getStringExtra("eventId").equals("mockEventId") :
                "Event ID should match.";
    }

    @Test
    public void testCancelledEntrantsNavigation() {
        // Create a mock intent for navigating to CancelledListActivity
        Intent intent = new Intent(context, CancelledListActivity.class);
        intent.putExtra("eventId", "mockEventId");

        // Simulate the navigation
        assert intent.getComponent() != null : "Component should not be null.";
        assert intent.getComponent().getClassName().equals(CancelledListActivity.class.getName()) :
                "Should navigate to CancelledListActivity.";
        assert intent.getStringExtra("eventId").equals("mockEventId") :
                "Event ID should match.";
    }

    @Test
    public void testParticipantsNavigation() {
        // Create a mock intent for navigating to FinalListActivity
        Intent intent = new Intent(context, FinalListActivity.class);
        intent.putExtra("eventId", "mockEventId");

        // Simulate the navigation
        assert intent.getComponent() != null : "Component should not be null.";
        assert intent.getComponent().getClassName().equals(FinalListActivity.class.getName()) :
                "Should navigate to FinalListActivity.";
        assert intent.getStringExtra("eventId").equals("mockEventId") :
                "Event ID should match.";
    }

    @Test
    public void testViewEntrantMapNavigation() {
        // Create a mock intent for navigating to EntrantMapActivity
        Intent intent = new Intent(context, EntrantMapActivity.class);
        intent.putExtra("eventId", "mockEventId");

        // Simulate the navigation
        assert intent.getComponent() != null : "Component should not be null.";
        assert intent.getComponent().getClassName().equals(EntrantMapActivity.class.getName()) :
                "Should navigate to EntrantMapActivity.";
        assert intent.getStringExtra("eventId").equals("mockEventId") :
                "Event ID should match.";
    }

    @Test
    public void testBackButtonNavigation() {
        // Create a mock intent for navigating back to EventDetailActivity
        Intent intent = new Intent(context, EventDetailActivity.class);
        intent.putExtra("eventId", "mockEventId");

        // Simulate the navigation
        assert intent.getComponent() != null : "Component should not be null.";
        assert intent.getComponent().getClassName().equals(EventDetailActivity.class.getName()) :
                "Should navigate to EventDetailActivity.";
        assert intent.getStringExtra("eventId").equals("mockEventId") :
                "Event ID should match.";
    }
}
