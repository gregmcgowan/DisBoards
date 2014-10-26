package com.drownedinsound.ui.activity;

import com.drownedinsound.annotations.UseDagger;
import com.drownedinsound.annotations.UseEventBus;
import com.drownedinsound.core.DisBoardsApp;

import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;

import java.lang.annotation.Annotation;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

/**
 * Created by gregmcgowan on 19/10/2014.
 */
public class DisBoardsActivity extends Activity {

    protected FragmentManager fragmentManager;

    @Inject
    protected EventBus eventBus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fragmentManager = getFragmentManager();

        if (containsAnnotation(UseDagger.class)
                || containsAnnotation(UseEventBus.class)) {
            DisBoardsApp.getApplication(this).inject(this);
        }

        if (containsAnnotation(UseEventBus.class)) {
            eventBus.register(this);
        }
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
