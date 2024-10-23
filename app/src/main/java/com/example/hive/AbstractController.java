package com.example.hive;

/**
 * Subclass this class to create a concrete controller that relays user action to the model.
 *
 * @param <T>
 * The object that this controller is related to.
 *
 * @author Zach
 */
public abstract class AbstractController<T> {

    /**
     * The model that this controller sends updates to.
     */
    private final AbstractModel<T> model;

    /**
     * Constructor class sets the model variable to model provided.
     *
     * @param model
     * The model to send updates to.
     */
    public AbstractController(AbstractModel<T> model) {
        this.model = model;
    }

    /**
     * Provides model that this controller updates.
     *
     * @return
     * Related model.
     */
    public AbstractModel<T> getModel() {
        return this.model;
    }

}
