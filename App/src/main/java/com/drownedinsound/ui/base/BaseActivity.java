package com.drownedinsound.ui.base;

import com.drownedinsound.R;
import com.drownedinsound.core.DisBoardsApp;

import android.app.FragmentManager;
import android.os.Bundle;
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

        fragmentManager = getFragmentManager();

        DisBoardsApp.getApplication(this).inject(this);
    }

    protected abstract int getLayoutResource();

    public Toolbar getToolbar() {
        return toolbar;
    }
}
