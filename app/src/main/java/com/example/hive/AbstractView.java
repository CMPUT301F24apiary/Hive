package com.example.hive;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Subclass this class to create a view that communicates with its model to stay updated, and
 * keeps its activity updated
 *
 * @param <T>
 * The object that this class deals with
 *
 * @author Zach
 */
public abstract class AbstractView<T> {

    /**
     * Model that updates the view
     */
    private AbstractModel<T> model;

    /**
     * Method to start observing the changes from the model
     *
     * @param model
     * The model to observe. This model will update the view whenever it changes.
     */
    public void startObserving(AbstractModel<T> model) {
        if (this.model != null) {
            throw new RuntimeException("Can't view two models!");
        }
        this.model = model;
        model.addView(this);
    }

    /**
     * Removes view from the model and resets this view's model variable.
     */
    public void closeView() {
        model.removeView(this);
        this.model = null;
    }

    /**
     * Abstract method to be implemented in concrete view class. Updates the view with data from the
     * model.
     *
     * @param whoUpdated
     * The model that updated the view.
     */
    public abstract void update(AbstractModel<T> whoUpdated);

    /**
     * Provides the model that this view is subscribed to.
     *
     * @return
     * The model that this view is related to.
     */
    public AbstractModel<T> getModel() {
        return this.model;
    }

}
