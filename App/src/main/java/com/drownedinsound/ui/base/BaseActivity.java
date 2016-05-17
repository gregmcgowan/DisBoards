package com.drownedinsound.ui.base;

import com.drownedinsound.R;
import com.drownedinsound.core.DisBoardsApp;
import com.drownedinsound.core.SessionComponent;
import com.drownedinsound.utils.EspressoIdlingResource;

import android.os.Bundle;
import android.support.annotation.VisibleForTesting;
import android.support.test.espresso.IdlingResource;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;


/**
 * Created by gregmcgowan on 19/10/2014.
 */
public abstract class BaseActivity extends AppCompatActivity implements Ui {

    protected Toolbar toolbar;

    protected FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getLayoutResource() != 0) {
            setContentView(getLayoutResource());
        }

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

        fragmentManager = getSupportFragmentManager();

        onSessionComponentCreated(DisBoardsApp.getApplication(this).getSessionComponent());
    }

    protected abstract void onSessionComponentCreated(SessionComponent sessionComponent);

    protected abstract int getLayoutResource();

    public Toolbar getToolbar() {
        return toolbar;
    }

    @VisibleForTesting
    public IdlingResource getCountingIdlingResource() {
        return EspressoIdlingResource.getIdlingResource();
    }

    @Override
    public boolean isBeingDestroyed() {
        return isFinishing();
    }

    @Override
    public int getID() {
        return hashCode();
    }
}
