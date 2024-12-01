package com.example.hive.Controllers;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.hive.Events.Event;
import com.example.hive.Models.Notification;
import com.example.hive.Models.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;

public class ListController extends FirebaseController {

    private FirebaseFirestore db;

    public ListController() {
        super();
        this.db = getDb();
    }

    // Method to get waiting list user IDs
    public void getWaitingListUIDs(String eventID, OnSuccessListener<ArrayList<String>> listener) {
        new EventController().getSingleEvent(eventID, event -> {
            if (event == null) {
                Log.d("getWaitingListUIDs", "Error getting event " + eventID);
            }
            db.collection("waiting-list").document(event.getWaitingListId())
                    .get().addOnSuccessListener(documentSnapshot -> {
                        ArrayList<String> userIDs = (ArrayList) documentSnapshot.get("user-ids");
                        if (userIDs == null) {
                            Log.d("GetWaitingListUIDs",
                                    "Waiting List user ids do not exist for event " + eventID);
                        } else {
                            listener.onSuccess(userIDs);
                        }
                    });
        });
    }

    /**
     * Draw the lottery and store results in firebase
     *
     * @param eventID
     * @param entrants
     * @param numParticipants
     * @return
     */
    public ArrayList<String> generateInvitedList(String eventID, ArrayList<String> entrants,
                                                 int numParticipants) {
        CollectionReference eventsCollection = db.collection("events");

        EventController eventController = new EventController();

        Log.d("ListController", "Starting to generate invited list for eventID: " + eventID);
        Log.d("ListController", "Number of entrants available: " + entrants.size() + ", Number of participants to select: " + numParticipants);

        // Proceed with drawing the lottery
        if (entrants.size() < numParticipants) {
            Log.d("ListController", "Number of entrants is less than the number of participants. Returning all entrants.");
            eventController.addInvitedList(eventID, entrants);
            eventController.updateWaitingList(eventID, new ArrayList<>());
            return entrants;
        }

        ArrayList<String> invited = new ArrayList<>();
        while (invited.size() < numParticipants) {
            // Get random entrant
            double randomNum = Math.random() % entrants.size();
            int pos = (int) Math.floor(randomNum);
            invited.add(entrants.remove(pos));
        }

        Log.d("ListController", "Invited users generated. Number of users invited: " + invited.size());

        // Add invited to the firebase after draw
        eventController.addInvitedList(eventID, invited);
        eventController.updateWaitingList(eventID, entrants);

        return invited;
    }

    /**
     * Create array of users to be displayed in the activity
     *
     * @param invited
     * @param listener
     */
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
//    public void notifyUserWin(Context context, String userId) {
//        fetchUserByDeviceId(userId, new OnUserFetchedListener() {
//            @Override
//            public void onUserFetched(User user) {
//                NotificationsController.showNotification(
//                        context,
//                        user.hashCode(), // Unique ID based on user
//                        "Congratulations!",
//                        "You have been chosen for the event!"
//                );
//            }
//
//            @Override
//            public void onError(Exception e) {
//                Log.e("ListController", "Failed to fetch user for winning notification", e);
//            }
//        });
//    }


    // Notify a user that they have not been selected in the lottery
//    public void notifyUserLose(Context context, String userId) {
//        fetchUserByDeviceId(userId, new OnUserFetchedListener() {
//            @Override
//            public void onUserFetched(User user) {
//                NotificationsController.showNotification(
//                        context,
//                        user.hashCode(), // Unique ID based on user
//                        "Thank you for participating",
//                        "Unfortunately, you were not chosen this time."
//                );
//            }
//
//            @Override
//            public void onError(Exception e) {
//                Log.e("ListController", "Failed to fetch user for losing notification", e);
//            }
//        });
//    }

