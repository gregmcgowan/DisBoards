package com.drownedinsound.events;

/**
 * Created by gregmcgowan on 29/11/14.
 */
public class RequestCompletedEvent {

    private Object tag;

    public RequestCompletedEvent(Object tag) {
        this.tag = tag;
    }

    public Object getIdentifier() {
        return tag;
    }
}
