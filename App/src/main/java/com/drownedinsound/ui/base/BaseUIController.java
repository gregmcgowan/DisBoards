package com.drownedinsound.ui.base;

import android.support.annotation.NonNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import timber.log.Timber;

/**
 * Created by gregmcgowan on 22/03/15.
 */
public abstract class BaseUIController {

    private final Set<Ui> mUis;

    private ConcurrentHashMap<Integer,HashMap<Object,CachedPair>> observablesCache = new ConcurrentHashMap<>();
    private ConcurrentHashMap<Integer,HashMap<Object,Subscription>> subscriptions = new ConcurrentHashMap<>();

    public BaseUIController() {
        mUis = new CopyOnWriteArraySet<>();
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
        resumeObservables(ui);
    }

    public void onUiAttached(Ui ui) {

    }

    protected void resumeObservables(@NonNull Ui ui) {
        int uiId = getId(ui);
        HashMap<Object, CachedPair> idObservables
                = getCachedObservables(uiId);

        Timber.d("Number of cached observables " + idObservables.size() + " for " + ui);

        for (Object tag : idObservables.keySet()) {
            CachedPair cachedPair = idObservables.get(tag);
            if (cachedPair != null) {
                if (!hasSubscription(ui,tag)) {
                    subscribe(ui, tag, cachedPair.getObserver(), cachedPair.getObservable());
                }
            }
        }
    }

    protected int getId(Ui ui) {
        return ui.getId();
    }

    protected synchronized HashMap<Object,CachedPair> getCachedObservables(int uiId) {
        HashMap<Object,CachedPair> idObservables
                = observablesCache.get(uiId);
        if(idObservables == null) {
            idObservables = new HashMap<>();
        }
        return idObservables;
    }

    private <T> void subscribe(Ui ui, Object tag, Observer<T> observer, Observable<T> observable) {
        Timber.d("Creating subscription for " + ui + " for observable " + tag);
        addSubscription(ui, tag, observable.subscribe(observer));
    }

    protected void addSubscription(@NonNull Ui ui, @NonNull Object tag, @NonNull Subscription subscription){
        HashMap<Object,Subscription> subscriptionHashMap = getSubscriptions(ui);
        subscriptionHashMap.put(tag, subscription);
        int uiId = getId(ui);
        subscriptions.put(uiId, subscriptionHashMap);
    }

    protected HashMap<Object,Subscription> getSubscriptions(@NonNull Ui ui) {
        int uiId = getId(ui);
        HashMap<Object,Subscription> subscriptionHashMap = subscriptions.get(uiId);
        if(subscriptionHashMap == null) {
            subscriptionHashMap = new HashMap<>();
        }
        return subscriptionHashMap;
    }

    public synchronized final void detachUi(@NonNull Ui ui) {
        onUiDetached(ui);

        removeSubscriptions(ui);

        if(ui.isBeingDestroyed()) {
            removeCached(ui);
        }
        mUis.remove(ui);
    }


    /**
     * Creates a subscription for the observer and observable to update the ui element. The subscription, observer
     * and observable are cached so that if the UI is unattached it can be resumed when the UI is reattached.
     * This does not check if the there is an existing subscription for the observer and observable.
     *
     * @param ui the UI element to be updated by
     * @param tag a unique identifier for the observable
     * @param observer  observer of the data.
     * @param observable the omitter of data
     * @param <T> the data type that the ui should be updated with
     */
    public <T> void subscribeAndCache(@NonNull Ui ui, @NonNull Object tag, @NonNull Observer<T> observer, @NonNull Observable<T> observable) {
        int uiId = getId(ui);
        HashMap<Object,CachedPair> idObservables
                = getCachedObservables(uiId);

        observable = observable.cache();

        WrappedObserver<T> wrappedObserver = new WrappedObserver<>(tag,uiId,observer);

        idObservables.put(tag, new CachedPair<>(observable, wrappedObserver));
        observablesCache.put(uiId, idObservables);

        subscribe(ui, tag, wrappedObserver, observable);
    }

