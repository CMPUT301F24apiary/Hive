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

public class FacilityController extends FirebaseController {

    private FirebaseFirestore db;

    public FacilityController() {
        super();
        this.db = super.getDb();
    }

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
