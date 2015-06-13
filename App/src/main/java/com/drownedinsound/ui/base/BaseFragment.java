package com.drownedinsound.ui.base;

import com.drownedinsound.annotations.UseDagger;
import com.drownedinsound.annotations.UseEventBus;
import com.drownedinsound.core.DisBoardsApp;
import com.drownedinsound.data.network.DisApiClient;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;

import java.lang.annotation.Annotation;
import java.lang.ref.WeakReference;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;
import timber.log.Timber;

public class BaseFragment extends Fragment implements Ui {

    private static final long WAIT_TIME_FOR_LOADING_VIEW = 500;
    private static final int MESSAGE_SHOW_LOADING_VIEW = 1;
    private static final int MESSAGE_HIDE_LOADING_VIEW = 2;

    protected static final int FADE_IN_OUT_LOADING_VIEW_ANIMATION_DURATION_MS = 500;

    protected Handler fragmentHander;

    @Inject
    protected EventBus eventBus;

    protected DisApiClient disApiClient;

    protected Handler loadingViewHandler;

    /**
     * Checks if this fragment is attached to a activity
     */
    public boolean isValid() {
        return getActivity() != null;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadingViewHandler = new LoadingViewHandler(new WeakReference<>(this));
        fragmentHander = new Handler();

        Timber.d("Get activity = "+ (getActivity()));

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

    protected DisApiClient getDisApiClient(){
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
    }


    public void requestToShowLoadingView() {
        if (loadingViewHandler != null) {
            Timber.d("Sending request to show loading view");
            loadingViewHandler.sendMessageDelayed(
                    loadingViewHandler.obtainMessage(MESSAGE_SHOW_LOADING_VIEW),
                    WAIT_TIME_FOR_LOADING_VIEW);
        }
    }

    public void showLoadingView(final IBinder hideSoftKeyboardToken) {
    }

    public void requestToHideLoadingView() {
        if (loadingViewHandler != null) {
            Timber.d("Sending request to hide loading view");
            loadingViewHandler.obtainMessage(MESSAGE_HIDE_LOADING_VIEW)
                    .sendToTarget();
        }
    }

    public void hideLoadingView() {
    }


    /**
     * This can be overridden by subclasses to provide a token to ensure the
     * softkeyboard is closed
     *
     * @return
     */
    protected IBinder getCloseSoftKeyboardToken() {
        return null;
    }


    protected int getUIIdentifier() {
        return this.hashCode();
    }

    private class LoadingViewHandler extends Handler {

        private WeakReference<BaseFragment> baseFragmentWeakReference;

        public LoadingViewHandler(WeakReference<BaseFragment> baseFragmentWeakReference) {
            this.baseFragmentWeakReference = baseFragmentWeakReference;
        }

        @Override
        public void handleMessage(android.os.Message msg) {
            if (baseFragmentWeakReference != null) {
                BaseFragment baseFragment = baseFragmentWeakReference.get();
                if (baseFragment != null) {
                    if (msg.what == MESSAGE_SHOW_LOADING_VIEW) {
                        Timber.d("Going to show loading view");
                        baseFragment.showLoadingView(baseFragment.getCloseSoftKeyboardToken());
                    } else {
                        Timber.d("Going to hide loading view");
                        removeMessages(MESSAGE_SHOW_LOADING_VIEW);
                        baseFragment.hideLoadingView();
                    }
                }
            }

        }
    }


}
