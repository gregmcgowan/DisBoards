package com.drownedinsound.ui.fragments;

import com.actionbarsherlock.app.SherlockFragment;
import com.drownedinsound.annotations.UseDagger;
import com.drownedinsound.annotations.UseEventBus;
import com.drownedinsound.core.DisBoardsApp;

import android.os.Bundle;
import android.os.Handler;

import java.lang.annotation.Annotation;


import javax.inject.Inject;

import de.greenrobot.event.EventBus;
import timber.log.Timber;

public class DisBoardsFragment extends SherlockFragment {

    protected Handler fragmentHander;

    @Inject
    protected EventBus eventBus;

    /**
     * Checks if this fragment is attached to a activity
     */
    public boolean isValid() {
        return getSherlockActivity() != null;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fragmentHander = new Handler();

        Timber.d("Get activity = "+ (getSherlockActivity()));

        if (containsAnnotation(UseDagger.class) || containsAnnotation(UseEventBus.class)) {
            DisBoardsApp.getApplication(getSherlockActivity()).inject(this);
        }

        if (containsAnnotation(UseEventBus.class)) {
            eventBus.register(this);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

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