    // Notify a user that they have another chance to join due to declined invitations
//    public void notifyUserReRegister(Context context, String userId) {
//        fetchUserByDeviceId(userId, new OnUserFetchedListener() {
//            @Override
//            public void onUserFetched(User user) {
//                NotificationsController.showNotification(
//                        context,
//                        user.hashCode(), // Unique ID based on user
//                        "Another Chance!",
//                        "A spot has opened up. Re-register now!"
//                );
//            }
//
//            @Override
//            public void onError(Exception e) {
//                Log.e("ListController", "Failed to fetch user for re-registration notification", e);
//            }
//        });
//    }

    // Run the lottery to select the winners
    public void runLottery(Context context, String eventID, int maxWinners, OnSuccessListener<ArrayList<User>> listener) {
        // Fetch the waiting list of entrants
        getWaitingListUIDs(eventID, waitingList -> {
            if (waitingList == null || waitingList.isEmpty()) {
                Log.d("ListController", "Waiting list is empty for event: " + eventID);
                Toast.makeText(context, "No entrants in the waiting list to draw a lottery.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Randomly select winners from the waiting list
            ArrayList<String> winners = generateInvitedList(eventID, new ArrayList<>(waitingList), maxWinners);

            // Add the invited list to the event in Firestore
            createInvitedUserList(winners, userList -> {
                // Notify winners and others
                notifyLotteryResults(context, eventID, winners, waitingList);
                Log.d("ListController", "Lottery drawn and notifications sent for event: " + eventID);
                Toast.makeText(context, "Lottery drawn successfully!", Toast.LENGTH_SHORT).show();
                listener.onSuccess(userList);
            });
        });
    }


    // Notify users after lottery results
    public void notifyLotteryResults(Context context, String eventID, ArrayList<String> invited, ArrayList<String> allEntrants) {
        // Notify the users who have won
        for (String userId : invited) {
//            notifyUserWin(context, userId);
            addNotification(userId, eventID, "win", "Congratulations! You have been chosen for the event ");
        }

        // Notify the users who have not won
        for (String userId : allEntrants) {
            if (!invited.contains(userId)) {
//                notifyUserLose(context, userId);
                addNotification(userId, eventID, "lose", "Thank you for participating. Unfortunately, you were not selected for the event ");
            }
        }

        Log.d("ListController", "Notifications sent for lottery results of event: " + eventID);
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
                    Log.e("ListController", "Error checking if user is invited", e);
                    listener.onSuccess(false);
                });
    }

    public void addNotification(String userID, String eventID, String type, String message) {
        HashMap<String, Object> data = new HashMap<>();
        data.put("type", type);
        data.put("eventid", eventID);
        data.put("content", message);
        DocumentReference userDoc = db.collection("users").document(userID);
        userDoc.get().addOnSuccessListener(doc -> {
            boolean permChosen = Boolean.TRUE.equals(doc.getBoolean("notificationChosen"));
            boolean permNotChosen = Boolean.TRUE.equals(doc.getBoolean("notificationNotChosen"));
            boolean permAll = Boolean.TRUE.equals(doc.getBoolean("notificationOrganizer"));
            if (!permAll) {
                Log.d("addNotification, all", "Permission Denied");
                return;
            }
            else if (!type.equals("win") && !type.equals("lose")) {
                userDoc.collection("notifications").add(data);
            }
            if (!permChosen && type.equals("win")) {
                Log.d("addNotification, win", "Permission Denied");
                return;
            } else if (permChosen && type.equals("win")) {
                userDoc.collection("notifications").add(data);
            }
            if (!permNotChosen && type.equals("lose")) {
                Log.d("addNotification, lose", "Permission Denied");
            } else if (permNotChosen && type.equals("lose")) {
                userDoc.collection("notifications").add(data);
            }
        });
    }

