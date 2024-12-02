package com.example.hive.Controllers;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.util.Pair;

import androidx.annotation.Nullable;

import com.example.hive.Facility;
import com.example.hive.Models.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;

/**
 * Controller to handle facility-related operations in the Firestore database.
 * Extends FirebaseController for base database functionality.
 *
 * @author Zach
 */
public class FacilityController extends FirebaseController {

    /** Database reference to Firestore */
    private final FirebaseFirestore db;

    /**
     * Constructor - initializes the controller with a database reference from parent class
     */
    public FacilityController() {
        super();
        this.db = super.getDb();
    }

    /**
     * Retrieves facility details for a user based on their device ID.
     * Gets the user's facility ID and fetches corresponding facility details.
     *
     * @param deviceId The device ID of the user
     * @param listener Callback to handle the retrieved Facility object
     */
    public void getUserFacilityDetails(String deviceId, OnSuccessListener<Facility> listener) {
        fetchUserByDeviceId(deviceId, new OnUserFetchedListener() {
            @Override
            public void onUserFetched(User user) {
                db.collection("facilities").document(user.getFacilityID()).get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Facility facility = documentSnapshot.toObject(Facility.class);
                        facility.setID(documentSnapshot.getId());
                        listener.onSuccess(facility);
                    }
                });
            }

            @Override
            public void onError(Exception e) {

            }
        });
    }

    /**
     * Adds a new facility to the database and links it to a user.
     * Handles optional facility picture upload using ImageController.
     *
     * @param context Application context for image handling
     * @param deviceID The device ID of the user creating the facility
     * @param name The name of the facility
     * @param email The email address for the facility
     * @param phone The phone number for the facility
     * @param picture Optional Uri of the facility's picture
     * @param listener Callback to handle the success/failure of the operation
     */
    public void addFacility(Context context, String deviceID, String name, String email,
                            String phone, @Nullable Uri picture,
                            OnSuccessListener<Boolean> listener) {
        HashMap<String, Object> data = new HashMap<>();
        data.put("name", name);
        data.put("email", email);
        data.put("phone", phone);
        CollectionReference coll = db.collection("facilities");
        ImageController imgControl = new ImageController();
        if (picture != null) {
            imgControl.saveImage(context, picture, "facility picture")
                    .addOnSuccessListener(urlAndID -> {
                data.put("pictureURL", urlAndID.first);
                coll.add(data).addOnSuccessListener(documentReference -> {
                    imgControl.updateImageRef(urlAndID.second, documentReference.getId());
                    HashMap<String, Object> userData = new HashMap<>();
                    userData.put("facilityID", documentReference.getId());
                    new FirebaseController().updateUserByDeviceId(deviceID, userData, success -> {
                        if (success) {
                            listener.onSuccess(Boolean.TRUE);
                        }
                    });
                }
                );
            });
        } else {
            data.put("pictureURL", null);
            coll.add(data).addOnSuccessListener(documentReference -> {
                HashMap<String, Object> userData = new HashMap<>();
                userData.put("facilityID", documentReference.getId());
                new FirebaseController().updateUserByDeviceId(deviceID, userData, success -> {
                    if (success) {
                        listener.onSuccess(Boolean.TRUE);
                    }
                });
            });
        }
    }

    /**
     * Updates an existing facility's information.
     * Handles updating or removing facility pictures and other facility data.
     *
     * @param context Application context for image handling
     * @param deviceID The device ID of the user editing the facility
     * @param data HashMap containing the fields to update and their new values
     * @param listener Callback to handle the success/failure of the operation
     */
    public void editFacility(Context context, String deviceID, HashMap<String, Object> data,
                             OnSuccessListener<Boolean> listener) {
        Uri picture = (Uri) data.get("pictureUri");
        CollectionReference coll = db.collection("facilities");
        ImageController imgControl = new ImageController();
        getUserFacilityDetails(deviceID, facility -> {
            if (picture != null) {
                if (facility.getPictureURL() == null) {
                    imgControl.saveImage(context, picture, "facility picture")
                        .addOnSuccessListener(urlAndID -> {
                            data.put("pictureURL", urlAndID.first);
                            coll.document(facility.getID()).update(data).addOnSuccessListener(documentReference -> {
                                    imgControl.updateImageRef(urlAndID.second, facility.getID());
                                    listener.onSuccess(Boolean.TRUE);
                                }
                            );
                        });
                } else {
                    imgControl.deleteImageAndUpdateRelatedDoc(picture.toString(), null, facility.getID(), success -> {
                        if (success) {
                            imgControl.saveImage(context, picture, "facility picture")
                                    .addOnSuccessListener(urlAndID -> {
                                        data.put("pictureURL", urlAndID.first);
                                        coll.document(facility.getID()).update(data).addOnSuccessListener(documentReference -> {
                                                    imgControl.updateImageRef(urlAndID.second, facility.getID());
                                                    listener.onSuccess(Boolean.TRUE);
                                                }
                                        );
                                    });
                        }
                    });
                }
            } else {
                data.put("pictureURL", null);
                coll.document(facility.getID()).update(data).addOnSuccessListener(documentReference -> {
                    listener.onSuccess(Boolean.TRUE);
                });
            }
        });
    }

    /**
     * Deletes a facility and removes the facility ID from the associated user.
     *
     * @param deviceID The device ID of the user whose facility should be deleted
     * @param listener Callback to handle the success/failure of the deletion
     */
    public void deleteFacility(String deviceID, OnSuccessListener<Boolean> listener) {
        fetchUserByDeviceId(deviceID, new OnUserFetchedListener() {
            @Override
            public void onUserFetched(User user) {
                if (!user.getFacilityID().isEmpty()) {
                    db.collection("facilities").document(user.getFacilityID()).delete()
                            .addOnSuccessListener(v -> {
                        HashMap<String, Object> data = new HashMap<>();
                        data.put("facilityID", "");
                        updateUserByDeviceId(deviceID, data, success -> {
                            if (success) {
                                listener.onSuccess(Boolean.TRUE);
                            }
                        });
                    });
                }
            }

            @Override
            public void onError(Exception e) {

            }
        });
    }
}