    public boolean hasSubscription(@NonNull Ui ui, @NonNull Object tag) {
        HashMap<Object,Subscription> subscriptionHashMap = getSubscriptions(ui);
        boolean hasSubscription =  subscriptionHashMap.containsKey(tag);

        Timber.d(tag + " for " + ui + (hasSubscription ? " has " : " does not have ")
                + "a subscription");

        return hasSubscription;
    }

    public boolean hasSubscription(@NonNull Ui ui) {
        HashMap<Object,Subscription> subscriptionHashMap = getSubscriptions(ui);
        boolean hasSubscriptions = subscriptionHashMap.size() > 0;

        Timber.d(ui + (hasSubscriptions ? " has " : " does not have") + " subscriptions");

        return hasSubscriptions;
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

    private void removeCached(Ui ui) {
        int uiId = getId(ui);

        observablesCache.remove(uiId);
    }

    protected void removeSubscriptions(@NonNull Ui ui) {
        int uiId = getId(ui);
        HashMap<Object,Subscription> subscriptionHashMap = getSubscriptions(ui);
        for(Subscription subscription : subscriptionHashMap.values()) {
            if(subscription != null && !subscription.isUnsubscribed()) {
                Timber.d("unsubscribing for " + ui);
                subscription.unsubscribe();
            }
        }
        subscriptions.remove(uiId);
    }


    protected void removeSubscription(@NonNull Ui ui, @NonNull Object tag) {
        HashMap<Object, Subscription> subscriptionHashMap = getSubscriptions(ui);
        Subscription subscription = subscriptionHashMap.remove(tag);
        if (subscription != null && !subscription.isUnsubscribed()) {
            Timber.d("Unsubscribing " + tag + " for " + ui);
            subscription.unsubscribe();
        } else {
            Timber.d("Already un-subscribed " + tag + " for " + ui);
        }
    }

    private void removeCached(Ui ui, Object tag) {
        int uiId = getId(ui);

        HashMap<Object,CachedPair> idObservables
                = getCachedObservables(uiId);

        boolean removed = idObservables.remove(tag) != null;
        Timber.d((removed ? "Removed" : "Did not remove") + " observable for " +tag);
    }
    /**
     * Base class for UI observers. Provides method to get the UI element it is
     * associated with
     *
     * @param <T> the data type that is being observed
     * @param <U> the UI type that is being updated with the data type
     */
    protected abstract class BaseObserver<T,U extends Ui> extends Subscriber<T> {

        private int uiID;

        protected BaseObserver(int uiID) {
            this.uiID = uiID;
        }

        protected U getUI() {
            return (U)findUi(uiID);
        }

        public void setUiID(int uiID) {
            this.uiID = uiID;
        }

        @Override
        public void onCompleted() {

        }
    }

    /**
     * Used internally for the controller. This will remove the cached subscription and observerables
     * if a result or error is returned
     *
     * @param <T>
     */
    private class WrappedObserver <T> implements Observer <T>  {

        private Object tag;
        private int uiID;
        private Observer<T> observer;

        private WrappedObserver(Object tag, int uiID, Observer<T> observer) {
            this.tag = tag;
            this.uiID = uiID;
            this.observer = observer;
        }

        @Override
        public void onCompleted() {
            if(observer != null) {
                observer.onCompleted();
            }
        }

        @Override
        public void onError(Throwable e) {
            Ui ui = findUi(uiID);

            Timber.d("OnError "+e + " for "+ ui);
            removeCached(ui,tag);
            removeSubscription(ui,tag);

            if(observer != null) {
                observer.onError(e);
            }
        }

        @Override
        public void onNext(T t) {
            Ui ui = findUi(uiID);

            Timber.d("OnNext "+t + " for "+ ui);

            removeCached(ui,tag);
            removeSubscription(ui,tag);

            if(observer != null) {
                observer.onNext(t);
            }
        }
    }

    private static class CachedPair <T> {

        private CachedPair(Observable<T> observable, WrappedObserver<T> observer) {
            this.observable = observable;
            this.observer = observer;
        }

        private Observable<T> observable;
        private WrappedObserver<T>  observer;

        public Observable<T> getObservable() {
            return observable;
        }

        public WrappedObserver<T>  getObserver() {
            return observer;
        }
    }
}
