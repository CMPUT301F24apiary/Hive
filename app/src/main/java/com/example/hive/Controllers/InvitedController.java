package com.example.hive.Controllers;

import com.example.hive.Models.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
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

}
