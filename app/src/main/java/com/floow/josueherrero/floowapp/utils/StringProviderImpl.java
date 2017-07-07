package com.floow.josueherrero.floowapp.utils;

import android.content.Context;

/**
 * Created by JosuéManuel on 05/07/2017.
 *
 * This provider avoids to have the context in the presenter.
 */

public final class StringProviderImpl implements StringProvider {

    private Context context;

    public StringProviderImpl(final Context context) {
        this.context = context;
    }

    @Override
    public String getString(final int id) {
        return context.getString(id);
    }

}
