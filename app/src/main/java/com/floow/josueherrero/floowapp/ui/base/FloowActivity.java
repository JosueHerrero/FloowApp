package com.floow.josueherrero.floowapp.ui.base;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v7.app.AppCompatActivity;

import com.floow.josueherrero.floowapp.R;

import butterknife.ButterKnife;

/**
 * Created by JosuéManuel on 25/06/2017.
 *
 * This is the base Activity for our application, it controls the attach/detach of the view.
 */

public abstract class FloowActivity<P extends FloowActivityPresenter> extends AppCompatActivity implements FloowActivityPresenter.FloowView {

    private static final String TAG = FloowActivity.class.getSimpleName();

    public FloowActivity() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getActivityLayout());
        ButterKnife.bind(this);
        this.getPresenter().recoverState(savedInstanceState);
    }

    @Override
    public void onResume() {
        this.getPresenter().attach(this);
        super.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        this.getPresenter().saveState(outState);
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.getPresenter().detach(this);
    }

    protected abstract P getPresenter();

    @LayoutRes
    protected int getActivityLayout() {
        return R.layout.activity_bar_location;
    }

}