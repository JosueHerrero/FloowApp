package com.floow.josueherrero.floowapp.ui.base;

import android.os.Bundle;

import org.parceler.Parcels;

/**
 * Created by JosuéManuel on 25/06/2017.
 *
 * This is the base presenter for our application, it manages the view attach and the state recovery.
 */

public abstract class FloowActivityPresenterImpl<S extends FloowActivityPresenterImpl.State, V extends FloowActivityPresenter.FloowView> implements FloowActivityPresenter<V> {

    private final static String KEY_STATE = "key:state";

    protected S state;
    protected V view;

    public FloowActivityPresenterImpl() {
    }

    public void saveState(Bundle outState) {
        outState.putParcelable("key:state", Parcels.wrap(this.state));
    }

    public void recoverState(Bundle savedInstanceState) {
        if (savedInstanceState != null
                && savedInstanceState.containsKey(KEY_STATE)) {

            this.state = (S) Parcels.unwrap(savedInstanceState.getParcelable(KEY_STATE));
        } else {
            this.state = this.newStateInstance();
        }

        if (this.state == null) {
            throw new IllegalStateException("Your presenter " + this.getClass().getSimpleName() + " returns a null object in newStateInstance().");
        }
    }

    public final void attach(V view) {
        this.view = view;
        this.onViewAttached();
    }

    public final void detach(V view) {
        if(this.view.equals(view)) {
            this.beforeViewDetached();
            this.view = null;
        } else {
            throw new IllegalArgumentException();
        }
    }

    public void onViewAttached() {
    }

    public void beforeViewDetached() {}

    public S getState() {
        return this.state;
    }

    public void setState(S state) {
        this.state = state;
    }

    public void setView(V view) {
        this.view = view;
    }

    protected final boolean hasView() {
        return this.view != null;
    }

    protected final V getView() {
        return this.view;
    }


    protected abstract S newStateInstance();


    public interface State {
    }

}