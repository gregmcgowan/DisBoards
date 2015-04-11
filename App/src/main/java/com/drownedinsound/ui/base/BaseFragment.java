package com.drownedinsound.ui.base;

import com.drownedinsound.annotations.UseDagger;
import com.drownedinsound.annotations.UseEventBus;
import com.drownedinsound.core.DisBoardsApp;
import com.drownedinsound.data.network.DisApiClient;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;

import java.lang.annotation.Annotation;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;
import timber.log.Timber;

public class BaseFragment extends Fragment {

    protected Handler fragmentHander;

    @Inject
    protected EventBus eventBus;

    DisApiClient disApiClient;

    /**
     * Checks if this fragment is attached to a activity
     */
    public boolean isValid() {
        return getActivity() != null;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fragmentHander = new Handler();

        Timber.d("Get activity = " + (getActivity()));

        if (containsAnnotation(UseDagger.class) || containsAnnotation(UseEventBus.class)) {
            DisBoardsApp.getApplication(getActivity()).inject(this);
        }

        if (containsAnnotation(UseEventBus.class)) {
            eventBus.register(this);
        }

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        disApiClient = DisBoardsApp.getApplication(getActivity()).getDisApiClient();
    }

    private boolean containsAnnotation(Class<? extends Annotation> annotationType) {
        return ((Object) this).getClass().getAnnotation(annotationType) != null;
    }

    protected DisApiClient getDisApiClient() {
        return disApiClient;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (containsAnnotation(UseEventBus.class)) {
            if (eventBus != null) {
                eventBus.unregister(this);
            }
        }

//        if(disApiClient != null) {
//            disApiClient.onDestroy();
//        }
    }
}
