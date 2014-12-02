package com.drownedinsound.events;

/**
 * Created by gregmcgowan on 29/11/14.
 */
public class RequestCompletedEvent {

    private String identifier;

    public RequestCompletedEvent(String identifier) {
        this.identifier = identifier;
    }

    public String getIdentifier() {
        return identifier;
    }
}
