package com.gregmcgowan.drownedinsound.events;

/**
 * Created by gregmcgowan on 27/10/2013.
 */
public class SetBoardPostFavouriteStatusResultEvent {

    boolean success;

    boolean newStatus;

    public SetBoardPostFavouriteStatusResultEvent(boolean success, boolean newStatus) {
        this.success = success;
        this.newStatus = newStatus;

    }

    public boolean isSuccess() {
        return success;
    }

    public boolean isNewStatus() {
        return newStatus;
    }
}
