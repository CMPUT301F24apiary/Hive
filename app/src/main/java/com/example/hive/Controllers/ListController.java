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

/**
 * Controller to handle list-related operations including waiting lists, invited lists,
 * cancelled lists, and final lists. Extends FirebaseController for database access.
 *
 * @author Zach
 */
public class ListController extends FirebaseController {

    /** Database reference to Firestore */
    private final FirebaseFirestore db;

    /**
     * Constructor - initializes the controller with a database reference from parent class
     */
    public ListController() {
        super();
        this.db = getDb();
    }

    /**
     * Retrieves the list of user IDs from an event's waiting list.
     *
     * @param eventID The ID of the event
     * @param listener Callback to handle the retrieved list of user IDs
     */
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
     * Draw the lottery and store results in firebase.
     *
     * @param eventID The ID of the event
     * @param entrants List of user IDs eligible for the lottery
     * @param numParticipants Number of participants to select
     * @return ArrayList of selected user IDs
     */
    public ArrayList<String> generateInvitedList(String eventID, ArrayList<String> entrants,
                                                 int numParticipants) {

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
     * Create array of users to be displayed in the activity.
     *
     * @param invited List of user IDs that were invited
     * @param listener Callback to handle the list of User objects
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

    /**
     * Runs the lottery process for an event, selecting winners and sending notifications.
     *
     * @param context Application context for notifications
     * @param eventID The ID of the event
     * @param maxWinners Maximum number of winners to select
     * @param listener Callback to handle the list of selected Users
     */
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

    /**
     * Sends notifications to users about lottery results.
     *
     * @param context Application context for notifications
     * @param eventID The ID of the event
     * @param invited List of selected user IDs
     * @param allEntrants List of all user IDs that entered
     */
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

    /**
     * Checks if a user is on the invited list for an event.
     *
     * @param eventID The ID of the event
     * @param userID The ID of the user to check
     * @param listener Callback with boolean result
     */
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

    /**
     * Adds a notification for a user.
     *
     * @param userID The ID of the user to notify
     * @param eventID The ID of the related event
     * @param type Type of notification ("win", "lose", etc.)
     * @param message The notification message
     */
    public void addNotification(String userID, String eventID, String type, String message) {
        HashMap<String, Object> data = new HashMap<>();
        data.put("type", type);
        data.put("eventid", eventID);
        data.put("content", message);
        db.collection("users").document(userID).collection("notifications").add(data);
    }

    /**
     * Retrieves the cancelled list for an event.
     *
     * @param eventId The ID of the event
     * @param listener Callback to handle the list of cancelled user IDs
     */
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

    /**
     * Retrieves notifications for a user.
     *
     * @param deviceID The device ID of the user
     * @param listener Callback to handle the list of notifications
     */
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

    /**
     * Adds a user to the final list of an event.
     *
     * @param eventID The ID of the event
     * @param userID The ID of the user to add
     * @param listener Callback to handle success/failure
     */
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

    /**
     * Adds a user to the cancelled list of an event.
     *
     * @param eventID The ID of the event
     * @param userID The ID of the user to add
     * @param listener Callback to handle success/failure
     */
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

    /**
     * Selects a new entrant from the waiting list when a spot becomes available.
     *
     * @param eventId The ID of the event
     * @param event The Event object
     */
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

    /**
     * Removes a user from the invited list of an event.
     *
     * @param eventID The ID of the event
     * @param userID The ID of the user to remove
     */
    public void removeUserFromInvited(String eventID, String userID) {
        db.collection("events").document(eventID)
                .collection("invited-list").document(userID).delete();
    }

}
