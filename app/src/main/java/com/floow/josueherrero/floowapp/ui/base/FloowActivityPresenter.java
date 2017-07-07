package com.floow.josueherrero.floowapp.ui.base;

import android.os.Bundle;

/**
 * Created by JosuéManuel on 25/06/2017.
 */

public interface FloowActivityPresenter<T extends FloowActivityPresenter.FloowView> {

    void attach(T view);

    void detach(T view);

    void saveState(Bundle bundle);

    void recoverState(Bundle bundle);

    void onViewAttached();

    void setView(T view);

    interface FloowView {


    }

}