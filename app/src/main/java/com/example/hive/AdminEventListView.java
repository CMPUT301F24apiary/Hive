package com.example.hive;

/**
 * View for the event list.
 *
 * @author Zach
 */
public class AdminEventListView extends AbstractView<TestEvent> {

    /**
     * The activity which is using this view.
     */
    private final AdminEventListActivity activity;

    /**
     * Constructor class sets the activity and starts observing provided model.
     *
     * @param model
     * The model to observe.
     * @param activity
     * The activity which created this view.
     */
    public AdminEventListView(AdminEventListModel model, AdminEventListActivity activity) {
        this.activity = activity;
        this.startObserving(model);
    }

    /**
     * Updates the view using the model's getAllFromDB method. Uses activity's updateList method
     * as the callback function.
     *
     * @param model
     * The model that updated the view.
     */
    @Override
    public void update(AbstractModel<TestEvent> model) {
        // Use the callback to update the list once the data is available
        model.getAllFromDB(activity::updateList);
    }

}
