package com.drownedinsound.ui.base;

import rx.Notification;
import rx.Observable;
import rx.functions.Func2;

/**
 * Created by gregmcgowan on 21/05/2016.
 */
public class EventUtils {

    public static  <D> Observable.Transformer<D,Event<D>> transformToEvent() {
        return new Observable.Transformer<D, Event<D>>() {
            @Override
            public Observable<Event<D>> call(
                    Observable<D> loginResponseObservable) {
                return loginResponseObservable
                        .materialize()
                        .scan(new Event<D>(null, Status.LOADING, null), new Func2<Event<D>, Notification<D>, Event<D>>() {
                            @Override
                            public Event<D> call(
                                    Event<D> dataEvent,
                                    Notification<D> loginResponseNotification) {
                                if (loginResponseNotification.isOnNext()) {
                                    return new Event<>(loginResponseNotification.getValue(),
                                            Status.IDLE, null);
                                } else if (loginResponseNotification.isOnError()) {
                                    return new Event<>(dataEvent.getData(),
                                            Status.ERROR, loginResponseNotification.getThrowable());
                                } else if (loginResponseNotification.isOnCompleted()) {
                                    return new Event<>(dataEvent.getData(),
                                            Status.IDLE, null);
                                } else {
                                    return null;
                                }
                            }
                        })
                        .startWith((new Event<D>(null, Status.LOADING,null)));
            }
        };
    }
}
