package com.gregmcgowan.drownedinsound.ui.fragments;

import android.os.Bundle;

import com.actionbarsherlock.app.SherlockFragment;
import com.gregmcgowan.drownedinsound.annotations.UseDagger;
import com.gregmcgowan.drownedinsound.annotations.UseEventBus;
import com.gregmcgowan.drownedinsound.core.DisBoardsApp;

import java.lang.annotation.Annotation;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

public class DisBoardsFragment extends SherlockFragment {

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

        if(containsAnnotation(UseDagger.class)) {
            DisBoardsApp.getApplication(getActivity()).inject(this);
        }

        if(containsAnnotation(UseEventBus.class)){
            eventBus.register(this);
        }
    }

    private boolean containsAnnotation(Class<? extends Annotation> annotationType){
        return ((Object) this).getClass().getAnnotation(annotationType) != null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(containsAnnotation(UseEventBus.class)){
            if(eventBus != null) {
                eventBus.unregister(this);
            }
        }
    }
}
