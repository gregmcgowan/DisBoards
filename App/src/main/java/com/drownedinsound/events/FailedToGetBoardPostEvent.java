package com.drownedinsound.events;

/**
 * Created by gregmcgowan on 01/11/15.
 */
public class FailedToGetBoardPostEvent {

    private int callingId;

    public FailedToGetBoardPostEvent(int callingId) {
        this.callingId = callingId;
    }

    public int getCallingId() {
        return callingId;
    }
}
