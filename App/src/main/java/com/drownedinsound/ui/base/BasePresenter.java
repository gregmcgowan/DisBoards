package com.drownedinsound.ui.base;

import android.support.annotation.NonNull;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Scheduler;

/**
 * Created by gregmcgowan on 19/05/2016.
 */
public class BasePresenter {

    private static final boolean DEBUG = true;

    private final Set<Ui> mUis;

    private Scheduler mainThreadScheduler;

    private Scheduler backgroundThreadScheduler;

    private Display display;

    private long observerableTimeoutSeconds = 10L;

    public BasePresenter(Scheduler mainThreadScheduler, Scheduler backgroundThreadScheduler) {
        mUis = new CopyOnWriteArraySet<>();
        this.mainThreadScheduler = mainThreadScheduler;
        this.backgroundThreadScheduler = backgroundThreadScheduler;

    }

    @SuppressWarnings("unchecked")
    public <T> Observable.Transformer<T, T> defaultTransformer() {
        return new Observable.Transformer<T, T>() {
            @Override
            public Observable<T> call(Observable<T> observable) {
                return observable.subscribeOn(backgroundThreadScheduler)
                        .observeOn(mainThreadScheduler);
            }
        };
    }

    protected Scheduler getMainThreadScheduler() {
        return mainThreadScheduler;
    }

    protected Scheduler getBackgroundThreadScheduler() {
        return backgroundThreadScheduler;
    }

    public synchronized final void uiCreated(@NonNull Ui ui) {
        mUis.add(ui);
        onUiCreated(ui);
    }

    public void onUiCreated(Ui ui) {

    }

    public void setObserverableTimeoutSeconds(long observerableTimeoutSeconds) {
        this.observerableTimeoutSeconds = observerableTimeoutSeconds;
    }

    /**
     * Attaches the UI to this controller and resumes any unfinished subscriptions previously
     * added by {@see subscribeAndCache}
     *
     * @param ui
     */
    public synchronized final void attachUi(@NonNull Ui ui) {
        mUis.add(ui);
        onUiAttached(ui);
    }

    public void onUiAttached(Ui ui) {

    }

    protected int getId(Ui ui) {
        return ui.getID();
    }


    public synchronized final void detachUi(@NonNull Ui ui) {
        onUiDetached(ui);
        mUis.remove(ui);
    }

    protected synchronized Ui findUi(final int id) {
        for (Ui ui : mUis) {
            if (getId(ui) == id) {
                return ui;
            }
        }
        return null;
    }

    protected Set<Ui> getUis() {
        return Collections.unmodifiableSet(mUis);
    }

    protected <T extends Ui> T findUi(Class<T> uiClss) {
        for (Ui ui : mUis) {
            if (uiClss.isInstance(ui)) {
                return (T) ui;
            }
        }
        return null;
    }


    public void onPause() {

    }

    public void onDestroy() {

    }

    public void onUiDetached(Ui ui) {

    }

    public void attachDisplay(Display display) {
        this.display = display;
    }

    public void detachDisplay(Display display){
        this.display = null;
    }

    public Display getDisplay() {
        return display;
    }




}
