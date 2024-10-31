package com.example.hive;

import android.app.Activity;

public class AdminEventController extends AbstractController<TestEvent> {
    private AdminEventDetailActivity activity;

    /**
     * Constructor class sets the model variable to model provided.
     *
     * @param model The model to send updates to.
     */
    public AdminEventController(AbstractModel<TestEvent> model, AdminEventDetailActivity activity) {
        super(model);
        this.activity = activity;
    }

    public void handleEventDeletion(String eventID) {
        getModel().deleteSingleFromDB(eventID, activity::onDelete);
    }

}