    public void fetchCancelledList(String eventId, OnSuccessListener<ArrayList<String>> listener) {
        Log.d("CancelledListActivity", "Fetching cancelled list for eventId: " + eventId);

        db.collection("events").document(eventId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                DocumentSnapshot eventDocument = task.getResult();
                String cancelledListId = eventDocument.getString("cancelledlistID");

                if (cancelledListId != null) {
                    Log.d("CancelledListActivity", "CancelledListID retrieved: " + cancelledListId);

                    db.collection("cancelled-list").document(cancelledListId).get()
                        .addOnCompleteListener(cancelledTask -> {
                            if (cancelledTask.isSuccessful() && cancelledTask.getResult() != null) {
                                DocumentSnapshot cancelledDocument = cancelledTask.getResult();
                                ArrayList<String> deviceIds = (ArrayList<String>) cancelledDocument.get("userIds");

                                if (deviceIds != null) {
                                    Log.d("CancelledListActivity", "Device IDs retrieved: " + deviceIds.toString());

                                    // List to hold usernames
                                    ArrayList<String> usernames = new ArrayList<>();

                                    // Fetch usernames corresponding to each deviceId
                                    for (String deviceId : deviceIds) {
                                        Log.d("CancelledListActivity", "Fetching username for deviceId: " + deviceId);

                                        db.collection("users").document(deviceId).get()
                                            .addOnCompleteListener(userTask -> {
                                                if (userTask.isSuccessful() && userTask.getResult() != null) {
                                                    DocumentSnapshot userDocument = userTask.getResult();
                                                    String username = userDocument.getString("username");
                                                    if (username != null) {
                                                        usernames.add(username);
                                                        Log.d("CancelledListActivity", "Username retrieved: " + username);
                                                    } else {
                                                        Log.e("CancelledListActivity", "No username found for deviceId " + deviceId);
                                                    }
                                                } else {
                                                    Log.e("CancelledListActivity", "Error fetching user for deviceId " + deviceId, userTask.getException());
                                                }

                                                // Check if all usernames have been fetched
                                                if (usernames.size() == deviceIds.size()) {
                                                    listener.onSuccess(usernames);
                                                }
                                            });
                                    }
                                } else {
                                    Log.e("CancelledListActivity", "No userIds found in cancelled list document");
                                }
                            } else {
                                Log.e("CancelledListActivity", "Error fetching cancelled list document: ", cancelledTask.getException());
                            }
                        });
                } else {
                    Log.e("CancelledListActivity", "No cancelledListId found in event document");
                    listener.onSuccess(null);
                }
            } else {
                Log.e("CancelledListActivity", "Error fetching event document: ", task.getException());
            }
        });
    }

    public void fetchNotifications(String deviceID, OnSuccessListener<ArrayList<Notification>> listener) {
        ArrayList<Notification> notifs = new ArrayList<>();
        db.collection("users").document(deviceID)
                .collection("notifications").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
            int totalDocs = queryDocumentSnapshots.size();
            if (totalDocs == 0) {
                listener.onSuccess(notifs);
            }
            for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                Notification notif = doc.toObject(Notification.class);
                if (notif != null) {
                    notif.setFirebaseId(doc.getId());
                    notifs.add(notif);
                }
                if (notifs.size() == totalDocs) {
                    listener.onSuccess(notifs);
                }
            }
        }).addOnFailureListener(e -> {
            Log.d("Error getting notifications",
                    e.getMessage() == null ? "Could not retrieve error message" : e.getMessage());
        });
    }

    public void addUserToFinalList(String eventID, String userID, OnSuccessListener<Boolean> listener) {
        new EventController().getSingleEvent(eventID, event -> {
            String finalListID = event.getFinalListID();
            if (finalListID == null) {
                DocumentReference docRef = db.collection("final-list").document();
                String newFinalListID = docRef.getId();
                HashMap<String, Object> data = new HashMap<>();
                ArrayList<String> deviceIds = new ArrayList<>();
                deviceIds.add(userID);
                data.put("deviceIds", deviceIds);
                docRef.set(data).addOnSuccessListener(v -> {
                    HashMap<String, Object> eventData = new HashMap<>();
                    db.collection("events").document(eventID)
                            .update("finallistID", newFinalListID)
                            .addOnSuccessListener(unused -> {
                                listener.onSuccess(Boolean.TRUE);
                            });
                }).addOnFailureListener(e -> {
                    Log.e("addUserToFinalList", "Failed to create new final list");
                    listener.onSuccess(Boolean.FALSE);
                });
            } else {
                DocumentReference docRef = db.collection("final-list").document(finalListID);
                docRef.get().addOnSuccessListener(doc -> {
                    ArrayList<String> users = (ArrayList<String>) doc.get("userIds");
                    if (users == null) {
                        users = new ArrayList<>();
                    }
                    users.add(userID);
                    docRef.update("userIds", users).addOnSuccessListener(v -> {
                        listener.onSuccess(Boolean.TRUE);
                    }).addOnFailureListener(e -> {
                        Log.e("addUserToFinalList", "Failed to update final list");
                        listener.onSuccess(Boolean.FALSE);
                    });
                }).addOnFailureListener(e -> {
                    Log.e("addUserToFinalList", "Failed to fetch final list");
                    listener.onSuccess(Boolean.FALSE);
                });
            }
        });
    }

    public void addUserToCancelledList(String eventID, String userID, OnSuccessListener<Boolean> listener) {
        new EventController().getSingleEvent(eventID, event -> {
            String cancelledListID = event.getCancelledListID();
            if (cancelledListID == null) {
                DocumentReference docRef = db.collection("cancelled-list").document();
                String newCancelledListID = docRef.getId();
                HashMap<String, Object> data = new HashMap<>();
                ArrayList<String> deviceIds = new ArrayList<>();
                deviceIds.add(userID);
                data.put("userIds", deviceIds);
                docRef.set(data).addOnSuccessListener(v -> {
                    HashMap<String, Object> eventData = new HashMap<>();
                    db.collection("events").document(eventID)
                            .update("cancelledlistID", newCancelledListID)
                            .addOnSuccessListener(unused -> {
                                pickNewEntrant(eventID, event);
                                removeUserFromInvited(eventID, userID);
                                listener.onSuccess(Boolean.TRUE);
                            });
                }).addOnFailureListener(e -> {
                    Log.e("addUserToCancelledList", "Failed to create new cancelled list");
                    listener.onSuccess(Boolean.FALSE);
                });
            } else {
                DocumentReference docRef = db.collection("cancelled-list").document(cancelledListID);
                docRef.get().addOnSuccessListener(doc -> {
                    ArrayList<String> users = (ArrayList<String>) doc.get("userIds");
                    if (users == null) {
                        users = new ArrayList<>();
                    }
                    users.add(userID);
                    docRef.update("userIds", users).addOnSuccessListener(v -> {
                        pickNewEntrant(eventID, event);
                        listener.onSuccess(Boolean.TRUE);
                    }).addOnFailureListener(e -> {
                        Log.e("addUserToCancelledList", "Failed to update cancelled list");
                        listener.onSuccess(Boolean.FALSE);
                    });
                }).addOnFailureListener(e -> {
                    Log.e("addUserToCancelledList", "Failed to fetch cancelled list");
                    listener.onSuccess(Boolean.FALSE);
                });
            }
        });
    }

    public void pickNewEntrant(String eventId, Event event) {
        getWaitingListUIDs(eventId, users -> {
            if (!users.isEmpty()) {
                int random = (int) Math.floor(Math.random() % users.size());
                String selected = users.remove(random);
                DocumentReference docRef = db.collection("waiting-list").document(event.getWaitingListId());
                docRef.get().addOnSuccessListener(doc -> {
                    ArrayList<String> selectedUsers = (ArrayList<String>) doc.get("user-ids");
                    selectedUsers.add(selected);
                    docRef.update("user-ids", selectedUsers).addOnSuccessListener(v -> {
                        new EventController().updateWaitingList(eventId, users);
                    });
                });
            }
        });

    }

    public void removeUserFromInvited(String eventID, String userID) {
        db.collection("events").document(eventID)
                .collection("invited-list").document(userID).delete();
    }


}
