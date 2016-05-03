package com.drownedinsound.ui.base;

import com.drownedinsound.core.DisBoardsApp;
import com.drownedinsound.core.SessionComponent;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;

import java.lang.ref.WeakReference;

import timber.log.Timber;

public abstract class BaseFragment extends Fragment implements Ui {

    private static final long WAIT_TIME_FOR_LOADING_VIEW = 500;

    private static final int MESSAGE_SHOW_LOADING_VIEW = 1;

    private static final int MESSAGE_HIDE_LOADING_VIEW = 2;

    protected static final int FADE_IN_OUT_LOADING_VIEW_ANIMATION_DURATION_MS = 500;

    protected Handler fragmentHander;

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

        onSessionComponentCreated(DisBoardsApp.getApplication(getActivity()).getSessionComponent());
    }

    protected abstract void onSessionComponentCreated(SessionComponent sessionComponent);

    @Override
    public void onDestroy() {
        super.onDestroy();
        Timber.d("BaseFragment onDestroy");
        loadingViewHandler.removeMessages(MESSAGE_SHOW_LOADING_VIEW);
        loadingViewHandler.removeMessages(MESSAGE_HIDE_LOADING_VIEW);
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
     */
    protected IBinder getCloseSoftKeyboardToken() {
        return null;
    }


    @Override
    public boolean isBeingDestroyed() {
        return isDetached() || isRemoving();
    }

    @Override
    public int getID() {
        return hashCode();
    }

    private static class LoadingViewHandler extends Handler {

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
                        Timber.d("Going to hide loading view has messages to view " + hasMessages(
                                MESSAGE_SHOW_LOADING_VIEW));
                        removeMessages(MESSAGE_SHOW_LOADING_VIEW);
                        baseFragment.hideLoadingView();
                    }
                }
            }

        }
    }


}
