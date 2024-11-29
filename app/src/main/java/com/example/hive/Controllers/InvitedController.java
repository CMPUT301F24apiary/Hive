package com.example.hive.Controllers;

import android.content.Context;
import android.util.Log;

import com.example.hive.Models.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class InvitedController extends FirebaseController {

    private FirebaseFirestore db;

    public InvitedController() {
        super();
        this.db = getDb();
    }

    // Method to get waiting list user IDs
    public void getWaitingListUIDs(String eventID, OnSuccessListener<ArrayList<String>> listener) {
        db.collection("events").document(eventID).collection("waiting-list")
                .get().addOnSuccessListener(queryDocumentSnapshots -> {
                    List<DocumentSnapshot> docs = queryDocumentSnapshots.getDocuments();
                    ArrayList<String> userIDs = new ArrayList<>();
                    for (DocumentSnapshot doc : docs) {
                        userIDs.add(doc.getId());
                    }
                    listener.onSuccess(userIDs);
                });
    }

    // Method to generate an invited list based on a lottery
    public ArrayList<String> generateInvitedList(String eventID, ArrayList<String> entrants, int numParticipants) {
        CollectionReference eventsCollection = db.collection("events");

        Log.d("InvitedController", "Starting to generate invited list for eventID: " + eventID);
        Log.d("InvitedController", "Number of entrants available: " + entrants.size() + ", Number of participants to select: " + numParticipants);

        // Set isLotteryDrawn to false before the lottery is drawn
        eventsCollection.document(eventID).update("isLotteryDrawn", Boolean.FALSE)
                .addOnSuccessListener(aVoid -> Log.d("InvitedController", "Lottery status set to false."))
                .addOnFailureListener(e -> Log.e("InvitedController", "Failed to set lottery status to false", e));

        // Proceed with drawing the lottery
        if (entrants.size() < numParticipants) {
            Log.d("InvitedController", "Number of entrants is less than the number of participants. Returning all entrants.");
            return entrants;
        }

        ArrayList<String> invited = new ArrayList<>();
        while (invited.size() < numParticipants) {
            // Get random entrant
            double randomNum = Math.random() % entrants.size();
            int pos = (int) Math.floor(randomNum);
            invited.add(entrants.remove(pos));
        }

        Log.d("InvitedController", "Invited users generated. Number of users invited: " + invited.size());

        // Set isLotteryDrawn to true after the lottery is drawn
        eventsCollection.document(eventID).update("isLotteryDrawn", Boolean.TRUE)
                .addOnSuccessListener(aVoid -> Log.d("InvitedController", "Lottery status set to true."))
                .addOnFailureListener(e -> Log.e("InvitedController", "Failed to set lottery status to true", e));

        return invited;
    }

    // Create the invited user list in Firestore
    public void createInvitedUserList(ArrayList<String> invited, OnSuccessListener<ArrayList<User>> listener) {
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

    // Notify a user that they have won the lottery
    public void notifyUserWin(Context context, String userId) {
        fetchUserByDeviceId(userId, new OnUserFetchedListener() {
            @Override
            public void onUserFetched(User user) {
                NotificationsController.showNotification(
                        context,
                        user.hashCode(), // Unique ID based on user
                        "Congratulations!",
                        "You have been chosen for the event!"
                );
            }

            @Override
            public void onError(Exception e) {
                Log.e("InvitedController", "Failed to fetch user for winning notification", e);
            }
        });
    }


    // Notify a user that they have not been selected in the lottery
    public void notifyUserLose(Context context, String userId) {
        fetchUserByDeviceId(userId, new OnUserFetchedListener() {
            @Override
            public void onUserFetched(User user) {
                NotificationsController.showNotification(
                        context,
                        user.hashCode(), // Unique ID based on user
                        "Thank you for participating",
                        "Unfortunately, you were not chosen this time."
                );
            }

            @Override
            public void onError(Exception e) {
                Log.e("InvitedController", "Failed to fetch user for losing notification", e);
            }
        });
    }

    // Notify a user that they have another chance to join due to declined invitations
    public void notifyUserReRegister(Context context, String userId) {
        fetchUserByDeviceId(userId, new OnUserFetchedListener() {
            @Override
            public void onUserFetched(User user) {
                NotificationsController.showNotification(
                        context,
                        user.hashCode(), // Unique ID based on user
                        "Another Chance!",
                        "A spot has opened up. Re-register now!"
                );
            }

            @Override
            public void onError(Exception e) {
                Log.e("InvitedController", "Failed to fetch user for re-registration notification", e);
            }
        });
    }

    // Check if a specific user is on the invited list for the event
    public void checkIfUserIsInvited(String eventID, String userID, OnSuccessListener<Boolean> listener) {
        db.collection("events").document(eventID).collection("invited-list").document(userID)
                .get().addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        listener.onSuccess(true);
                    } else {
                        listener.onSuccess(false);
                    }
                }).addOnFailureListener(e -> {
                    Log.e("InvitedController", "Error checking if user is invited", e);
                    listener.onSuccess(false);
                });
    }
}
