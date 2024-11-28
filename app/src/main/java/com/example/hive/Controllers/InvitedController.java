package com.example.hive.Controllers;

import static android.icu.number.NumberRangeFormatter.with;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.hive.Models.User;
import com.example.hive.NotificationActivity;
import com.example.hive.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Firebase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InvitedController extends FirebaseController {

    private FirebaseFirestore db;
    private EventController eventControl;

    public InvitedController() {
        super();
        this.db = getDb();
        this.eventControl = new EventController();
    }

    public void getWaitingListUIDs(String eventID, OnSuccessListener<ArrayList<String>> listener) {
        db.collection("events").document(eventID).collection("waiting-list")
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> docs = queryDocumentSnapshots.getDocuments();
                ArrayList<String> userIDs = new ArrayList<>();
                for (DocumentSnapshot doc:docs) {
                    userIDs.add(doc.getId());
                }
                listener.onSuccess(userIDs);
            }
        });
    }

    public ArrayList<String> generateInvitedList(ArrayList<String> entrants, int numParticipants) {
        if (entrants.size() < numParticipants) {
            return entrants;
        }
        ArrayList<String> invited = new ArrayList<>();
        while (invited.size() < numParticipants) {
            // Get random entrant
            double randomNum = Math.random() % entrants.size();
            int pos = (int) Math.floor(randomNum);
            invited.add(entrants.remove(pos));
        }
        return invited;
    }

    public void createInvitedUserList(ArrayList<String> invited,
                                      OnSuccessListener<ArrayList<User>> listener) {
        ArrayList<User> userList = new ArrayList<>();
        int[] completedFetches = {0};  // Using array to modify in lambda

        // If invited list is empty, return immediately
        if (invited.isEmpty()) {
            listener.onSuccess(userList);
            return;
        }

        for (String id : invited) {
            fetchUserByDeviceId(id, new OnUserFetchedListener() {
                @Override
                public void onUserFetched(User user) {
                    userList.add(user);
                    completedFetches[0]++;

//                    Intent i = new Intent(context, NotificationActivity.class);
//                    PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
//                            i, PendingIntent.FLAG_IMMUTABLE);
//
//                    NotificationCompat.Builder builder =
//                            new NotificationCompat.Builder(context, "default_channel");
//                    builder.setSmallIcon(R.drawable.hivelogo)
//                            .setContentTitle("You have been chosen for an event!")
//                            .setContentText("You have been selected to enrol in the "
//                                    + title + " event.")
//                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//                            .setContentIntent(pendingIntent);
//
//                    NotificationManager notificationManager =
//                            (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

                    // Give each notification a unique ID so they don't override each other
//                    int notificationId = (int) System.currentTimeMillis();
//                    notificationManager.notify(notificationId, builder.build());

                    // Only call listener when all fetches are complete
                    if (completedFetches[0] == invited.size()) {
                        listener.onSuccess(userList);
                    }
                }

                @Override
                public void onError(Exception e) {
                    completedFetches[0]++;
                    // Even on error, check if all fetches are complete
                    if (completedFetches[0] == invited.size()) {
                        listener.onSuccess(userList);
                    }
                }
            });
        }
    }

    public void sendNotificationToSelected(String msg, String id) {

        new EventController().getInvitedList(id, invited -> {
            if (invited.first) {
                for (String uid : invited.second) {
                    HashMap<String, Object> data = new HashMap<>();
                    data.put("eventID", id);
                    data.put("message", msg);
                    data.put("notificationType", "Invite");
                    db.collection("users")
                            .document(uid)
                            .collection("notifications")
                            .add(data);
                }
            }
        });
    }

}
