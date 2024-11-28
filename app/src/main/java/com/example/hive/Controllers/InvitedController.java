package com.example.hive.Controllers;

import android.content.Context;

import com.example.hive.Models.User;


import com.google.android.gms.tasks.OnSuccessListener;
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

    public void getWaitingListUIDs(String eventID, OnSuccessListener<ArrayList<String>> listener) {
        db.collection("events").document(eventID).collection("waiting-list")
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> docs = queryDocumentSnapshots.getDocuments();
                        ArrayList<String> userIDs = new ArrayList<>();
                        for (DocumentSnapshot doc : docs) {
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

    /**
     * Notify a user that they have won the lottery.
     *
     * @param context       The application context.
     * @param userId        The user ID of the winner.
     */
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
                // Handle error if user fetch fails
            }
        });
    }

    /**
     * Notify a user that they have not been selected in the lottery.
     *
     * @param context       The application context.
     * @param userId        The user ID of the non-winner.
     */
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
                // Handle error if user fetch fails
            }
        });
    }

    /**
     * Notify a user that they have another chance to join due to declined invitations.
     *
     * @param context       The application context.
     * @param userId        The user ID of the user being re-invited.
     */
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
                // Handle error if user fetch fails
            }
        });
    }

    /**
     * Example method to demonstrate handling lottery results with notifications.
     *
     * @param context         The application context.
     * @param eventID         The event ID.
     * @param numParticipants The number of participants to be selected.
     */
    public void handleLotteryResults(Context context, String eventID, int numParticipants) {
        getWaitingListUIDs(eventID, waitingList -> {
            ArrayList<String> invitedList = generateInvitedList(waitingList, numParticipants);
            ArrayList<String> nonInvitedList = new ArrayList<>(waitingList);
            nonInvitedList.removeAll(invitedList);

            // Notify winners
            for (String userId : invitedList) {
                notifyUserWin(context, userId);
            }

            // Notify non-winners
            for (String userId : nonInvitedList) {
                notifyUserLose(context, userId);
            }
        });
    }
}
