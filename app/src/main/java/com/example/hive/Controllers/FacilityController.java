package com.example.hive.Controllers;

import com.example.hive.Models.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class FacilityController extends FirebaseController {

    private FirebaseFirestore db;

    public FacilityController() {
        super();
        this.db = super.getDb();
    }

    private void getUserFacility(String deviceId, OnSuccessListener<String> listener) {
        fetchUserByDeviceId(deviceId, new OnUserFetchedListener() {
            @Override
            public void onUserFetched(User user) {
                db.collection("facilities").get(user.facilityId).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        listener.onSuccess(queryDoc);
                    }
                })
            }

            @Override
            public void onError(Exception e) {

            }
        });
    }
}
