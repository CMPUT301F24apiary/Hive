package com.example.hive.Controllers;

import com.google.firebase.firestore.FirebaseFirestore;

public class InvitedController extends FirebaseController {

    private FirebaseFirestore db;

    public InvitedController() {
        super();
        this.db = getDb();
    }

}
