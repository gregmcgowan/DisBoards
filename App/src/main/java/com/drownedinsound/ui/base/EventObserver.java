package com.drownedinsound.ui.base;

import rx.Subscriber;

/**
 * Created by gregmcgowan on 21/05/2016.
 */
public abstract class EventObserver <T> extends Subscriber<Event<T>> {

    @Override
    public void onCompleted() {

    }

    @Override
    public void onError(Throwable e) {

    }

    @Override
    public void onNext(Event<T> event) {
        switch (event.getStatus()) {
            case LOADING:
                loading(event.getData());
                break;
            case IDLE:
                idle(event.getData());
                break;
            case ERROR:
                error(event.getError());
                break;
        }

    }


    public abstract void loading(T data);

    public abstract void idle(T data);

    public abstract void error(Throwable throwable);
}
