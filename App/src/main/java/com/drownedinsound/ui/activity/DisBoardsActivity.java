package com.drownedinsound.ui.activity;
;
import com.drownedinsound.R;
import com.drownedinsound.annotations.UseDagger;
import com.drownedinsound.annotations.UseEventBus;
import com.drownedinsound.core.DisBoardsApp;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;

import java.lang.annotation.Annotation;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

/**
 * Created by gregmcgowan on 19/10/2014.
 */
public abstract class DisBoardsActivity extends ActionBarActivity {


    protected Toolbar toolbar;

    protected FragmentManager fragmentManager;

    @Inject
    protected EventBus eventBus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getLayoutResource() != 0) {
            setContentView(getLayoutResource());
        }

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        }

        fragmentManager = getSupportFragmentManager();

        if (containsAnnotation(UseDagger.class)
                || containsAnnotation(UseEventBus.class)) {
            DisBoardsApp.getApplication(this).inject(this);
        }

        if (containsAnnotation(UseEventBus.class)) {
            eventBus.register(this);
        }
    }


    protected abstract int getLayoutResource();

    private boolean containsAnnotation(Class<? extends Annotation> annotationType) {
        return ((Object) this).getClass().getAnnotation(annotationType) != null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (containsAnnotation(UseEventBus.class)) {
            if (eventBus != null) {
                eventBus.unregister(this);
            }
        }
    }
}
