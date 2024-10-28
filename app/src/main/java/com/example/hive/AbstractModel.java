package com.example.hive;

import androidx.collection.ArraySet;

import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;
import java.util.Set;

/**
 * AbstractModel class
 * - Subclass AbstractModel to create a model that updates views and communicates
 * with firestore database
 *
 * @param <T>
 * The object that this model deals with
 *
 * @author Zach
 */
public abstract class AbstractModel<T> {

    /**
     * The set of views handled by this model
     */
    private final Set<AbstractView<T>> views;
    /**
     * The instance of <code>FirebaseManager</code> used to communicate with firestore
     */
    private final FirebaseManager dbManager;

    /**
     * Constructor class - creates a new <code>ArraySet</code> to hold the views that this model
     * updates, and gets the instance of <code>FirebaseManager</code>
     */
    protected AbstractModel() {
        this.views = new ArraySet<AbstractView<T>>();
        this.dbManager = FirebaseManager.getInstance();
    }

    /**
     * Adds given view to the set of views and calls the view's <code>update()</code> method to
     * ensure it is consistent with the data
     *
     * @param view
     * The view to add to the set
     */
    public void addView(AbstractView<T> view) {
        views.add(view);
        view.update(this);
    }

    /**
     * Removes the given view from the set
     *
     * @param view
     * The view to remove
     */
    public void removeView(AbstractView<T> view) {
        views.remove(view);
    }

    /**
     * Provides the entire set of views
     *
     * @return
     * The set of views
     */
    protected Set<AbstractView<T>> getViews() {
        return this.views;
    }

    /**
     * Provides the <code>FirebaseManager</code> instance
     *
     * @return
     * The FirebaseManager
     */
    protected FirebaseManager getManager() {
        return this.dbManager;
    }

    /**
     * Notify the views that the model has updated. Goes through set of views and calls the
     * <code>update()</code> method on each one.
     */
    public void notifyViews() {
        for (AbstractView<T> view : views) {
            view.update(this);
        }
    }

    /**
     * Abstract class that must be implemented in each concrete model. Gets all documents from
     * the firestore database and then calls the provided function
     *
     * @param callback
     * The function to run after all data has been retrieved.
     */
    public abstract void getAllFromDB(OnSuccessListener<ArrayList<T>> callback);

}
